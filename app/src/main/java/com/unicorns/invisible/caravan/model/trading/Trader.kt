package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
sealed interface Trader {
    fun isOpen(): Boolean
    fun openingCondition(activity: MainActivity): String
    fun getName(): Int
    fun getCards(): List<Pair<Card, Int>>
    fun getStyles(): List<Style>

    fun getCards(back: CardBack, cards1Number: Int): List<Pair<Card, Int>> {
        val rand = Random(save.challengesHash)
        val deck1 = CustomDeck(back, false).toList().shuffled(rand)
        var cards1 = deck1.take(cards1Number)
        if (cards1.all { save.isCardAvailableAlready(it) }) {
            cards1 = deck1.takeLast(cards1Number)
        }

        if (!back.hasAltPlayable()) {
            return cards1.map { card -> card to save.getPriceOfCard(card) }
        }

        val deck2 = CustomDeck(back, true).toList().shuffled(rand)
        var cards2 = deck2.take(cards1Number / 2)
        if (cards2.all { save.isCardAvailableAlready(it) }) {
            cards2 = deck2.takeLast(cards1Number / 2)
        }
        return (cards1 + cards2).map { card -> card to save.getPriceOfCard(card) }
    }
}