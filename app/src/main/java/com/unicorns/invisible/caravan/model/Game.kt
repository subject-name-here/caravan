package com.unicorns.invisible.caravan.model

import androidx.compose.runtime.saveable.Saver
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Deck
import com.unicorns.invisible.caravan.save.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString


@Serializable
class Game(val playerDeck: Deck, val enemyDeck: Deck) {
    val playerCaravans = listOf(Caravan(), Caravan(), Caravan())
    val enemyCaravans = listOf(Caravan(), Caravan(), Caravan())

    var isPlayerTurn = true

    fun startGame() {
        playerDeck.shuffle()
        enemyDeck.shuffle()
        playerDeck.initHand()
        enemyDeck.initHand()
    }

    fun afterPlayerMove(callback: () -> Unit) {
        isPlayerTurn = false
        if (playerDeck.hand.size < 5 && playerDeck.deckSize > 0) {
            playerDeck.addToHand()
        }

        CoroutineScope(Dispatchers.Default).launch {
            delay(2500L)
            enemyMove()
            callback()
        }
    }

    private fun enemyMove() {
        // TODO: stragedy
        if (enemyDeck.hand.isNotEmpty()) {
            enemyDeck.hand.removeAt(0)
        }
        if (enemyDeck.hand.size < 5 && enemyDeck.deckSize > 0) {
            enemyDeck.addToHand()
        }

        isPlayerTurn = true
    }
}

val GameSaver = Saver<Game, String>(
    save = { json.encodeToString(it) },
    restore = { json.decodeFromString<Game>(it) }
)