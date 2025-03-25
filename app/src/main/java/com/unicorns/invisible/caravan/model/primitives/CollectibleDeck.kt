package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CResources.Companion.MAX_NUMBER_OF_DECKS
import com.unicorns.invisible.caravan.model.primitives.CResources.Companion.MIN_DECK_SIZE
import com.unicorns.invisible.caravan.model.primitives.CResources.Companion.MIN_NUM_OF_NUMBERS
import kotlinx.serialization.Serializable


@Serializable
class CollectibleDeck() {
    private val cards = HashSet<CardWithPrice>()
    val size: Int
        get() = cards.size

    constructor(back: CardBack): this() {
        RankNumber.entries.forEach { rank ->
            Suit.entries.forEach { suit ->
                cards.add(CardNumber(rank, suit, back))
            }
        }
        Suit.entries.forEach { suit ->
            cards.add(CardFaceSuited(RankFace.JACK, suit, back))
            cards.add(CardFaceSuited(RankFace.QUEEN, suit, back))
            cards.add(CardFaceSuited(RankFace.KING, suit, back))
        }
        cards.add(CardJoker(CardJoker.Number.ONE, back))
        cards.add(CardJoker(CardJoker.Number.TWO, back))
    }

    fun toCardList(): List<Card> = cards.map {
        when (it) {
            is CardFaceSuited -> it
            is CardJoker -> it
            is CardNumber -> it
        }
    }
    fun toList() = cards.toList()
    fun add(c: CardWithPrice) = cards.add(c)
    fun remove(c: CardWithPrice) = cards.remove(c)
    operator fun contains(c: CardWithPrice): Boolean {
        return c in cards
    }

    fun isCustomDeckValid(): Boolean {
        val numOfDecks = cards.distinctBy { it.getBack() }.size
        val numOfNumbers = cards.count { it is CardNumber }
        return size >= MIN_DECK_SIZE &&
                numOfNumbers >= MIN_NUM_OF_NUMBERS &&
                numOfDecks <= MAX_NUMBER_OF_DECKS
    }

    fun isDeckCourier6(): Boolean {
        return toCardList().all {
            it is CardFaceSuited && it.rank == RankFace.KING ||
                    it is CardNumber && it.rank in listOf(RankNumber.SIX, RankNumber.TEN)
        }
    }
}