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
data object GomorrahTrader : Trader {
    var isVulpesDefeated = false
    var isLvl6Reached = false
    override fun isOpen() = isVulpesDefeated && isLvl6Reached
    override fun openingCondition() = R.string.gomorrah_trader_condition

    override fun getName(): Int = R.string.gomorrah_trader_name

    override fun getCards(): List<Pair<Card, Int>> {
        val rand = Random(save.challengesHash)
        val deck1 = CustomDeck(CardBack.GOMORRAH, false)
        val deck2 = CustomDeck(CardBack.GOMORRAH, true)
        val cards1 = deck1.toList().shuffled(rand).take(13)
        val cards2 = deck2.toList().shuffled(rand).take(5)
        return (cards1 + cards2).map { card -> card to save.getPriceOfCard(card) }
    }

    override fun getStyles(): List<Style> = listOf(Style.PIP_GIRL)
}