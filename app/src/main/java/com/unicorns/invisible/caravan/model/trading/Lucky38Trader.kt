package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
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

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.LUCKY_38)
}