package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import kotlinx.serialization.Serializable


@Serializable
class ChallengeWin5Games : Challenge {
    private val beatenEnemies = mutableListOf<Enemy>()

    override fun processMove(move: Challenge.Move, game: Game) {}

    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            if (game.enemy !in beatenEnemies) {
                beatenEnemies.add(game.enemy)
            }
        }
    }

    override fun getName(activity: MainActivity): String {
        return activity.getString(R.string.simple_challenge)
    }

    override fun getDescription(activity: MainActivity): String {
        return activity.getString(R.string.simple_challenge_descr)
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