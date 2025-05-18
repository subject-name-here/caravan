package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.fisto
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimpleOnPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardNuclear
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.WWType
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class EnemyFisto : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.fisto
    override val isEven
        get() = false
    override val level: Int
        get() = 2
    override val isAvailable: Boolean
        get() = true

    override fun createDeck(): CResources {
        return CResources(CustomDeck(CardBack.GOMORRAH).apply {
            listOf(
                CardBack.VAULT_21_DAY,
                CardBack.TOPS,
                CardBack.LUCKY_38,
                CardBack.ULTRA_LUXE,
                CardBack.STANDARD
            ).forEach { back ->
                add(CardJoker(CardJoker.Number.ONE, back))
                add(CardJoker(CardJoker.Number.TWO, back))
            }

            removeAll { it is CardFaceSuited }
            removeAll { it is CardBase && it.rank.value < 5 }

            repeat(5) {
                add(CardWildWasteland(WWType.YES_MAN))
                add(CardWildWasteland(WWType.MUGGY))
            }
        })
    }

    override val maxBets: Int
        get() = 3
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 10

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM).move(game, speed)
            return
        }

        val wws = game.enemyCResources.hand.filterIsInstance<CardWildWasteland>().shuffled()
        wws.forEach { ww ->
            val index = game.enemyCResources.hand.indexOf(ww)
            when (ww.wwType) {
                WWType.YES_MAN -> {
                    game.enemyCaravans.shuffled().forEach { caravan ->
                        if (caravan.getValue() != 26 && !caravan.isEmpty()) {
                            if (caravan.getValue() !in (21..26) || Random.nextBoolean()) {
                                val card = caravan.cards.find { it.canAddModifier(ww) }
                                if (card != null) {
                                    val state = gameToState(game)
                                    val indexC = game.enemyCaravans.indexOf(caravan)
                                    state.enemy[indexC] = 26
                                    if (checkTheOutcome(state) != 1) {
                                        card.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                                        return
                                    }
                                }
                            }
                        }
                    }
                }
                WWType.MUGGY -> {
                    game.enemyCaravans.shuffled().forEach { caravan ->
                        if (!caravan.isEmpty() && !caravan.cards.any { it.hasActiveMuggy }) {
                            if (caravan.cards.any { it.hasActiveYesMan }) {
                                val card = caravan.cards.find { it.canAddModifier(ww) }
                                if (card != null) {
                                    card.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                                    return
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }

        val jokers = game.enemyCResources.hand.filterIsInstance<CardJoker>()
        jokers.forEach { joker ->
            val index = game.enemyCResources.hand.indexOf(joker)
            if (Random.nextBoolean()) {
                if (StrategyJokerSimpleOnPlayer(index).move(game, speed)) {
                    return
                }
            }
        }

        if (StrategyPutNumbersSimple().move(game, speed)) {
            return
        }

        game.enemyCaravans.forEach { caravan ->
            if (caravan.getValue() > 26) {
                caravan.dropCaravan(speed)
                return
            }
        }

        game.enemyCResources.dropCardFromHand(game.enemyCResources.hand.withIndex().minBy {
            when (val card = it.value) {
                is CardBase -> card.rank.value
                is CardFace -> card.rank.value
                is CardNuclear -> 15
                is CardWildWasteland -> 16
            }
        }.index, speed)
    }
}