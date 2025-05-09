package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardModifier


class StrategyJackToSelfSimple(val index: Int) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val jack = game.enemyCResources.hand[index] as CardModifier
        val card = game.enemyCaravans
            .filter { it.getValue() > 26 }
            .flatMap { it.cards }
            .filter { it.canAddModifier(jack) }
            .maxByOrNull { it.getValue() }
        if (card != null) {
            card.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
            return true
        }
        return false
    }
}