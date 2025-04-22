package com.unicorns.invisible.caravan.model.trading

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.lucky_38_trader_cond
import caravan.composeapp.generated.resources.lucky_38_trader_empty
import caravan.composeapp.generated.resources.lucky_38_trader_welcome
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.saveGlobal
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString


@Serializable
class Lucky38Trader : Trader {
    override fun isOpen() = saveGlobal.capsInHand >= 1000
    override suspend fun openingCondition() = getString(Res.string.lucky_38_trader_cond)

    override fun getUpdateRate() = 24

    override fun getWelcomeMessage() = Res.string.lucky_38_trader_welcome
    override fun getEmptyStoreMessage() = Res.string.lucky_38_trader_empty

    override fun getSymbol() = "38"

    override fun getCards(): List<CardWithPrice> =
        getCards(CardBack.LUCKY_38) + getCards(CardBack.LUCKY_38_SPECIAL)
}