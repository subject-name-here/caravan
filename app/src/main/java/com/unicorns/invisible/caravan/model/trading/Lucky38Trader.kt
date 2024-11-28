package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
data object Lucky38Trader : Trader {
    var isMrHouseBeaten = false
    override fun isOpen() = isMrHouseBeaten
    override fun openingCondition() = R.string.lucky_38_trader_cond

    override fun getName(): Int = R.string.lucky_38_trader_name

    override fun getCards(): List<Pair<Card, Int>> {
        val rand = Random(save.challengesHash)
        val deck1 = CustomDeck(CardBack.LUCKY_38, false)
        val deck2 = CustomDeck(CardBack.LUCKY_38, true)
        val cards1 = deck1.toList().shuffled(rand).take(9)
        val cards2 = deck2.toList().shuffled(rand).take(7)
        return (cards1 + cards2).map { card -> card to save.getPriceOfCard(card) }
    }

    override fun getStyles(): List<Style> = listOf(Style.DESERT, Style.ALASKA_FRONTIER)
}