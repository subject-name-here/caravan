package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.trading.Trader.Companion.booleanToPlusOrMinus
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

    override fun getName(): Int = R.string.tops_trader_name
    override fun getSymbol() = "T"

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.TOPS)
    override fun getStyles(): List<Style> = listOf(Style.NEW_WORLD)
}