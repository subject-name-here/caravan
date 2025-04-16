package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardModifier

class StrategyKingHard(val index: Int) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val cardA = game.playerCaravans
            .filter { it.getValue() in (13..26) }
            .flatMap { it.cards }
            .filter { cardA ->
                val state = gameToState(game)
                val indexC = game.playerCaravans.withIndex().first { cardA in it.value.cards }.index
                when (indexC) {
                    0 -> state.player.v1 += cardA.getValue()
                    1 -> state.player.v2 += cardA.getValue()
                    2 -> state.player.v3 += cardA.getValue()
                }
                checkTheOutcome(state) != 1 && game.playerCaravans[indexC].getValue() + cardA.getValue() > 26
            }
            .maxByOrNull { it.getValue() }
        if (cardA != null) {
            cardA.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
            return true
        }

        val cardB = game.enemyCaravans
            .filter { it.getValue() < 26 }
            .flatMap { it.cards }
            .filter { it.canAddModifier(game.enemyCResources.hand[index] as CardModifier) }
            .filter { cardB ->
                val state = gameToState(game)
                val indexC = game.enemyCaravans.withIndex().first { cardB in it.value.cards }.index
                when (indexC) {
                    0 -> state.enemy.v1 += cardB.getValue()
                    1 -> state.enemy.v2 += cardB.getValue()
                    2 -> state.enemy.v3 += cardB.getValue()
                }
                checkTheOutcome(state) != 1 && game.enemyCaravans[indexC].getValue() + cardB.getValue() <= 26
            }
            .maxByOrNull { it.getValue() }
        if (cardB != null) {
            cardB.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
            return true
        }
        return false
    }
}