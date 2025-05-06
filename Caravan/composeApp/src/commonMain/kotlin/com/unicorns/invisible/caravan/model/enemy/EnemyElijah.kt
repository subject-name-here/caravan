package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.elijah
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.GamePossibleResult
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropLadiesFirst
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingRuiner
import com.unicorns.invisible.caravan.model.enemy.strategy.checkOnResult
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardBase
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

        fun checkAnyReady(p0: Int, e0: Int): Int {
            return when {
                p0 in (21..26) && (p0 > e0 || e0 > 26) -> 1
                e0 in (21..26) && (e0 > p0 || p0 > 26) -> 1
                else -> 0
            }
        }

        val king = hand.filterIsInstance<CardFace>().find { it.rank == RankFace.KING }
        val jack = hand.filterIsInstance<CardFace>().find { it.rank == RankFace.JACK }
        val joker = hand.filterIsInstance<CardJoker>().firstOrNull()

        // 1. Check if adding king will cause victory.

        if (king != null) {
            val kingIndex = hand.indexOf(king)
            game.enemyCaravans.withIndex().shuffled().forEach { (caravanIndex, enemyCaravan) ->
                enemyCaravan.cards.forEach { card ->
                    val futureValue = enemyCaravan.getValue() + card.getValue()
                    val playerValue = game.playerCaravans[caravanIndex].getValue()
                    if (
                        card.canAddModifier(king) &&
                        checkOnResult(game, caravanIndex) in listOf(
                            GamePossibleResult.GAME_ON,
                            GamePossibleResult.ENEMY_VICTORY_IS_POSSIBLE,
                            GamePossibleResult.IMMINENT_ENEMY_VICTORY
                        ) && futureValue in (21..26) && (futureValue > playerValue || playerValue > 26)
                    ) {
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
                    val futureValue = caravan.getValue() + card.rank.value
                    val playerValue = game.playerCaravans[caravanIndex].getValue()
                    if (
                        caravan.canPutCardOnTop(card) &&
                        checkOnResult(game, caravanIndex) in listOf(
                            GamePossibleResult.GAME_ON,
                            GamePossibleResult.ENEMY_VICTORY_IS_POSSIBLE,
                            GamePossibleResult.IMMINENT_ENEMY_VICTORY
                        ) && futureValue in (21..26) && (futureValue > playerValue || playerValue > 26)
                    ) {
                        caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
                        return
                    }
                }
        }

        val needsRuiner = (0..2)
            .sumOf { checkAnyReady(
                game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue()
            ) } >= 2
        if (needsRuiner && king != null) {
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
                    val res = checkOnResult(game, caravanIndex) == GamePossibleResult.IMMINENT_PLAYER_VICTORY
                    if (ten != null && !(res && enemyCaravan.getValue() == 16)
                    ) {
                        ten.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                        return
                    }
                }
            }
        }

        hand.filterIsInstance<CardBase>().forEach { card ->
            val cardIndex = hand.indexOf(card)
            game.enemyCaravans.withIndex().sortedByDescending { it.value.getValue() }
                .forEach { (caravanIndex, caravan) ->
                    if (caravan.size < 2 && caravan.getValue() + card.rank.value <= 26 && caravan.canPutCardOnTop(card)) {
                        if (!(checkOnResult(game, caravanIndex) == GamePossibleResult.IMMINENT_PLAYER_VICTORY && caravan.getValue() + card.rank.value in (21..26))) {
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