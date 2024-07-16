package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable
import kotlin.math.pow


@Serializable
class CardWithModifier(val card: Card) {
    private val modifiers: MutableList<Card> = mutableListOf()
    fun addModifier(card: Card) {
        modifiers.add(card)
        card.caravanAnimationMark = Card.AnimationMark.MOVING_IN
        if (card.isSpecial()) {
            if (card.isAlt && (card.back == CardBack.WILD_WASTELAND || card.back == CardBack.UNPLAYABLE)) {
                hasBomb = true
            }
            if (!card.isAlt && card.isSpecial()) {
                when (card.getWildWastelandCardType()) {
                    Card.WildWastelandCardType.DIFFICULT_PETE -> hasActivePete = true
                    Card.WildWastelandCardType.FEV -> hasActiveFev = true
                    Card.WildWastelandCardType.UFO -> hasActiveUfo = true
                    else -> {}
                }
            }
        } else {
            if (card.rank == Rank.JOKER) {
                hasActiveJoker = true
            }
        }
    }
    fun copyModifiersFrom(mods: List<Card>) {
        modifiers.addAll(mods)
    }

    fun canAddModifier(card: Card): Boolean {
        return card.isFace() && !isProtectedByMuggy && (modifiers.size < 3 || !card.isSpecial() && card.rank == Rank.JACK)
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

    fun isQueenReversingSequence() = modifiers.count { !it.isSpecial() && it.rank == Rank.QUEEN } % 2 == 1

    fun hasJacks() = modifiers.any { !it.isSpecial() && it.rank == Rank.JACK }

    fun modifiersCopy() = modifiers.toList()

    fun getValue(): Int {
        return if (card.isFace()) {
            0
        } else {
            card.rank.value * (2.0.pow(modifiers.count { !it.isSpecial() && it.rank == Rank.KING })).toInt()
        }
    }

    fun getTopSuit(): Suit {
        return modifiers.findLast { !it.isSpecial() && it.rank == Rank.QUEEN }?.suit ?: card.suit
    }
}