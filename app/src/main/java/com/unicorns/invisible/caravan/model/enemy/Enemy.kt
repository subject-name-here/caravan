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

    // Returns true if the selling of enemy caravan with caravanIndex will end game in favour of player.
    // Set caravanIndex as -1 to get true if 2 out of 3 caravans are sold.
    fun checkMoveOnDefeat(game: Game, caravanIndex: Int): Boolean {
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

    fun checkMoveOnProbableDefeat(game: Game, caravanIndex: Int): Boolean {
        val otherCaravansIndices = game.enemyCaravans.indices.filter { it != caravanIndex }
        var score = 0
        fun check(p0: Int, e0: Int) {
            if (p0 >= 11 && e0 != 26) {
                score++
            }
        }
        otherCaravansIndices.forEach {
            check(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue())
        }
        return score == 1
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
        if (score >= 2) {
            return true
        } else if (score == 1) {
            otherCaravansIndices.forEach {
                check(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue())
            }
            if (score == 2) {
                return true
            }
        }
        return false
    }
}