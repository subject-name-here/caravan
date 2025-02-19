package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


class EnemyTutorial : Enemy {
    override fun createDeck(): CResources = CResources(CardBack.STANDARD, true)

    var onRemoveFromHand: () -> Unit = {}

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val cardIndex = hand.withIndex().filter { !it.value.isModifier() }.random().index
            val caravan = game.enemyCaravans.find { it.isEmpty() }!!
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
            onRemoveFromHand()
            return
        }

        hand.withIndex().shuffled().forEach { (cardIndex, card) ->
            if (!card.rank.isFace()) {
                game.enemyCaravans.shuffled().forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 20) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            onRemoveFromHand()
                            return
                        }
                    }
                }
            }
        }

        game.enemyCResources.dropCardFromHand(hand.indices.random())
        onRemoveFromHand()
    }
}