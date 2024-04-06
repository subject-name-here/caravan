package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable


@Serializable
data object EnemyEasy : Enemy() {
    override suspend fun makeMove(game: Game) {
        val deck = game.enemyDeck

        deck.hand.forEach { card ->
            if (card.rank.value <= 10) {
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
                val caravan = game.playerCaravans.filter { it.getValue() > 0 }.randomOrNull()
                if (caravan != null) {
                    caravan.cards.removeAt(caravan.cards.indices.random())
                    deck.hand.remove(card)
                    return
                }
            }
        }

        deck.hand.removeAt(deck.hand.indices.random())
    }
}