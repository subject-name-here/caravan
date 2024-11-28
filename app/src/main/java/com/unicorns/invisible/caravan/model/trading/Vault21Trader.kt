package com.unicorns.invisible.caravan.model.trading

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
data object Vault21Trader : Trader {
    override fun isOpen(): Boolean {
        val calendar = Calendar.getInstance()
        val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
        return dayNumber == 21 || save.wins >= 21
    }
    override fun openingCondition() = R.string.vault_21_trader_condition

    override fun getName(): Int = R.string.vault_21_trader_name

    override fun getCards(): List<Pair<Card, Int>> {
        val rand = Random(save.challengesHash)
        val deck1 = CustomDeck(CardBack.VAULT_21, false)
        val deck2 = CustomDeck(CardBack.VAULT_21, true)
        val cards1 = deck1.toList().shuffled(rand).take(rand.nextInt(6, 8))
        val cards2 = deck2.toList().shuffled(rand).take(rand.nextInt(1, 3))
        return (cards1 + cards2).map { card -> card to save.getPriceOfCard(card) }
    }

    override fun getStyles(): List<Style> = listOf(Style.VAULT_21, Style.VAULT_22)
}