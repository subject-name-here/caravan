package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.play_188_challenge
import caravan.composeapp.generated.resources.play_188_challenge_descr
import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


@Serializable
class ChallengePlay188 : ChallengeInfinite {
    private var cardsPlayed = 0

    override fun processMove(move: Challenge.Move, game: Game) {
        if (move.moveCode == 3 || move.moveCode == 4) {
            cardsPlayed++
        }
    }

    override fun processGameResult(game: Game) {}

    override fun getName(): StringResource {
        return Res.string.play_188_challenge
    }

    override suspend fun getDescription(): String {
        return getString(Res.string.play_188_challenge_descr)
    }

    override fun getProgress(): String {
        return "$cardsPlayed / 188"
    }

    override fun isCompleted(): Boolean {
        return cardsPlayed >= 188
    }

    override fun restartChallenge() {
        cardsPlayed = 0
    }
}