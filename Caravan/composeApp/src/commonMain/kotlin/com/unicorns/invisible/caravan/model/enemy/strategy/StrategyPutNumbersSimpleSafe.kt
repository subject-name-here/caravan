package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase

class StrategyPutNumbersSimpleSafe : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val baseCards = game.enemyCResources.hand.filterIsInstance<CardBase>().shuffled()
        val caravans = game.enemyCaravans.withIndex().shuffled()
        baseCards.forEach { card ->
            caravans.forEach { (indexC, caravan) ->
                if (caravan.canPutCardOnTop(card) && caravan.getValue() + card.rank.value <= 26) {
                    val state = gameToState(game)
                    when (indexC) {
                        0 -> state.enemy.v1 += card.rank.value
                        1 -> state.enemy.v2 += card.rank.value
                        2 -> state.enemy.v3 += card.rank.value
                    }
                    if (checkTheOutcome(state) != 1) {
                        val index = game.enemyCResources.hand.indexOf(card)
                        caravan.putCardOnTop(game.enemyCResources.removeFromHand(index, speed) as CardBase, speed)
                        return true
                    }
                }
            }
        }
        return false
    }
}