package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.utils.booleanToPlusOrMinus
import kotlinx.serialization.Serializable


@Serializable
class TopsTrader : Trader {
    var bennyDefeated = false
    var isLuc10Defeated = false
    var isVictorDefeated = false
    override fun isOpen() = bennyDefeated && isLuc10Defeated && isVictorDefeated
    override fun openingCondition(activity: MainActivity) =
        activity.getString(
            R.string.tops_trader_condition,
            booleanToPlusOrMinus(bennyDefeated),
            booleanToPlusOrMinus(isLuc10Defeated),
            booleanToPlusOrMinus(isVictorDefeated),
        )

    override fun getUpdateRate() = 8

    override fun getWelcomeMessage() = R.string.tops_trader_welcome
    override fun getEmptyStoreMessage() = R.string.tops_trader_empty

    override fun getSymbol() = "T"

    override fun getCards(): List<CardWithPrice> = getCards(CardBack.TOPS)
}