package com.unicorns.invisible.caravan.model

import androidx.compose.runtime.saveable.Saver
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Deck
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
    val playerDeck: Deck,
    val enemyDeck: Deck,
    private val enemy: Enemy
) {
    val playerCaravans = listOf(Caravan(), Caravan(), Caravan())
    val enemyCaravans = listOf(Caravan(), Caravan(), Caravan())

    var isPlayerTurn = true

    @Transient
    var onWin: () -> Unit = {}
    @Transient
    var onLose: () -> Unit = {}

    var isGameOver = 0
        set(value) {
            field = value
            when (value) {
                -1 -> onLose()
                1 -> onWin()
            }
        }

    fun startGame() {
        playerDeck.shuffle()
        enemyDeck.shuffle()
        playerDeck.initHand()
        enemyDeck.initHand()
    }

    fun afterPlayerMove(callback: () -> Unit) {
        if (playerDeck.hand.size < 5 && playerDeck.deckSize > 0) {
            playerDeck.addToHand()
        }
        isPlayerTurn = false

        if (checkOnGameOver()) {
            return
        }

        CoroutineScope(Dispatchers.Default).launch {
            enemyMove()
            callback()
        }
    }

    private suspend fun enemyMove() {
        enemy.makeMove(this)
        if (enemyDeck.hand.size < 5 && enemyDeck.deckSize > 0) {
            enemyDeck.addToHand()
        }

        isPlayerTurn = true
        if (checkOnGameOver()) {
            return
        }
    }

    private fun checkOnGameOver(): Boolean {
        if (!isPlayerTurn && enemyDeck.hand.size == 0) {
            isGameOver = 1
            return true
        }
        if (isPlayerTurn && playerDeck.hand.size == 0) {
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
}

val GameSaver = Saver<Game, String>(
    save = { json.encodeToString(it) },
    restore = { json.decodeFromString<Game>(it) }
)