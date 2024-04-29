package com.unicorns.invisible.caravan.model.primitives

import kotlinx.serialization.Serializable


@Serializable
class Caravan {
    private val cardsMutable = mutableListOf<CardWithModifier>()
    val cards
        get() = cardsMutable.toList()
    val size: Int
        get() = cardsMutable.size
    fun isEmpty() = cardsMutable.size == 0
    fun isFull() = size >= 10

    fun dropCaravan() {
        cardsMutable.clear()
    }

    fun removeAllJackedCards() {
        cardsMutable.removeAll { it.hasJacks() }
    }
    fun jokerRemoveAllRanks(card: Card) {
        cardsMutable.removeAll { it.card.rank == card.rank && !it.hasActiveJoker }
    }
    fun jokerRemoveAllSuits(card: Card) {
        cardsMutable.removeAll { it.card.suit == card.suit && !it.hasActiveJoker }
    }

    fun getValue(): Int {
        return cards.sumOf { it.getValue() }
    }

    fun canPutCardOnTop(card: Card): Boolean {
        if (isFull() || card.isFace()) return false
        if (isEmpty()) {
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
                // TODO: are we sure???
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