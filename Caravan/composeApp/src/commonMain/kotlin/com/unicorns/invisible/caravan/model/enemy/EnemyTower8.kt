package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.GamePossibleResult
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropLadiesFirst
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToSelfMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimpleOnPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingRuiner
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingToSelfMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueenToSelf
import com.unicorns.invisible.caravan.model.enemy.strategy.checkOnResult
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.Suit


data object EnemyTower8 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.LUCKY_38_SPECIAL).apply {
        addAll(CollectibleDeck(CardBack.LUCKY_38))
        addAll(CollectibleDeck(CardBack.VAULT_21_NIGHT))
        removeAll { it is CardNumber && it.suit == Suit.HEARTS }
        removeAll { it is CardFaceSuited && it.suit == Suit.HEARTS }
    })

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM).move(game, speed)
            return
        }


        // 1) Check if we have winning move
        val hand = game.enemyCResources.hand
        game.enemyCaravans.withIndex().forEach { caravan ->
            if (checkOnResult(gameToState(game), caravan.index).isEnemyMoveWins()) {
                // If caravan is overweight, check on Jacks
                val jack = hand.filterIsInstance<CardFace>().find { card -> card.rank == RankFace.JACK }
                if (jack != null) {
                    if (StrategyJackHard(hand.indexOf(jack), StrategyJackHard.Direction.TO_SELF).move(game, speed)) {
                        return
                    }
                }

                // If caravan is underweight, check on Kings
                val king = hand.filterIsInstance<CardFace>().find { card -> card.rank == RankFace.KING }
                if (king != null) {
                    if (StrategyKingHard(hand.indexOf(king), StrategyKingHard.Direction.TO_SELF).move(game, speed)) {
                        return
                    }
                }
            }
        }

        val isProblem = checkOnResult(gameToState(game)).isPlayerMoveWins()
        if (isProblem) {
            val jokers = game.enemyCResources.hand.filterIsInstance<CardJoker>()
            jokers.forEach { joker ->
                val index = game.enemyCResources.hand.indexOf(joker)
                if (StrategyJokerSimple(index, isHard = true).move(game, speed)) {
                    return
                }
            }
            val cardFaces = game.enemyCResources.hand.filterIsInstance<CardFace>()
            cardFaces.forEach { modifier ->
                val index = game.enemyCResources.hand.indexOf(modifier)
                when (modifier.rank) {
                    RankFace.JACK -> {
                        if (StrategyJackHard(index).move(game, speed)) {
                            return
                        }
                    }
                    RankFace.KING -> {
                        if (StrategyKingRuiner(index, isHard = true).move(game, speed)) {
                            return
                        }
                    }
                    else -> {}
                }
            }
        }

        if (StrategyPutNumbersHard().move(game, speed)) {
            return
        }

        val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().shuffled()
        modifiers.forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            when (modifier.rank) {
                RankFace.JACK -> {
                    if (StrategyJackHard(index, StrategyJackHard.Direction.TO_SELF).move(game, speed)) {
                        return
                    }
                }
                RankFace.QUEEN -> {
                    if (StrategyQueenToSelf(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.KING -> {
                    if (StrategyKingHard(index, StrategyKingHard.Direction.TO_SELF).move(game, speed)) {
                        return
                    }
                }
                RankFace.JOKER -> {
                    if (StrategyJokerSimpleOnPlayer(index, isHard = true).move(game, speed)) {
                        return
                    }
                }
            }
        }

        game.enemyCaravans.forEachIndexed { indexC, caravan ->
            if (caravan.getValue() > 26) {
                caravan.dropCaravan(speed)
                return
            }
        }

        StrategyDropLadiesFirst().move(game, speed)
    }
}