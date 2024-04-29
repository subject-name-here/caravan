package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


@Serializable
class Card(val rank: Rank, val suit: Suit, val back: CardBack) {
    fun isFace() = rank.isFace()

    fun getName() = "${this.rank} of ${if (this.rank != Rank.JOKER) this.suit else (this.suit.ordinal + 1)}"
}