package com.unicorns.invisible.caravan.model.primitives

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.unicorns.invisible.caravan.AnimationSpeed
import kotlinx.coroutines.delay
import kotlin.math.pow


class CardWithModifier(val card: CardBase) {
    var recomposeResources by mutableIntStateOf(0)
    private val modifiers: MutableList<CardModifier> = mutableListOf()
    suspend fun addModifier(card: CardModifier, speed: AnimationSpeed) {
        modifiers.add(card)
        recomposeResources++
        delay(speed.delay)

        if (card is CardNuclear) {
            hasBomb = true
        } else if (card is CardWildWasteland) {
            when (card.type) {
                WWType.DIFFICULT_PETE -> hasActivePete = true
                WWType.FEV -> hasActiveFev = true
                WWType.UFO -> hasActiveUfo = true
                WWType.CAZADOR -> hasActiveCazador = true
                WWType.MUGGY -> hasActiveMuggy = true
                WWType.YES_MAN -> hasActiveYesMan = true
            }
        } else if (card is CardJoker) {
            hasActiveJoker = true
        }
    }
    fun copyModifiersFrom(mods: List<CardModifier>) {
        modifiers.addAll(mods)
    }

    fun canAddModifier(card: CardModifier): Boolean {
        val isOrdinaryJack = card is CardFace && card.rank == RankFace.JACK
        return !isProtectedByMuggy && (modifiers.size < 3 || isOrdinaryJack)
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
        .count { it is CardFace && it.rank == RankFace.QUEEN } % 2 == 1

    fun hasJacks() = modifiers.any { it is CardFace && it.rank == RankFace.JACK }

    fun modifiersCopy(): List<CardModifier> {
        return modifiers.toList()
    }

    fun getValue(): Int {
        return card.rank.value * (2.0.pow(modifiers.count { it is CardFace && it.rank == RankFace.KING })).toInt()
    }

    fun getTopSuit(): Suit {
        return modifiers
            .filterIsInstance<CardFaceSuited>()
            .findLast { it.rank == RankFace.QUEEN }?.suit ?: card.suit
    }

    fun copyWild(src: CardWithModifier) {
        hasActiveCazador = src.hasActiveCazador
        hasActiveMuggy = src.hasActiveMuggy
        hasActiveYesMan = src.hasActiveYesMan
    }
}