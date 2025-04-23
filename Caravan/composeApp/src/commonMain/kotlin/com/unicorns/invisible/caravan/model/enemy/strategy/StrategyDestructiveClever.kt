package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.RankFace

class StrategyDestructiveClever : Strategy {
    override suspend fun move(game: Game, speed: AnimationSpeed): Boolean {
        val indexJack = game.enemyCResources.hand.indexOfFirst { it is CardFace && it.rank == RankFace.JACK }
        val cardToJack = game.playerCaravans
            .filter { it.getValue() in (21..26) }
            .flatMap { it.cards }
            .filter { cardA ->
                val state = gameToState(game)
                val indexC = game.playerCaravans.withIndex().first { cardA in it.value.cards }.index
                when (indexC) {
                    0 -> state.player.v1 -= cardA.getValue()
                    1 -> state.player.v2 -= cardA.getValue()
                    2 -> state.player.v3 -= cardA.getValue()
                }
                checkTheOutcome(state) != 1
            }
            .maxByOrNull { it.getValue() }
        if (cardToJack != null && indexJack in game.enemyCResources.hand.indices) {
            cardToJack.addModifier(game.enemyCResources.removeFromHand(indexJack, speed) as CardModifier, speed)
            return true
        }

        val indexKing = game.enemyCResources.hand.indexOfFirst { it is CardFace && it.rank == RankFace.KING }
        val cardToKing = game.playerCaravans
            .filter { it.getValue() in (21..26) }
            .flatMap { it.cards }
            .filter { cardA ->
                val state = gameToState(game)
                val indexC = game.playerCaravans.withIndex().first { cardA in it.value.cards }.index
                when (indexC) {
                    0 -> state.player.v1 += cardA.getValue()
                    1 -> state.player.v2 += cardA.getValue()
                    2 -> state.player.v3 += cardA.getValue()
                }
                checkTheOutcome(state) != 1
            }
            .maxByOrNull { it.getValue() }
        if (cardToKing != null && indexKing in game.enemyCResources.hand.indices) {
            cardToKing.addModifier(game.enemyCResources.removeFromHand(indexKing, speed) as CardModifier, speed)
            return true
        }

        val indexJoker = game.enemyCResources.hand.indexOfFirst { it is CardJoker }
        return if (indexJoker != -1) {
            StrategyJokerSimple(indexJoker).move(game, speed)
        } else {
            false
        }
    }
}