package com.unicorns.invisible.caravan.model.primitives

import kotlinx.serialization.Serializable


@Serializable
data class CardWithModifier(val card: Card, val numOfKings: Int, val topQueen: Card) {
    fun getValue(): Int {
        return if (card.isFace()) {
            0
        } else {
            card.rank.value * (numOfKings + 1)
        }
    }
}