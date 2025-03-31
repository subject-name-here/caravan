package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.all_yours_21
import caravan.composeapp.generated.resources.all_yours_26
import caravan.composeapp.generated.resources.empty_string
import caravan.composeapp.generated.resources.win_a_game_all_yours_21
import caravan.composeapp.generated.resources.win_a_game_all_yours_26
import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString


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

    override fun getName() = when (weight) {
        21 -> Res.string.all_yours_21
        26 -> Res.string.all_yours_26
        else -> Res.string.empty_string
    }
    override suspend fun getDescription() = when (weight) {
        21 -> getString(Res.string.win_a_game_all_yours_21)
        26 -> getString(Res.string.win_a_game_all_yours_26)
        else -> ""
    }

    override fun getProgress(): String {
        return if (isCompleted) "1 / 1" else "0 / 1"
    }

    override fun isCompleted(): Boolean = isCompleted
}