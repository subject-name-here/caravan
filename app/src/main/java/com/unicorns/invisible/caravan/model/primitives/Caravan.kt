package com.unicorns.invisible.caravan.model.primitives

import kotlinx.serialization.Serializable


@Serializable
class Caravan {
    val cards = mutableListOf<CardWithModifier>()
    val size: Int
        get() = cards.size
    fun isFull() = size >= 10

    fun dropCaravan() {
        cards.clear()
    }

    fun getValue(): Int {
        return cards.sumOf { it.getValue() }
    }

    fun putCardOnTop(card: Card): Boolean {
        if (isFull() || card.isFace()) return false
        if (size == 0) {
            cards.add(CardWithModifier(card))
            return true
        }
        val last = cards.last()
        if (last.card.rank == card.rank) {
            return false
        }

        if (last.getTopSuit() == card.suit || cards.size == 1) {
            cards.add(CardWithModifier(card))
            return true
        }

        val preLast = cards[cards.lastIndex - 1]
        when {
            last.card.rank == preLast.card.rank -> {
                cards.add(CardWithModifier(card))
                return true
            }
            last.card.rank > preLast.card.rank -> {
                if (
                    last.isQueenReversingSequence() && card.rank < last.card.rank ||
                    !last.isQueenReversingSequence() && card.rank > last.card.rank
                ) {
                    cards.add(CardWithModifier(card))
                    return true
                }
            }
            last.card.rank < preLast.card.rank -> {
                if (
                    last.isQueenReversingSequence() && card.rank > last.card.rank ||
                    !last.isQueenReversingSequence() && card.rank < last.card.rank
                ) {
                    cards.add(CardWithModifier(card))
                    return true
                }
            }
        }

        return false
    }
}