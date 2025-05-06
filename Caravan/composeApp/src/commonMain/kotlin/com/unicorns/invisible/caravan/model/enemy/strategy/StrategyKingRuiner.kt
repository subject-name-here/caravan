package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardModifier


class StrategyKingRuiner(val index: Int) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val hand = game.enemyCResources.hand
        val king = hand[index] as CardFaceSuited

        val caravans = game.playerCaravans
            .filter { it.getValue() >= 11 }
            .sortedBy { if (it.getValue() in (21..26)) 0 else (26 - it.getValue()) }

        caravans.forEach { playerCaravan ->
            val cardToKing = playerCaravan.cards
                .filter {
                    it.canAddModifier(king) &&
                            (playerCaravan.getValue() + it.getValue() > 26) &&
                            (playerCaravan.getValue() - it.getValue() !in (21..26))
                }
                .maxByOrNull { it.getValue() }
            if (cardToKing != null) {
                cardToKing.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                return true
            }
        }
        caravans.forEach { playerCaravan ->
            val cardToKing = playerCaravan
                .cards
                .filter { it.canAddModifier(king) && (playerCaravan.getValue() + it.getValue() > 26) }
                .maxByOrNull { it.getValue() }
            if (cardToKing != null) {
                cardToKing.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                return true
            }
        }

        return false
    }
}