package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.simple_challenge
import caravan.composeapp.generated.resources.simple_challenge_descr
import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


@Serializable
class ChallengeWin6Games : ChallengeInfinite {
    private val beatenEnemies = mutableListOf<String>()

    override fun processMove(move: Challenge.Move, game: Game) {}

    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            val name = game.enemy::class.simpleName ?: ""
            if (name !in beatenEnemies) {
                beatenEnemies.add(name)
            }
        }
    }

    override fun getName(): StringResource {
        return Res.string.simple_challenge
    }

    override suspend fun getDescription(): String {
        return getString(Res.string.simple_challenge_descr)
    }

    override fun getProgress(): String {
        return "${beatenEnemies.size} / 6"
    }

    override fun isCompleted(): Boolean {
        return beatenEnemies.size >= 6
    }

    override fun restartChallenge() {
        beatenEnemies.clear()
    }
}