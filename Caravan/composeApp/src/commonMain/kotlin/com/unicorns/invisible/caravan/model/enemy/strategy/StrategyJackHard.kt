package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardModifier


class StrategyJackHard(val index: Int, val direction: Direction = Direction.BOTH) : Strategy {
    enum class Direction {
        TO_PLAYER,
        TO_SELF,
        BOTH
    }

    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val jack = game.enemyCResources.hand[index] as CardModifier

        if (direction != Direction.TO_SELF) {
            val cardA = game.playerCaravans
                .filter { it.getValue() in (11..26) }
                .flatMap { it.cards }
                .filter { it.canAddModifier(jack) }
                .filter { cardA ->
                    val state = gameToState(game)
                    val indexC = game.playerCaravans.withIndex().first { cardA in it.value.cards }.index
                    when (indexC) {
                        0 -> state.player.v1 -= cardA.getValue()
                        1 -> state.player.v2 -= cardA.getValue()
                        2 -> state.player.v3 -= cardA.getValue()
                    }
                    checkTheOutcome(state) != 1 && !checkOnResult(state).isPlayerMoveWins()
                }
                .maxByOrNull { it.getValue() }
            if (cardA != null) {
                cardA.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                return true
            }
        }

        if (direction != Direction.TO_PLAYER) {
            val cardB = game.enemyCaravans
                .filter { it.getValue() > 26 }
                .flatMap { it.cards }
                .filter { it.canAddModifier(jack) }
                .filter { cardB ->
                    val state = gameToState(game)
                    val indexC = game.enemyCaravans.withIndex().first { cardB in it.value.cards }.index
                    when (indexC) {
                        0 -> state.enemy.v1 -= cardB.getValue()
                        1 -> state.enemy.v2 -= cardB.getValue()
                        2 -> state.enemy.v3 -= cardB.getValue()
                    }
                    checkTheOutcome(state) != 1 && !checkOnResult(state).isPlayerMoveWins()
                }
                .maxByOrNull { cardB ->
                    val state = gameToState(game)
                    val indexC = game.enemyCaravans.withIndex().first { cardB in it.value.cards }.index
                    when (indexC) {
                        0 -> state.enemy.v1 -= cardB.getValue()
                        1 -> state.enemy.v2 -= cardB.getValue()
                        2 -> state.enemy.v3 -= cardB.getValue()
                    }
                    if (state.enemy[indexC] in (21..26)) {
                        100 + state.enemy[indexC]
                    } else {
                        cardB.getValue()
                    }
                }
            if (cardB != null) {
                cardB.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                return true
            }
        }

        return false
    }
}