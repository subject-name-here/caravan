package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


@Serializable
// TODO: why not data class?
class Card(val rank: Rank, val suit: Suit, val back: CardBack) {
    fun isFace() = rank.isFace()
}