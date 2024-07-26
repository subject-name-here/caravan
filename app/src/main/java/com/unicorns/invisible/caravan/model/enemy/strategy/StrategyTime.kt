package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyTutorial
import com.unicorns.invisible.caravan.model.enemy.EnemyYesMan
import com.unicorns.invisible.caravan.model.primitives.Rank

object StrategyTime : Strategy {
    override fun move(game: Game): Boolean {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.maxBy { it.getValue() }.dropCaravan()
            return true
        }

        hand.withIndex().filter { !it.value.isSpecial() }.sortedBy { it.value.rank.value }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.QUEEN) {
                val possibleQueenCaravans = game.enemyCaravans
                    .filter { c ->
                        c.size >= 2 &&
                                hand.all { !c.canPutCardOnTop(it) } &&
                                c.cards.last().canAddModifier(card)
                    }
                if (possibleQueenCaravans.isNotEmpty()) {
                    possibleQueenCaravans
                        .first()
                        .cards
                        .last()
                        .addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return true
                }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedBy { it.getValue() }.forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.canPutCardOnTop(card)) {
                            val caravanIndex = game.enemyCaravans.indexOf(caravan)
                            if (!EnemyYesMan.checkMoveOnDefeat(
                                    game,
                                    caravanIndex
                                ) && caravan.getValue() + card.rank.value > game.playerCaravans[caravanIndex].getValue()
                            ) {
                                caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                                return true
                            }
                        }
                    }
                }
            }
        }

        return false
    }
}