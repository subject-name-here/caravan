package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.utils.checkMoveOnDefeat


object StrategyRush : Strategy {
    override fun move(game: Game): Boolean {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }

        hand.withIndex()
            .filter { it.value.isOrdinary() }
            .sortedByDescending { it.value.rank.value }
            .forEach { (cardIndex, card) ->
                if (card.rank == Rank.KING) {
                    game.enemyCaravans.forEachIndexed { caravanIndex, enemyCaravan ->
                        enemyCaravan.cards.sortedByDescending { it.card.rank.value }
                            .forEach { caravanCard ->
                                if (
                                    enemyCaravan.getValue() + caravanCard.getValue() in (21..26) &&
                                    caravanCard.canAddModifier(card) &&
                                    !checkMoveOnDefeat(game, caravanIndex)
                                ) {
                                    caravanCard.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                                    return true
                                }
                            }
                    }
                }

                if (!card.rank.isFace()) {
                    game.enemyCaravans.withIndex().sortedByDescending { it.value.getValue() }
                        .forEach { (caravanIndex, caravan) ->
                            if (caravan.getValue() + card.rank.value <= 26 && caravan.canPutCardOnTop(card)) {
                                if (!(checkMoveOnDefeat(game, caravanIndex) && caravan.getValue() + card.rank.value in (21..26))) {
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