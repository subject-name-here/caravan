package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.primitives.Card
import kotlinx.serialization.Serializable


@Serializable
sealed interface Trader {
    fun isOpen(): Boolean
    fun openingCondition(): Int
    fun getName(): Int
    fun getCards(): List<Pair<Card, Int>>
    fun getStyles(): List<Style>
}