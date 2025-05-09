package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardModifier

class StrategyJackToPlayer(val index: Int) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val jack = game.enemyCResources.hand[index] as CardModifier
        val cardA = game.playerCaravans
            .flatMap { it.cards }
            .filter { it.canAddModifier(jack) }
            .filter { cardA ->
                val state = gameToState(game)
                val indexC = game.playerCaravans.withIndex().first { cardA in it.value.cards }.index
                when (indexC) {
                    0 -> state.player.v1 -= cardA.getValue()
                    1 -> state.player.v2 -= cardA.getValue()
                    2 -> state.player.v3 -= cardA.getValue()
                }
                checkTheOutcome(state) != 1 && cardA.getValue() > 1 && cardA.canAddModifier(jack)
            }
            .maxByOrNull { it.getValue() }
        if (cardA != null) {
            cardA.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
            return true
        }
        return false
    }
}