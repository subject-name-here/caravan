package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


@Serializable
class Card(val rank: Rank, val suit: Suit, val back: CardBack, val isAlt: Boolean = false) {
    var handAnimationMark = AnimationMark.STABLE
    var caravanAnimationMark = AnimationMark.STABLE

    fun isFace() = rank.isFace()

    override fun toString(): String {
        return "${this.hashCode()}; ${this.rank.ordinal}; ${this.suit.ordinal}; ${this.back.ordinal}; ${this.isAlt};"
    }

    enum class AnimationMark {
        STABLE,
        MOVING_IN,
        MOVING_IN_WIP,
        MOVING_OUT,
        MOVING_OUT_WIP,
    }
}