package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


@Serializable
class Card(val rank: Rank, val suit: Suit, val back: CardBack, var isAlt: Boolean? = null) {
    fun isFace() = rank.isFace()
}