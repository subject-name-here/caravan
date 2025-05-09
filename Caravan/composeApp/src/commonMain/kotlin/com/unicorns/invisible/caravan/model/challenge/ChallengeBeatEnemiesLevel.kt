package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.challenge_lvl_1_desc
import caravan.composeapp.generated.resources.challenge_lvl_1_name
import caravan.composeapp.generated.resources.challenge_lvl_2_desc
import caravan.composeapp.generated.resources.challenge_lvl_2_name
import caravan.composeapp.generated.resources.challenge_lvl_3_desc
import caravan.composeapp.generated.resources.challenge_lvl_3_name
import caravan.composeapp.generated.resources.challenge_lvl_4_desc
import caravan.composeapp.generated.resources.challenge_lvl_4_name
import caravan.composeapp.generated.resources.challenge_lvl_5_desc
import caravan.composeapp.generated.resources.challenge_lvl_5_name
import caravan.composeapp.generated.resources.challenge_lvl_6_desc
import caravan.composeapp.generated.resources.challenge_lvl_6_name
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.EnemyEasyPete
import com.unicorns.invisible.caravan.model.enemy.EnemyNash
import com.unicorns.invisible.caravan.model.enemy.EnemyNoBark
import com.unicorns.invisible.caravan.model.enemy.EnemyOliver
import com.unicorns.invisible.caravan.model.enemy.EnemyRingo
import com.unicorns.invisible.caravan.model.enemy.EnemySalt
import com.unicorns.invisible.caravan.model.enemy.EnemySnuffles
import com.unicorns.invisible.caravan.model.enemy.EnemyTabitha
import com.unicorns.invisible.caravan.model.enemy.EnemyVeronica
import com.unicorns.invisible.caravan.model.enemy.EnemyVictor
import com.unicorns.invisible.caravan.model.enemy.EnemyViqueen
import com.unicorns.invisible.caravan.model.enemy.EnemyVulpes
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


@Serializable
class ChallengeBeatEnemiesLevel(private val lvl: Int) : ChallengeDaily {
    private val enemyList: List<String?>
        get() = when (lvl) {
            1 -> listOf(EnemyOliver::class.simpleName, EnemyRingo::class.simpleName, EnemySnuffles::class.simpleName)
            2 -> listOf(EnemyVeronica::class.simpleName, EnemyEasyPete::class.simpleName, EnemySalt::class.simpleName)
            3 -> listOf(EnemyVictor::class.simpleName, EnemyNash::class.simpleName, EnemyVeronica::class.simpleName)
            4 -> listOf(EnemyNoBark::class.simpleName, EnemyVictor::class.simpleName, EnemyNash::class.simpleName)
            5 -> listOf(EnemyNoBark::class.simpleName, EnemyVulpes::class.simpleName, EnemyViqueen::class.simpleName)
            else -> listOf(EnemyTabitha::class.simpleName, EnemyVulpes::class.simpleName, EnemyViqueen::class.simpleName)
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
        return when (lvl) {
            1 -> Res.string.challenge_lvl_1_name
            2 -> Res.string.challenge_lvl_2_name
            3 -> Res.string.challenge_lvl_3_name
            4 -> Res.string.challenge_lvl_4_name
            5 -> Res.string.challenge_lvl_5_name
            else -> Res.string.challenge_lvl_6_name
        }
    }

    override suspend fun getDescription(): String {
        return when (lvl) {
            1 -> getString(Res.string.challenge_lvl_1_desc)
            2 -> getString(Res.string.challenge_lvl_2_desc)
            3 -> getString(Res.string.challenge_lvl_3_desc)
            4 -> getString(Res.string.challenge_lvl_4_desc)
            5 -> getString(Res.string.challenge_lvl_5_desc)
            else -> getString(Res.string.challenge_lvl_6_desc)
        }
    }

    override fun getProgress(): String {
        return isBeaten.joinToString { if (it) "+" else "-" }
    }

    override fun isCompleted(): Boolean {
        return isBeaten.all { it }
    }
}