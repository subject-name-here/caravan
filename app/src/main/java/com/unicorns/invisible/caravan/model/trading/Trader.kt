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

    fun getCards(back: CardBack, nBig: Int): List<Pair<Card, Int>> {
        fun takeCardsFromDeck(deck: List<Card>, n: Int): List<Card> {
            return (deck.take(n) + deck.takeLast(n)).distinct().take(n)
        }

        val b = back.ordinal
        val rand = Random(save.challengesHash xor (b * 31 + 22229) xor (b * b * b + 13))

        val cards1 = takeCardsFromDeck(CustomDeck(back, false).toList().shuffled(rand), nBig)
        val cards2 = if (back.hasAltPlayable()) {
            takeCardsFromDeck(CustomDeck(back, true).toList().shuffled(rand), nBig / 2)
        } else {
            emptyList()
        }

        return (cards1 + cards2).map { card -> card to save.getPriceOfCard(card) }
    }
}