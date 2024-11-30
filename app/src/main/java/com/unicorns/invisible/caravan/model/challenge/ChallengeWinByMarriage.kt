package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class ChallengeWinByMarriage : Challenge {
    @Transient
    private var wasLastMoveMarriage = false
    private var completedFlag = false
    override fun processMove(move: Challenge.Move, game: Game) {
        val isCardKing = move.handCard?.let { it.rank == Rank.KING && it.isOrdinary() } == true
        if (isCardKing) {
            val kingOwner = (game.enemyCaravans + game.playerCaravans)
                .flatMap { it.cards }
                .find { move.handCard in it.modifiersCopy() } ?: return
            val queens = kingOwner.modifiersCopy()
                .filter { it.isOrdinary() && it.rank == Rank.QUEEN && it.suit == move.handCard.suit }
            if (queens.isNotEmpty()) {
                wasLastMoveMarriage = true
                return
            }
        }
        wasLastMoveMarriage = false
    }

    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            if (wasLastMoveMarriage) {
                completedFlag = true
            }
        }
    }

    override fun getName(activity: MainActivity): String {
        return activity.getString(R.string.marriage)
    }

    override fun getDescription(activity: MainActivity): String {
        return activity.getString(R.string.win_a_game_by_marriage)
    }

    override fun getProgress(): String {
        return if (completedFlag) "1 / 1" else "0 / 1"
    }

    override fun isCompleted(): Boolean = completedFlag
}