package com.unicorns.invisible.caravan.model

import androidx.compose.runtime.saveable.Saver
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString


@Serializable
class Game(
    val playerCResources: CResources,
    private val enemy: Enemy
) {
    val playerCaravans = listOf(Caravan(), Caravan(), Caravan())
    val enemyCaravans = listOf(Caravan(), Caravan(), Caravan())

    val enemyCResources = enemy.createDeck()

    var isPlayerTurn = true

    @Transient
    var onWin: () -> Unit = {}
    @Transient
    var onLose: () -> Unit = {}

    private var isGameOver = 0
        set(value) {
            field = value
            when (value) {
                -1 -> onLose()
                1 -> onWin()
            }
        }
    fun isOver() = isGameOver != 0

    var isCorrupted = false

    fun isInitStage() = playerCResources.hand.size > 5 || enemyCResources.hand.size > 5

    private fun initDeck(cResources: CResources, maxNumOfFaces: Int) {
        cResources.shuffleDeck()
        var tmpHand = cResources.getInitHand()
        while (tmpHand.count { it.isFace() } > maxNumOfFaces) {
            tmpHand = cResources.getInitHand()
        }
        cResources.initHand(tmpHand)
    }
    fun startGame(maxNumOfFaces: Int = 5) {
        initDeck(playerCResources, maxNumOfFaces)
        initDeck(enemyCResources, maxNumOfFaces)
    }

    private suspend fun processFieldAndHand(cResources: CResources, updateView: () -> Unit) {
        val caravans = playerCaravans + enemyCaravans
        val cards = caravans.flatMap { it.cards }
        if (cards.any { it.hasJacks() || it.hasActiveJoker } || (cResources.hand.size < 5 && cResources.deckSize > 0)) {
            processJacks()
            processJoker()

            if (cResources.hand.size < 5) {
                cResources.addToHand()
            }
            updateView()
            delay(700L)
        }
    }

    fun afterPlayerMove(updateView: () -> Unit) {
        isPlayerTurn = false
        CoroutineScope(Dispatchers.Default).launch {
            delay(700L)
            processFieldAndHand(playerCResources, updateView)
            if (checkOnGameOver()) {
                return@launch
            }

            enemy.makeMove(this@Game)
            updateView()

            delay(700L)
            processFieldAndHand(enemyCResources, updateView)

            isPlayerTurn = true
            checkOnGameOver()
            updateView()
        }
    }

    private fun processJacks() {
        (playerCaravans + enemyCaravans).forEach { caravan ->
            caravan.removeAllJackedCards()
        }
    }

    private fun checkOnGameOver(): Boolean {
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

    private fun processJoker() {
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

val GameSaver = Saver<Game, String>(
    save = { json.encodeToString(it) },
    restore = { json.decodeFromString<Game>(it) }
)