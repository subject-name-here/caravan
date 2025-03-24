package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable


@Serializable
class ChallengeWin5Games : ChallengeInfinite {
    private val beatenEnemies = mutableListOf<String>()

    override fun processMove(move: Challenge.Move, game: Game) {}

    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            val name = game.enemy.javaClass.name
            if (name !in beatenEnemies) {
                beatenEnemies.add(name)
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