package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToSelfSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingToSelfSimple
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardAtomic
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFBomb
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.RankFace
import kotlinx.serialization.Serializable
import kotlin.math.min
import kotlin.random.Random


@Serializable
class EnemyVeronica : EnemyPvEWithBank() {
    override val nameId
        get() = R.string.pve_enemy_veronica
    override val isEven
        get() = true

    override fun createDeck() = CResources(CardBack.ULTRA_LUXE, 0)

    override var bank: Int = 0
    override val maxBank: Int
        get() = 30
    override val bet: Int
        get() = if (bank == 0) 0 else min(bank, 15)

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM).move(game, speed)
            return
        }

        val baseCards = game.enemyCResources.hand.filterIsInstance<CardBase>()
        val caravans = game.enemyCaravans.shuffled()
        baseCards.forEach { card ->
            caravans.forEach { caravan ->
                if (caravan.canPutCardOnTop(card) && caravan.getValue() + card.rank.value <= 26) {
                    val index = game.enemyCResources.hand.indexOf(card)
                    caravan.putCardOnTop(game.enemyCResources.removeFromHand(index, speed) as CardBase, speed)
                    return
                }
            }
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
                        val cards = game.enemyCaravans.flatMap { it.cards }.shuffled()
                        if (cards.isNotEmpty()) {
                            cards.random().addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                        }
                        return
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