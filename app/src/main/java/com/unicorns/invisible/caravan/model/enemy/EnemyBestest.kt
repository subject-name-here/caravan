package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyCheckFuture
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyCheckFuture.reses
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
data object EnemyBestest : Enemy() {
    override fun createDeck(): CResources = CResources(CardBack.VAULT_21, false)
    override fun getRewardBack() = CardBack.VAULT_21

    fun init() {}
    fun clear() {}

    private fun afterMove(game: Game) {}

    override fun makeMove(game: Game) {
        if (game.isInitStage()) {
            val hand = game.enemyCResources.hand
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        reses.clear()
        val predictResult = StrategyCheckFuture.move(game)

        if (predictResult) {
            afterMove(game)
            return
        }

        if (StrategyJoker.move(game)) {
            afterMove(game)
            return
        }

        EnemySecuritron38.makeMove(game)
        afterMove(game)
    }
}