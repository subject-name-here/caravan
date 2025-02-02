package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.trading.Trader.Companion.booleanToPlusOrMinus
import kotlinx.serialization.Serializable


@Serializable
class SierraMadreTrader : Trader {
    var elijahDefeated = false
    var ulyssesBeaten = false
    var drMobiusBeaten = false
    override fun isOpen() = elijahDefeated && ulyssesBeaten && drMobiusBeaten
    override fun openingCondition(activity: MainActivity) =
        activity.getString(
            R.string.sierra_madre_trader_cond,
            booleanToPlusOrMinus(elijahDefeated),
            booleanToPlusOrMinus(ulyssesBeaten),
            booleanToPlusOrMinus(drMobiusBeaten),
        )

    override fun getName(): Int = R.string.sierra_madre_trader_name
    override fun getSymbol() = "SM"

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.SIERRA_MADRE)
    override fun getStyles(): List<Style> = listOf(Style.SIERRA_MADRE, Style.MADRE_ROJA)
}