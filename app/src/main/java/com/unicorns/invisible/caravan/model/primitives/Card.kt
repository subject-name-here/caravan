package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


@Serializable
class Card(val rank: Rank, val suit: Suit, val back: CardBack, val isAlt: Boolean = false) {
    fun isFace() = rank.isFace()
    fun copy(): Card = Card(rank, suit, back, isAlt)

    override fun toString(): String {
        return "${this.hashCode()}; ${this.rank.ordinal}; ${this.suit.ordinal}; ${this.back.ordinal}; ${this.isAlt};"
    }
}