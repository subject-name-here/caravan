package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.utils.booleanToPlusOrMinus
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

    override fun getUpdateRate() = 24

    override fun getWelcomeMessage() = R.string.sierra_madre_trader_welcome
    override fun getEmptyStoreMessage() = R.string.sierra_madre_trader_empty

    override fun getSymbol() = "SM"

    override fun getCards(): List<CardWithPrice> = getCards(CardBack.SIERRA_MADRE)
}