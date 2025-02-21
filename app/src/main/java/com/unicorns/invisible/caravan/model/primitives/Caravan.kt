package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class Caravan {
    private val cardsMutable = mutableListOf<CardWithModifier>()
    val cards
        get() = cardsMutable.toList()
    val size: Int
        get() = cardsMutable.size

    fun isEmpty() = size == 0
    fun isFull() = size >= 10

    private fun removeAll(predicate: (CardWithModifier) -> Boolean) {
        val toRemove = cardsMutable.filter(predicate)
        toRemove.forEach {
            it.card.caravanAnimationMark = Card.AnimationMark.MOVING_OUT
        }
        cardsMutable.removeAll(toRemove)
    }

    fun dropCaravan() {
        removeAll { true }
    }
    fun removeAllJackedCards() {
        removeAll { it.hasJacks() }
    }
    fun jokerRemoveAllRanks(card: Card) {
        removeAll { it.card.rank == card.rank && !it.hasActiveJoker }
    }
    fun jokerRemoveAllSuits(card: Card) {
        removeAll { it.card.suit == card.suit && !it.hasActiveJoker }
    }

    private fun replaceCards(getCard: (CardWithModifier) -> Pair<Rank, Suit>) {
        cardsMutable.forEach { it.card.caravanAnimationMark = Card.AnimationMark.MOVED_OUT }
        val copy = cardsMutable.toList()
        cardsMutable.clear()
        copy.forEach { cardCopy ->
            val mods = cardCopy.modifiersCopy()
            val card = getCard(cardCopy)
            cardsMutable.add(
                CardWithModifier(Card(card.first, card.second, CardBack.WILD_WASTELAND, false)).apply {
                    copyModifiersFrom(mods); copyWild(cardCopy)
                }
            )
        }
    }
    fun getCazadorPoison(isReversed: Boolean) {
        if (!isReversed) {
            removeAll { it.card.rank.value == 1 }
        }

        val changeOrdinal: (Int) -> Int = if (isReversed) Int::inc else Int::dec
        replaceCards {
            val newOrdinal = (changeOrdinal(it.card.rank.ordinal)).coerceIn(0, 9)
            Rank.entries[newOrdinal] to it.card.suit
        }
    }
    fun getPetePower() {
        replaceCards { Rank.TEN to it.card.suit }
    }
    fun getUfo(seed: Int) {
        val rand = Random(seed)
        removeAll { rand.nextBoolean() && !it.hasActiveUfo }
    }

    fun getValue(): Int {
        return if (cards.any { it.hasActiveYesMan }) {
            26
        } else {
            cards.sumOf { it.getValue() }
        }
    }

    fun canPutCardOnTop(card: Card): Boolean {
        if (isFull() || card.isModifier()) return false
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
        card.caravanAnimationMark = Card.AnimationMark.MOVING_IN
        cardsMutable.add(CardWithModifier(card))
    }
}