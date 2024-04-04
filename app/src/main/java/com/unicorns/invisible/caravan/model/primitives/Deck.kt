package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack


class Deck(val back: CardBack) {
    private val cards: MutableList<Card> = mutableListOf()
    val hand: MutableList<Card> = mutableListOf()

    init {
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

    fun shuffle() {
        cards.shuffle()
    }

    fun initHand() {
        repeat(8) {
            addToHand()
        }
    }

    fun addToHand() {
        hand.add(cards.removeAt(0))
    }

    val deckSize: Int = cards.size
}