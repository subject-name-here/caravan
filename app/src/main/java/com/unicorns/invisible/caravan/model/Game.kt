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
    private val enemy: Enemy
) {
    val playerCaravans = listOf(Caravan(), Caravan(), Caravan())
    val enemyCaravans = listOf(Caravan(), Caravan(), Caravan())

    val enemyDeck = enemy.createDeck()

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

    private fun initDeck(deck: Deck, maxNumOfFaces: Int) {
        var tmpHand = deck.getInitHand()
        while (tmpHand.count { it.isFace() } > maxNumOfFaces) {
            tmpHand = deck.getInitHand()
        }
        deck.initHand(tmpHand)
    }
    fun startGame(maxNumOfFaces: Int = 5) {
        initDeck(playerDeck, maxNumOfFaces)
        initDeck(enemyDeck, maxNumOfFaces)
    }

    private suspend fun processFieldAndHand(deck: Deck, updateView: () -> Unit) {
        val caravans = playerCaravans + enemyCaravans
        val modifiers = caravans.flatMap { it.cards }.flatMap { it.modifiers }
        if (modifiers.any { it.rank == Rank.JACK || it.rank == Rank.JOKER } || deck.hand.size < 5) {
            processJacks()
            processJoker()

            if (deck.hand.size < 5) {
                deck.addToHand()
            }
            updateView()
            delay(700L)
        }
    }

    fun afterPlayerMove(updateView: () -> Unit) {
        isPlayerTurn = false
        CoroutineScope(Dispatchers.Default).launch {
            delay(700L)
            processFieldAndHand(playerDeck, updateView)
            if (checkOnGameOver()) {
                return@launch
            }

            enemy.makeMove(this@Game)
            updateView()

            delay(700L)
            processFieldAndHand(enemyDeck, updateView)

            isPlayerTurn = true
            checkOnGameOver()
        }
    }

    private fun processJacks() {
        (playerCaravans + enemyCaravans).forEach { caravan ->
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
        (playerCaravans + enemyCaravans).forEach { caravan ->
            caravan.cards.filter {
                it.hasActiveJoker
            }.forEach {
                putJokerOntoCard(it.card)
                it.hasActiveJoker = false
            }
        }
    }

    private fun putJokerOntoCard(card: Card) {
        if (card.rank == Rank.ACE) {
            (playerCaravans + enemyCaravans).forEach { caravan ->
                caravan.cards.filter { it.card.suit == card.suit && it.card != card }.forEach { caravan.cards.remove(it) }
            }
        } else {
            (playerCaravans + enemyCaravans).forEach { caravan ->
                caravan.cards.filter { it.card.rank == card.rank && it.card != card }.forEach { caravan.cards.remove(it) }
            }
        }
    }
}

val GameSaver = Saver<Game, String>(
    save = { json.encodeToString(it) },
    restore = { json.decodeFromString<Game>(it) }
)