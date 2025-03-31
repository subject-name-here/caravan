package com.unicorns.invisible.caravan.model.trading

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.tops_trader_condition
import caravan.composeapp.generated.resources.tops_trader_empty
import caravan.composeapp.generated.resources.tops_trader_welcome
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.utils.booleanToPlusOrMinus
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString


@Serializable
class TopsTrader : Trader {
    var bennyDefeated = false
    var isLuc10Defeated = false
    var isVictorDefeated = false
    override fun isOpen() = bennyDefeated && isLuc10Defeated && isVictorDefeated
    override suspend fun openingCondition() =
        getString(
            Res.string.tops_trader_condition,
            booleanToPlusOrMinus(bennyDefeated),
            booleanToPlusOrMinus(isLuc10Defeated),
            booleanToPlusOrMinus(isVictorDefeated),
        )

    override fun getUpdateRate() = 8

    override fun getWelcomeMessage() = Res.string.tops_trader_welcome
    override fun getEmptyStoreMessage() = Res.string.tops_trader_empty

    override fun getSymbol() = "T"

    override fun getCards(): List<CardWithPrice> =
        getCards(CardBack.TOPS) + getCards(CardBack.TOPS_RED)
}