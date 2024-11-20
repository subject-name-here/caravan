package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class ChallengeWinByPlayingJoker : Challenge {
    @Transient
    private var wasLastMoveJoker = false
    private var completedFlag = false
    override fun processMove(move: Challenge.Move, game: Game) {
        val isCardJoker = move.handCard?.let { it.rank == Rank.JOKER && it.isOrdinary() } == true
        wasLastMoveJoker = move.moveCode == 4 && isCardJoker
    }

    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            if (wasLastMoveJoker) {
                completedFlag = true
            }
        }
    }

    override fun getName(activity: MainActivity): String {
        return activity.getString(R.string.the_killing_joke)
    }

    override fun getDescription(activity: MainActivity): String {
        return activity.getString(R.string.win_a_game_by_playing_joker)
    }

    override fun getProgress(): String {
        return if (completedFlag) "1 / 1" else "0 / 1"
    }

    override fun isCompleted(): Boolean = completedFlag
}