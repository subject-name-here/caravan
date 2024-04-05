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

        enemyCaravans[0].also { it.cards.addAll(listOf(
            CardWithModifier(enemyDeck.hand[0]),
            CardWithModifier(enemyDeck.hand[1]),
            CardWithModifier(enemyDeck.hand[2]),
            CardWithModifier(enemyDeck.hand[3]),
            CardWithModifier(enemyDeck.hand[4]),
            CardWithModifier(enemyDeck.hand[5]),
        )) }
        playerCaravans[0].also {
            it.cards.addAll(listOf(
                CardWithModifier(playerDeck.hand[0]),
                CardWithModifier(playerDeck.hand[1]),
                CardWithModifier(playerDeck.hand[2]),
                CardWithModifier(playerDeck.hand[3]),
                CardWithModifier(playerDeck.hand[4]),
            ))
        }

        enemyCaravans[2].also { it.cards.addAll(listOf(CardWithModifier(enemyDeck.hand[2]), CardWithModifier(enemyDeck.hand[3]))) }
        playerCaravans[2].also { it.cards.addAll(listOf(CardWithModifier(playerDeck.hand[2]), CardWithModifier(playerDeck.hand[3]))) }
    }
}

val GameSaver = Saver<Game, String>(
    save = { json.encodeToString(it) },
    restore = { json.decodeFromString<Game>(it) }
)