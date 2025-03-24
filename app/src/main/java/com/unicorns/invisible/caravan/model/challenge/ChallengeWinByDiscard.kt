package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class ChallengeWinByDiscard : ChallengeDaily {
    @Transient
    private var wasLastMoveDiscard = false
    private var completedFlag = false
    override fun processMove(move: Challenge.Move, game: Game) {
        wasLastMoveDiscard = move.moveCode == 1
    }

    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            if (wasLastMoveDiscard) {
                completedFlag = true
            }
        }
    }

    override fun getName(activity: MainActivity): String {
        return activity.getString(R.string.boulder_city)
    }

    override fun getDescription(activity: MainActivity): String {
        return activity.getString(R.string.win_a_game_by_discarding_your_caravan)
    }

    override fun getProgress(): String {
        return if (completedFlag) "1 / 1" else "0 / 1"
    }

    override fun isCompleted(): Boolean = completedFlag
}