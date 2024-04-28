package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


@Serializable
class CustomDeck() : HashSet<Card>() {
    constructor(back: CardBack) : this() {
        Rank.entries.forEach { rank ->
            if (rank == Rank.JOKER) {
                add(Card(rank, Suit.HEARTS, back))
                add(Card(rank, Suit.SPADES, back))
            } else {
                Suit.entries.forEach { suit ->
                    add(Card(rank, suit, back))
                }
            }
        }
    }
}