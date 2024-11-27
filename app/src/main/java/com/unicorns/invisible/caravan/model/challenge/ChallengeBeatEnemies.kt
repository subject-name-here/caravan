package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyBenny
import com.unicorns.invisible.caravan.model.enemy.EnemyCrooker
import com.unicorns.invisible.caravan.model.enemy.EnemyDrMobius
import com.unicorns.invisible.caravan.model.enemy.EnemyEasyPete
import com.unicorns.invisible.caravan.model.enemy.EnemyElijah
import com.unicorns.invisible.caravan.model.enemy.EnemyHanlon
import com.unicorns.invisible.caravan.model.enemy.EnemyLuc10
import com.unicorns.invisible.caravan.model.enemy.EnemyMadnessCardinal
import com.unicorns.invisible.caravan.model.enemy.EnemyNash
import com.unicorns.invisible.caravan.model.enemy.EnemyNoBark
import com.unicorns.invisible.caravan.model.enemy.EnemySnuffles
import com.unicorns.invisible.caravan.model.enemy.EnemyTabitha
import com.unicorns.invisible.caravan.model.enemy.EnemyTheManInTheMirror
import com.unicorns.invisible.caravan.model.enemy.EnemyUlysses
import com.unicorns.invisible.caravan.model.enemy.EnemyVeronica
import com.unicorns.invisible.caravan.model.enemy.EnemyVictor
import com.unicorns.invisible.caravan.model.enemy.EnemyVulpes
import kotlinx.serialization.Serializable


@Serializable
class ChallengeBeatEnemies(private val code: Int) : Challenge {
    private val enemyList: List<Enemy>
        get() = when (code) {
            1 -> listOf(EnemyVeronica, EnemyElijah)
            2 -> listOf(EnemyHanlon, EnemyCrooker)
            3 -> listOf(EnemyBenny, EnemyLuc10)
            4 -> listOf(EnemyTabitha, EnemySnuffles)
            5 -> listOf(EnemyNash, EnemyNoBark, EnemyEasyPete)
            6 -> listOf(EnemyUlysses, EnemyDrMobius)
            7 -> listOf(EnemyVeronica, EnemyVictor, EnemyVulpes)
            8 -> listOf(EnemyMadnessCardinal, EnemyTheManInTheMirror)
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
            1 -> activity.getString(R.string.beat_enemies_1_name)
            2 -> activity.getString(R.string.beat_enemies_2_name)
            3 -> activity.getString(R.string.beat_enemies_3_name)
            4 -> activity.getString(R.string.beat_enemies_4_name)
            5 -> activity.getString(R.string.beat_enemies_5_name)
            6 -> activity.getString(R.string.beat_enemies_6_name)
            7 -> activity.getString(R.string.beat_enemies_7_name)
            8 -> activity.getString(R.string.beat_enemies_8_name)
            else -> ""
        }
    }

    override fun getDescription(activity: MainActivity): String {
        return when (code) {
            1 -> activity.getString(R.string.beat_enemies_1_desc)
            2 -> activity.getString(R.string.beat_enemies_2_desc)
            3 -> activity.getString(R.string.beat_enemies_3_desc)
            4 -> activity.getString(R.string.beat_enemies_4_desc)
            5 -> activity.getString(R.string.beat_enemies_5_desc)
            6 -> activity.getString(R.string.beat_enemies_6_desc)
            7 -> activity.getString(R.string.beat_enemies_7_desc)
            8 -> activity.getString(R.string.beat_enemies_8_desc)
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