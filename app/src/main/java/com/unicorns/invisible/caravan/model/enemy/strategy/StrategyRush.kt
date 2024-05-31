package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank

object StrategyRush : Strategy {
    override fun move(game: Game): Boolean {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }

        hand.withIndex().sortedByDescending { it.value.rank.value }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.KING) {
                game.enemyCaravans.shuffled().forEach { enemyCaravan ->
                    enemyCaravan.cards.sortedByDescending { it.card.rank.value }.forEach { caravanCard ->
                        if (enemyCaravan.getValue() + caravanCard.getValue() in (21..26) && caravanCard.canAddModifier(card)) {
                            caravanCard.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                            return true
                        }
                    }
                }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedBy { it.getValue() }.forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return true
                        }
                    }
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.maxBy { it.getValue() }.dropCaravan()
            return true
        }

        return false
    }
}