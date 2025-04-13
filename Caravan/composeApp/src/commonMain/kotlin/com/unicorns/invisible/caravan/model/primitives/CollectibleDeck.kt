package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CResources.Companion.MAX_NUMBER_OF_DECKS
import com.unicorns.invisible.caravan.model.primitives.CResources.Companion.MIN_DECK_SIZE
import com.unicorns.invisible.caravan.model.primitives.CResources.Companion.MIN_NUM_OF_NUMBERS
import kotlinx.serialization.Serializable


@Serializable
class CollectibleDeck() {
    private val cards = ArrayList<CardWithPrice>()
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
    fun add(c: CardWithPrice): Boolean {
        return if (c !in this) {
            cards.add(c)
        } else {
            false
        }
    }
    fun remove(c: CardWithPrice): Boolean {
        return cards.remove(find(c))
    }
    operator fun contains(c: CardWithPrice): Boolean {
        return find(c) != null
    }
    fun find(c: CardWithPrice) = cards.find {
        when (c) {
            is CardFaceSuited -> it is CardFaceSuited && it.rank == c.rank && it.suit == c.suit && it.cardBack == c.cardBack
            is CardJoker -> it is CardJoker && it.number == c.number && it.cardBack == c.cardBack
            is CardNumber -> it is CardNumber && it.rank == c.rank && it.suit == c.suit && it.cardBack == c.cardBack
        }
    }

    fun isCustomDeckValid(): Boolean {
        val hasNCR = cards.any { it.getBack() == CardBack.NCR }
        val hasLegion = cards.any { it.getBack() == CardBack.LEGION }
        val numOfDecks = cards.distinctBy { it.getBack() }.size
        val numOfNumbers = cards.count { it is CardNumber }
        return size >= MIN_DECK_SIZE &&
                numOfNumbers >= MIN_NUM_OF_NUMBERS &&
                numOfDecks <= MAX_NUMBER_OF_DECKS && !(hasNCR && hasLegion)
    }
}