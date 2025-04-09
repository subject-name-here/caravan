package com.unicorns.invisible.caravan.model.trading

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.sierra_madre_trader_cond
import caravan.composeapp.generated.resources.sierra_madre_trader_empty
import caravan.composeapp.generated.resources.sierra_madre_trader_welcome
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString


@Serializable
class SierraMadreTrader : Trader {
    override fun isOpen() = save.storyProgress >= 11
    override suspend fun openingCondition() = getString(Res.string.sierra_madre_trader_cond,)

    override fun getUpdateRate() = 24

    override fun getWelcomeMessage() = Res.string.sierra_madre_trader_welcome
    override fun getEmptyStoreMessage() = Res.string.sierra_madre_trader_empty

    override fun getSymbol() = "SM"

    override fun getCards(): List<CardWithPrice> =
        getCards(CardBack.SIERRA_MADRE_DIRTY) + getCards(CardBack.SIERRA_MADRE_CLEAN)
}