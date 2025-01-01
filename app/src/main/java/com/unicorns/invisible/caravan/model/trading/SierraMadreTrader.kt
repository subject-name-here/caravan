package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable


@Serializable
class SierraMadreTrader : Trader {
    var elijahBeaten = 0
    override fun isOpen() = elijahBeaten >= 6 && save.storyChaptersProgress >= 1
    override fun openingCondition(activity: MainActivity) =
        activity.getString(R.string.sierra_madre_trader_cond, elijahBeaten.toString())

    override fun getName(): Int = R.string.sierra_madre_trader_name

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.SIERRA_MADRE, 12)

    override fun getStyles(): List<Style> = listOf(Style.SIERRA_MADRE, Style.MADRE_ROJA)
}