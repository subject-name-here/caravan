package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import java.util.Calendar


@Serializable
class UltraLuxeTrader : Trader {
    override fun isOpen(): Boolean {
        val calendar = Calendar.getInstance()
        val dayNumber = calendar.get(Calendar.DAY_OF_WEEK)
        return dayNumber == Calendar.MONDAY || dayNumber == Calendar.THURSDAY || save.betaReward
    }

    override fun openingCondition(activity: MainActivity) = activity.getString(R.string.ultra_luxe_trader_condition)
    override fun getUpdateRate() = 6

    override fun getWelcomeMessage() = R.string.ultra_luxe_trader_welcome
    override fun getEmptyStoreMessage() = R.string.ultra_luxe_trader_empty

    override fun getSymbol() = "UL"

    override fun getCards(): List<CardWithPrice> = getCards(CardBack.ULTRA_LUXE)
}