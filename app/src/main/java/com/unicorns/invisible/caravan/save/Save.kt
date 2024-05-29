package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


@OptIn(ExperimentalSerializationApi::class)
@Serializable
class Save {
    @EncodeDefault
    var selectedDeck: Pair<CardBack, Boolean> = CardBack.STANDARD to false

    @EncodeDefault
    val availableDecks = CardBack.entries.associateWith { false }.toMutableMap().apply {
        this[CardBack.STANDARD] = true
    }

    @EncodeDefault
    val availableDecksAlt = CardBack.entries.associateWith { false }.toMutableMap().apply {
        this[CardBack.STANDARD] = true
    }
    @EncodeDefault
    val altDecksChosen = CardBack.entries.associateWith { false }.toMutableMap()

    @EncodeDefault
    var styleId: Int = 1

    @EncodeDefault
    val customDeck: CustomDeck = CustomDeck()
    var useCustomDeck: Boolean = false

    @EncodeDefault
    val availableCards: MutableSet<Card> = HashSet(CustomDeck(CardBack.STANDARD, false).toList())

    fun getCustomDeckCopy(): CustomDeck {
        val deck = CustomDeck()
        customDeck.toList().forEach {
            if (it.isAlt == altDecksChosen[it.back]) {
                deck.add(it)
            }
        }
        return deck
    }

    @EncodeDefault
    var gamesStarted = 0
    @EncodeDefault
    var gamesFinished = 0
    @EncodeDefault
    var wins = 0
}