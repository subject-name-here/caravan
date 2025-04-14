package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.RankNumber

class StrategyJokerSimple(val index: Int) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val joker = game.enemyCResources.hand[index] as CardJoker
        val cards = (game.playerCaravans + game.enemyCaravans)
            .flatMap { it.cards }
            .filter {
                it.canAddModifier(joker)
            }
        val best = cards.maxByOrNull { card ->
            val state = gameToState(game)
            var sum = 0
            if (card.card.rank == RankNumber.ACE) {
                game.playerCaravans.forEachIndexed { index, caravan ->
                    val caravanDelta = caravan.cards
                        .filter { it.card.suit == card.card.suit }
                        .sumOf { it.getValue() }
                    when (index) {
                        0 -> {
                            state.player.v1 -= caravanDelta
                        }
                        1 -> {
                            state.player.v2 -= caravanDelta
                        }
                        2 -> {
                            state.player.v3 -= caravanDelta
                        }
                    }
                    sum += caravanDelta
                }
                game.enemyCaravans.forEachIndexed { index, caravan ->
                    val caravanDelta = caravan.cards
                        .filter { it.card.suit == card.card.suit }
                        .sumOf { it.getValue() }
                    when (index) {
                        0 -> {
                            state.enemy.v1 -= caravanDelta
                        }
                        1 -> {
                            state.enemy.v2 -= caravanDelta
                        }
                        2 -> {
                            state.enemy.v3 -= caravanDelta
                        }
                    }
                    sum -= caravanDelta / 3
                }
            } else {
                game.playerCaravans.forEachIndexed { index, caravan ->
                    val caravanDelta = caravan.cards
                        .filter { it.card.rank == card.card.rank }
                        .sumOf { it.getValue() }
                    when (index) {
                        0 -> {
                            state.player.v1 -= caravanDelta
                        }
                        1 -> {
                            state.player.v2 -= caravanDelta
                        }
                        2 -> {
                            state.player.v3 -= caravanDelta
                        }
                    }
                    sum += caravanDelta
                }
                game.enemyCaravans.forEachIndexed { index, caravan ->
                    val caravanDelta = caravan.cards
                        .filter { it.card.rank == card.card.rank }
                        .sumOf { it.getValue() }
                    when (index) {
                        0 -> {
                            state.enemy.v1 -= caravanDelta
                        }
                        1 -> {
                            state.enemy.v2 -= caravanDelta
                        }
                        2 -> {
                            state.enemy.v3 -= caravanDelta
                        }
                    }
                    sum -= caravanDelta / 3
                }
            }
            if (checkTheOutcome(state) == 1) {
                -5000
            } else if (checkTheOutcome(state) == -1) {
                5000
            } else {
                sum
            }
        }
        if (best != null) {
            best.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
            return true
        }
        return false
    }
}