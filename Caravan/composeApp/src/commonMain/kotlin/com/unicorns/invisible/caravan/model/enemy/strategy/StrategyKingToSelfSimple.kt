package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardModifier


class StrategyKingToSelfSimple(val index: Int) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val king = game.enemyCResources.hand[index] as CardModifier
        game.enemyCaravans.filter { it.getValue() < 26 }.forEach { caravan ->
            caravan.cards.filter { it.canAddModifier(king) }.sortedByDescending { it.getValue() }.forEach { card ->
                if (caravan.getValue() + card.getValue() <= 26) {
                    card.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                    return true
                }
            }
        }
        return false
    }
}