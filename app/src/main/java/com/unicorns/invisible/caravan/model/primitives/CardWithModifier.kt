package com.unicorns.invisible.caravan.model.primitives

import kotlinx.serialization.Serializable


@Serializable
class CardWithModifier(
    val card: Card,
    val modifiers: MutableList<Card> = mutableListOf()
) {
    val numOfKings: Int
        get() = modifiers.count { it.rank == Rank.KING }
    val topQueen: Card?
        get() = modifiers.findLast { it.rank == Rank.QUEEN }

    fun getValue(): Int {
        return if (card.isFace()) {
            0
        } else {
            card.rank.value * (numOfKings + 1)
        }
    }
}