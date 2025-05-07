package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardModifier

class StrategyKingToSelfMedium(val index: Int) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val cardB = game.enemyCaravans
            .asSequence()
            .filter { it.getValue() < 26 }
            .flatMap { it.cards }
            .filter { it.canAddModifier(game.enemyCResources.hand[index] as CardModifier) }
            .filter { cardB ->
                val state = gameToState(game)
                val indexC = game.enemyCaravans.withIndex().first { cardB in it.value.cards }.index
                when (indexC) {
                    0 -> state.enemy.v1 += cardB.getValue()
                    1 -> state.enemy.v2 += cardB.getValue()
                    2 -> state.enemy.v3 += cardB.getValue()
                }
                checkTheOutcome(state) != 1 && game.enemyCaravans[indexC].getValue() + cardB.getValue() <= 26
            }
            .maxByOrNull { it.getValue() }
        if (cardB != null) {
            cardB.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
            return true
        }
        return false
    }
}