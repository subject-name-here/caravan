package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.utils.getNow
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import kotlin.random.Random


@Serializable
sealed interface Trader {
    fun isOpen(): Boolean
    suspend fun openingCondition(): String

    fun getUpdateRate(): Int
    private fun getUpdatePartOfHash() = getNow().hour / getUpdateRate()

    fun getWelcomeMessage(): StringResource
    fun getEmptyStoreMessage(): StringResource
    fun getSymbol(): String
    fun getCards(): List<CardWithPrice>

    fun getCards(back: CardBack): List<CardWithPrice> {
        val bshl = (back.ordinal * 32 + getUpdatePartOfHash()) shl 23
        val b = bshl - back.ordinal - 1
        val todayHash = saveGlobal.dailyHash
        val rand = Random(todayHash xor (b * 31 + 22229) xor (b * b * b + 13))

        val deck = CollectibleDeck(back).toList().shuffled(rand).map { it to rand.nextBoolean() }
        val n = 7

        val cards = mutableListOf<CardWithPrice>()
        deck.forEach { card ->
            if (!saveGlobal.isCardAvailableAlready(card.first) || card.second) {
                cards.add(card.first)
            }
        }
        return cards.take(n)
    }
}