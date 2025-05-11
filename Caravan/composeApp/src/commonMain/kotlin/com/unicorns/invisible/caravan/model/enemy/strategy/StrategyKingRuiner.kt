package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import kotlin.math.abs


class StrategyKingRuiner(val index: Int, val isHard: Boolean = false) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val hand = game.enemyCResources.hand
        val king = hand[index] as CardFaceSuited

        val caravans = game.playerCaravans
            .filter { it.getValue() >= 11 }
            .sortedBy { if (it.getValue() in (21..26)) 0 else abs(26 - it.getValue()) }

        caravans.forEachIndexed { indexC, playerCaravan ->
            val cardToKing = playerCaravan.cards
                .filter { cardA ->
                    val state = gameToState(game)
                    when (indexC) {
                        0 -> state.player.v1 += cardA.getValue()
                        1 -> state.player.v2 += cardA.getValue()
                        2 -> state.player.v3 += cardA.getValue()
                    }

                    cardA.canAddModifier(king) &&
                            checkTheOutcome(state) != 1 &&
                            (!isHard ||(0..2).none { checkOnResult(state, it).isPlayerMoveWins() }) &&
                            playerCaravan.getValue() + cardA.getValue() > 26 &&
                            playerCaravan.getValue() - cardA.getValue() !in (21..26)
                }
                .maxByOrNull { it.getValue() }
            if (cardToKing != null) {
                cardToKing.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                return true
            }
        }
        caravans.forEachIndexed { indexC, playerCaravan ->
            val cardToKing = playerCaravan.cards
                .filter { cardA ->
                    val state = gameToState(game)
                    when (indexC) {
                        0 -> state.player.v1 += cardA.getValue()
                        1 -> state.player.v2 += cardA.getValue()
                        2 -> state.player.v3 += cardA.getValue()
                    }

                    cardA.canAddModifier(king) &&
                            checkTheOutcome(state) != 1 &&
                            (!isHard ||(0..2).none { checkOnResult(state, it).isPlayerMoveWins() }) &&
                            playerCaravan.getValue() + cardA.getValue() > 26
                }
                .maxByOrNull { it.getValue() }
            if (cardToKing != null) {
                cardToKing.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                return true
            }
        }

        return false
    }
}