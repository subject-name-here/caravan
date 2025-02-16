package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class Card(val rank: Rank, val suit: Suit, val back: CardBack, val isAlt: Boolean) {
    @Transient
    var handAnimationMark = AnimationMark.STABLE
    @Transient
    var caravanAnimationMark = AnimationMark.STABLE

    fun isModifier() = rank.isFace() || isNuclear()

    override fun toString(): String {
        return "${this.hashCode() % 22229}; " +
                "${this.rank.name}; ${this.suit.name}; ${this.back.name};" +
                "${if (this.isAlt) " ALT!" else ""};"
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
        return back == CardBack.NUCLEAR
    }

    fun isWildWasteland(): Boolean {
        return back == CardBack.WILD_WASTELAND && rank.isFace()
    }

    fun isOrdinary(): Boolean {
        return !isWildWasteland() && !isNuclear()
    }

    enum class WildWastelandCardType(val rank: Rank, val suit: Suit) {
        CAZADOR(Rank.QUEEN, Suit.HEARTS),
        UFO(Rank.JACK, Suit.SPADES),
        MUGGY(Rank.KING, Suit.HEARTS),
        FEV(Rank.KING, Suit.CLUBS),
        YES_MAN(Rank.KING, Suit.DIAMONDS),
        DIFFICULT_PETE(Rank.KING, Suit.SPADES);
    }

    fun getWildWastelandType(): WildWastelandCardType? {
        if (!isWildWasteland()) return null
        return WildWastelandCardType.entries.firstOrNull { it.rank == this.rank && it.suit == this.suit }
    }

    fun getPriceOfCard(): Int {
        val base = if (isAlt) 30.0 else 10.0
        val rankMult = when (rank) {
            Rank.ACE, Rank.SIX, Rank.QUEEN -> 1.0
            Rank.TWO, Rank.THREE -> 0.8
            Rank.FOUR, Rank.FIVE -> 0.9
            Rank.SEVEN, Rank.EIGHT, Rank.NINE -> 1.1
            Rank.TEN -> 1.2
            Rank.JACK -> 1.3
            Rank.KING -> 1.4
            Rank.JOKER -> 1.5
        }
        return (base * rankMult).toInt()
    }
}