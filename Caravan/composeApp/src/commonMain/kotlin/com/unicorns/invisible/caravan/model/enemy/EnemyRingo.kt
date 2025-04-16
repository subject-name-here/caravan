package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.ringo
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToSelfSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingToSelfSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueenToSelfSimple
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import kotlinx.serialization.Serializable
import kotlin.math.min
import kotlin.random.Random


@Serializable
class EnemyRingo : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.ringo
    override val isEven
        get() = true
    override val isAvailable: Int
        get() = 1

    override fun createDeck(): CResources {
        return CResources(CustomDeck(CardBack.TOPS))
    }

    override val maxBets: Int
        get() = 1
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 30

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM).move(game, speed)
            return
        }

        val baseCards = game.enemyCResources.hand.filterIsInstance<CardBase>().shuffled()
        val caravans = game.enemyCaravans.shuffled()
        baseCards.forEach { card ->
            caravans.forEach { caravan ->
                if (Random.nextBoolean() && caravan.canPutCardOnTop(card) && caravan.getValue() + card.rank.value <= 26) {
                    val index = game.enemyCResources.hand.indexOf(card)
                    caravan.putCardOnTop(game.enemyCResources.removeFromHand(index, speed) as CardBase, speed)
                    return
                }
            }
        }

        val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().shuffled()
        modifiers.forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            when (modifier.rank) {
                RankFace.JACK -> {
                    if (Random.nextBoolean() && StrategyJackToSelfSimple(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.QUEEN -> {
                    if (Random.nextBoolean() && StrategyQueenToSelfSimple(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.KING -> {
                    if (Random.nextBoolean() && StrategyKingToSelfSimple(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.JOKER -> {
                    if (Random.nextBoolean()) {
                        val cards = game.enemyCaravans.flatMap { it.cards }.shuffled()
                        if (cards.isNotEmpty()) {
                            cards.random().addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                            return
                        }
                    }
                }
            }
        }

        game.enemyCaravans.forEach { caravan ->
            if (Random.nextBoolean() && caravan.getValue() > 26) {
                caravan.dropCaravan(speed)
                return
            }
        }

        game.enemyCResources.dropCardFromHand(game.enemyCResources.hand.indices.random(), speed)
    }
}