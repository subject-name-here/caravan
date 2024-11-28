package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
data object TopsTrader : Trader {
    var isBennyDefeated = 0
    override fun isOpen() = isBennyDefeated >= 7
    override fun openingCondition() = R.string.tops_trader_condition

    override fun getName(): Int = R.string.tops_trader_name

    override fun getCards(): List<Pair<Card, Int>> {
        val rand = Random(save.challengesHash)
        val deck1 = CustomDeck(CardBack.TOPS, false)
        val deck2 = CustomDeck(CardBack.TOPS, true)
        val cards1 = deck1.toList().shuffled(rand).take(rand.nextInt(6, 8))
        val cards2 = deck2.toList().shuffled(rand).take(rand.nextInt(1, 3))
        val wildCardBack = listOf(
            CardBack.ULTRA_LUXE,
            CardBack.GOMORRAH,
            CardBack.LUCKY_38,
            CardBack.VAULT_21
        ).random()
        val cards3 = listOf(Card(Rank.entries.random(), Suit.entries.random(), wildCardBack, false))
        return (cards1 + cards2 + cards3).map { card -> card to save.getPriceOfCard(card) }
    }

    override fun getStyles(): List<Style> = listOf(Style.NEW_WORLD)
}