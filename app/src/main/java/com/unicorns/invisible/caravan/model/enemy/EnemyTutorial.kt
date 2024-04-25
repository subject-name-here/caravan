package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Deck
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class EnemyTutorial : Enemy() {
    override fun createDeck(): Deck = Deck(CardBack.TOPS)
    @Transient
    var update: () -> Unit = {}
    override suspend fun makeMove(game: Game) {
        update()
        val deck = game.enemyDeck

        if (game.isInitStage()) {
            val card = deck.hand.filter { !it.isFace() }.random()
            val caravan = game.enemyCaravans.shuffled().filter { it.cards.isEmpty() }.random()
            caravan.putCardOnTop(card)
            deck.hand.remove(card)
            return
        }

        deck.hand.shuffled().forEach { card ->
            if (!card.rank.isFace()) {
                game.enemyCaravans.shuffled().forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 20) {
                        if (caravan.putCardOnTop(card)) {
                            deck.hand.remove(card)
                            return
                        }
                    }
                }
            }
        }

        deck.hand.removeAt(deck.hand.indices.random())
    }
}