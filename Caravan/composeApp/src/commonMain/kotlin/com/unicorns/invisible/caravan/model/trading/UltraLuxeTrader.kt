package com.unicorns.invisible.caravan.model.trading

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.ultra_luxe_trader_condition
import caravan.composeapp.generated.resources.ultra_luxe_trader_empty
import caravan.composeapp.generated.resources.ultra_luxe_trader_welcome
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.utils.getNow
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString


@Serializable
data object UltraLuxeTrader : Trader {
    override fun isOpen(): Boolean {
        val dayNumber = getNow().dayOfWeek
        return dayNumber == DayOfWeek.MONDAY || dayNumber == DayOfWeek.THURSDAY
    }

    override suspend fun openingCondition() = getString(Res.string.ultra_luxe_trader_condition)
    override fun getUpdateRate() = 1

    override fun getWelcomeMessage() = Res.string.ultra_luxe_trader_welcome
    override fun getEmptyStoreMessage() = Res.string.ultra_luxe_trader_empty

    override fun getSymbol() = "UL"

    override fun getCards(): List<CardWithPrice> =
        getCards(CardBack.ULTRA_LUXE) + getCards(CardBack.ULTRA_LUXE_CRIME)
}