package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardModifier

class StrategyQueenToSelf(val index: Int) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val hand = game.enemyCResources.hand
        val queen = hand[index] as CardFace
        val possibleQueenCaravans = game.enemyCaravans.withIndex()
            .filter { ci ->
                val c = ci.value
                c.size >= 2 && c.getValue() < 26 && hand.all { it !is CardBase || !c.canPutCardOnTop(it) } && c.cards.last().canAddModifier(queen)
            }
        val caravan = possibleQueenCaravans.maxByOrNull { (caravanIndex, caravan) ->
            if (caravan.getValue() > game.playerCaravans[caravanIndex].getValue() && caravan.getValue() in (21..26)) {
                0
            } else if (caravan.getValue() in (21..26)) {
                12
            } else {
                caravan.getValue()
            }
        }
        if (caravan != null) {
            caravan.value.cards.last().addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
            return true
        }
        return false
    }
}