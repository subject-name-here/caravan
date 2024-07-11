package com.unicorns.invisible.caravan.model.primitives

import kotlinx.serialization.Serializable
import kotlin.math.pow


@Serializable
class CardWithModifier(val card: Card) {
    private val modifiers: MutableList<Card> = mutableListOf()
    fun addModifier(card: Card) {
        modifiers.add(card)
        card.caravanAnimationMark = Card.AnimationMark.MOVING_IN
        if (card.rank == Rank.JOKER) {
            if (card.suit == Suit.SPADES) {
                hasBomb = true
            } else {
                hasActiveJoker = true
            }
        }
    }

    fun canAddModifier(card: Card): Boolean {
        return card.isFace() && (modifiers.size < 3 || card.rank == Rank.JACK)
    }

    var hasActiveJoker: Boolean = false
        private set
    var hasBomb: Boolean = false
        private set

    fun deactivateJoker() {
        hasActiveJoker = false
        hasBomb = false
    }

    fun isQueenReversingSequence() = modifiers.count { it.rank == Rank.QUEEN } % 2 == 1

    fun hasJacks() = modifiers.any { it.rank == Rank.JACK }

    fun modifiersCopy() = modifiers.toList()

    fun getValue(): Int {
        return if (card.isFace()) {
            0
        } else {
            card.rank.value * (2.0.pow(modifiers.count { it.rank == Rank.KING })).toInt()
        }
    }

    fun getTopSuit(): Suit {
        return modifiers.findLast { it.rank == Rank.QUEEN }?.suit ?: card.suit
    }
}