package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable


@Serializable
class ChallengeWinWithEmptyDeck : ChallengeDaily {
    private var completedFlag = false
    override fun processMove(move: Challenge.Move, game: Game) {}

    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            if (game.playerCResources.deckSize == 0) {
                completedFlag = true
            }
        }
    }

    override fun getName(activity: MainActivity): String {
        return activity.getString(R.string.empty_deck)
    }

    override fun getDescription(activity: MainActivity): String {
        return activity.getString(R.string.win_a_game_empty_deck)
    }

    override fun getProgress(): String {
        return if (completedFlag) "1 / 1" else "0 / 1"
    }

    override fun isCompleted(): Boolean = completedFlag
}