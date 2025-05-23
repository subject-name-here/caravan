package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.snuffles
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardAtomic
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardNuclear
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.WWType
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class EnemySnuffles : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.snuffles
    override val isEven
        get() = false
    override val level: Int
        get() = 1
    override val isAvailable: Boolean
        get() = true

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(CardBack.LUCKY_38, CardBack.GOMORRAH, CardBack.ULTRA_LUXE, CardBack.TOPS).forEach { back ->
            addAll(CollectibleDeck(back))
        }
        repeat(4) { add(CardAtomic()) }
        WWType.entries.forEach { ww -> repeat(2) { add(CardWildWasteland(ww)) } }
    })


    override val maxBets: Int
        get() = 0
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 5

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            val card = game.enemyCResources.hand.withIndex().filter { it.value is CardBase }.random()
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(card.index, speed) as CardBase, speed)
            return
        }
        val crazys = game.enemyCResources.hand.withIndex()
            .filter { it.value is CardWildWasteland || it.value is CardNuclear }
            .shuffled()
        val cardsAll = (game.playerCaravans + game.enemyCaravans).flatMap { it.cards }.shuffled()
        cardsAll.forEach { card ->
            crazys.forEach { modifier ->
                if (card.canAddModifier(modifier.value as CardModifier) && Random.nextBoolean()) {
                    card.addModifier(game.enemyCResources.removeFromHand(modifier.index, speed) as CardModifier, speed)
                    return
                }
            }
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