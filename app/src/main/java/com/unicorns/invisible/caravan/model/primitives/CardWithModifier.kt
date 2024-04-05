package com.unicorns.invisible.caravan.model.primitives

import kotlinx.serialization.Serializable


@Serializable
class CardWithModifier(
    val card: Card,
    val numOfKings: Int = 0,
    val topQueen: Card? = null,
    val modifiers: MutableList<Card> = mutableListOf()
) {
    fun getValue(): Int {
        return if (card.isFace()) {
            0
        } else {
            card.rank.value * (numOfKings + 1)
        }
    }
}