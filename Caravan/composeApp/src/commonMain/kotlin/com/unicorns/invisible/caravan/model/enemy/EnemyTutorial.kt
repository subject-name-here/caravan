package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardBase


class EnemyTutorial : Enemy {
    override fun createDeck(): CResources = CResources(CardBack.STANDARD_UNCOMMON)

    var onRemoveFromHand: () -> Unit = {}

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val cardIndex = hand.withIndex().filter { it.value is CardBase }.random().index
            val caravan = game.enemyCaravans.find { it.isEmpty() }!!
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
            onRemoveFromHand()
            return
        }

        hand.withIndex().shuffled().forEach { (cardIndex, card) ->
            if (card is CardBase) {
                game.enemyCaravans.shuffled().forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 20) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
                            onRemoveFromHand()
                            return
                        }
                    }
                }
            }
        }

        game.enemyCResources.dropCardFromHand(hand.indices.random(), speed)
        onRemoveFromHand()
    }
}