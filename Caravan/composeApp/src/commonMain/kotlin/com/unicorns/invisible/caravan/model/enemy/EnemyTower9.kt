package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructiveClever
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropLadiesFirst
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersHard
import com.unicorns.invisible.caravan.model.enemy.strategy.checkIfEnemyVictoryIsClose
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit


data object EnemyTower9 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        val backs = listOf(
            CardBack.LEGION,
            CardBack.STANDARD,
            CardBack.STANDARD_UNCOMMON,
            CardBack.STANDARD_RARE,
            CardBack.VAULT_21_DAY,
            CardBack.VAULT_21_NIGHT
        )
        listOf(Suit.SPADES, Suit.CLUBS).forEach { suit ->
            backs.forEach { back ->
                RankNumber.entries.forEach { rank ->
                    if (rank.value > 2) {
                        add(CardNumber(rank, suit, back))
                    }
                }
                Suit.entries.forEach { s ->
                    add(CardFaceSuited(RankFace.JACK, s, back))
                    add(CardFaceSuited(RankFace.KING, s, back))
                }

                add(CardJoker(CardJoker.Number.ONE, back))
                add(CardJoker(CardJoker.Number.TWO, back))
            }
        }
    })

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.MAX_FIRST_TO_RANDOM).move(game, speed)
            return
        }

        if ((0..2).any { checkIfEnemyVictoryIsClose(gameToState(game), it) }) {
            if (StrategyDestructiveClever().move(game, speed)) {
                return
            }
        }

        if (StrategyPutNumbersHard().move(game, speed)) {
            return
        }

        val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().sortedByDescending { it.rank.value }
        modifiers.forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            when (modifier.rank) {
                RankFace.JACK -> {
                    if (StrategyJackHard(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.KING -> {
                    if (StrategyKingHard(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.JOKER -> {
                    if (StrategyJokerSimple(index, isHard = true).move(game, speed)) {
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