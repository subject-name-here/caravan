package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


@Serializable
class CustomDeck() {
    private val cards = ArrayList<Card>()

    constructor(back: CardBack) : this() {
        Rank.entries.forEach { rank ->
            if (rank == Rank.JOKER) {
                cards.add(Card(rank, Suit.HEARTS, back))
                cards.add(Card(rank, Suit.CLUBS, back))
            } else {
                Suit.entries.forEach { suit ->
                    cards.add(Card(rank, suit, back))
                }
            }
        }
    }

    val size: Int
        get() = cards.size

    fun takeRandom(n: Int) = cards.shuffled().take(n)

    fun add(element: Card) = if (!contains(element)) cards.add(element) else false
    operator fun get(index: Int) = cards[index]

    fun count(predicate: (Card) -> Boolean) = cards.count(predicate)
    fun firstOrNull() = cards.firstOrNull()
    fun removeFirst() = cards.removeFirst()
    fun remove(card: Card) = cards.removeAll { it.suit == card.suit && it.back == card.back && it.rank == card.rank }
    fun removeAll(elements: Collection<Card>) = elements.forEach { remove(it) }

    operator fun contains(card: Card): Boolean {
        return cards.any { it.suit == card.suit && it.back == card.back && it.rank == card.rank }
    }

    fun toList() = cards.toList()

    fun shuffle() = cards.shuffle()
}