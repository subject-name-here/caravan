package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


@OptIn(ExperimentalSerializationApi::class)
@Serializable
class Save {
    @EncodeDefault
    val availableDecks = CardBack.entries.associateWith { false }.toMutableMap().apply {
        this[CardBack.STANDARD] = true
        this[CardBack.SIERRA_MADRE] = true // TODO: remove later.
    }

    @EncodeDefault
    var selectedDeck: CardBack = CardBack.STANDARD

    @EncodeDefault
    var gamesStarted = 0
    @EncodeDefault
    var gamesFinished = 0
    @EncodeDefault
    var wins = 0
}