package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import java.util.Calendar
import kotlin.random.Random


@Serializable
class Vault21Trader : Trader {
    override fun isOpen(): Boolean {
        val calendar = Calendar.getInstance()
        val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
        return dayNumber == 21 || save.wins >= 21
    }
    override fun openingCondition(activity: MainActivity) =
        activity.getString(R.string.vault_21_trader_condition)

    override fun getName(): Int = R.string.vault_21_trader_name

    override fun getCards(): List<Pair<Card, Int>> = getCards(CardBack.VAULT_21, 9)
    override fun getStyles(): List<Style> = listOf(Style.VAULT_21, Style.VAULT_22)
}