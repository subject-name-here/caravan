package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.crooker
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropAllButFace
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimpleOnPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSuperSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingRuiner
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueenToSelf
import com.unicorns.invisible.caravan.model.enemy.strategy.checkIfPlayerVictoryIsClose
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import kotlinx.serialization.Serializable
import kotlin.math.abs


@Serializable
class EnemyCrooker : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.crooker
    override val isEven: Boolean
        get() = false
    override val level: Int
        get() = 3
    override val isAvailable: Boolean
        get() = true

    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.NCR).apply {
        addAll(CollectibleDeck(CardBack.VAULT_21_DAY))
        addAll(CollectibleDeck(CardBack.LUCKY_38_SPECIAL))
    })

    override val maxBets: Int
        get() = 6
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 25

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.MIN_FIRST_TO_RANDOM).move(game, speed)
            return
        }

        val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().shuffled()
        if (checkIfPlayerVictoryIsClose(gameToState(game))) {
            modifiers.forEach { modifier ->
                val index = game.enemyCResources.hand.indexOf(modifier)
                when (modifier.rank) {
                    RankFace.JACK -> {
                        if (StrategyJackToPlayer(index).move(game, speed)) {
                            return
                        }
                    }
                    RankFace.KING -> {
                        if (StrategyKingRuiner(index).move(game, speed)) {
                            return
                        }
                    }
                    RankFace.JOKER -> {
                        if (StrategyJokerSimpleOnPlayer(index).move(game, speed)) {
                            return
                        }
                    }
                    else -> {}
                }
            }
        }

        val numbers = game.enemyCResources.hand.filterIsInstance<CardBase>()
        val caravans = game.enemyCaravans
            .filter { it.getValue() < 26 }
            .sortedByDescending { it.getValue() }

        caravans.forEachIndexed { indexC, caravan ->
            numbers.shuffled().forEach { card ->
                if (caravan.canPutCardOnTop(card) && caravan.getValue() + card.rank.value <= 26) {
                    val state = gameToState(game)
                    when (indexC) {
                        0 -> state.enemy.v1 += card.rank.value
                        1 -> state.enemy.v2 += card.rank.value
                        2 -> state.enemy.v3 += card.rank.value
                    }

                    suspend fun putCard() {
                        val index = game.enemyCResources.hand.indexOf(card)
                        caravan.putCardOnTop(game.enemyCResources.removeFromHand(index, speed) as CardBase, speed)
                    }

                    if (checkTheOutcome(state) != 1 && !checkIfPlayerVictoryIsClose(state)) {
                        if (caravan.isEmpty() || caravan.getValue() + card.rank.value in (21..26)) {
                            putCard()
                            return
                        } else {
                            val last = caravan.cards.last().card
                            if (abs(card.rank.value - last.rank.value) <= 2) {
                                putCard()
                                return
                            }
                        }
                    }
                }
            }
        }

        modifiers.forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            when (modifier.rank) {
                RankFace.JACK -> {
                    if (StrategyJackToPlayer(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.QUEEN -> {
                    if (StrategyQueenToSelf(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.KING -> {
                    if (StrategyKingToPlayer(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.JOKER -> {
                    if (StrategyJokerSuperSimple(index).move(game, speed)) {
                        return
                    }
                }
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