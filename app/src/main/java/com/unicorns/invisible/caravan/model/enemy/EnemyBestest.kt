package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
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

        if (StrategyJoker.move(game)) {
            game.jokerPlayedSound()
            return
        }

        // TODO!!!

        EnemySecuritron38.makeMove(game)
    }
}