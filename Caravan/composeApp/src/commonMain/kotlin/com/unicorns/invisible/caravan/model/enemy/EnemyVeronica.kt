package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.pve_enemy_veronica
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToSelfSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingToSelfSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersSimple
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.RankFace
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class EnemyVeronica : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.pve_enemy_veronica
    override val isEven
        get() = true
    override val level: Int
        get() = 2
    override val isAvailable: Boolean
        get() = true

    override fun createDeck() = CResources(CardBack.ULTRA_LUXE)

    override val maxBets: Int
        get() = 2
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 15

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM).move(game, speed)
            return
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
            if (caravan.getValue() > 26) {
                caravan.dropCaravan(speed)
                return
            }
        }

        game.enemyCResources.dropCardFromHand(game.enemyCResources.hand.indices.random(), speed)
    }
}