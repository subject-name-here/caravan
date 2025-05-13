package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.pve_enemy_chief_hanlon
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropAllButFace
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimpleOnPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingRuiner
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueenToSelf
import com.unicorns.invisible.caravan.model.enemy.strategy.checkIfPlayerVictoryIsClose
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.RankFace
import kotlinx.serialization.Serializable


@Serializable
class EnemyHanlon : EnemyPvENoBank() {
    override val nameId
        get() = Res.string.pve_enemy_chief_hanlon
    override val isEven
        get() = true
    override val level: Int
        get() = 5
    override val isAvailable: Boolean
        get() = true

    override var curCards: Int = maxCards

    override var wins: Int = 0
    override var winsBlitz: Int = 0

    override fun createDeck() = CResources(CardBack.NCR)

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.MIN_FIRST_TO_RANDOM).move(game, speed)
            return
        }

        val hand = game.enemyCResources.hand
        if (checkIfPlayerVictoryIsClose(gameToState(game))) {
            val modifiers = hand.filterIsInstance<CardFace>().sortedByDescending { it.rank.value }

            modifiers.forEach { modifier ->
                val index = hand.indexOf(modifier)
                when (modifier.rank) {
                    RankFace.JACK -> {
                        if (StrategyJackHard(index, StrategyJackHard.Direction.TO_PLAYER).move(game, speed)) {
                            return
                        }
                    }
                    RankFace.KING -> {
                        if (StrategyKingRuiner(index, isHard = true).move(game, speed)) {
                            return
                        }
                    }
                    RankFace.JOKER -> {
                        if (StrategyJokerSimpleOnPlayer(index, isHard = true).move(game, speed)) {
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

        game.enemyCaravans.forEach { caravan ->
            if (caravan.getValue() > 26) {
                caravan.dropCaravan(speed)
                return
            }
        }

        val jacks = hand.filter { it is CardFace && it.rank == RankFace.JACK }
        val queens = hand.filter { it is CardFace && it.rank == RankFace.QUEEN }
        val kings = hand.filter { it is CardFace && it.rank == RankFace.KING }
        val jokers = hand.filterIsInstance<CardJoker>()
        val numbers = hand.filterIsInstance<CardBase>()
        if (kings.size > 1 || numbers.isEmpty()) {
            val kingIndex = hand.indexOf(kings.first())
            if (StrategyKingHard(kingIndex).move(game, speed)) {
                return
            }
        }
        if (jokers.size > 1 || numbers.isEmpty()) {
            val jokerIndex = hand.indexOf(jokers.first())
            if (StrategyJokerSimple(jokerIndex, isHard = true).move(game, speed)) {
                return
            }
        }
        if (jacks.size > 1 || numbers.isEmpty()) {
            val jackIndex = hand.indexOf(jacks.first())
            if (StrategyJackHard(jackIndex).move(game, speed)) {
                return
            }
        }
        if (queens.isNotEmpty()) {
            val queenIndex = hand.indexOf(queens.first())
            if (StrategyQueenToSelf(queenIndex).move(game, speed)) {
                return
            }
        }

        StrategyDropAllButFace(RankFace.JOKER).move(game, speed)
    }
}