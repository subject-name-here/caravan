package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructive
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyRush
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyTime
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
data object EnemyBetter : Enemy() {
    override fun createDeck(): CResources = CResources(CardBack.LUCKY_38, false)
    override fun getRewardBack() = CardBack.LUCKY_38

    override fun makeMove(game: Game) {
        fun check(p0: Int, e0: Int): Float {
            return when {
                p0 in (21..26) && (p0 > e0 || e0 > 26) -> 2f
                p0 > 11 && (e0 != 26 || e0 == p0) -> 0.5f
                else -> 0f
            }
        }
        val score = game.playerCaravans.indices.map { check(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue()) }
        if (2f !in score) {
            if (StrategyRush.move(game)) {
                return
            }
        } else if (score.sum() > 2f) {
            if (StrategyDestructive.move(game)) {
                return
            }
        }

        if (StrategyTime.move(game)) {
            return
        }

        EnemyHard.makeMove(game)
    }
}