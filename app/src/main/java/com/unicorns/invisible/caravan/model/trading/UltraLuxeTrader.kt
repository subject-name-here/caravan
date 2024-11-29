package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import java.util.Calendar
import kotlin.random.Random


@Serializable
class UltraLuxeTrader : Trader {
    override fun isOpen(): Boolean {
        val calendar = Calendar.getInstance()
        val dayNumber = calendar.get(Calendar.DAY_OF_WEEK)
        return dayNumber == Calendar.MONDAY || dayNumber == Calendar.THURSDAY
    }

    override fun openingCondition(activity: MainActivity) = activity.getString(R.string.ultra_luxe_trader_condition)

    override fun getName(): Int = R.string.ultra_luxe_trader_name

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.ULTRA_LUXE, 12)

    override fun getStyles(): List<Style> {
        return listOf(Style.OLD_WORLD)
    }
}