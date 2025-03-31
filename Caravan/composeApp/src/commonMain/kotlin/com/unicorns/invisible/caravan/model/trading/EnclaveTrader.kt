package com.unicorns.invisible.caravan.model.trading

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.enclave_trader_cond
import caravan.composeapp.generated.resources.enclave_trader_empty
import caravan.composeapp.generated.resources.enclave_trader_welcome
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString


@Serializable
class EnclaveTrader : Trader {
    var isTowerBeaten = false
    override fun isOpen() = isTowerBeaten
    override suspend fun openingCondition() = getString(Res.string.enclave_trader_cond)

    override fun getUpdateRate() = 1

    override fun getWelcomeMessage() = Res.string.enclave_trader_welcome
    override fun getEmptyStoreMessage() = Res.string.enclave_trader_empty

    override fun getSymbol() = "E"

    override fun getCards(): List<CardWithPrice> = getCards(CardBack.ENCLAVE)
}