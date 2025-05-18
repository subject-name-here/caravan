package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.RankNumber


class StrategyJokerSimple(val index: Int, val isHard: Boolean = false) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val joker = game.enemyCResources.hand[index] as CardJoker
        val cards = (game.playerCaravans + game.enemyCaravans)
            .flatMap { it.cards }
            .filter { it.canAddModifier(joker) }

        fun getValue(card: CardWithModifier): Int {
            val state = gameToState(game)
            var sum = 0
            if (card.card.rank == RankNumber.ACE) {
                game.playerCaravans.forEachIndexed { index, caravan ->
                    val caravanDelta = caravan.cards
                        .filter { it.card.suit == card.card.suit && it != card }
                        .sumOf { it.getValue() }
                    state.player[index] -= caravanDelta
                    sum += caravanDelta
                }
                game.enemyCaravans.forEachIndexed { index, caravan ->
                    val caravanDelta = caravan.cards
                        .filter { it.card.suit == card.card.suit && it != card }
                        .sumOf { it.getValue() }
                    state.enemy[index] -= caravanDelta
                    sum -= caravanDelta / 3
                }
            } else {
                game.playerCaravans.forEachIndexed { index, caravan ->
                    val caravanDelta = caravan.cards
                        .filter { it.card.rank == card.card.rank && it != card }
                        .sumOf { it.getValue() }
                    state.player[index] -= caravanDelta
                    sum += caravanDelta
                }
                game.enemyCaravans.forEachIndexed { index, caravan ->
                    val caravanDelta = caravan.cards
                        .filter { it.card.rank == card.card.rank && it != card }
                        .sumOf { it.getValue() }
                    state.enemy[index] -= caravanDelta
                    sum -= caravanDelta / 3
                }
            }
            return if (checkTheOutcome(state) == 1) {
                -5000
            } else if (isHard && checkOnResult(state).isPlayerMoveWins()) {
                -2500
            } else if (checkTheOutcome(state) == -1) {
                5000
            } else {
                sum
            }
        }

        val best = cards.maxByOrNull { getValue(it) }
        if (best != null && getValue(best) > 0) {
            best.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
            return true
        }
        return false
    }
}