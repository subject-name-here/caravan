package com.unicorns.invisible.caravan.model.primitives

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.unicorns.invisible.caravan.AnimationSpeed
import kotlinx.coroutines.delay
import kotlin.random.Random


class Caravan {
    var recomposeResources by mutableIntStateOf(0)

    private val cardsMutable = mutableListOf<CardWithModifier>()
    val cards
        get() = cardsMutable.toList()
    val size: Int
        get() = cardsMutable.size

    fun isEmpty() = size == 0
    fun isFull() = size >= 10

    private suspend fun removeAll(speed: AnimationSpeed, predicate: (CardWithModifier) -> Boolean) {
        val toRemove = cardsMutable.filter(predicate)
        toRemove.forEach { it.card.caravanAnimationMark = Card.AnimationMark.MOVING_OUT }
        if (toRemove.isNotEmpty()) {
            delay(speed.delay)
        }
        cardsMutable.removeAll(toRemove)
        recomposeResources++
    }

    suspend fun dropCaravan(speed: AnimationSpeed) {
        removeAll(speed) { true }
    }
    suspend fun removeAllJackedCards(speed: AnimationSpeed) {
        removeAll(speed) { it.hasJacks() }
    }
    suspend fun jokerRemoveAllRanks(card: CardBase, speed: AnimationSpeed) {
        removeAll(speed) { it.card.rank == card.rank && !it.hasActiveJoker }
    }
    suspend fun jokerRemoveAllSuits(card: CardBase, speed: AnimationSpeed) {
        removeAll(speed) { it.card.suit == card.suit && !it.hasActiveJoker }
    }

    private fun replaceCards(getCard: (CardWithModifier) -> Pair<RankNumber, Suit>) {
        cardsMutable.forEach { it.card.caravanAnimationMark = Card.AnimationMark.MOVED_OUT }
        val copy = cardsMutable.toList()
        cardsMutable.clear()
        copy.forEach { cardCopy ->
            val mods = cardCopy.modifiersCopy()
            val card = getCard(cardCopy)
            cardsMutable.add(
                CardWithModifier(CardNumberWW(card.first, card.second)).apply {
                    copyModifiersFrom(mods)
                    copyWild(cardCopy)
                }
            )
        }
        recomposeResources++
    }
    suspend fun getCazadorPoison(isReversed: Boolean, speed: AnimationSpeed) {
        if (!isReversed) {
            removeAll(speed) { it.card.rank.value == 1 }
        }

        val changeOrdinal: (Int) -> Int = if (isReversed) Int::inc else Int::dec
        replaceCards {
            val newOrdinal = (changeOrdinal(it.card.rank.ordinal)).coerceIn(0, 9)
            RankNumber.entries[newOrdinal] to it.card.suit
        }
    }
    fun getPetePower() {
        replaceCards { RankNumber.TEN to it.card.suit }
    }
    suspend fun getUfo(seed: Int, speed: AnimationSpeed) {
        val rand = Random(seed)
        removeAll(speed) { rand.nextBoolean() && !it.hasActiveUfo }
    }

    fun getValue(): Int {
        return if (cards.any { it.hasActiveYesMan }) {
            26
        } else {
            cards.sumOf { it.getValue() }
        }
    }

    fun canPutCardOnTop(card: CardBase): Boolean {
        if (isFull()) return false
        if (isEmpty()) return true

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

    suspend fun putCardOnTop(card: CardBase, speed: AnimationSpeed) {
        cardsMutable.add(CardWithModifier(card))
        recomposeResources++
        delay(speed.delay)
    }
}