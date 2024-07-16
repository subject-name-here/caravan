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

    fun isEmpty() = cardsMutable.size == 0
    fun isFull() = size >= 10
    var canBeDisbanded = true

    fun dropCaravan() {
        cardsMutable.forEach { it.card.caravanAnimationMark = Card.AnimationMark.MOVING_OUT }
        cardsMutable.clear()
    }

    fun removeAllJackedCards() {
        cardsMutable.forEach {
            if (it.hasJacks()) {
                it.card.caravanAnimationMark = Card.AnimationMark.MOVING_OUT
            }
        }
        cardsMutable.removeAll { it.hasJacks() }
    }

    fun jokerRemoveAllRanks(card: Card) {
        cardsMutable.forEach {
            if (it.card.rank == card.rank && !it.hasActiveJoker) {
                it.card.caravanAnimationMark = Card.AnimationMark.MOVING_OUT
            }
        }
        cardsMutable.removeAll { it.card.rank == card.rank && !it.hasActiveJoker }
    }

    fun jokerRemoveAllSuits(card: Card) {
        cardsMutable.forEach {
            if (it.card.suit == card.suit && !it.hasActiveJoker) {
                it.card.caravanAnimationMark = Card.AnimationMark.MOVING_OUT
            }
        }
        cardsMutable.removeAll { it.card.suit == card.suit && !it.hasActiveJoker }
    }

    fun getCazadorPoison(isReversed: Boolean = false) {
        cardsMutable.forEach { it.card.caravanAnimationMark = Card.AnimationMark.MOVING_OUT }
        cardsMutable.removeAll { it.card.rank.ordinal == 0 }
        val copy = cardsMutable.toList()
        cardsMutable.clear()
        if (isReversed) {
            copy.forEach {
                val mods = it.modifiersCopy()
                cardsMutable.add(CardWithModifier(
                    Card(Rank.entries[(it.card.rank.ordinal + 1).coerceAtMost(9)], it.card.suit, CardBack.WILD_WASTELAND, false)
                ).apply {
                    copyModifiersFrom(mods)
                })
            }
        } else {
            copy.forEach {
                val mods = it.modifiersCopy()
                cardsMutable.add(CardWithModifier(
                    Card(Rank.entries[(it.card.rank.ordinal - 1).coerceAtLeast(0)], it.card.suit, CardBack.WILD_WASTELAND, false)
                ).apply {
                    copyModifiersFrom(mods)
                })
            }
        }
    }
    fun getPetePower() {
        cardsMutable.forEach { it.card.caravanAnimationMark = Card.AnimationMark.MOVING_OUT }
        val copy = cardsMutable.toList()
        cardsMutable.clear()
        copy.forEach {
            val mods = it.modifiersCopy()
            cardsMutable.add(CardWithModifier(
                Card(Rank.TEN, it.card.suit, CardBack.WILD_WASTELAND, false)
            ).apply {
                copyModifiersFrom(mods)
            })
        }
    }
    fun getUfo(seed: Int) {
        val rand = Random(seed)
        val toRemove = cardsMutable.filter { rand.nextBoolean() && !it.hasActiveUfo }
        toRemove.forEach { it.card.caravanAnimationMark = Card.AnimationMark.MOVING_OUT }
        cardsMutable.removeAll(toRemove)
    }

    fun getValue(): Int {
        if (cards.any {
            it.modifiersCopy().any {
                mod -> mod.back == CardBack.WILD_WASTELAND &&
                        !mod.isAlt &&
                        mod.getWildWastelandCardType() == Card.WildWastelandCardType.YES_MAN
            }
        }) {
            return 26
        }
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