package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.attrition
import caravan.composeapp.generated.resources.win_a_game_attrition
import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


@Serializable
class ChallengeAttrition : ChallengeDaily {
    private var completedFlag = false
    override fun processMove(move: Challenge.Move, game: Game) {}

    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            if (game.enemyCResources.hand.isEmpty() && !game.isPlayerTurn) {
                completedFlag = true
            }
        }
    }

    override fun getName(): StringResource {
        return Res.string.attrition
    }

    override suspend fun getDescription(): String {
        return getString(Res.string.win_a_game_attrition)
    }

    override fun getProgress(): String {
        return if (completedFlag) "1 / 1" else "0 / 1"
    }

    override fun isCompleted(): Boolean = completedFlag
}