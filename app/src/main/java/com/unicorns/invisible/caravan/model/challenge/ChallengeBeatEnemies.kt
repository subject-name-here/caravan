package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyBenny
import com.unicorns.invisible.caravan.model.enemy.EnemyBestest
import com.unicorns.invisible.caravan.model.enemy.EnemyBetter
import com.unicorns.invisible.caravan.model.enemy.EnemyEasy
import com.unicorns.invisible.caravan.model.enemy.EnemyHard
import com.unicorns.invisible.caravan.model.enemy.EnemyHouse
import com.unicorns.invisible.caravan.model.enemy.EnemyMedium
import com.unicorns.invisible.caravan.model.enemy.EnemyNash
import com.unicorns.invisible.caravan.model.enemy.EnemyNoBark
import com.unicorns.invisible.caravan.model.enemy.EnemySecuritron38
import com.unicorns.invisible.caravan.model.enemy.EnemySix
import com.unicorns.invisible.caravan.model.enemy.EnemySwank
import kotlinx.serialization.Serializable


@Serializable
class ChallengeBeatEnemies(private val code: Int) : Challenge {
    private val enemies: List<Enemy>
        get() = when (code) {
            1 -> {
                listOf(EnemyBestest)
            }
            2 -> {
                listOf(EnemySix)
            }
            3 -> {
                listOf(EnemyHouse)
            }
            4 -> {
                listOf(EnemyBetter, EnemySecuritron38)
            }
            5 -> {
                listOf(EnemyNoBark, EnemyMedium)
            }
            6 -> {
                listOf(EnemyNash, EnemyEasy)
            }
            7 -> {
                listOf(EnemyHard, EnemyBenny, EnemySwank)
            }
            else -> listOf()
        }

    private val isBeaten = Array(enemies.size) { false }
    override fun processMove(move: Challenge.Move, game: Game) {}

    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            val index = enemies.indexOfFirst { it.javaClass == game.enemy.javaClass }
            if (index != -1) {
                isBeaten[index] = true
            }
        }
    }

    override fun getName(activity: MainActivity): String {
        return when (code) {
            1 -> activity.getString(R.string.standing_here_i_realize)
            2 -> activity.getString(R.string.courier_six)
            3 -> activity.getString(R.string.house_has_gone_bust)
            4 -> activity.getString(R.string.robots)
            5 -> activity.getString(R.string.unlikely_alliance_1)
            6 -> activity.getString(R.string.unlikely_alliance_2)
            7 -> activity.getString(R.string.tops_guns)
            else -> ""
        }
    }

    override fun getDescription(activity: MainActivity): String {
        val enemies = when (code) {
            1 -> {
                listOf(activity.getString(R.string.pve_enemy_best))
            }
            2 -> {
                listOf(activity.getString(R.string.pve_enemy_cheater))
            }
            3 -> {
                listOf(activity.getString(R.string.mr_house))
            }
            4 -> {
                listOf(activity.getString(R.string.pve_enemy_better), activity.getString(R.string.pve_enemy_38))
            }
            5 -> {
                listOf(activity.getString(R.string.no_bark), activity.getString(R.string.pve_enemy_medium))
            }
            6 -> {
                listOf(activity.getString(R.string.johnson_nash), activity.getString(R.string.pve_enemy_easy))
            }
            7 -> {
                listOf(activity.getString(R.string.pve_enemy_hard), activity.getString(R.string.benny), activity.getString(R.string.pve_enemy_queen))
            }
            else -> listOf()
        }
        return activity.getString(R.string.defeat_enemies, enemies.joinToString())
    }

    override fun getProgress(): String {
        return isBeaten.joinToString { if (it) "+" else "-" }
    }

    override fun isCompleted(): Boolean {
        return isBeaten.all { it }
    }
}