package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable


@Serializable
data object EnemyCheater : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        CardBack.entries.forEach { back ->
            Suit.entries.forEach { suit ->
                add(Card(Rank.SIX, suit, back))
                add(Card(Rank.TEN, suit, back))
                add(Card(Rank.KING, suit, back))
            }
        }
    })
    override fun getRewardDeck(): CardBack = CardBack.SIERRA_MADRE

    override suspend fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.first { it.cards.isEmpty() }
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        hand.withIndex().sortedBy { -it.value.rank.value }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.KING) {
                game.enemyCaravans.forEach { enemyCaravan ->
                    if (enemyCaravan.getValue() in listOf(10, 16)) {
                        val ten = enemyCaravan.cards.find { it.card.rank == Rank.TEN && it.getValue() == 10 }
                        if (ten != null && ten.canAddModifier(card)) {
                            ten.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedBy { -it.getValue() }.forEach { caravan ->
                    if (caravan.size < 2 && caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }

            if (card.rank == Rank.KING) {
                game.playerCaravans.forEach { playerCaravan ->
                    if (playerCaravan.getValue() in (13..26)) {
                        val cardToKing = playerCaravan.cards
                            .filter { it.getValue() + playerCaravan.getValue() > 26 }
                            .maxByOrNull { it.getValue() }
                        if (cardToKing != null && cardToKing.canAddModifier(card)) {
                            cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        game.enemyCResources.removeFromHand(hand.indices.random())
    }
}