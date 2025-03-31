package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.marriage
import caravan.composeapp.generated.resources.win_a_game_by_marriage
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.RankFace
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


@Serializable
class ChallengeWinByMarriage : ChallengeDaily {
    @Transient
    private var wasLastMoveMarriage = false
    private var completedFlag = false
    override fun processMove(move: Challenge.Move, game: Game) {
        val isCardKing = move.handCard is CardFaceSuited && move.handCard.rank == RankFace.KING
        if (isCardKing) {
            val kingOwner = (game.enemyCaravans + game.playerCaravans)
                .flatMap { it.cards }
                .find { move.handCard in it.modifiersCopy() }
            if (kingOwner != null) {
                val queens = kingOwner.modifiersCopy().filter {
                    it is CardFaceSuited && it.rank == RankFace.QUEEN && it.suit == move.handCard.suit
                }
                wasLastMoveMarriage = queens.isNotEmpty()
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

    override fun getName(): StringResource {
        return Res.string.marriage
    }

    override suspend fun getDescription(): String {
        return getString(Res.string.win_a_game_by_marriage)
    }

    override fun getProgress(): String {
        return if (completedFlag) "1 / 1" else "0 / 1"
    }

    override fun isCompleted(): Boolean = completedFlag
}