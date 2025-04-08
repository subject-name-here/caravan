package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.RankFace


class StrategyDropLadiesFirst : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {

        val cardToDrop = game.enemyCResources.hand.withIndex().minByOrNull { value ->
            when (value.value) {
                is CardBase -> {
                    (value.value as CardBase).rank.value
                }
                is CardFaceSuited -> {
                    val rank = (value.value as CardFaceSuited).rank
                    if (rank == RankFace.QUEEN) {
                        0
                    } else {
                        rank.value
                    }
                }
                is CardJoker -> {
                    14
                }
                else -> {
                    15
                }
            }
        } ?: return false

        game.enemyCResources.dropCardFromHand(cardToDrop.index, speed)
        return true
    }
}