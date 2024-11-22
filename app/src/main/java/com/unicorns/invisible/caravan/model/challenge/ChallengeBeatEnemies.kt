package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import kotlinx.serialization.Serializable


@Serializable
class ChallengeBeatEnemies(private val code: Int) : Challenge {
    // TODO: better pairs!
    private val enemyList: List<Enemy> = when (code) {
        else -> emptyList()
    }
    private val isBeaten = Array(enemyList.size) { false }

    override fun processMove(
        move: Challenge.Move,
        game: Game
    ) {}

    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            val index = enemyList.indexOfFirst { it.javaClass == game.enemy.javaClass }
            if (index != -1) {
                isBeaten[index] = true
            }
        }
    }

    override fun getName(activity: MainActivity): String {
        return when (code) {
            else -> ""
        }
    }

    override fun getDescription(activity: MainActivity): String {
        return when (code) {
            else -> ""
        }
    }

    override fun getProgress(): String {
        return isBeaten.joinToString { if (it) "+" else "-" }
    }

    override fun isCompleted(): Boolean {
        return isBeaten.all { it }
    }
}