package com.unicorns.invisible.caravan.model.trading

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.chinese_trader_condition
import caravan.composeapp.generated.resources.chinese_trader_empty
import caravan.composeapp.generated.resources.chinese_trader_welcome
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.saveGlobal
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString


@Serializable
data object ChineseTrader : Trader {
    override fun isOpen() = saveGlobal.storyCompleted
    override suspend fun openingCondition() = getString(Res.string.chinese_trader_condition)

    override fun getUpdateRate() = 24

    override fun getWelcomeMessage() = Res.string.chinese_trader_welcome
    override fun getEmptyStoreMessage() = Res.string.chinese_trader_empty

    override fun getSymbol(): String = "•"

    override fun getCards(): List<CardWithPrice> = getCards(CardBack.CHINESE)
}