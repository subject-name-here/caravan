package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.Rank


class StrategyJackOnSelf(
    private val caravan: Caravan,
    private val shouldPutJackOnto: (CardWithModifier) -> Boolean
) : Strategy {
    override fun move(game: Game): Boolean {
        val hand = game.enemyCResources.hand
        val jack = hand.withIndex().find { card -> card.value.rank == Rank.JACK }
        if (jack != null) {
            caravan.cards
                .filter { card -> card.canAddModifier(jack.value) }
                .sortedBy { card -> card.getValue() }
                .forEach { card ->
                    if (shouldPutJackOnto(card)) {
                        card.addModifier(game.enemyCResources.removeFromHand(jack.index))
                        return true
                    }
                }
        }
        return false
    }
}