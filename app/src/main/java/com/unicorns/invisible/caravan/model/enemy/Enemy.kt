package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
sealed class Enemy {
    abstract fun makeMove(game: Game)
    abstract fun createDeck(): CResources
    open fun getRewardBack(): CardBack? = null
    open fun isAlt() = false

    fun checkMoveOnPossibleDefeat(game: Game, caravanIndex: Int): Boolean {
        val otherCaravansIndices = game.enemyCaravans.indices.filter { it != caravanIndex }
        var score = 0
        fun check(p0: Int, e0: Int) {
            if (p0 in (21..26) && (p0 > e0 || e0 > 26)) {
                score++
            }
        }
        otherCaravansIndices.forEach {
            check(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue())
        }
        return score == 2
    }
    fun checkMoveOnPossibleVictory(game: Game, caravanIndex: Int): Boolean {
        val otherCaravansIndices = game.enemyCaravans.indices.filter { it != caravanIndex }
        var score = 0
        fun check(e0: Int, p0: Int) {
            if (e0 in (21..26) && (e0 > p0 || p0 > 26)) {
                score++
            }
        }
        otherCaravansIndices.forEach {
            check(game.enemyCaravans[it].getValue(), game.playerCaravans[it].getValue())
        }
        return score >= 1
    }
}