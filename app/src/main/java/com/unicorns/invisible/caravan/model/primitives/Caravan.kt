package com.unicorns.invisible.caravan.model.primitives

import kotlinx.serialization.Serializable


@Serializable
class Caravan {
    val cards = mutableListOf<CardWithModifier>()

    fun dropCaravan() {
        cards.clear()
    }
}