package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank


data object EnemyHard : Enemy() {
    override suspend fun makeMove(game: Game) {
        val deck = game.enemyDeck
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }

        if (game.isInitStage()) {
            val card = deck.hand.filter { !it.isFace() }.random()
            val caravan = game.enemyCaravans.shuffled().filter { it.cards.isEmpty() }.random()
            caravan.putCardOnTop(card)
            deck.hand.remove(card)
            return
        }

        deck.hand.shuffled().forEach { card ->
            if (card.rank == Rank.JACK) {
                val caravan = game.playerCaravans.filter { it.getValue() in (1..26) }.maxByOrNull { it.getValue() }
                if (caravan != null) {
                    caravan.cards.maxBy { it.getValue() }.modifiers.add(card)
                    deck.hand.remove(card)
                    return
                }

                if (overWeightCaravans.isNotEmpty()) {
                    val enemyCaravan = overWeightCaravans.random()
                    val cardToDelete = enemyCaravan.cards.maxBy { it.getValue() }
                    cardToDelete.modifiers.add(card)
                    deck.hand.remove(card)
                    return
                }
            }
            if (card.rank == Rank.KING) {
                val caravan = game.playerCaravans.filter { it.getValue() in (21..26) }.randomOrNull()
                if (caravan != null) {
                    val cardsToKing = caravan.cards.filter { it.card.rank.value > 5 }.maxByOrNull { it.card.rank.value }
                    if (cardsToKing != null) {
                        cardsToKing.modifiers.add(card)
                        deck.hand.remove(card)
                        return
                    }
                }

                game.enemyCaravans.filter { it.getValue() in (1..25) }.forEach { enemyCaravan ->
                    enemyCaravan.cards.sortedBy { -it.card.rank.value }.forEach { caravanCard ->
                        if (enemyCaravan.getValue() + caravanCard.card.rank.value in (16..26)) {
                            caravanCard.modifiers.add(card)
                            deck.hand.remove(card)
                            return
                        }
                    }
                }
            }

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
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        val uselessCards = deck.hand.filter { it.rank == Rank.QUEEN || it.rank == Rank.JOKER }
        if (uselessCards.isNotEmpty()) {
            deck.hand.remove(uselessCards.random())
        } else {
            deck.hand.removeAt(deck.hand.indices.random())
        }
    }
}