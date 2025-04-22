package com.unicorns.invisible.caravan.model.trading

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.gomorrah_trader_condition
import caravan.composeapp.generated.resources.gomorrah_trader_empty
import caravan.composeapp.generated.resources.gomorrah_trader_welcome
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.saveGlobal
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString


@Serializable
class GomorrahTrader : Trader {
    override fun isOpen() = saveGlobal.gamesFinished >= 666
    override suspend fun openingCondition() = getString(Res.string.gomorrah_trader_condition)

    override fun getUpdateRate() = 12

    override fun getWelcomeMessage() = Res.string.gomorrah_trader_welcome
    override fun getEmptyStoreMessage() = Res.string.gomorrah_trader_empty

    override fun getSymbol() = "G"

    override fun getCards(): List<CardWithPrice> =
        getCards(CardBack.GOMORRAH) + getCards(CardBack.GOMORRAH_DARK)
}