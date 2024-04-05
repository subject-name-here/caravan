package com.unicorns.invisible.caravan.model

import androidx.compose.runtime.saveable.Saver
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.Deck
import com.unicorns.invisible.caravan.save.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString


@Serializable
class Game(val playerDeck: Deck, val enemyDeck: Deck) {
    val playerCaravans = listOf(Caravan(), Caravan(), Caravan())
    val enemyCaravans = listOf(Caravan(), Caravan(), Caravan())

    fun startGame() {
        playerDeck.shuffle()
        enemyDeck.shuffle()
        playerDeck.initHand()
        enemyDeck.initHand()
    }

    fun afterPlayerMove() {
        if (playerDeck.hand.size < 5 && playerDeck.deckSize > 0) {
            playerDeck.addToHand()
        }
    }
}

val GameSaver = Saver<Game, String>(
    save = { json.encodeToString(it) },
    restore = { json.decodeFromString<Game>(it) }
)