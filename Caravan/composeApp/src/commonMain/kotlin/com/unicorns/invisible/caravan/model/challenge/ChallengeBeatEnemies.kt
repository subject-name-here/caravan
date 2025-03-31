package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.beat_enemies_1_name
import caravan.composeapp.generated.resources.beat_enemies_2_name
import caravan.composeapp.generated.resources.beat_enemies_3_name
import caravan.composeapp.generated.resources.beat_enemies_4_name
import caravan.composeapp.generated.resources.beat_enemies_5_name
import caravan.composeapp.generated.resources.beat_enemies_6_name
import caravan.composeapp.generated.resources.beat_enemies_7_name
import caravan.composeapp.generated.resources.beat_enemies_8_name
import caravan.composeapp.generated.resources.beat_enemies_1_desc
import caravan.composeapp.generated.resources.beat_enemies_2_desc
import caravan.composeapp.generated.resources.beat_enemies_3_desc
import caravan.composeapp.generated.resources.beat_enemies_4_desc
import caravan.composeapp.generated.resources.beat_enemies_5_desc
import caravan.composeapp.generated.resources.beat_enemies_6_desc
import caravan.composeapp.generated.resources.beat_enemies_7_desc
import caravan.composeapp.generated.resources.beat_enemies_8_desc
import caravan.composeapp.generated.resources.empty_string
import com.unicorns.invisible.caravan.model.Game
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
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


@Serializable
class ChallengeBeatEnemies(private val code: Int) : ChallengeDaily {
    private val enemyList: List<String?>
        get() = when (code) {
            1 -> listOf(EnemyVeronica::class.simpleName, EnemyElijah::class.simpleName)
            2 -> listOf(EnemyHanlon::class.simpleName, EnemyCrooker::class.simpleName)
            3 -> listOf(EnemyBenny::class.simpleName, EnemyLuc10::class.simpleName)
            4 -> listOf(EnemyTabitha::class.simpleName, EnemySnuffles::class.simpleName)
            5 -> listOf(EnemyNash::class.simpleName, EnemyNoBark::class.simpleName, EnemyEasyPete::class.simpleName)
            6 -> listOf(EnemyUlysses::class.simpleName, EnemyDrMobius::class.simpleName)
            7 -> listOf(EnemyVeronica::class.simpleName, EnemyVictor::class.simpleName, EnemyVulpes::class.simpleName)
            8 -> listOf(EnemyMadnessCardinal::class.simpleName, EnemyTheManInTheMirror::class.simpleName)
            else -> emptyList()
        }
    private val isBeaten = Array(enemyList.size) { false }

    override fun processMove(move: Challenge.Move, game: Game) {}

    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            val index = enemyList.indexOfFirst { it == game.enemy::class.simpleName }
            if (index != -1) {
                isBeaten[index] = true
            }
        }
    }

    override fun getName(): StringResource {
        return when (code) {
            1 -> Res.string.beat_enemies_1_name
            2 -> Res.string.beat_enemies_2_name
            3 -> Res.string.beat_enemies_3_name
            4 -> Res.string.beat_enemies_4_name
            5 -> Res.string.beat_enemies_5_name
            6 -> Res.string.beat_enemies_6_name
            7 -> Res.string.beat_enemies_7_name
            8 -> Res.string.beat_enemies_8_name
            else -> Res.string.empty_string
        }
    }

    override suspend fun getDescription(): String {
        return when (code) {
            1 -> getString(Res.string.beat_enemies_1_desc)
            2 -> getString(Res.string.beat_enemies_2_desc)
            3 -> getString(Res.string.beat_enemies_3_desc)
            4 -> getString(Res.string.beat_enemies_4_desc)
            5 -> getString(Res.string.beat_enemies_5_desc)
            6 -> getString(Res.string.beat_enemies_6_desc)
            7 -> getString(Res.string.beat_enemies_7_desc)
            8 -> getString(Res.string.beat_enemies_8_desc)
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