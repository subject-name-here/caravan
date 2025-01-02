package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.SelectCard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInitStage
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
data object EnemyLuc10 : Enemy {
    override fun createDeck() = CResources(CardBack.LUCKY_38, true)
    override fun getBankNumber() = 15

    override fun makeMove(game: Game) {
        if (game.isInitStage()) {
            StrategyInitStage(SelectCard.RANDOM_TO_RANDOM).move(game)
            return
        }

        EnemyFrank.makeMove(game)

        // TODO:
    }
}