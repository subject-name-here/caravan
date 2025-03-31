package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.boulder_city
import caravan.composeapp.generated.resources.win_a_game_by_discarding_your_caravan
import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


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

    override fun getName(): StringResource {
        return Res.string.boulder_city
    }

    override suspend fun getDescription(): String {
        return getString(Res.string.win_a_game_by_discarding_your_caravan)
    }

    override fun getProgress(): String {
        return if (completedFlag) "1 / 1" else "0 / 1"
    }

    override fun isCompleted(): Boolean = completedFlag
}