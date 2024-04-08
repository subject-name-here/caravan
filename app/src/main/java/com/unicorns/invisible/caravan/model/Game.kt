package com.unicorns.invisible.caravan.model

import androidx.compose.runtime.saveable.Saver
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Deck
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

    private var isGameOver = 0
        set(value) {
            field = value
            when (value) {
                -1 -> onLose()
                1 -> onWin()
            }
        }
    fun isOver() = isGameOver != 0

    fun isInitStage() = playerDeck.hand.size > 5 || enemyDeck.hand.size > 5

    fun startGame() {
        while (true) {
            playerDeck.shuffle()
            val hand = playerDeck.getInitHand()
            if (hand.count { it.isFace() } <= 5) {
                break
            }
        }
        while (true) {
            enemyDeck.shuffle()
            val hand = enemyDeck.getInitHand()
            if (hand.count { it.isFace() } <= 5) {
                break
            }
        }

        playerDeck.initHand()
        enemyDeck.initHand()
    }

    fun afterPlayerMove(updateView: () -> Unit) = CoroutineScope(Dispatchers.Default).launch {
        delay(700L)
        if (playerDeck.hand.size < 5) {
            playerDeck.addToHand()
        }
        processJacks()
        processJoker()
        updateView()
        delay(700L)

        isPlayerTurn = false
        if (checkOnGameOver()) {
            return@launch
        }

        enemy.makeMove(this@Game)
        updateView()

        delay(700L)
        if (enemyDeck.hand.size < 5) {
            enemyDeck.addToHand()
        }
        processJacks()
        processJoker()
        updateView()
        delay(700L)

        isPlayerTurn = true
        checkOnGameOver()
    }

    private fun processJacks() {
        playerCaravans.forEach { caravan ->
            caravan.cards.removeAll {
                it.hasJacks()
            }
        }
        enemyCaravans.forEach { caravan ->
            caravan.cards.removeAll {
                it.hasJacks()
            }
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

    private fun processJoker() {
        playerCaravans.forEach { caravan ->
            val jokersOwners = caravan.cards.filter {
                it.hasActiveJoker
            }
            jokersOwners.forEach {
                putJokerOntoCard(it.card)
                it.hasActiveJoker = false
            }
        }
        enemyCaravans.forEach { caravan ->
            val jokersOwners = caravan.cards.filter {
                it.hasActiveJoker
            }
            jokersOwners.forEach {
                putJokerOntoCard(it.card)
                it.hasActiveJoker = false
            }
        }
    }

    private fun putJokerOntoCard(card: Card) {
        if (card.rank == Rank.ACE) {
            playerCaravans.forEach { caravan ->
                caravan.cards.filter { it.card.suit == card.suit && it.card != card }.forEach { caravan.cards.remove(it) }
            }
            enemyCaravans.forEach { caravan ->
                caravan.cards.filter { it.card.suit == card.suit && it.card != card }.forEach { caravan.cards.remove(it) }
            }
        } else {
            playerCaravans.forEach { caravan ->
                caravan.cards.filter { it.card.rank == card.rank && it.card != card }.forEach { caravan.cards.remove(it) }
            }
            enemyCaravans.forEach { caravan ->
                caravan.cards.filter { it.card.rank == card.rank && it.card != card }.forEach { caravan.cards.remove(it) }
            }
        }
    }
}

val GameSaver = Saver<Game, String>(
    save = { json.encodeToString(it) },
    restore = { json.decodeFromString<Game>(it) }
)