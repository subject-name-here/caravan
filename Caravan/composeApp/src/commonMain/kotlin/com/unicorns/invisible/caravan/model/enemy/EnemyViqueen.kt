package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.viqueen
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropAllButFace
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimpleOnPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueenToSelf
import com.unicorns.invisible.caravan.model.enemy.strategy.checkIfPlayerVictoryIsClose
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
    override val level: Int
        get() = 5
    override val isAvailable: Boolean
        get() = true

    override var wins: Int = 0
    override var winsBlitz: Int = 0

    override var curCards: Int = maxCards

    override fun createDeck(): CResources {
        return CResources(CardBack.VIKING)
    }
    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.MAX_FIRST_TO_RANDOM).move(game, speed)
            return
        }

        if (checkIfPlayerVictoryIsClose(gameToState(game))) {
            val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().shuffled()

            modifiers.forEach { modifier ->
                val index = game.enemyCResources.hand.indexOf(modifier)
                when (modifier.rank) {
                    RankFace.JACK -> {
                        if (StrategyJackHard(index, StrategyJackHard.Direction.TO_PLAYER).move(game, speed)) {
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
        }

        if (StrategyPutNumbersHard().move(game, speed)) {
            return
        }

        val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().sortedByDescending {
            when (it.rank) {
                RankFace.JACK -> 3
                RankFace.QUEEN -> 2
                RankFace.KING -> 1
                RankFace.JOKER -> 0
            }
        }

        val hand = game.enemyCResources.hand
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
                    if (
                        hand.count { it is CardFace && it.rank == RankFace.KING } > 1 &&
                        StrategyKingHard(index, StrategyKingHard.Direction.TO_SELF).move(game, speed)
                    ) {
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

        StrategyDropAllButFace(RankFace.JOKER).move(game, speed)
    }
}