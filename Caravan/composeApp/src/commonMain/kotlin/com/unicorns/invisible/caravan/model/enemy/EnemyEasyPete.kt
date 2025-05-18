package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.easy_pete
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToSelfSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingToSelfSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.checkOnResult
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardAtomic
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.WWType
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class EnemyEasyPete : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.easy_pete
    override val isEven: Boolean
        get() = true
    override val level: Int
        get() = 2

    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.STANDARD).apply {
        removeAll { it is CardJoker }
        add(CardAtomic())
        add(CardWildWasteland(WWType.DIFFICULT_PETE))
        add(CardWildWasteland(WWType.DIFFICULT_PETE))
    })

    override val maxBets: Int
        get() = 3
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 7
    override val isAvailable: Boolean
        get() = true

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM).move(game, speed)
            return
        }

        val hand = game.enemyCResources.hand
        if (checkOnResult(gameToState(game)).isPlayerMoveWins()) {
            val atomics = hand.filterIsInstance<CardAtomic>()
            if (atomics.isNotEmpty()) {
                val atomic = atomics.first()
                val index = hand.indexOf(atomic)
                val card = game.enemyCaravans
                    .flatMap { it.cards }
                    .firstOrNull { it.canAddModifier(atomic) }
                if (card != null) {
                    card.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                    return
                }
            }

            val dps = hand.filterIsInstance<CardWildWasteland>()
            if (dps.isNotEmpty()) {
                val dp = dps.first()
                val index = hand.indexOf(dp)
                val card = (game.enemyCaravans + game.playerCaravans).flatMap { it.cards }.firstOrNull { it.canAddModifier(dp) }
                if (card != null) {
                    card.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                    return
                }
            }
        }

        if (StrategyPutNumbersSimple().move(game, speed)) {
            return
        }

        val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>()
        modifiers.forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            when (modifier.rank) {
                RankFace.JACK -> {
                    if (StrategyJackToSelfSimple(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.QUEEN -> {
                    if (Random.nextBoolean()) {
                        game.enemyCResources.dropCardFromHand(index, speed)
                        return
                    }
                }
                RankFace.KING -> {
                    if (StrategyKingToSelfSimple(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.JOKER -> {
                    if (Random.nextBoolean()) {
                        val cards = game.enemyCaravans.flatMap { it.cards }.filter { it.canAddModifier(modifier) }.shuffled()
                        if (cards.isNotEmpty()) {
                            cards.random().addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                            return
                        }
                    }
                }
            }
        }

        game.enemyCaravans.forEach { caravan ->
            if (caravan.getValue() > 26) {
                caravan.dropCaravan(speed)
                return
            }
        }

        game.enemyCResources.dropCardFromHand(game.enemyCResources.hand.indices.random(), speed)
    }
}