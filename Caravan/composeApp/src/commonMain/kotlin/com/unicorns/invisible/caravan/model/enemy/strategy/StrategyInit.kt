package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase

class StrategyInit(val type: Type) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val (caravan, card) = when (type) {
            Type.RANDOM -> {
                val card = game.enemyCResources.hand.filterIsInstance<CardBase>().random()
                val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
                caravan to card
            }
            Type.MAX_FIRST_TO_RANDOM -> {
                val card = game.enemyCResources.hand.filterIsInstance<CardBase>().maxBy { it.rank.value }
                val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
                caravan to card
            }
            Type.MIN_FIRST_TO_RANDOM -> {
                val card = game.enemyCResources.hand.filterIsInstance<CardBase>().minBy { it.rank.value }
                val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
                caravan to card
            }

            Type.RANDOM_TO_LTR -> {
                val card = game.enemyCResources.hand.filterIsInstance<CardBase>().random()
                val caravan = game.enemyCaravans.first { it.isEmpty() }
                caravan to card
            }
            Type.RANDOM_TO_RTL -> {
                val card = game.enemyCResources.hand.filterIsInstance<CardBase>().random()
                val caravan = game.enemyCaravans.last { it.isEmpty() }
                caravan to card
            }
        }
        val index = game.enemyCResources.hand.indexOf(card)
        caravan.putCardOnTop(game.enemyCResources.removeFromHand(index, speed) as CardBase, speed)
        return true
    }

    enum class Type {
        RANDOM,
        MAX_FIRST_TO_RANDOM,
        MIN_FIRST_TO_RANDOM,
        RANDOM_TO_LTR,
        RANDOM_TO_RTL,
    }
}