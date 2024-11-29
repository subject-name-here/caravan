package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class Lucky38Trader : Trader {
    var isMrHouseBeaten = false
    override fun isOpen() = isMrHouseBeaten
    override fun openingCondition(activity: MainActivity) =
        activity.getString(R.string.lucky_38_trader_cond)

    override fun getName(): Int = R.string.lucky_38_trader_name

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.LUCKY_38, 12)

    override fun getStyles(): List<Style> = listOf(Style.DESERT, Style.ALASKA_FRONTIER)
}