package com.unicorns.invisible.caravan.model.trading

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.vault_21_trader_condition
import caravan.composeapp.generated.resources.vault_21_trader_empty
import caravan.composeapp.generated.resources.vault_21_trader_welcome
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.utils.getNow
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString


@Serializable
class Vault21Trader : Trader {
    override fun isOpen(): Boolean {
        val dayNumber = getNow().dayOfMonth
        return dayNumber == 21 || saveGlobal.wins >= 21
    }
    override suspend fun openingCondition() =
        getString(Res.string.vault_21_trader_condition)

    override fun getUpdateRate() = 4

    override fun getWelcomeMessage() = Res.string.vault_21_trader_welcome
    override fun getEmptyStoreMessage() = Res.string.vault_21_trader_empty

    override fun getSymbol() = "21"

    override fun getCards(): List<CardWithPrice> =
        getCards(CardBack.VAULT_21_DAY) + getCards(CardBack.VAULT_21_NIGHT)
}