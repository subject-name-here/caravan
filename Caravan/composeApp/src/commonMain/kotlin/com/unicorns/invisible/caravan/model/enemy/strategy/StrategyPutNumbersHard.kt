package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase
import kotlin.math.abs


class StrategyPutNumbersHard : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val numbers = game.enemyCResources.hand.filterIsInstance<CardBase>().sortedByDescending { it.rank.value }
        val caravans = game.enemyCaravans.filter { it.getValue() < 26 }.sortedByDescending { if (it.getValue() in (21..26)) 13 else it.getValue() }
        caravans.forEachIndexed { indexC, caravan ->
            numbers.forEach { card ->
                if (caravan.canPutCardOnTop(card) && caravan.getValue() + card.rank.value <= 26) {
                    val state = gameToState(game)
                    state.enemy[indexC] += card.rank.value

                    suspend fun putCard() {
                        val index = game.enemyCResources.hand.indexOf(card)
                        caravan.putCardOnTop(game.enemyCResources.removeFromHand(index, speed) as CardBase, speed)
                    }

                    if (checkTheOutcome(state) == -1) {
                        putCard()
                        return true
                    } else if (checkTheOutcome(state) != 1 && !checkOnResult(state).isPlayerMoveWins()) {
                        if (caravan.isEmpty() || caravan.getValue() + card.rank.value in (21..26)) {
                            putCard()
                            return true
                        } else {
                            if (caravan.size == 1 || caravan.cards.last().card.rank == caravan.cards[caravan.size - 2].card.rank) {
                                val last = caravan.cards.last().card
                                if (card.rank.value in (5..8)) {
                                    putCard()
                                    return true
                                }
                                if (card.rank.value in (2..4) && last.rank.value < card.rank.value) {
                                    putCard()
                                    return true
                                }
                                if (card.rank.value == 9 && last.rank.value > card.rank.value) {
                                    putCard()
                                    return true
                                }
                            } else {
                                val last = caravan.cards.last().card
                                if (abs(card.rank.value - last.rank.value) < 4) {
                                    putCard()
                                    return true
                                }
                            }
                        }
                    }
                }
            }
        }

        return false
    }
}