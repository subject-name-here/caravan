package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable


@Serializable
data object EnemyEasy : Enemy() {
    override fun createDeck(): CResources = CResources(CardBack.ULTRA_LUXE)
    override fun getRewardDeck(): CardBack = CardBack.ULTRA_LUXE

    override suspend fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val cardIndex = hand.withIndex().filter { !it.value.isFace() }.random().index
            val caravan = game.enemyCaravans.first { it.size == 0 }
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
            return
        }

        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        hand.withIndex().shuffled().forEach { (cardIndex, card) ->
            if (!card.rank.isFace()) {
                game.enemyCaravans.shuffled().forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }
            if (card.rank == Rank.JACK) {
                val caravan = game.playerCaravans.filter { it.getValue() in (21..26) }.randomOrNull()
                if (caravan != null) {
                    caravan.cards.maxBy { it.getValue() }
                        .addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }
            if (card.rank == Rank.KING) {
                val caravan = game.playerCaravans.filter { it.getValue() >= 21 }.randomOrNull()
                if (caravan != null) {
                    val cardsToKing = caravan.cards.maxByOrNull { it.card.rank.value }
                    if (cardsToKing != null) {
                        cardsToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
        }

        game.enemyCResources.removeFromHand(hand.indices.random())
    }
}