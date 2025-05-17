package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.dr_mobius
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropLadiesFirst
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimpleOnPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueenToSelf
import com.unicorns.invisible.caravan.model.enemy.strategy.checkIfPlayerVictoryIsClose
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
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class EnemyDrMobius : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.dr_mobius
    override val isEven: Boolean
        get() = false
    override val level: Int
        get() = 4
    override val isAvailable: Boolean
        get() = true

    override fun createDeck() = CResources(CustomDeck().apply {
        repeat(3) {
            add(generateCardFace())
        }
        repeat(5) {
            add(generateCardNumber())
        }
    })

    override val maxBets: Int
        get() = 8
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 11

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        makeMoveInner(game, speed)
        if (game.enemyCResources.hand.size < 5) {
            game.enemyCResources.addOnTop(if (Random.nextInt(14) < 10) generateCardNumber() else generateCardFace())
        }
    }

    private suspend fun makeMoveInner(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM).move(game, speed)
            return
        }

        if (checkIfPlayerVictoryIsClose(gameToState(game))) {
            val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().shuffled()

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
        }

        if (StrategyPutNumbersHard().move(game, speed)) {
            return
        }

        game.enemyCResources.hand.filterIsInstance<CardFace>().shuffled().forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            when (modifier.rank) {
                RankFace.JACK -> {
                    if (StrategyJackHard(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.QUEEN -> {
                    if (StrategyQueenToSelf(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.KING -> {
                    if (StrategyKingHard(index).move(game, speed)) {
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

        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.maxBy { it.getValue() }.dropCaravan(speed)
            return
        }

        StrategyDropLadiesFirst().move(game, speed)
    }

    fun generateCardFace(): CardFace {
        val rank = RankFace.entries.random()
        return if (rank == RankFace.JOKER) {
            CardJoker(CardJoker.Number.entries.random(), generateBack())
        } else {
            CardFaceSuited(rank, Suit.entries.random(), generateBack())
        }
    }

    fun generateCardNumber(): CardNumber {
        val rank = RankNumber.entries.random()
        return CardNumber(rank, Suit.entries.random(), generateBack())
    }

    fun generateBack() = listOf(
        CardBack.STANDARD,
        CardBack.STANDARD_UNCOMMON,
        CardBack.STANDARD_RARE,
        CardBack.STANDARD_MYTHIC,
    ).random()
}