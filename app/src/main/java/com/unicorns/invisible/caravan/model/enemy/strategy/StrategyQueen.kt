package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank

object StrategyQueen : Strategy {
    override fun move(game: Game): Boolean {
        val hand = game.enemyCResources.hand
        hand.withIndex().filter { it.value.rank == Rank.QUEEN }.forEach { (cardIndex, card) ->
            val possibleQueenCaravans = game.enemyCaravans.withIndex()
                .filter { ci ->
                    val c = ci.value
                    c.size >= 2 && c.getValue() < 26 && hand.all { !c.canPutCardOnTop(it) } && c.cards.last().canAddModifier(card)
                }
            val caravan = possibleQueenCaravans.maxByOrNull { (caravanIndex, caravan) ->
                if (caravan.getValue() > game.playerCaravans[caravanIndex].getValue() && caravan.getValue() in (21..26)) {
                    0
                } else if (caravan.getValue() in (21..26)) {
                    13
                } else {
                    caravan.getValue()
                }
            }
            if (caravan != null) {
                caravan.value.cards.last().addModifier(game.enemyCResources.removeFromHand(cardIndex))
                return true
            }
        }
        return false
    }
}