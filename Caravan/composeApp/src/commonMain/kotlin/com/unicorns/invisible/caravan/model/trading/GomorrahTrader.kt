package com.unicorns.invisible.caravan.model.trading

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.gomorrah_trader_condition
import caravan.composeapp.generated.resources.gomorrah_trader_empty
import caravan.composeapp.generated.resources.gomorrah_trader_welcome
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.utils.booleanToPlusOrMinus
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString


@Serializable
class GomorrahTrader : Trader {
    var vulpesDefeated = false
    var oliverDefeated = false
    var cardinalDefeated = false
    override fun isOpen() = vulpesDefeated && oliverDefeated && cardinalDefeated
    override suspend fun openingCondition() =
        getString(
            Res.string.gomorrah_trader_condition,
            booleanToPlusOrMinus(vulpesDefeated),
            booleanToPlusOrMinus(oliverDefeated),
            booleanToPlusOrMinus(cardinalDefeated),
        )

    override fun getUpdateRate() = 12

    override fun getWelcomeMessage() = Res.string.gomorrah_trader_welcome
    override fun getEmptyStoreMessage() = Res.string.gomorrah_trader_empty

    override fun getSymbol() = "G"

    override fun getCards(): List<CardWithPrice> =
        getCards(CardBack.GOMORRAH) + getCards(CardBack.GOMORRAH_DARK)
}