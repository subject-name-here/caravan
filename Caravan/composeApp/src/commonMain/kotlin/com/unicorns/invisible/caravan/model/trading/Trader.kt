package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.utils.getNow
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

        fun shuffleCards(deck: CollectibleDeck) = deck.toList().sortedWith { o1, o2 ->
            when (o1) {
                is CardNumber -> {
                    if (o2 !is CardNumber) {
                        1
                    } else {
                        if (o1.rank != o2.rank) {
                            o2.rank.value - o1.rank.value
                        } else {
                            o1.suit.ordinal - o2.suit.ordinal
                        }
                    }
                }
                is CardFaceSuited -> {
                    when (o2) {
                        is CardJoker -> {
                            1
                        }
                        is CardFaceSuited -> {
                            if (o1.rank != o2.rank) {
                                o2.rank.value - o1.rank.value
                            } else {
                                o1.suit.ordinal - o2.suit.ordinal
                            }
                        }
                        is CardNumber -> {
                            -1
                        }
                    }
                }
                is CardJoker -> {
                    if (o2 is CardJoker) {
                        o2.number.ordinal - o1.number.ordinal
                    } else {
                        -1
                    }
                }
            }
        }.shuffled(rand)

        val n = 7

        val extra = if (rand.nextBoolean()) {
            shuffleCards(CollectibleDeck(CardBack.STANDARD_UNCOMMON)).take(1)
        } else {
            emptyList()
        }

        return extra + shuffleCards(CollectibleDeck(back)).take(n)
    }
}