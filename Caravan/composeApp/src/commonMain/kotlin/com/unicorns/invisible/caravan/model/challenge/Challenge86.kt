package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.play_86_challenge
import caravan.composeapp.generated.resources.play_86_challenge_descr
import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


@Serializable
class Challenge86 : ChallengeInfinite {
    private var cardsDropped = 0

    override fun processMove(move: Challenge.Move, game: Game) {
        if (move.moveCode == 2) {
            cardsDropped++
        }
    }

    override fun processGameResult(game: Game) {}

    override fun getName(): StringResource {
        return Res.string.play_86_challenge
    }

    override suspend fun getDescription(): String {
        return getString(Res.string.play_86_challenge_descr)
    }

    override fun getProgress(): String {
        return "$cardsDropped / 86"
    }

    override fun isCompleted(): Boolean {
        return cardsDropped >= 86
    }

    override fun restartChallenge() {
        cardsDropped = 0
    }
}