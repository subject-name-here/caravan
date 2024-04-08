package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


@Serializable
class Deck(val back: CardBack) {
    @OptIn(ExperimentalSerializationApi::class)
    @EncodeDefault
    private val cards: MutableList<Card> = mutableListOf<Card>().apply {
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
    val hand: MutableList<Card> = mutableListOf()

    fun shuffle() {
        cards.shuffle()
    }

    fun getInitHand() = cards.take(8)
    fun initHand() {
        repeat(8) {
            addToHand()
        }
    }

    fun addToHand() {
        if (cards.size > 0) {
            hand.add(cards.removeAt(0))
        }
    }

    val deckSize: Int
        get() = cards.size
}