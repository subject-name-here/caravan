package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.GamePossibleResult
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropLadiesFirst
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingRuiner
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.checkOnResult
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace


data object EnemyTower6 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.SIERRA_MADRE_DIRTY).apply {
        addAll(CollectibleDeck(CardBack.SIERRA_MADRE_CLEAN))
        removeAll { it is CardFace && it.rank == RankFace.QUEEN }
        listOf(
            CardBack.LUCKY_38,
            CardBack.TOPS,
            CardBack.LUCKY_38,
            CardBack.ULTRA_LUXE,
        ).forEach { back ->
            add(CardJoker(CardJoker.Number.ONE, back))
            add(CardJoker(CardJoker.Number.TWO, back))
        }
    })

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM).move(game, speed)
            return
        }

        val isProblem = (0..2).map { checkOnResult(gameToState(game), it) }.any { it in listOf(
            GamePossibleResult.IMMINENT_PLAYER_VICTORY,
            GamePossibleResult.PLAYER_VICTORY_IS_POSSIBLE,
            GamePossibleResult.GAME_ON
        ) }

        if (isProblem) {
            val jokers = game.enemyCResources.hand.filterIsInstance<CardJoker>()
            jokers.forEach { joker ->
                val index = game.enemyCResources.hand.indexOf(joker)
                if (StrategyJokerMedium(index).move(game, speed)) {
                    return
                }
            }
            val cardFaces = game.enemyCResources.hand.filterIsInstance<CardFace>()
            cardFaces.forEach { modifier ->
                val index = game.enemyCResources.hand.indexOf(modifier)
                when (modifier.rank) {
                    RankFace.JACK -> {
                        if (StrategyJackMedium(index).move(game, speed)) {
                            return
                        }
                    }
                    RankFace.KING -> {
                        if (StrategyKingRuiner(index).move(game, speed)) {
                            return
                        }
                    }
                    else -> {}
                }
            }
        }

        if (StrategyPutNumbersMedium().move(game, speed)) {
            return
        }

        val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().shuffled()
        modifiers.forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            when (modifier.rank) {
                RankFace.JACK -> {
                    if (StrategyJackToPlayer(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.KING -> {
                    if (StrategyKingToPlayer(index).move(game, speed)) {
                        return
                    }
                }
                else -> {}
            }
        }

        game.enemyCaravans.forEachIndexed { indexC, caravan ->
            if (caravan.getValue() > 26) {
                val state = gameToState(game)
                when (indexC) {
                    0 -> state.enemy.v1 = 0
                    1 -> state.enemy.v2 = 0
                    2 -> state.enemy.v3 = 0
                }
                if (checkTheOutcome(state) != 1) {
                    caravan.dropCaravan(speed)
                    return
                }
            }
        }

        StrategyDropLadiesFirst().move(game, speed)
    }
}