package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropAllButFace
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueenToSelf
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.RankFace


data object EnemyTower3 : Enemy {
    override fun createDeck(): CResources = CResources(CardBack.STANDARD_RARE)

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM).move(game, speed)
            return
        }

        if (StrategyPutNumbersHard().move(game, speed)) {
            return
        }

        val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().shuffled()

        modifiers.forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            when (modifier.rank) {
                RankFace.JACK -> {
                    if (StrategyJackToPlayer(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.QUEEN -> {
                    if (StrategyQueenToSelf(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.KING -> {
                    if (StrategyKingToPlayer(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.JOKER -> {
                    val cards = game.enemyCaravans.flatMap { it.cards }.shuffled()
                    if (cards.isNotEmpty()) {
                        cards.random().addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                        return
                    }
                }
            }
        }

        game.enemyCaravans.forEachIndexed { indexC, caravan ->
            if (caravan.getValue() > 26) {
                val state = gameToState(game)
                when (indexC) {
                    0 -> state.enemy.v1 = 0
                    1 -> state.enemy.v2 = 0
                    2 -> state.enemy.v3 = 0
                }
                if (checkTheOutcome(state) != 1) {
                    caravan.dropCaravan(speed)
                    return
                }
            }
        }

        StrategyDropAllButFace(RankFace.QUEEN).move(game, speed)
    }
}