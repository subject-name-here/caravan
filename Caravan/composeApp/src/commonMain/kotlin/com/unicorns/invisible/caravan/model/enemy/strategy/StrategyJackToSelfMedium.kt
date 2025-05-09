package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardModifier

class StrategyJackToSelfMedium(val index: Int) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val cardA = game.enemyCaravans
            .filter { it.getValue() > 26 }
            .flatMap { it.cards }
            .filter { cardA ->
                val state = gameToState(game)
                val indexC = game.enemyCaravans.withIndex().first { cardA in it.value.cards }.index
                val res = when (indexC) {
                    0 -> {
                        state.player.v1 -= cardA.getValue()
                        state.player.v1
                    }
                    1 -> {
                        state.player.v2 -= cardA.getValue()
                        state.player.v2
                    }
                    2 -> {
                        state.player.v3 -= cardA.getValue()
                        state.player.v3
                    }
                    else -> 0
                }
                checkTheOutcome(state) != 1 && res in (21..26)
            }
            .maxByOrNull { it.getValue() }
        if (cardA != null) {
            cardA.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
            return true
        }

        val cardB = game.enemyCaravans
            .filter { it.getValue() > 26 }
            .flatMap { it.cards }
            .filter { cardB ->
                val state = gameToState(game)
                val indexC = game.enemyCaravans.withIndex().first { cardB in it.value.cards }.index
                when (indexC) {
                    0 -> {
                        state.player.v1 -= cardB.getValue()
                    }
                    1 -> {
                        state.player.v2 -= cardB.getValue()
                    }
                    2 -> {
                        state.player.v3 -= cardB.getValue()
                    }
                }
                checkTheOutcome(state) != 1
            }
            .maxByOrNull { it.getValue() }
        if (cardB != null) {
            cardB.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
            return true
        }

        return false
    }
}