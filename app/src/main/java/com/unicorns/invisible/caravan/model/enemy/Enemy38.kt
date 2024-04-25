package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Deck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable


@Serializable
data object Enemy38 : Enemy() {
    override fun createDeck(): Deck = Deck(CardBack.LUCKY_38).apply {
        Rank.entries.forEach { rank ->
            if (rank.value < 6) {
                Suit.entries.forEach { suit ->
                    removeFromDeck(rank, suit)
                }
            }
        }
    }
    override suspend fun makeMove(game: Game) {
        val deck = game.enemyDeck
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val playersReadyCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }

        if (game.isInitStage()) {
            val card = deck.hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.shuffled().filter { it.cards.isEmpty() }.random()
            caravan.putCardOnTop(card)
            deck.hand.remove(card)
            return
        }

        deck.hand.sortedBy { -it.rank.value }.forEach { card ->
            if (card.rank == Rank.KING) {
                if (playersReadyCaravans.isNotEmpty()) {
                    val caravanToOverburden = playersReadyCaravans.maxBy { it.getValue() }
                    val cardToKing = caravanToOverburden.cards.filter { caravanToOverburden.getValue() + it.getValue() > 26 }.maxByOrNull { it.getValue() }
                    if (cardToKing != null) {
                        cardToKing.modifiers.add(card)
                        deck.hand.remove(card)
                        return
                    }
                }
                game.enemyCaravans.forEach { enemyCaravan ->
                    enemyCaravan.cards.sortedBy { -it.card.rank.value }.forEach { caravanCard ->
                        if (enemyCaravan.getValue() + caravanCard.getValue() in (21..26)) {
                            caravanCard.modifiers.add(card)
                            deck.hand.remove(card)
                            return
                        }
                    }
                }
            }

            if (card.rank == Rank.JACK) {
                if (playersReadyCaravans.isNotEmpty()) {
                    val caravanToAttack = playersReadyCaravans.random()
                    val cardToJack = caravanToAttack.cards.maxByOrNull { it.getValue() }
                    if (cardToJack != null) {
                        cardToJack.modifiers.add(card)
                        deck.hand.remove(card)
                        return
                    }
                }

                if (overWeightCaravans.isNotEmpty()) {
                    val enemyCaravan = overWeightCaravans.random()
                    val cardToDelete = enemyCaravan.cards.maxBy { it.getValue() }
                    cardToDelete.modifiers.add(card)
                    deck.hand.remove(card)
                    return
                }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedBy { -it.getValue() }.forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.putCardOnTop(card)) {
                            deck.hand.remove(card)
                            return
                        }
                    }
                }
            }

            if (card.rank == Rank.JOKER) {
                // TODO
                deck.hand.remove(card)
                return
            }
            if (card.rank == Rank.QUEEN) {
                // TODO
                deck.hand.remove(card)
                return
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.maxBy { it.getValue() }.dropCaravan()
            return
        }

        deck.hand.removeAt(deck.hand.indices.random())
    }
}