package com.unicorns.invisible.caravan.model.primitives

import kotlinx.serialization.Serializable
import kotlin.math.pow


@Serializable
class CardWithModifier(val card: Card) {
    private val modifiers: MutableList<Card> = mutableListOf()
    fun addModifier(card: Card) {
        modifiers.add(card)
        card.caravanAnimationMark = Card.AnimationMark.MOVING_IN
        if (card.isNuclear()) {
            hasBomb = true
        } else if (card.isWildWasteland()) {
            when (card.getWildWastelandCardType()) {
                Card.WildWastelandCardType.DIFFICULT_PETE -> hasActivePete = true
                Card.WildWastelandCardType.FEV -> hasActiveFev = true
                Card.WildWastelandCardType.UFO -> hasActiveUfo = true
                else -> {}
            }
        } else if (card.rank == Rank.JOKER) {
            hasActiveJoker = true
        }
    }
    fun copyModifiersFrom(mods: List<Card>) {
        modifiers.addAll(mods)
    }

    fun canAddModifier(card: Card): Boolean {
        val isOrdinaryJack = card.rank == Rank.JACK && card.isOrdinary()
        return card.isFace() && !isProtectedByMuggy && (modifiers.size < 3 || isOrdinaryJack)
    }

    var hasActiveJoker: Boolean = false
        private set
    var hasBomb: Boolean = false
        private set

    var isProtectedByMuggy: Boolean = false

    var hasActiveUfo: Boolean = false
        private set
    var hasActiveFev: Boolean = false
        private set
    var hasActivePete: Boolean = false
        private set

    fun deactivateJoker() {
        hasActiveJoker = false
    }
    fun deactivateBomb() {
        hasBomb = false
    }
    fun deactivateUfo() {
        hasActiveUfo = false
    }
    fun deactivateFev() {
        hasActiveFev = false
    }
    fun deactivatePete() {
        hasActivePete = false
    }

    fun isQueenReversingSequence() = modifiers.count { it.isOrdinary() && it.rank == Rank.QUEEN } % 2 == 1

    fun hasJacks() = modifiers.any { it.isOrdinary() && it.rank == Rank.JACK }

    fun modifiersCopy() = modifiers.toList()

    fun getValue(): Int {
        return if (card.isFace()) {
            0
        } else {
            card.rank.value * (2.0.pow(modifiers.count { it.isOrdinary() && it.rank == Rank.KING })).toInt()
        }
    }

    fun getTopSuit(): Suit {
        return modifiers.findLast { it.isOrdinary() && it.rank == Rank.QUEEN }?.suit ?: card.suit
    }
}