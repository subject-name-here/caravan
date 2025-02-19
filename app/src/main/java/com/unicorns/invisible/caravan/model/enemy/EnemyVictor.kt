package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.SelectCard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructive
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInitStage
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyRush
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyTime
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.trading.TopsTrader
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable


@Serializable
data object EnemyVictor : EnemyPve {
    override fun getNameId() = R.string.pve_enemy_victor
    override fun isEven() = true

    override fun createDeck() = CResources(CardBack.LUCKY_38, false)

    override fun getBank(): Int {
        return 0
    }

    override fun refreshBank() {

    }

    override fun getBet(): Int? {
        return 0
    }

    override fun retractBet() {

    }

    override fun addReward(reward: Int) {

    }

    override fun makeMove(game: Game) {
        if (game.isInitStage()) {
            StrategyInitStage(SelectCard.MAX_TO_LTR).move(game)
            return
        }

        fun check(p0: Int, e0: Int): Float {
            return when {
                p0 in (21..26) && (p0 > e0 || e0 > 26) -> 2f
                p0 > 11 && (e0 != 26 || e0 == p0) -> 0.5f
                else -> 0f
            }
        }

        val score = game.playerCaravans.indices.map {
            check(
                game.playerCaravans[it].getValue(),
                game.enemyCaravans[it].getValue()
            )
        }
        if (2f !in score) {
            if (StrategyRush.move(game)) {
                return
            }
        } else if (score.sum() > 2f) {
            if (StrategyJoker.move(game)) {
                game.jokerPlayedSound()
                return
            }
            if (StrategyDestructive.move(game)) {
                return
            }
        }

        if (StrategyTime.move(game)) {
            return
        }

        EnemyHanlon.makeMove(game)
    }
}