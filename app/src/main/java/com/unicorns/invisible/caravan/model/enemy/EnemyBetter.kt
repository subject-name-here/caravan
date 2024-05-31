package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyAggressive
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKeeper
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyRush
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyTime
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
data object EnemyBetter : Enemy() {
    override fun createDeck(): CResources = CResources(CardBack.LUCKY_38, false)
    override fun getRewardBack() = CardBack.LUCKY_38

    override suspend fun makeMove(game: Game) {
        fun check(p0: Int, e0: Int): Float {
            return when {
                p0 in (21..26) && (p0 > e0 || e0 > 26) -> 2f
                p0 in (11..26) && (e0 != 26 || e0 == p0) -> 0.5f
                e0 in (21..26) && (e0 > p0 || p0 > 26) -> -2f
                e0 in (11..26) && (p0 != 26 || p0 == e0) -> -0.5f
                else -> 0f
            }
        }
        val score = game.playerCaravans.indices.map { check(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue()) }
        if (2f !in score || 0.5f !in score && score.sum() < 3.2f) {
            if (StrategyRush.move(game)) {
                return
            }
        } else {
            if (score.filter { it > 0 }.sum() > 2.4f) {
                if (StrategyAggressive.move(game)) {
                    return
                }
            }
        }

        if (StrategyKeeper.move(game)) {
            return
        }

        if (StrategyTime.move(game)) {
            return
        }

        EnemyHard.makeMove(game)
    }
}