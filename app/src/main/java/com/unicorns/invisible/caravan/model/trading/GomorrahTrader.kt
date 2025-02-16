package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.utils.booleanToPlusOrMinus
import kotlinx.serialization.Serializable


@Serializable
class GomorrahTrader : Trader {
    var vulpesDefeated = false
    var oliverDefeated = false
    var cardinalDefeated = false
    override fun isOpen() = vulpesDefeated && oliverDefeated && cardinalDefeated
    override fun openingCondition(activity: MainActivity) =
        activity.getString(
            R.string.gomorrah_trader_condition,
            booleanToPlusOrMinus(vulpesDefeated),
            booleanToPlusOrMinus(oliverDefeated),
            booleanToPlusOrMinus(cardinalDefeated),
        )

    override fun getUpdateRate() = 12

    override fun getWelcomeMessage() = R.string.gomorrah_trader_welcome
    override fun getEmptyStoreMessage() = R.string.gomorrah_trader_empty

    override fun getSymbol() = "G"

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.GOMORRAH)
}