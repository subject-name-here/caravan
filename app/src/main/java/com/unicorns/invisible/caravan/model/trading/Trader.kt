package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import java.util.Calendar
import kotlin.random.Random


@Serializable
sealed interface Trader {
    fun isOpen(): Boolean
    fun openingCondition(activity: MainActivity): String

    fun getUpdateRate(): Int
    fun getUpdatePartOfHash() = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) / getUpdateRate()

    fun getWelcomeMessage(): Int
    fun getEmptyStoreMessage(): Int
    fun getSymbol(): String
    fun getCards(): List<Pair<Card, Int>>

    fun getCards(back: CardBack): List<Pair<Card, Int>> {
        // TODO: better seed
        val bshl = (back.ordinal * 32 + getUpdatePartOfHash()) shl 23
        val b = bshl - back.ordinal - 1
        val todayHash = save.dailyHash
        val rand = Random(todayHash xor (b * 31 + 22229) xor (b * b * b + 13))

        fun shuffleCards(deck: CustomDeck) = deck.toList().shuffled(rand).filter {
            rand.nextBoolean() || !save.isCardAvailableAlready(it)
        }

        val n = 6

        val cards1 = shuffleCards(CustomDeck(back, false)).take(n)
        val cards2 = if (back.hasAlt()) {
            shuffleCards(CustomDeck(back, true)).take(n / 2)
        } else {
            emptyList()
        }

        val cards = (cards1 + cards2).toMutableList()
        return cards.map { card -> card to save.getPriceOfCard(card) }
    }
}