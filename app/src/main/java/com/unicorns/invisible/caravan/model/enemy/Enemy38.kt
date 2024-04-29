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
data object Enemy38 : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        Rank.entries.filter { it.value >= 6 }.forEach { rank ->
            if (rank == Rank.JOKER) {
                add(Card(rank, Suit.HEARTS, CardBack.LUCKY_38))
                add(Card(rank, Suit.SPADES, CardBack.LUCKY_38))
            } else {
                Suit.entries.forEach { suit ->
                    add(Card(rank, suit, CardBack.LUCKY_38))
                }
            }
        }
    })
    override fun getRewardDeck(): CardBack = CardBack.LUCKY_38

    override suspend fun makeMove(game: Game) {
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val playersReadyCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.shuffled().filter { it.cards.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        hand.withIndex().sortedBy { -it.value.rank.value }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.KING) {
                if (playersReadyCaravans.isNotEmpty()) {
                    val caravanToOverburden = playersReadyCaravans.maxBy { it.getValue() }
                    val cardToKing = caravanToOverburden.cards.filter { caravanToOverburden.getValue() + it.getValue() > 26 }.maxByOrNull { it.getValue() }
                    if (cardToKing != null) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
                game.enemyCaravans.forEach { enemyCaravan ->
                    enemyCaravan.cards.sortedBy { -it.card.rank.value }.forEach { caravanCard ->
                        if (enemyCaravan.getValue() + caravanCard.getValue() in (21..26)) {
                            caravanCard.addModifier(game.enemyCResources.removeFromHand(cardIndex))
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
                        cardToJack.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }

                if (overWeightCaravans.isNotEmpty()) {
                    val enemyCaravan = overWeightCaravans.random()
                    val cardToDelete = enemyCaravan.cards.maxBy { it.getValue() }
                    cardToDelete.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedBy { -it.getValue() }.forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }

            if (card.rank == Rank.JOKER) {
                // TODO
                game.enemyCResources.removeFromHand(cardIndex)
                return
            }
            if (card.rank == Rank.QUEEN) {
                // TODO
                game.enemyCResources.removeFromHand(cardIndex)
                return
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.maxBy { it.getValue() }.dropCaravan()
            return
        }

        game.enemyCResources.removeFromHand(hand.indices.random())
    }
}