package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import java.util.Calendar


@Serializable
class Vault21Trader : Trader {
    override fun isOpen(): Boolean {
        val calendar = Calendar.getInstance()
        val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
        return dayNumber == 21 || save.wins >= 21
    }
    override fun openingCondition(activity: MainActivity) =
        activity.getString(R.string.vault_21_trader_condition)

    override fun getUpdateRate() = 4

    override fun getWelcomeMessage() = R.string.vault_21_trader_welcome
    override fun getEmptyStoreMessage() = R.string.vault_21_trader_empty

    override fun getSymbol() = "21"

    override fun getCards(): List<CardWithPrice> =
        getCards(CardBack.VAULT_21_DAY) + getCards(CardBack.VAULT_21_NIGHT)
}