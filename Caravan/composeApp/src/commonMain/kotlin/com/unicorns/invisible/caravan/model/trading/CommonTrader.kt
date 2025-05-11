package com.unicorns.invisible.caravan.model.trading

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.common_trader_cond
import caravan.composeapp.generated.resources.common_trader_empty
import caravan.composeapp.generated.resources.common_trader_welcome
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.utils.getNow
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.getString

class CommonTrader : Trader {
    override fun isOpen(): Boolean {
        val dayNumber = getNow().dayOfWeek
        return dayNumber != DayOfWeek.SUNDAY
    }
    override suspend fun openingCondition() = getString(Res.string.common_trader_cond)

    override fun getUpdateRate() = 24

    override fun getWelcomeMessage() = Res.string.common_trader_welcome
    override fun getEmptyStoreMessage() = Res.string.common_trader_empty

    override fun getSymbol() = "?"

    override fun getCards(): List<CardWithPrice> = emptyList()
}