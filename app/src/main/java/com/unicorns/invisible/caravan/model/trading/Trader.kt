package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
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
    fun getCards(): List<CardWithPrice>

    fun getCards(back: CardBack): List<CardWithPrice> {
        val bshl = (back.ordinal * 32 + getUpdatePartOfHash()) shl 23
        val b = bshl - back.ordinal - 1
        val todayHash = save.dailyHash
        val rand = Random(todayHash xor (b * 31 + 22229) xor (b * b * b + 13))

        fun shuffleCards(deck: CollectibleDeck) = deck.toList().shuffled(rand)

        val n = 7

        return shuffleCards(CollectibleDeck(back)).take(n)
    }
}