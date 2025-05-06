package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToSelfSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimpleOnPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingToSelfSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueenToSelfSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.WWType
import kotlin.random.Random

data object EnemyTower2 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.TOPS).apply {
        repeat(9) { add(CardWildWasteland(WWType.YES_MAN)) }
    })

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM).move(game, speed)
            return
        }

        val wws = game.enemyCResources.hand.filterIsInstance<CardWildWasteland>().shuffled()
        wws.forEach { ww ->
            val index = game.enemyCResources.hand.indexOf(ww)
            when (ww.wwType) {
                WWType.YES_MAN -> {
                    game.enemyCaravans.shuffled().forEach { caravan ->
                        if (caravan.getValue() != 26 && !caravan.isEmpty()) {
                            if (caravan.getValue() !in (21..26) || Random.nextBoolean()) {
                                val card = caravan.cards.find { it.canAddModifier(ww) }
                                if (card != null) {
                                    val state = gameToState(game)
                                    val indexC = game.enemyCaravans.indexOf(caravan)
                                    when (indexC) {
                                        0 -> state.enemy.v1 = 26
                                        1 -> state.enemy.v2 = 26
                                        2 -> state.enemy.v3 = 26
                                    }
                                    if (checkTheOutcome(state) != 1) {
                                        card.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                                        return
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }


        if (StrategyPutNumbersSimple().move(game, speed)) {
            return
        }


        val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().shuffled()
        modifiers.forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            when (modifier.rank) {
                RankFace.JACK -> {
                    if (StrategyJackToSelfSimple(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.QUEEN -> {
                    if (StrategyQueenToSelfSimple(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.KING -> {
                    if (StrategyKingToSelfSimple(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.JOKER -> {
                    if (StrategyJokerSimpleOnPlayer(index).move(game, speed)) {
                        return
                    }
                }
            }
        }

        game.enemyCaravans.shuffled().forEach { caravan ->
            if (caravan.getValue() > 26) {
                caravan.dropCaravan(speed)
                return
            }
        }

        game.enemyCResources.dropCardFromHand(game.enemyCResources.hand.withIndex().minBy {
            when (val card = it.value) {
                is CardBase -> card.rank.value
                is CardFace -> card.rank.value
                else -> 15
            }
        }.index, speed)
    }
}