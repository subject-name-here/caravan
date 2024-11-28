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
data object UltraLuxeTrader : Trader {
    override fun isOpen(): Boolean {
        val calendar = Calendar.getInstance()
        val dayNumber = calendar.get(Calendar.DAY_OF_WEEK)
        return dayNumber == Calendar.MONDAY || dayNumber == Calendar.THURSDAY
    }

    override fun openingCondition(): Int = R.string.ultra_luxe_trader_condition

    override fun getName(): Int = R.string.ultra_luxe_trader_name

    override fun getCards(): List<Pair<Card, Int>> {
        val rand = Random(save.challengesHash)
        val deck1 = CustomDeck(CardBack.ULTRA_LUXE, false)
        val deck2 = CustomDeck(CardBack.ULTRA_LUXE, true)
        val cards1 = deck1.toList().shuffled(rand).take(rand.nextInt(5, 7))
        val cards2 = deck2.toList().shuffled(rand).take(rand.nextInt(1, 3))
        return (cards1 + cards2).map { card -> card to save.getPriceOfCard(card) }
    }

    override fun getStyles(): List<Style> {
        return listOf(Style.OLD_WORLD)
    }
}