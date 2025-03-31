package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardModifier


class StrategyKingToSelfSimple(val index: Int) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        game.enemyCaravans.filter { it.getValue() < 26 }.forEach { caravan ->
            caravan.cards.sortedByDescending { it.getValue() }.forEach { card ->
                if (caravan.getValue() + card.getValue() <= 26) {
                    card.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                    return true
                }
            }
        }
        return false
    }
}