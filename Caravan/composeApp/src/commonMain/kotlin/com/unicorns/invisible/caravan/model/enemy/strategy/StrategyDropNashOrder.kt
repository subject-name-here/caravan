package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.RankFace

class StrategyDropNashOrder : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val cardToDrop = game.enemyCResources.hand.withIndex().shuffled().minByOrNull { value ->
            when (value.value) {
                is CardBase -> {
                    // It's SIX
                    6
                }
                is CardFaceSuited -> {
                    val rank = (value.value as CardFaceSuited).rank
                    when (rank) {
                        RankFace.JACK -> {
                            2
                        }
                        RankFace.QUEEN -> {
                            1
                        }
                        RankFace.KING -> {
                            13
                        }
                        else -> 0
                    }
                }
                else -> {
                    0
                }
            }
        } ?: return false

        game.enemyCResources.dropCardFromHand(cardToDrop.index, speed)
        return true
    }
}