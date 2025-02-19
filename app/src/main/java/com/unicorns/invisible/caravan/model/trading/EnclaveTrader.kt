package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import kotlinx.serialization.Serializable


@Serializable
class EnclaveTrader : Trader {
    var isTowerBeaten = false
    override fun isOpen() = isTowerBeaten
    override fun openingCondition(activity: MainActivity) =
        activity.getString(R.string.enclave_trader_cond)

    override fun getUpdateRate() = 1

    override fun getWelcomeMessage() = R.string.enclave_trader_welcome
    override fun getEmptyStoreMessage() = R.string.enclave_trader_empty

    override fun getSymbol() = "E"

    override fun getCards(): List<Card> = getCards(CardBack.ENCLAVE)
}