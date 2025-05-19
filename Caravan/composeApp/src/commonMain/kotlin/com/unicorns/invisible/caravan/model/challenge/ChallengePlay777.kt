package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.play_188_challenge
import caravan.composeapp.generated.resources.play_188_challenge_descr
import caravan.composeapp.generated.resources.play_777_challenge
import caravan.composeapp.generated.resources.play_777_challenge_descr
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

@Serializable
class ChallengePlay777 : ChallengeInfinite {
    private var pointsPlayed = 0

    override fun processMove(move: Challenge.Move, game: Game) {
        if (move.moveCode == 3 || move.moveCode == 4) {
            val card = move.handCard ?: return
            if (card is CardBase) {
                pointsPlayed += card.rank.value
            }
        }
    }

    override fun processGameResult(game: Game) {}

    override fun getName(): StringResource {
        return Res.string.play_777_challenge
    }

    override suspend fun getDescription(): String {
        return getString(Res.string.play_777_challenge_descr)
    }

    override fun getProgress(): String {
        return "$pointsPlayed / 777"
    }

    override fun isCompleted(): Boolean {
        return pointsPlayed >= 777
    }

    override fun restartChallenge() {
        pointsPlayed = 0
    }
}