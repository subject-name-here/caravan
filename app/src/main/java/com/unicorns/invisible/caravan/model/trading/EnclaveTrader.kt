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
class EnclaveTrader : Trader {
    var isTowerBeaten = false
    override fun isOpen() = isTowerBeaten

    override fun openingCondition(activity: MainActivity) =
        activity.getString(R.string.enclave_trader_cond)

    override fun getName(): Int = R.string.enclave_trader_name

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.ENCLAVE, 10)

    override fun getStyles(): List<Style> = emptyList()
}