package com.unicorns.invisible.caravan.model.primitives

import kotlinx.serialization.Serializable


@Serializable
class CardWithModifier(
    val card: Card,
    val modifiers: MutableList<Card> = mutableListOf()
) {
    private val numOfKings: Int
        get() = modifiers.count { it.rank == Rank.KING }
    val topQueen: Card? // TODO: maybe make it private, but give some boolean on outside???
        get() = modifiers.findLast { it.rank == Rank.QUEEN }

    fun getValue(): Int {
        return if (card.isFace()) {
            0
        } else {
            card.rank.value * (numOfKings + 1)
        }
    }

    fun getTopSuit(): Suit {
        return if (topQueen == null) {
            card.suit
        } else {
            topQueen?.suit ?: card.suit
        }
    }
}