package com.unicorns.invisible.caravan.model.primitives

import kotlinx.serialization.Serializable
import kotlin.math.pow


@Serializable
class CardWithModifier(val card: Card) {
    private val modifiers: MutableList<Card> = mutableListOf()
    fun addModifier(card: Card): Boolean {
        if (card.rank != Rank.JACK && modifiers.size >= 3) {
            return false
        }
        modifiers.add(card)
        if (card.rank == Rank.JOKER) {
            hasActiveJoker = true
        }
        return true
    }

    var hasActiveJoker: Boolean = false
        private set
    fun deactivateJoker() {
        hasActiveJoker = false
    }

    private val numOfKings: Int
        get() = modifiers.count { it.rank == Rank.KING }
    fun isQueenReversingSequence() = modifiers.count { it.rank == Rank.QUEEN } % 2 == 1

    fun hasJacks() = modifiers.any { it.rank == Rank.JACK }

    fun hasModifiers() = modifiers.size > 0
    fun modifiersCopy() = modifiers.toList()

    fun getValue(): Int {
        return if (card.isFace()) {
            0
        } else {
            card.rank.value * (2.0.pow(numOfKings)).toInt()
        }
    }

    fun getTopSuit(): Suit {
        return modifiers.findLast { it.rank == Rank.QUEEN }?.suit ?: card.suit
    }
}