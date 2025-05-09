package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase
import kotlin.math.abs


class StrategyPutNumbersMedium : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val numbers = game.enemyCResources.hand.filterIsInstance<CardBase>()
        val caravans = game.enemyCaravans

        val candidates = caravans.flatMapIndexed { indexC, caravan ->
            numbers.map { card ->
                caravan to card to if (caravan.canPutCardOnTop(card) && caravan.getValue() + card.rank.value <= 26) {
                    val state = gameToState(game)
                    when (indexC) {
                        0 -> state.enemy.v1 += card.rank.value
                        1 -> state.enemy.v2 += card.rank.value
                        2 -> state.enemy.v3 += card.rank.value
                    }
                    if (checkTheOutcome(state) == 1) {
                        -500
                    } else if (checkTheOutcome(state) == -1) {
                        500
                    } else {
                        val v = card.rank.value
                        if (caravan.isEmpty()) 3 + abs(v - 5) else 6 - abs(v - caravan.cards.last().card.rank.value)
                    }
                } else {
                    -500
                }
            }
        }

        val best = candidates.maxByOrNull { it.second }

        if (best != null && best.second > 0) {
            val index = game.enemyCResources.hand.indexOf(best.first.second)
            best.first.first.putCardOnTop(game.enemyCResources.removeFromHand(index, speed) as CardBase, speed)
            return true
        }

        return false
    }
}