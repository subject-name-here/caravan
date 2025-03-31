package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.utils.getNow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import kotlin.random.Random


@Serializable
sealed interface Trader {
    fun isOpen(): Boolean
    suspend fun openingCondition(): String

    fun getUpdateRate(): Int
    fun getUpdatePartOfHash() = getNow().hour / getUpdateRate()

    fun getWelcomeMessage(): StringResource
    fun getEmptyStoreMessage(): StringResource
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