package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyCareful
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyCheckFuture
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructive
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyRush
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyTime
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
data object EnemyBestest : Enemy() {
    override fun createDeck(): CResources = CResources(CardBack.VAULT_21, false)
    override fun getRewardBack() = CardBack.VAULT_21

    override fun makeMove(game: Game) {
        if (game.isInitStage()) {
            val hand = game.enemyCResources.hand
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        fun check(p0: Int, e0: Int): Boolean {
            return p0 in (21..26) && (p0 > e0 || e0 > 26)
        }
        val score = game.playerCaravans.indices.map { check(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue()) }
        val antiScore = game.enemyCaravans.indices.map { check(game.enemyCaravans[it].getValue(), game.playerCaravans[it].getValue()) }

        if ((score.any { it } || antiScore.any { it }) && StrategyCheckFuture.move(game)) {
            return
        }

        val strategies = mutableListOf(
            StrategyDestructive,
            StrategyRush,
            StrategyCareful,
            StrategyTime,
        )
        strategies.toList().forEach {
            val copy = game.copy()
            it.move(copy)
            copy.processJacks()
            copy.processJoker()
            copy.checkOnGameOver()
            if (copy.isGameOver == -1) {
                it.move(game)
                return
            } else if (copy.isGameOver == 1) {
                strategies.remove(it)
            }
        }

        if (StrategyJoker.move(game)) {
            return
        }

        if (score.any { it }) {
            if (StrategyDestructive in strategies && StrategyDestructive.move(game)) {
                return
            }
        }
        if (StrategyRush in strategies && StrategyRush.move(game)) {
            return
        }
        if (game.enemyCResources.deckSize <= game.playerCResources.deckSize) {
            if (StrategyCareful in strategies && StrategyCareful.move(game)) {
                return
            }
        }

        EnemyBetter.makeMove(game)
    }
}