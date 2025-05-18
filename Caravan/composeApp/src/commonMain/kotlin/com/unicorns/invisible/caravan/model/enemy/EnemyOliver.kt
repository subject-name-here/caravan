package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.pve_enemy_oliver_real
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class EnemyOliver : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.pve_enemy_oliver_real
    override val isEven
        get() = true
    override val level: Int
        get() = 1
    override val isAvailable: Boolean
        get() = true

    override fun createDeck() = CResources(CardBack.STANDARD)


    override val maxBets: Int
        get() = 1
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

        val cards = game.enemyCResources.hand.withIndex()
            .filter { it.value is CardBase }
            .shuffled()
        val caravans = game.enemyCaravans.shuffled()
        cards.forEach { card ->
            caravans.forEach { caravan ->
                if (caravan.canPutCardOnTop(card.value as CardBase) && Random.nextBoolean()) {
                    caravan.putCardOnTop(game.enemyCResources.removeFromHand(card.index, speed) as CardBase, speed)
                    return
                }
            }
        }

        if (Random.nextBoolean()) {
            val modifiers = game.enemyCResources.hand.withIndex()
                .filter { it.value is CardModifier }
                .shuffled()
            val cards2 = (game.playerCaravans + game.enemyCaravans).flatMap { it.cards }.shuffled()
            cards2.forEach { card ->
                modifiers.forEach { modifier ->
                    if (card.canAddModifier(modifier.value as CardModifier) && Random.nextBoolean()) {
                        card.addModifier(game.enemyCResources.removeFromHand(modifier.index, speed) as CardModifier, speed)
                        return
                    }
                }
            }
        }

        if (Random.nextBoolean()) {
            game.enemyCaravans.shuffled().forEach { caravan ->
                if (!caravan.isEmpty() && Random.nextBoolean()) {
                    caravan.dropCaravan(speed)
                    return
                }
            }
        }

        game.enemyCResources.dropCardFromHand(game.enemyCResources.hand.indices.random(), speed)
    }
}