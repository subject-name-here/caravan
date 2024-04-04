package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


@OptIn(ExperimentalSerializationApi::class)
@Serializable
class Save {
    @EncodeDefault
    val availableDecks = CardBack.entries.associateWith { false }.toMutableMap().apply { this[CardBack.STANDARD] = true }

    @EncodeDefault
    var selectedDeck: CardBack = CardBack.STANDARD
}