package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.RankNumber

class StrategyJokerMedium(val index: Int) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val joker = game.enemyCResources.hand[index] as CardJoker
        val cards = (game.playerCaravans + game.enemyCaravans)
            .flatMap { it.cards }
            .filter {
                it.canAddModifier(joker)
            }

        fun getValue(card: CardWithModifier): Int {
            val state = gameToState(game)
            var sum = 0
            val filter = if (card.card.rank == RankNumber.ACE) {
                { it: CardWithModifier -> it.card.suit == card.card.suit && it != card }
            } else {
                { it: CardWithModifier -> it.card.rank == card.card.rank && it != card }
            }
            game.playerCaravans.forEachIndexed { index, caravan ->
                val caravanDelta = caravan.cards
                    .filter(filter)
                    .sumOf { it.getValue() }
                when (index) {
                    0 -> {
                        state.player.v1 -= caravanDelta
                        val isSold = state.player.v1 in (21..26)
                        sum += when {
                            game.playerCaravans[0].getValue() > 26 -> {
                                if (isSold) {
                                    -10
                                } else if (state.player.v1 > 26) {
                                    -1
                                } else {
                                    -2
                                }
                            }
                            game.playerCaravans[0].getValue() in 21..26 -> {
                                if (isSold) {
                                    -7
                                } else {
                                    caravanDelta
                                }
                            }
                            else -> {
                                caravanDelta
                            }
                        }
                    }
                    1 -> {
                        state.player.v2 -= caravanDelta
                        val isSold = state.player.v2 in (21..26)
                        sum += when {
                            game.playerCaravans[1].getValue() > 26 -> {
                                if (isSold) {
                                    -10
                                } else if (state.player.v2 > 26) {
                                    -1
                                } else {
                                    -2
                                }
                            }
                            game.playerCaravans[1].getValue() in 21..26 -> {
                                if (isSold) {
                                    -7
                                } else {
                                    caravanDelta
                                }
                            }
                            else -> {
                                caravanDelta
                            }
                        }
                    }
                    2 -> {
                        state.player.v3 -= caravanDelta
                        val isSold = state.player.v3 in (21..26)
                        sum += when {
                            game.playerCaravans[2].getValue() > 26 -> {
                                if (isSold) {
                                    -100
                                } else if (state.player.v3 > 26) {
                                    -caravanDelta
                                } else {
                                    -caravanDelta / 3
                                }
                            }
                            game.playerCaravans[2].getValue() in (21..26) -> {
                                if (isSold) {
                                    -caravanDelta
                                } else {
                                    caravanDelta
                                }
                            }
                            else -> {
                                caravanDelta
                            }
                        }
                    }
                }
            }
            game.enemyCaravans.forEachIndexed { index, caravan ->
                val caravanDelta = caravan.cards
                    .filter(filter)
                    .sumOf { it.getValue() }
                when (index) {
                    0 -> {
                        state.enemy.v1 -= caravanDelta
                        val isSold = state.enemy.v1 in (21..26)
                        sum += when {
                            game.enemyCaravans[0].getValue() > 26 -> {
                                if (isSold) {
                                    13
                                } else if (state.enemy.v1 > 26) {
                                    3
                                } else {
                                    0
                                }
                            }
                            game.enemyCaravans[0].getValue() in 21..26 -> {
                                if (isSold) {
                                    -caravanDelta
                                } else {
                                    -4 * caravanDelta
                                }
                            }
                            else -> {
                                -caravanDelta
                            }
                        }
                    }
                    1 -> {
                        state.enemy.v2 -= caravanDelta
                        val isSold = state.enemy.v2 in (21..26)
                        sum += when {
                            game.enemyCaravans[1].getValue() > 26 -> {
                                if (isSold) {
                                    13
                                } else if (state.enemy.v2 > 26) {
                                    3
                                } else {
                                    0
                                }
                            }
                            game.enemyCaravans[1].getValue() in 21..26 -> {
                                if (isSold) {
                                    -caravanDelta
                                } else {
                                    -4 * caravanDelta
                                }
                            }
                            else -> {
                                -caravanDelta
                            }
                        }
                    }
                    2 -> {
                        state.enemy.v3 -= caravanDelta
                        val isSold = state.enemy.v3 in (21..26)
                        sum += when {
                            game.enemyCaravans[2].getValue() > 26 -> {
                                if (isSold) {
                                    13
                                } else if (state.enemy.v3 > 26) {
                                    3
                                } else {
                                    0
                                }
                            }
                            game.enemyCaravans[2].getValue() in 21..26 -> {
                                if (isSold) {
                                    -caravanDelta
                                } else {
                                    -4 * caravanDelta
                                }
                            }
                            else -> {
                                -caravanDelta
                            }
                        }
                    }
                }
            }
            return if (checkTheOutcome(state) == 1) {
                -5000
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