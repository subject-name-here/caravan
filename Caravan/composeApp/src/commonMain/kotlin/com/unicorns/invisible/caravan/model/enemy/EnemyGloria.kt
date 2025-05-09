package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.gloria_van_graff
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropAllButFace
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToSelfMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSuperSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingHard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingToSelfMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueenToSelf
import com.unicorns.invisible.caravan.model.enemy.strategy.checkIfPlayerVictoryIsClose
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable


@Serializable
class EnemyGloria : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.gloria_van_graff
    override val isEven
        get() = false
    override val level: Int
        get() = 4
    override val isAvailable: Boolean
        get() = true

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        val backs = listOf(
            CardBack.ULTRA_LUXE_CRIME,
            CardBack.GOMORRAH_DARK,
            CardBack.LEGION,
            CardBack.NCR
        )
        backs.forEach { back ->
            RankNumber.entries.forEach { rank ->
                add(CardNumber(rank, Suit.SPADES, back))
            }
            add(CardFaceSuited(RankFace.JACK, Suit.SPADES, back))
            add(CardFaceSuited(RankFace.QUEEN, Suit.SPADES, back))
            add(CardJoker(CardJoker.Number.ONE, back))
            add(CardJoker(CardJoker.Number.TWO, back))
        }
        (backs + listOf(CardBack.TOPS_RED, CardBack.LUCKY_38_SPECIAL)).forEach { back ->
            Suit.entries.forEach { suit ->
                add(CardFaceSuited(RankFace.KING, suit, back))
            }
        }
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
            StrategyInit(StrategyInit.Type.MAX_FIRST_TO_RANDOM).move(game, speed)
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
                        if (StrategyKingHard(index).move(game, speed)) {
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

        val king = game.enemyCResources.hand.find { it is CardFace && it.rank == RankFace.KING }
        if (king != null && king is CardModifier) {
            val kingIndex = game.enemyCResources.hand.indexOf(king)
            val caravan = game.playerCaravans
                .filter {
                    it.getValue() in (21..26) && it.cards.any { card -> card.canAddModifier(king) }
                }
                .maxByOrNull { it.getValue() }
            if (caravan != null) {
                val card = caravan.cards.filter { it.canAddModifier(king) && caravan.getValue() + it.getValue() > 26 }.maxByOrNull { it.getValue() }
                if (card != null) {
                    card.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardModifier, speed)
                    return
                }
            }
        }

        if (StrategyPutNumbersMedium().move(game, speed)) {
            return
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

        StrategyDropAllButFace(RankFace.KING).move(game, speed)
    }
}