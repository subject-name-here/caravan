package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class EnemyTutorial : Enemy() {
    override fun createDeck(): CResources = CResources(CardBack.TOPS)
    override fun getRewardDeck(): CardBack = CardBack.TOPS

    @Transient
    var update: () -> Unit = {}
    override suspend fun makeMove(game: Game) {
        update()
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val cardIndex = hand.withIndex().filter { !it.value.isFace() }.random().index
            val caravan = game.enemyCaravans.filter { it.size == 0 }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
            return
        }

        hand.withIndex().shuffled().forEach { (cardIndex, card) ->
            if (!card.rank.isFace()) {
                game.enemyCaravans.shuffled().forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 20) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }
        }

        game.enemyCResources.removeFromHand(hand.indices.random())
    }
}