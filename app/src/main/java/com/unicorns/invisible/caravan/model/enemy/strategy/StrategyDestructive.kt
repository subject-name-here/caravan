package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.json
import kotlinx.serialization.encodeToString


object StrategyDestructive : Strategy {
    override fun move(game: Game): Boolean {
        val hand = game.enemyCResources.hand

        if (StrategyJoker.move(game)) {
            return true
        }

        val king = hand.withIndex().find { it.value.rank == Rank.KING }
        if (king != null) {
            val caravan = game.playerCaravans.filter { it.getValue() in (21..26) }.maxByOrNull { it.getValue() }
            if (caravan != null) {
                val cardToKing = caravan.cards.filter { it.canAddModifier(king.value) }.maxByOrNull { it.getValue() }
                if (cardToKing != null) {
                    cardToKing.addModifier(game.enemyCResources.removeFromHand(king.index))
                    return true
                }
            }
        }

        val jack = hand.withIndex().find { it.value.rank == Rank.JACK }
        if (jack != null) {
            val caravan = game.playerCaravans.filter { !it.isEmpty() && it.getValue() <= 26 }.maxByOrNull { it.getValue() }
            val cardToJack = caravan?.cards?.filter { it.canAddModifier(jack.value) }?.maxBy { it.getValue() }
            if (cardToJack != null) {
                cardToJack.addModifier(game.enemyCResources.removeFromHand(jack.index))
                return true
            }
        }

        return false
    }
}