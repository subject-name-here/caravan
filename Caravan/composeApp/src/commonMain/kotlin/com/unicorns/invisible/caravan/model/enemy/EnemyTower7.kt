package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropLadiesFirst
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingRuiner
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.checkOnResult
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import kotlin.random.Random

data object EnemyTower7 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.NCR).apply {
        addAll(CollectibleDeck(CardBack.LUCKY_38))
        addAll(CollectibleDeck(CardBack.LUCKY_38_SPECIAL))
        addAll(CollectibleDeck(CardBack.VAULT_21_DAY))
        addAll(CollectibleDeck(CardBack.VAULT_21_NIGHT))
        removeAll { it is CardNumber && it.rank.value <= 3 }
        removeAll { it is CardFace && it.rank == RankFace.QUEEN }
        removeAll { it is CardJoker }
    })

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.MAX_FIRST_TO_RANDOM).move(game, speed)
            return
        }

        val isBad = checkOnResult(gameToState(game)).isPlayerMoveWins()
        if (!isBad && Random.nextBoolean() && StrategyPutNumbersMedium().move(game, speed)) {
            return
        }

        val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().sortedByDescending { it.rank.value }
        modifiers.forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            when (modifier.rank) {
                RankFace.JACK -> {
                    if (isBad && StrategyJackToPlayer(index).move(game, speed)) {
                        return
                    }
                    if (StrategyJackMedium(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.KING -> {
                    if (isBad && StrategyKingRuiner(index).move(game, speed)) {
                        return
                    }
                    if (StrategyKingMedium(index).move(game, speed)) {
                        return
                    }
                }
                else -> {}
            }
        }

        if (StrategyPutNumbersMedium().move(game, speed)) {
            return
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