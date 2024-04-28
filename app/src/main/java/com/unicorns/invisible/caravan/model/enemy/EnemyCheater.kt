package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Deck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable


@Serializable
data object EnemyCheater : Enemy() {
    override fun createDeck(): Deck = Deck(CustomDeck().apply {
        CardBack.entries.forEach { back ->
            if (back == CardBack.STANDARD) {
                return@forEach
            }
            Suit.entries.forEach { suit ->
                add(Card(Rank.SIX, suit, back))
                add(Card(Rank.EIGHT, suit, back))
                add(Card(Rank.TEN, suit, back))
                add(Card(Rank.KING, suit, back))
                add(Card(Rank.JACK, suit, back))
            }
        }
    })

    override fun getRewardDeck(): CardBack = CardBack.SIERRA_MADRE

    override suspend fun makeMove(game: Game) {
        val deck = game.enemyDeck
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }

        if (game.isInitStage()) {
            val card = deck.hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.shuffled().filter { it.cards.isEmpty() }.random()
            caravan.putCardOnTop(card)
            deck.hand.remove(card)
            return
        }

        deck.hand.sortedBy { -it.rank.value }.forEach { card ->
            if (card.rank == Rank.KING) {
                game.enemyCaravans.forEach { enemyCaravan ->
                    if (enemyCaravan.getValue() !in (21..26) && enemyCaravan.getValue() > 0) {
                        if (enemyCaravan.cards.size == 1 && enemyCaravan.cards.first().card.rank.value != 6) {
                            enemyCaravan.cards.first().modifiers.add(card)
                            deck.hand.remove(card)
                            return
                        } else if (enemyCaravan.cards.size == 2) {
                            val eight = enemyCaravan.cards.find { it.card.rank == Rank.EIGHT }
                            if (eight != null && eight.getValue() == 8) {
                                eight.modifiers.add(card)
                                deck.hand.remove(card)
                                return
                            } else {
                                val cardToKing = enemyCaravan.cards.maxByOrNull { it.card.rank.value }
                                if (cardToKing != null && cardToKing.getValue() <= 10) {
                                    cardToKing.modifiers.add(card)
                                    deck.hand.remove(card)
                                    return
                                }
                            }
                        }
                    }
                }
            }

            if (card.rank == Rank.JACK) {
                val caravanToAttack = game.playerCaravans.filter { it.getValue() <= 26 }.maxByOrNull { it.getValue() }
                val cardToJack = caravanToAttack?.cards?.maxByOrNull { it.getValue() }
                if (cardToJack != null) {
                    cardToJack.modifiers.add(card)
                    deck.hand.remove(card)
                    return
                }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedBy { -it.getValue() }.forEach { caravan ->
                    if (caravan.cards.size < 2 && caravan.getValue() + card.rank.value <= 26) {
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

        deck.hand.removeAt(deck.hand.indices.random())
    }
}