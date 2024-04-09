package com.unicorns.invisible.caravan.model.primitives

import kotlinx.serialization.Serializable
import kotlin.math.pow


@Serializable
class CardWithModifier(val card: Card) {
    val modifiers: MutableList<Card> = mutableListOf()

    private val numOfKings: Int
        get() = modifiers.count { it.rank == Rank.KING }
    fun isQueenReversingSequence() = modifiers.count { it.rank == Rank.QUEEN } % 2 == 1

    fun hasJacks() = modifiers.any { it.rank == Rank.JACK }

    var hasActiveJoker: Boolean = false

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