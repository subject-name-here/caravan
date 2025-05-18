package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import kotlin.math.abs


class StrategyQueenToSelf(val index: Int) : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val hand = game.enemyCResources.hand
        val queen = hand[index] as CardFace
        val caravan = game.enemyCaravans
            .filter { c ->
                c.size >= 2 && c.getValue() < 26 && hand.all { it !is CardBase || !c.canPutCardOnTop(it) } && c.cards.last().canAddModifier(queen)
            }.maxByOrNull { caravan ->
                abs(5 - caravan.cards.last().card.rank.value)
            }
        if (caravan != null) {
            caravan.cards.last().addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
            return true
        }
        return false
    }
}