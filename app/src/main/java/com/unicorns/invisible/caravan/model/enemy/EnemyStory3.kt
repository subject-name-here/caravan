package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardAtomic
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFBomb
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CardNumberWW
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit


class EnemyStory3(val showMessage: (Int) -> Unit) : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.GOMORRAH, 0).apply {
        removeAll(toList().filter { it is CardNumber && it.rank.value < 5 || it is CardFace && it.rank == RankFace.QUEEN })
        add(CardFaceSuited(RankFace.JACK, Suit.SPADES, CardBack.GOMORRAH, 1))
        add(CardFaceSuited(RankFace.KING, Suit.SPADES, CardBack.GOMORRAH, 1))
        add(CardNumber(RankNumber.ACE, Suit.SPADES, CardBack.GOMORRAH, 1))
    })

    private var shownMessage = false
    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            when (hand.size) {
                8 -> {
                    game.playerCResources.addCardToHandDirect(CardNumber(RankNumber.TWO, Suit.HEARTS, CardBack.STANDARD, 0))
                    showMessage(1)
                }
                7 -> {
                    game.playerCResources.addCardToHandDirect(CardNumber(RankNumber.TWO, Suit.HEARTS, CardBack.LUCKY_38, 1))
                    game.playerCResources.addCardToHandDirect(CardNumber(RankNumber.TWO, Suit.HEARTS, CardBack.VAULT_21, 0))
                    showMessage(2)
                }
                6 -> {
                    game.playerCResources.addCardToHandDirect(CardNumber(RankNumber.TWO, Suit.HEARTS, CardBack.GOMORRAH, 0))
                    game.playerCResources.addCardToHandDirect(CardNumber(RankNumber.TWO, Suit.HEARTS, CardBack.VAULT_21, 1))
                    showMessage(3)
                }
            }

            val cardIndex = hand.withIndex().filter { it.value is CardBase }.random().index
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
            return
        }

        if (!shownMessage) {
            shownMessage = true
            showMessage(4)
            game.playerCResources.addNewDeck(CustomDeck(CardBack.STANDARD, 2).apply {
                removeAll(toList().filter { it is CardNumber && it.rank.value < 5 || it is CardFace && it.rank == RankFace.QUEEN })
            })
            game.playerCResources.addCardToHandDirect(CardFaceSuited(RankFace.KING, Suit.HEARTS, CardBack.STANDARD, 1))
            game.playerCResources.shuffleDeck()
        }

        hand.withIndex().sortedBy {
            when (val c = it.value) {
                is CardBase -> c.rank.value
                is CardFace -> c.rank.value
                else -> 15
            }
        }.forEach { (cardIndex, card) ->
            if (card is CardBase) {
                game.enemyCaravans.shuffled().forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
                            return
                        }
                    }
                }
            }
            card as CardFace
            if (card.rank == RankFace.JACK) {
                val caravan = game.enemyCaravans.filter { it.getValue() > 26 }.randomOrNull()
                if (caravan != null) {
                    val cardToAdd = caravan.cards.maxBy { it.getValue() }
                    if (cardToAdd.canAddModifier(card)) {
                        cardToAdd.addModifier(game.enemyCResources.removeFromHand(cardIndex, speed) as CardFace, speed)
                        return
                    }
                }
            }
            if (card.rank == RankFace.JACK) {
                val caravan = game.playerCaravans.filter { it.getValue() in (16..26) }
                    .maxByOrNull { it.getValue() }
                val cardToJack = caravan?.cards?.maxBy { it.getValue() }
                if (cardToJack != null && cardToJack.canAddModifier(card)) {
                    cardToJack.addModifier(game.enemyCResources.removeFromHand(cardIndex, speed) as CardFace, speed)
                    return
                }
            }
            if (card.rank == RankFace.KING) {
                val caravan =
                    game.enemyCaravans.filter { it.getValue() < 21 }.randomOrNull()
                if (caravan != null) {
                    val cardToKing = caravan.cards.filter { it.canAddModifier(card) }
                        .filter { caravan.getValue() + it.getValue() <= 26 }
                        .maxByOrNull { it.card.rank.value }
                    if (cardToKing != null) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex, speed) as CardFace, speed)
                        return
                    }
                }
            }
            if (card.rank == RankFace.KING) {
                val caravan =
                    game.playerCaravans.filter { it.getValue() in (21..26) }.randomOrNull()
                if (caravan != null) {
                    val cardToKing = caravan.cards.maxByOrNull { it.getValue() }
                    if (cardToKing != null && cardToKing.canAddModifier(card)) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex, speed) as CardFace, speed)
                        return
                    }
                }
            }

            if (card is CardJoker) {
                val cards = (game.playerCaravans + game.enemyCaravans).flatMap { it.cards }.groupBy { it.card.rank }
                val maxRank = cards.entries.maxBy { it.value.size }
                val cardsRank = maxRank.value
                if (cardsRank.isNotEmpty()) {
                    val cardToJoke = cardsRank.random()
                    if (cardToJoke.canAddModifier(card)) {
                        cardToJoke.addModifier(game.enemyCResources.removeFromHand(cardIndex, speed) as CardJoker, speed)
                        game.jokerPlayedSound()
                        return
                    }
                }
            }
        }

        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan(speed)
            return
        }

        game.enemyCResources.dropCardFromHand(hand.withIndex().minBy { when (val c = it.value) {
            is CardBase -> c.rank.value
            is CardFace -> c.rank.value
            else -> 15
        } }.index, speed)
    }
}