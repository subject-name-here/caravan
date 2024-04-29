package com.unicorns.invisible.caravan.model.primitives

import kotlinx.serialization.Serializable


@Serializable
class Caravan {
    private val cardsMutable = mutableListOf<CardWithModifier>()
    val cards
        get() = cardsMutable.toList()
    val size: Int
        get() = cards.size
    fun isFull() = size >= 10

    fun dropCaravan() {
        cardsMutable.clear()
    }

    fun removeAllJackedCards() {
        cardsMutable.removeAll { it.hasJacks() }
    }
    fun removeAllRanks(card: Card) {
        cardsMutable.removeAll { it.card.rank == card.rank }
    }
    fun removeAllSuits(card: Card) {
        cardsMutable.removeAll { it.card.suit == card.suit }
    }

    fun getValue(): Int {
        return cards.sumOf { it.getValue() }
    }

    fun canPutCardOnTop(card: Card): Boolean {
        if (isFull() || card.isFace()) return false
        if (size == 0) {
            return true
        }
        val last = cards.last()
        if (last.card.rank == card.rank) {
            return false
        }

        if (last.getTopSuit() == card.suit || cards.size == 1) {
            return true
        }

        val preLast = cards[cards.lastIndex - 1]
        when {
            last.card.rank == preLast.card.rank -> {
                return true
            }
            last.card.rank > preLast.card.rank -> {
                if (
                    last.isQueenReversingSequence() && card.rank < last.card.rank ||
                    !last.isQueenReversingSequence() && card.rank > last.card.rank
                ) {
                    return true
                }
            }
            last.card.rank < preLast.card.rank -> {
                if (
                    last.isQueenReversingSequence() && card.rank > last.card.rank ||
                    !last.isQueenReversingSequence() && card.rank < last.card.rank
                ) {
                    return true
                }
            }
        }

        return false
    }
    fun putCardOnTop(card: Card) {
        cardsMutable.add(CardWithModifier(card))
    }
}