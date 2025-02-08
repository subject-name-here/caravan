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
    fun getSymbol(): String
    fun getCards(): List<Pair<Card, Int>>
    fun getStyles(): List<Style>

    fun getCards(back: CardBack, nBig: Int = 13): List<Pair<Card, Int>> {
        if (nBig <= 1) {
            return emptyList()
        }

        val b = back.ordinal
        val rand = Random(save.challengesHash xor (b * 31 + 22229) xor (b * b * b + 13))

        val cards1 = CustomDeck(back, false).toList().shuffled(rand).take(nBig)
        val cards2 = if (back.hasAltPlayable()) {
            CustomDeck(back, true).toList().shuffled(rand).take(nBig / 2 + 1)
        } else {
            emptyList()
        }

        val cards = (cards1 + cards2).toMutableList()
        return if (cards.all { save.isCardAvailableAlready(it) }) {
            getCards(back, nBig / 2)
        } else {
            cards.map { card -> card to save.getPriceOfCard(card) }
        }
    }

    companion object {
        fun booleanToPlusOrMinus(it: Boolean): String {
            return if (it) "+" else "-"
        }
    }
}