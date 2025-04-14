package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase

class StrategyPutNumbersSimple : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val baseCards = game.enemyCResources.hand.filterIsInstance<CardBase>()
        val caravans = game.enemyCaravans.shuffled()
        baseCards.forEach { card ->
            caravans.forEach { caravan ->
                if (caravan.canPutCardOnTop(card) && caravan.getValue() + card.rank.value <= 26) {
                    val index = game.enemyCResources.hand.indexOf(card)
                    caravan.putCardOnTop(game.enemyCResources.removeFromHand(index, speed) as CardBase, speed)
                    return true
                }
            }
        }
        return false
    }
}