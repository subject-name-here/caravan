package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable


@Serializable
class ChallengeAll26(private val weight: Int) : ChallengeDaily {
    override fun processMove(
        move: Challenge.Move,
        game: Game
    ) {}

    private var isCompleted = false
    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            if (game.playerCaravans.all { it.getValue() == weight }) {
                isCompleted = true
            }
        }
    }

    override fun getName(activity: MainActivity) = when (weight) {
        21 -> activity.getString(R.string.all_yours_21)
        26 -> activity.getString(R.string.all_yours_26)
        else -> "#"
    }
    override fun getDescription(activity: MainActivity) = when (weight) {
        21 -> activity.getString(R.string.win_a_game_all_yours_21)
        26 -> activity.getString(R.string.win_a_game_all_yours_26)
        else -> "#"
    }

    override fun getProgress(): String {
        return if (isCompleted) "1 / 1" else "0 / 1"
    }

    override fun isCompleted(): Boolean = isCompleted
}