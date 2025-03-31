package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.the_killing_joke
import caravan.composeapp.generated.resources.win_a_game_by_playing_joker
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


@Serializable
class ChallengeWinByPlayingJoker : ChallengeDaily {
    @Transient
    private var wasLastMoveJoker = false
    private var completedFlag = false
    override fun processMove(move: Challenge.Move, game: Game) {
        val isCardJoker = move.handCard?.let { it is CardJoker } == true
        wasLastMoveJoker = move.moveCode == 4 && isCardJoker
    }

    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            if (wasLastMoveJoker) {
                completedFlag = true
            }
        }
    }

    override fun getName(): StringResource {
        return Res.string.the_killing_joke
    }

    override suspend fun getDescription(): String {
        return getString(Res.string.win_a_game_by_playing_joker)
    }

    override fun getProgress(): String {
        return if (completedFlag) "1 / 1" else "0 / 1"
    }

    override fun isCompleted(): Boolean = completedFlag
}