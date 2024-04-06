package com.unicorns.invisible.caravan.model.primitives

import kotlinx.serialization.Serializable


@Serializable
class Caravan {
    val cards = mutableListOf<CardWithModifier>()

    fun dropCaravan() {
        cards.clear()
    }

    fun getValue(): Int {
        return cards.sumOf { it.getValue() }
    }

    fun isFull() = cards.size >= 10

    fun putCardOnTop(card: Card): Boolean {
        if (isFull() || card.rank.value > 10) return false
        if (cards.isEmpty()) {
            cards.add(CardWithModifier(card))
            return true
        }
        val last = cards.last()
        if (last.card.rank == card.rank) {
            return false
        }

        if (last.getTopSuit() == card.suit) {
            cards.add(CardWithModifier(card))
            return true
        }

        if (cards.size == 1) {
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
                if (card.rank > last.card.rank || last.topQueen != null) {
                    cards.add(CardWithModifier(card))
                    return true
                }
            }
            last.card.rank < preLast.card.rank -> {
                if (card.rank < last.card.rank || last.topQueen != null) {
                    cards.add(CardWithModifier(card))
                    return true
                }
            }
        }

        return false
    }
}