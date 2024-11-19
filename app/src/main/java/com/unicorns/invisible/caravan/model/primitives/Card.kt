package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


@Serializable
class Card(val rank: Rank, val suit: Suit, val back: CardBack, val isAlt: Boolean) {
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
        MOVING_OUT_ALT,
        MOVING_OUT_ALT_WIP,
        MOVED_OUT;

        fun isMovingIn(): Boolean {
            return this in listOf(MOVING_IN, MOVING_IN_WIP)
        }

        fun isMovingOut(): Boolean {
            return this !in listOf(STABLE, MOVING_IN, MOVING_IN_WIP)
        }
    }

    fun isNuclear(): Boolean {
        return (back == CardBack.CHINESE || back == CardBack.ENCLAVE) && isAlt
    }

    fun isWildWasteland(): Boolean {
        return back == CardBack.MADNESS && isAlt
    }

    fun isOrdinary(): Boolean {
        return !isWildWasteland() && !isNuclear()
    }

    fun getWildWastelandCardType(): WildWastelandCardType? {
        if (!isWildWasteland()) {
            return null
        }

        if (this.rank == Rank.QUEEN) {
            return WildWastelandCardType.CAZADOR
        }
        if (this.rank == Rank.JACK) {
            return WildWastelandCardType.UFO
        }
        return when (this.rank to this.suit) {
            Rank.KING to Suit.HEARTS -> WildWastelandCardType.MUGGY
            Rank.KING to Suit.SPADES -> WildWastelandCardType.DIFFICULT_PETE
            Rank.KING to Suit.DIAMONDS -> WildWastelandCardType.YES_MAN
            Rank.KING to Suit.CLUBS -> WildWastelandCardType.FEV
            else -> null
        }
    }

    enum class WildWastelandCardType {
        CAZADOR,
        DIFFICULT_PETE,
        FEV,
        MUGGY,
        UFO,
        YES_MAN
    }
}