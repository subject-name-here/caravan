package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.elijah
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropLadiesFirst
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingRuiner
import com.unicorns.invisible.caravan.model.enemy.strategy.checkOnResult
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class EnemyElijah : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.elijah
    override val isEven
        get() = false
    override val level: Int
        get() = 3
    override val isAvailable: Boolean
        get() = true

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(
            CardBack.STANDARD_MYTHIC,
            CardBack.STANDARD,
            CardBack.SIERRA_MADRE_DIRTY
        ).forEach { back ->
            Suit.entries.forEach { suit ->
                add(CardNumber(RankNumber.SIX, suit, back))
                add(CardNumber(RankNumber.TEN, suit, back))
                add(CardFaceSuited(RankFace.KING, suit, back))
            }
        }
    })

    override val maxBets: Int
        get() = 6
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 10

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        val hand = game.enemyCResources.hand
        val under26Caravans = game.enemyCaravans.filterIndexed { index, it ->
            it.getValue() in (21..25) && game.playerCaravans[index].getValue() >= it.getValue()
        }
        val overweightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }

        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.MAX_FIRST_TO_RANDOM).move(game, speed)
            return
        }

        val king = hand.filterIsInstance<CardFace>().find { it.rank == RankFace.KING }

        // 1. Check if adding king will cause victory.
        if (king != null) {
            val kingIndex = hand.indexOf(king)
            game.enemyCaravans.withIndex().shuffled().forEach { (caravanIndex, enemyCaravan) ->
                enemyCaravan.cards.forEach { card ->
                    val state = gameToState(game)
                    state.enemy[caravanIndex] += card.getValue()
                    if (card.canAddModifier(king) && checkTheOutcome(state) == -1) {
                        card.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardModifier, speed)
                        return
                    }
                }
            }
        }

        // Check if adding base card will cause victory
        hand.filterIsInstance<CardBase>().forEach { card ->
            val cardIndex = hand.indexOf(card)
            game.enemyCaravans.withIndex().sortedByDescending { it.value.getValue() }
                .forEach { (caravanIndex, caravan) ->
                    val state = gameToState(game)
                    state.enemy[caravanIndex] += card.rank.value
                    if (caravan.canPutCardOnTop(card) && checkTheOutcome(state) == -1) {
                        caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
                        return
                    }
                }
        }

        if (checkOnResult(gameToState(game)).isPlayerMoveWins() && king != null) {
            val index = hand.indexOf(king)
            if (StrategyKingRuiner(index).move(game, speed)) {
                return
            }
        }

        if (king != null) {
            val index = hand.indexOf(king)
            game.enemyCaravans.withIndex().shuffled().forEach { (caravanIndex, enemyCaravan) ->
                if (enemyCaravan.getValue() in listOf(10, 16)) {
                    val ten = enemyCaravan.cards.find { it.card.rank == RankNumber.TEN && it.getValue() == 10 && it.canAddModifier(king) }
                    if (ten != null) {
                        val state = gameToState(game)
                        state.enemy[caravanIndex] += ten.getValue()
                        if (!checkOnResult(state, caravanIndex).isPlayerMoveWins()) {
                            ten.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                            return
                        }
                    }
                }
            }
        }

        hand.filterIsInstance<CardBase>().forEach { card ->
            val cardIndex = hand.indexOf(card)
            game.enemyCaravans.withIndex().sortedByDescending { it.value.getValue() }
                .forEach { (caravanIndex, caravan) ->
                    if (caravan.size < 2 && caravan.getValue() + card.rank.value <= 26 && caravan.canPutCardOnTop(card)) {
                        val state = gameToState(game)
                        state.enemy[caravanIndex] += card.rank.value
                        if (!(checkOnResult(state, caravanIndex).isPlayerMoveWins())) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
                            return
                        }
                    }
                }
        }

        if (Random.nextBoolean() && king != null) {
            val index = hand.indexOf(king)
            if (StrategyKingRuiner(index).move(game, speed)) {
                return
            }
        }

        if (overweightCaravans.isNotEmpty()) {
            overweightCaravans.random().dropCaravan(speed)
            return
        }
        if (Random.nextBoolean() && under26Caravans.isNotEmpty()) {
            under26Caravans.minBy { it.getValue() }.dropCaravan(speed)
            return
        }

        StrategyDropLadiesFirst().move(game, speed)
    }
}