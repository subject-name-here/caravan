package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable


@Serializable
data object EnemyEasy : Enemy() {
    override suspend fun makeMove(game: Game) {
        val deck = game.enemyDeck

        if (game.isInitStage()) {
            val card = deck.hand.filter { !it.isFace() }.random()
            val caravan = game.enemyCaravans.shuffled().filter { it.cards.isEmpty() }.random()
            caravan.putCardOnTop(card)
            deck.hand.remove(card)
            return
        }

        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        deck.hand.shuffled().forEach { card ->
            if (!card.rank.isFace()) {
                game.enemyCaravans.shuffled().forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.putCardOnTop(card)) {
                            deck.hand.remove(card)
                            return
                        }
                    }
                }
            }
            if (card.rank == Rank.JACK) {
                val caravan = game.playerCaravans.filter { it.getValue() in (13..26) }.randomOrNull()
                if (caravan != null) {
                    caravan.cards.maxBy { it.getValue() }.modifiers.add(card)
                    deck.hand.remove(card)
                    return
                }
            }
            if (card.rank == Rank.KING) {
                val caravan = game.playerCaravans.filter { it.getValue() >= 21 }.randomOrNull()
                if (caravan != null) {
                    val cardsToKing = caravan.cards.filter { it.card.rank.value > 5 }.maxByOrNull { it.card.rank.value }
                    if (cardsToKing != null) {
                        cardsToKing.modifiers.add(card)
                        deck.hand.remove(card)
                        return
                    }
                }
            }
        }

        deck.hand.removeAt(deck.hand.indices.random())
    }
}