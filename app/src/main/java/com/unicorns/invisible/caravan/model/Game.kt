package com.unicorns.invisible.caravan.model

import androidx.compose.runtime.saveable.Saver
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
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
    var saySomething: (Int, Int) -> Unit = { _, _ -> }

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

    suspend fun processFieldAndHand(cResources: CResources, updateView: () -> Unit) {
        val caravans = playerCaravans + enemyCaravans
        val cards = caravans.flatMap { it.cards }
        if (cards.any { it.hasJacks() || it.hasActiveJoker } || (cResources.hand.size < 5 && cResources.deckSize > 0)) {
            delay(760L)
            processJacks()
            processJoker()

            if (cResources.hand.size < 5) {
                cResources.addToHand()
            }

            updateView()
        }
    }

    fun afterPlayerMove(updateView: () -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            isPlayerTurn = false
            processFieldAndHand(playerCResources, updateView)
            delay(760L)
            if (checkOnGameOver()) {
                return@launch
            }

            enemy.makeMove(this@Game)
            updateView()
            processFieldAndHand(enemyCResources, updateView)
            delay(760L)
            isPlayerTurn = true
            checkOnGameOver()
            updateView()
            delay(760L)
        }
    }

    fun processJacks() {
        (playerCaravans + enemyCaravans).forEach { caravan ->
            caravan.removeAllJackedCards()
        }
    }

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

    fun copy(): Game {
        return Game(
            CResources(CustomDeck()),
            enemy
        ).also {
            it.playerCResources.copyFrom(this.playerCResources)
            it.enemyCResources.copyFrom(this.enemyCResources)
            it.playerCaravans.forEachIndexed { index, caravan ->
                caravan.copyFrom(this.playerCaravans[index])
            }
            it.enemyCaravans.forEachIndexed { index, caravan ->
                caravan.copyFrom(this.enemyCaravans[index])
            }
        }
    }
}

val GameSaver = Saver<Game, String>(
    save = { json.encodeToString(it) },
    restore = { json.decodeFromString<Game>(it) }
)