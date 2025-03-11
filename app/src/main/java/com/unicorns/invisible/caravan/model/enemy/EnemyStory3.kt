package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit


class EnemyStory3(val showMessage: (Int) -> Unit) : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.GOMORRAH, false).apply {
        removeAll(toList().filter { it.rank.value < 5 || it.rank == Rank.QUEEN })
        add(Card(Rank.JACK, Suit.SPADES, CardBack.GOMORRAH, true))
        add(Card(Rank.KING, Suit.SPADES, CardBack.GOMORRAH, true))
        add(Card(Rank.ACE, Suit.SPADES, CardBack.GOMORRAH, true))
    })

    private var shownMessage = false
    override suspend fun makeMove(game: Game, delay: Long) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            when (hand.size) {
                8 -> {
                    game.playerCResources.addCardToHandDirect(Card(Rank.TWO, Suit.HEARTS, CardBack.STANDARD, false))
                    showMessage(1)
                }
                7 -> {
                    game.playerCResources.addCardToHandDirect(Card(Rank.TWO, Suit.HEARTS, CardBack.LUCKY_38, true))
                    game.playerCResources.addCardToHandDirect(Card(Rank.TWO, Suit.HEARTS, CardBack.VAULT_21, false))
                    showMessage(2)
                }
                6 -> {
                    game.playerCResources.addCardToHandDirect(Card(Rank.TWO, Suit.HEARTS, CardBack.GOMORRAH, false))
                    game.playerCResources.addCardToHandDirect(Card(Rank.TWO, Suit.HEARTS, CardBack.VAULT_21, true))
                    showMessage(3)
                }
            }

            val cardIndex = hand.withIndex().filter { !it.value.isModifier() }.random().index
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
            return
        }

        if (!shownMessage) {
            shownMessage = true
            showMessage(4)
            game.playerCResources.addNewDeck(CustomDeck(CardBack.STANDARD, false).apply {
                removeAll(toList().filter { it.rank.value < 5 || it.rank == Rank.QUEEN })
            })
            game.playerCResources.addCardToHandDirect(Card(Rank.KING, Suit.HEARTS, CardBack.STANDARD, true))
            game.playerCResources.shuffleDeck()
        }

        hand.withIndex().sortedBy { it.value.rank.value }.forEach { (cardIndex, card) ->
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
                val caravan = game.enemyCaravans.filter { it.getValue() > 26 }.randomOrNull()
                if (caravan != null) {
                    val cardToAdd = caravan.cards.maxBy { it.getValue() }
                    if (cardToAdd.canAddModifier(card)) {
                        cardToAdd.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
            if (card.rank == Rank.JACK) {
                val caravan = game.playerCaravans.filter { it.getValue() in (16..26) }
                    .maxByOrNull { it.getValue() }
                val cardToJack = caravan?.cards?.maxBy { it.getValue() }
                if (cardToJack != null && cardToJack.canAddModifier(card)) {
                    cardToJack.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }
            if (card.rank == Rank.KING) {
                val caravan =
                    game.enemyCaravans.filter { it.getValue() < 21 }.randomOrNull()
                if (caravan != null) {
                    val cardToKing = caravan.cards.filter { it.canAddModifier(card) }
                        .filter { caravan.getValue() + it.getValue() <= 26 }
                        .maxByOrNull { it.card.rank.value }
                    if (cardToKing != null) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
            if (card.rank == Rank.KING) {
                val caravan =
                    game.playerCaravans.filter { it.getValue() in (21..26) }.randomOrNull()
                if (caravan != null) {
                    val cardToKing = caravan.cards.maxByOrNull { it.getValue() }
                    if (cardToKing != null && cardToKing.canAddModifier(card)) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }

            if (card.rank == Rank.JOKER) {
                val cards = (game.playerCaravans + game.enemyCaravans).flatMap { it.cards }.groupBy { it.card.rank }
                val maxRank = cards.entries.maxBy { it.value.size }
                val cardsRank = maxRank.value
                if (cardsRank.isNotEmpty()) {
                    val cardToJoke = cardsRank.random()
                    if (cardToJoke.canAddModifier(card)) {
                        cardToJoke.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        game.jokerPlayedSound()
                        return
                    }
                }
            }
        }

        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        game.enemyCResources.dropCardFromHand(hand.withIndex().minBy { it.value.rank.value }.index)
    }
}