package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import kotlinx.serialization.Serializable


@Serializable
class GomorrahTrader : Trader {
    var isVulpesDefeated = 0
    override fun isOpen() = isVulpesDefeated >= 6
    override fun openingCondition(activity: MainActivity) =
        activity.getString(R.string.gomorrah_trader_condition, isVulpesDefeated.toString())

    override fun getName(): Int = R.string.gomorrah_trader_name

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.GOMORRAH)

    override fun getStyles(): List<Style> = listOf(Style.PIP_GIRL)
}