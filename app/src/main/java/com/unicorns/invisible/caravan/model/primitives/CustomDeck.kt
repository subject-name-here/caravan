package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


@Serializable
class CustomDeck() : MutableSet<Card> {
    private val cards = HashSet<Card>()

    constructor(back: CardBack) : this() {
        Rank.entries.forEach { rank ->
            if (rank == Rank.JOKER) {
                cards.add(Card(rank, Suit.HEARTS, back))
                cards.add(Card(rank, Suit.SPADES, back))
            } else {
                Suit.entries.forEach { suit ->
                    cards.add(Card(rank, suit, back))
                }
            }
        }
    }

    override fun add(element: Card): Boolean = cards.add(element)

    override fun addAll(elements: Collection<Card>): Boolean = cards.addAll(elements)

    override val size: Int
        get() = cards.size

    override fun clear() = cards.clear()

    override fun isEmpty(): Boolean = cards.isEmpty()

    override fun containsAll(elements: Collection<Card>): Boolean = cards.containsAll(elements)

    override fun contains(element: Card): Boolean = cards.contains(element)

    override fun iterator(): MutableIterator<Card> = cards.iterator()

    override fun retainAll(elements: Collection<Card>): Boolean = cards.retainAll(elements.toSet())

    override fun removeAll(elements: Collection<Card>): Boolean = cards.removeAll(elements.toSet())

    override fun remove(element: Card): Boolean = cards.remove(element)
}