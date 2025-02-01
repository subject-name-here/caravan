package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import kotlinx.serialization.Serializable


@Serializable
class EnclaveTrader : Trader {
    var isTowerBeaten = false
    override fun isOpen() = isTowerBeaten

    override fun openingCondition(activity: MainActivity) =
        activity.getString(R.string.enclave_trader_cond)

    override fun getName(): Int = R.string.enclave_trader_name
    override fun getSymbol() = "E"

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.ENCLAVE)
    override fun getStyles(): List<Style> = listOf(Style.ENCLAVE)
}