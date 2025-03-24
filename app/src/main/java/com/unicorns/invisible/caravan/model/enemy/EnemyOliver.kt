package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
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
        get() = R.string.pve_enemy_oliver_real
    override val isEven
        get() = true

    override fun createDeck() = CResources(CardBack.STANDARD, 0)


    override var bank: Int = 0
    override val maxBank: Int
        get() = 15
    override val bet: Int
        get() = bank

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
            val cards = (game.playerCaravans + game.enemyCaravans).flatMap { it.cards }.shuffled()
            cards.forEach { card ->
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