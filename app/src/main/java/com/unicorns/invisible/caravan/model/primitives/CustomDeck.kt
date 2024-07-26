package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


@Serializable
class CustomDeck() {
    private val cards = ArrayList<Card>()

    constructor(back: CardBack, isAlt: Boolean) : this() {
        Rank.entries.forEach { rank ->
            if (rank == Rank.JOKER) {
                cards.add(Card(rank, Suit.HEARTS, back, isAlt))
                cards.add(Card(rank, Suit.CLUBS, back, isAlt))
            } else {
                Suit.entries.forEach { suit ->
                    cards.add(Card(rank, suit, back, isAlt))
                }
            }
        }
    }

    val size: Int
        get() = cards.size

    fun takeRandom(n: Int) = cards.shuffled().take(n)

    fun add(element: Card) = cards.add(element)
    fun addOnTop(element: Card) = cards.add(0, element)
    operator fun get(index: Int) = cards[index]

    fun count(predicate: (Card) -> Boolean) = cards.count(predicate)
    fun firstOrNull() = cards.firstOrNull()
    fun removeFirst() = cards.removeFirst()

    private fun getEqPredicate(it: Card): (Card) -> Boolean = { c ->
        it.suit == c.suit && it.back == c.back && it.rank == c.rank && it.isAlt == c.isAlt
    }

    fun removeAll(elements: Collection<Card>) = elements.forEach { cardToRemove ->
        cards.removeAll(getEqPredicate(cardToRemove))
    }

    operator fun contains(card: Card): Boolean {
        return cards.any(getEqPredicate(card))
    }

    fun toList() = cards.toList()

    fun shuffle() = cards.shuffle()
}