package com.unicorns.invisible.caravan.model

import androidx.compose.runtime.saveable.Saver
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID


@Serializable
class Game(
    val playerCResources: CResources,
    val enemy: Enemy
) {
    val playerCaravans = listOf(Caravan(), Caravan(), Caravan())
    val enemyCaravans = listOf(Caravan(), Caravan(), Caravan())

    val enemyCResources = enemy.createDeck()

    var isPlayerTurn = true

    var id = UUID.randomUUID().toString()

    @Transient
    var onWin: () -> Unit = {}

    @Transient
    var onLose: () -> Unit = {}

    @Transient
    var jokerPlayedSound: () -> Unit = {}

    var isGameOver = 0
        private set(value) {
            field = value
            when (value) {
                -1 -> onLose()
                1 -> onWin()
            }
        }

    fun isOver() = isGameOver != 0

    var isCorrupted = false

    var isExchangingCards = false
    fun isInitStage(): Boolean {
        return playerCResources.hand.size > 5 || enemyCResources.hand.size > 5
    }

    fun initDeck(cResources: CResources, maxNumOfFaces: Int, initHand: Boolean = true) {
        cResources.shuffleDeck()
        var tmpHand = cResources.getTopHand()
        while (tmpHand.count { it.isFace() } > maxNumOfFaces) {
            cResources.shuffleDeck()
            tmpHand = cResources.getTopHand()
        }
        if (initHand) {
            cResources.initHand(tmpHand)
        }
    }

    fun startGame(maxNumOfFaces: Int = 5) {
        initDeck(playerCResources, maxNumOfFaces)
        initDeck(enemyCResources, maxNumOfFaces)
    }

    fun processField(): Boolean {
        val caravans = playerCaravans + enemyCaravans
        val cards = caravans.flatMap { it.cards }
        if (cards.any { it.hasJacks() || it.hasActiveJoker }) {
            processJacks()
            processJoker()

            return true
        }
        return false
    }

    fun processHand(cResources: CResources): Boolean {
        if (cResources.hand.size < 5 && cResources.deckSize > 0) {
            cResources.addToHand()
            return true
        }
        return false
    }

    fun afterPlayerMove(updateView: () -> Unit, speed: AnimationSpeed) {
        CoroutineScope(Dispatchers.Default).launch {
            if (speed.delay != 0L) {
                delay(speed.delay * 3) // Move card from hand; move card ontoField
            } else {
                delay(380L)
            }
            isPlayerTurn = false
            if (processField()) { // Remove cards; move cards within caravan
                updateView()
                if (speed.delay != 0L) {
                    delay(speed.delay * 2)
                }
            }
            if (processHand(playerCResources)) { // Take card into hand
                updateView()
                if (speed.delay != 0L) {
                    delay(speed.delay)
                }
            }

            if (checkOnGameOver()) {
                updateView()
                return@launch
            }

            if (speed.delay != 0L) {
                delay(speed.delay * 2) // Just break.
            } else {
                delay(380L)
            }

            enemy.makeMove(this@Game)
            updateView()
            if (speed.delay != 0L) {
                delay(speed.delay * 3) // Move card from hand; move card ontoField
            } else {
                delay(380L)
            }
            if (processField()) { // Remove cards; move cards within caravan
                updateView()
                if (speed.delay != 0L) {
                    delay(speed.delay * 2)
                }
            }
            if (processHand(enemyCResources)) { // Take card into hand
                updateView()
                if (speed.delay != 0L) {
                    delay(speed.delay)
                }
            }

            isPlayerTurn = true
            checkOnGameOver()
            if (speed.delay != 0L) {
                delay(speed.delay * 2) // Just break.
            }
            updateView()
        }
    }

    private fun processJacks() {
        (playerCaravans + enemyCaravans).forEach { caravan ->
            caravan.removeAllJackedCards()
        }
    }

    @Transient
    var specialGameOverCondition: () -> Int = { 0 }
    fun checkOnGameOver(): Boolean {
        if (!isPlayerTurn && enemyCResources.hand.isEmpty()) {
            isGameOver = 1
            return true
        }
        if (isPlayerTurn && playerCResources.hand.isEmpty()) {
            isGameOver = -1
            return true
        }

        var scorePlayer = 0
        var scoreEnemy = 0

        fun checkLine(p0: Int, e0: Int) {
            if (p0 in (21..26)) {
                if (e0 in (21..26)) {
                    when {
                        p0 > e0 -> scorePlayer++
                        p0 < e0 -> scoreEnemy++
                    }
                } else {
                    scorePlayer++
                }
            } else if (e0 in (21..26)) {
                scoreEnemy++
            }
        }

        checkLine(playerCaravans[0].getValue(), enemyCaravans[0].getValue())
        checkLine(playerCaravans[1].getValue(), enemyCaravans[1].getValue())
        checkLine(playerCaravans[2].getValue(), enemyCaravans[2].getValue())

        if (scorePlayer + scoreEnemy == 3) {
            return if (scorePlayer > scoreEnemy) {
                isGameOver = 1
                true
            } else {
                isGameOver = -1
                true
            }
        }

        val special = specialGameOverCondition()
        if (special != 0) {
            isGameOver = special
            return true
        }

        isGameOver = 0
        return false
    }

    fun processJoker() {
        (playerCaravans + enemyCaravans).forEach { caravan ->
            caravan.cards.filter {
                it.hasActiveJoker
            }.forEach {
                putJokerOntoCard(it.card)
                it.deactivateJoker()
            }
        }
    }

    private fun putJokerOntoCard(card: Card) {
        if (card.rank == Rank.ACE) {
            (playerCaravans + enemyCaravans).forEach { caravan ->
                caravan.jokerRemoveAllSuits(card)
            }
        } else {
            (playerCaravans + enemyCaravans).forEach { caravan ->
                caravan.jokerRemoveAllRanks(card)
            }
        }
    }
}

var currentGame: Game? = null
val GameSaver = Saver<Game, String>(
    save = { "" },
    restore = { currentGame }
)