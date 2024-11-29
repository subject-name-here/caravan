package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import kotlinx.serialization.Serializable


@Serializable
class TopsTrader : Trader {
    var isBennyDefeated = 0
    override fun isOpen() = isBennyDefeated >= 7
    override fun openingCondition(activity: MainActivity) =
        activity.getString(R.string.tops_trader_condition, isBennyDefeated.toString())

    override fun getName(): Int = R.string.tops_trader_name

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.TOPS, 10)

    override fun getStyles(): List<Style> = listOf(Style.NEW_WORLD)
}