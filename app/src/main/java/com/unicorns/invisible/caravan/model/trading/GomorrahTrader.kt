package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.trading.Trader.Companion.booleanToPlusOrMinus
import kotlinx.serialization.Serializable


@Serializable
class GomorrahTrader : Trader {
    var isVulpesDefeated = false
    var isOliverDefeated = false
    var isCardinalDefeated = false
    override fun isOpen() = isVulpesDefeated && isOliverDefeated && isCardinalDefeated
    override fun openingCondition(activity: MainActivity) =
        activity.getString(
            R.string.gomorrah_trader_condition,
            booleanToPlusOrMinus(isVulpesDefeated),
            booleanToPlusOrMinus(isOliverDefeated),
            booleanToPlusOrMinus(isCardinalDefeated),
        )

    override fun getName(): Int = R.string.gomorrah_trader_name
    override fun getSymbol() = "G"

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.GOMORRAH)
    override fun getStyles(): List<Style> = listOf(Style.PIP_GIRL)
}