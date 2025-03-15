package com.unicorns.invisible.caravan.model.primitives

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.Serializable
import kotlin.math.pow


@Serializable
class CardWithModifier(val card: Card) {
    var recomposeResources by mutableIntStateOf(0)
    private val modifiers: MutableList<Card> = mutableListOf()
    fun addModifier(card: Card) {
        modifiers.add(card)
        recomposeResources++
        if (card.isNuclear()) {
            hasBomb = true
        } else when (card.getWildWastelandType()) {
            Card.WildWastelandCardType.DIFFICULT_PETE -> hasActivePete = true
            Card.WildWastelandCardType.FEV -> hasActiveFev = true
            Card.WildWastelandCardType.UFO -> hasActiveUfo = true
            Card.WildWastelandCardType.CAZADOR -> hasActiveCazador = true
            Card.WildWastelandCardType.MUGGY -> hasActiveMuggy = true
            Card.WildWastelandCardType.YES_MAN -> hasActiveYesMan = true
            else -> {
                if (card.rank == Rank.JOKER) {
                    hasActiveJoker = true
                }
            }
        }
    }
    fun copyModifiersFrom(mods: List<Card>) {
        modifiers.addAll(mods)
    }

    fun canAddModifier(card: Card): Boolean {
        val isOrdinaryJack = card.rank == Rank.JACK && card.isOrdinary()
        return card.isModifier() && !isProtectedByMuggy && (modifiers.size < 3 || isOrdinaryJack)
    }

    var hasActiveJoker: Boolean = false
        private set
    var hasBomb: Boolean = false
        private set

    var hasActiveUfo: Boolean = false
        private set
    var hasActiveFev: Boolean = false
        private set
    var hasActivePete: Boolean = false
        private set
    var hasActiveCazador: Boolean = false
        private set
    var hasActiveMuggy: Boolean = false
        private set
    var hasActiveYesMan: Boolean = false
        private set

    var isProtectedByMuggy: Boolean = false

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

    fun isQueenReversingSequence() = modifiers
        .count { it.isOrdinary() && it.rank == Rank.QUEEN } % 2 == 1

    fun hasJacks() = modifiers.any { it.isOrdinary() && it.rank == Rank.JACK }

    fun modifiersCopy(): List<Card> {
        return modifiers.toList()
    }

    fun getValue(): Int {
        return if (card.isModifier()) {
            0
        } else {
            card.rank.value * (2.0.pow(modifiers.count { it.isOrdinary() && it.rank == Rank.KING })).toInt()
        }
    }

    fun getTopSuit(): Suit {
        return modifiers.findLast { it.isOrdinary() && it.rank == Rank.QUEEN }?.suit ?: card.suit
    }

    fun copyWild(src: CardWithModifier) {
        hasActiveCazador = src.hasActiveCazador
        hasActiveMuggy = src.hasActiveMuggy
        hasActiveYesMan = src.hasActiveYesMan
    }
}