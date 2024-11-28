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
data object SierraMadreTrader : Trader {
    var elijahBeaten = false
    override fun isOpen() = elijahBeaten && save.storyChaptersProgress >= 1
    override fun openingCondition() = R.string.sierra_madre_trader_cond

    override fun getName(): Int = R.string.sierra_madre_trader_name

    override fun getCards(): List<Pair<Card, Int>> {
        val rand = Random(save.challengesHash)
        val deck1 = CustomDeck(CardBack.SIERRA_MADRE, false)
        val deck2 = CustomDeck(CardBack.SIERRA_MADRE, true)
        val cards1 = deck1.toList().shuffled(rand).take(11)
        val cards2 = deck2.toList().shuffled(rand).take(7)
        return (cards1 + cards2).map { card -> card to save.getPriceOfCard(card) }
    }

    override fun getStyles(): List<Style> = listOf(Style.SIERRA_MADRE, Style.MADRE_ROJA)
}