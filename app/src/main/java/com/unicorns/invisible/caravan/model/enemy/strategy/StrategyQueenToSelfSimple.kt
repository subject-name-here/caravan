package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardModifier


class StrategyQueenToSelfSimple(val index: Int) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val hand = game.enemyCResources.hand.filterIsInstance<CardBase>()
        if (hand.isEmpty()) {
            return false
        }
        game.enemyCaravans.filter { it.getValue() < 26 }.shuffled().forEach { caravan ->
            if (caravan.size > 1 && hand.all { !caravan.canPutCardOnTop(it) }) {
                val last = caravan.cards[caravan.cards.lastIndex]
                val preLast = caravan.cards[caravan.cards.lastIndex - 1]
                when {
                    last.card.rank > preLast.card.rank -> {
                        if (last.isQueenReversingSequence()) {
                            if (hand.any { it.rank.value < last.card.rank.value }) {
                                last.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                                return true
                            }
                        } else {
                            if (hand.any { it.rank.value > last.card.rank.value }) {
                                last.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                                return true
                            }
                        }
                    }

                    last.card.rank < preLast.card.rank -> {
                        if (last.isQueenReversingSequence()) {
                            if (hand.any { it.rank.value > last.card.rank.value }) {
                                last.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                                return true
                            }
                        } else {
                            if (hand.any { it.rank.value < last.card.rank.value }) {
                                last.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
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