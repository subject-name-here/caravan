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
class GomorrahTrader : Trader {
    var isVulpesDefeated = false
    override fun isOpen() = isVulpesDefeated
    override fun openingCondition(activity: MainActivity) =
        activity.getString(R.string.gomorrah_trader_condition)

    override fun getName(): Int = R.string.gomorrah_trader_name

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.GOMORRAH, 9)

    override fun getStyles(): List<Style> = listOf(Style.PIP_GIRL)
}