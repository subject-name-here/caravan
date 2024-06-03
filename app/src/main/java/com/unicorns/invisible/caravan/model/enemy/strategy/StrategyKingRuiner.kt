package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank

object StrategyKingRuiner : Strategy {
    override fun move(game: Game): Boolean {
        val hand = game.enemyCResources.hand
        val kings = hand.withIndex().filter { it.value.rank == Rank.KING }
        if (kings.isEmpty()) {
            return false
        }

        val (cardIndex, card) = kings.random()
        val caravans = game.playerCaravans
            .filter { it.getValue() >= 11 }
            .sortedBy { if (it.getValue() in (21..26)) 0 else (26 - it.getValue()) }

        caravans.forEach { playerCaravan ->
            val cardToKing = playerCaravan.cards
                .filter {
                    it.canAddModifier(card) &&
                            (playerCaravan.getValue() + it.getValue() > 26) &&
                            (playerCaravan.getValue() - it.getValue() !in (21..26))
                }
                .maxByOrNull { it.getValue() }
            if (cardToKing != null) {
                cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                return true
            }
        }
        caravans.forEach { playerCaravan ->
            val cardToKing = playerCaravan
                .cards
                .filter { it.canAddModifier(card) && (playerCaravan.getValue() + it.getValue() > 26) }
                .maxByOrNull { it.getValue() }
            if (cardToKing != null) {
                cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                return true
            }
        }

        return false
    }
}