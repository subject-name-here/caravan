package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.viqueen
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropAllButFace
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToSelfMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingToSelfMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueenToSelf
import com.unicorns.invisible.caravan.model.enemy.strategy.checkIfPlayerVictoryIsClose
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.RankFace
import kotlinx.serialization.Serializable


@Serializable
class EnemyViqueen : EnemyPvENoBank() {
    override val nameId
        get() = Res.string.viqueen
    override val isEven
        get() = true
    override val isAvailable: Int
        get() = 3

    override var wins: Int = 0
    override var winsBlitz: Int = 0

    override fun createDeck(): CResources {
        return CResources(CardBack.VIKING)
    }
    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.MIN_FIRST_TO_RANDOM).move(game, speed)
            return
        }

        if (checkIfPlayerVictoryIsClose(gameToState(game))) {
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
                    RankFace.JOKER -> {
                        if (StrategyJokerSimple(index).move(game, speed)) {
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

        val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().sortedByDescending {
            when (it.rank) {
                RankFace.JACK -> 2
                RankFace.QUEEN -> 1
                RankFace.KING -> 3
                RankFace.JOKER -> 0
            }
        }

        modifiers.forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            when (modifier.rank) {
                RankFace.JACK -> {
                    if (StrategyJackToSelfMedium(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.QUEEN -> {
                    if (StrategyQueenToSelf(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.KING -> {
                    if (StrategyKingToSelfMedium(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.JOKER -> {}
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

        StrategyDropAllButFace(RankFace.JOKER).move(game, speed)
    }
}