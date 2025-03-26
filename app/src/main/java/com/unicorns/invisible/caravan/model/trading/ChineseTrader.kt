package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable


@Serializable
class ChineseTrader : Trader {
    var is1921Entered = false
    override fun isOpen() = is1921Entered && save.storyCompleted
    override fun openingCondition(activity: MainActivity) =
        activity.getString(R.string.chinese_trader_condition)

    override fun getUpdateRate() = 1

    override fun getWelcomeMessage() = R.string.chinese_trader_welcome
    override fun getEmptyStoreMessage() = R.string.chinese_trader_empty

    override fun getSymbol(): String = "•"

    override fun getCards(): List<CardWithPrice> = getCards(CardBack.CHINESE)
}