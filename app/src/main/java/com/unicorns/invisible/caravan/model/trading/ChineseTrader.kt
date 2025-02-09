package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable


@Serializable
class ChineseTrader : Trader {
    var is1921Entered = false
    override fun isOpen() = is1921Entered // TODO: 3.0 - && save.storyProgress >= 9

    override fun openingCondition(activity: MainActivity) =
        activity.getString(R.string.chinese_trader_condition_tmp)
        // activity.getString(R.string.chinese_trader_condition)

    override fun getName(): Int = R.string.chinese_trader_name
    override fun getSymbol(): String = "•"

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.CHINESE)
    override fun getStyles(): List<Style> = emptyList()
}