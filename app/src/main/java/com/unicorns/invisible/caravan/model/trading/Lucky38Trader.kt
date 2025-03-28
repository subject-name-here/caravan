package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable


@Serializable
class Lucky38Trader : Trader {
    var isMrHouseBeaten = false
    override fun isOpen() = isMrHouseBeaten || save.betaReward
    override fun openingCondition(activity: MainActivity) =
        activity.getString(R.string.lucky_38_trader_cond)

    override fun getUpdateRate() = 24

    override fun getWelcomeMessage() = R.string.lucky_38_trader_welcome
    override fun getEmptyStoreMessage() = R.string.lucky_38_trader_empty

    override fun getSymbol() = "38"

    override fun getCards(): List<CardWithPrice> =
        getCards(CardBack.LUCKY_38) + getCards(CardBack.LUCKY_38_SPECIAL)
}