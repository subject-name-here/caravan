package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.utils.checkMoveOnDefeat


object StrategyDestructive : Strategy {
    override fun move(game: Game): Boolean {
        val hand = game.enemyCResources.hand

        val king = hand.withIndex().find { it.value.isOrdinary() && it.value.rank == Rank.KING }
        if (king != null) {
            val caravan = game.playerCaravans.filter { it.getValue() in (21..26) }
                .maxByOrNull { it.getValue() }
            if (caravan != null) {
                val cardToKing = caravan.cards.filter { it.canAddModifier(king.value) }
                    .maxByOrNull { it.getValue() }
                if (cardToKing != null) {
                    val futureValue = caravan.getValue() + cardToKing.getValue()
                    val caravanIndex = game.playerCaravans.indexOf(caravan)
                    val enemyValue = game.enemyCaravans[caravanIndex].getValue()
                    if (!(checkMoveOnDefeat(game, caravanIndex) && enemyValue in (21..26) &&
                                (enemyValue > futureValue || futureValue > 26))
                    ) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(king.index))
                        return true
                    }
                }
            }
        }

        val jack = hand.withIndex().find { it.value.isOrdinary() && it.value.rank == Rank.JACK }
        if (jack != null) {
            val caravan = game.playerCaravans.filter { !it.isEmpty() && it.getValue() <= 26 }
                .maxByOrNull { it.getValue() }
            val cardToJack =
                caravan?.cards?.filter { it.canAddModifier(jack.value) }?.maxByOrNull { it.getValue() }
            if (cardToJack != null) {
                val futureValue = caravan.getValue() - cardToJack.getValue()
                val caravanIndex = game.playerCaravans.indexOf(caravan)
                val enemyValue = game.enemyCaravans[caravanIndex].getValue()
                if (!(checkMoveOnDefeat(game, caravanIndex) && enemyValue in (21..26) &&
                            (enemyValue > futureValue || futureValue > 26))
                ) {
                    cardToJack.addModifier(game.enemyCResources.removeFromHand(jack.index))
                    return true
                }
            }
        }

        return false
    }
}