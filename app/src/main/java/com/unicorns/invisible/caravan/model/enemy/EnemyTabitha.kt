package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable


@Serializable
data object EnemyTabitha : Enemy {
    override fun createDeck() = CResources(CardBack.LUCKY_38, false)
    override fun getBankNumber() = 8

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val cardIndex = hand.withIndex().filter { !it.value.isFace() }.random().index
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
            return
        }

        hand.withIndex().sortedByDescending {
            when (it.value.rank) {
                Rank.JOKER -> 7
                Rank.JACK -> 6
                Rank.QUEEN -> 4
                Rank.KING -> 5
                Rank.ACE -> 3
                else -> it.value.rank.value
            }
        }.forEach { (cardIndex, card) ->
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
                val caravans = game.playerCaravans.filter { it.getValue() in (21..26) }
                if (caravans.isNotEmpty()) {
                    val caravan = caravans.maxBy { it.getValue() }
                    val cardToAdd = caravan.cards.maxBy { it.getValue() }
                    if (cardToAdd.canAddModifier(card)) {
                        cardToAdd.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
            if (card.rank == Rank.KING) {
                val caravans = game.playerCaravans.filter { it.getValue() in (21..26) }
                if (caravans.isNotEmpty()) {
                    val caravan = caravans.maxBy { it.getValue() }
                    val cardToKing = caravan.cards.filter { it.canAddModifier(card) }
                        .maxByOrNull { it.card.rank.value }
                    if (cardToKing != null && cardToKing.canAddModifier(card)) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }

            if (card.rank == Rank.QUEEN) {
                val caravan = game.playerCaravans
                    .filterNot { it.isEmpty() }
                    .randomOrNull()
                if (caravan != null) {
                    val cardToQueen = caravan.cards.last()
                    if (cardToQueen.canAddModifier(card)) {
                        cardToQueen.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }

            if (card.rank == Rank.JOKER) {
                val cardToJoker = (game.playerCaravans + game.enemyCaravans)
                    .flatMap { it.cards }
                    .filter { it.canAddModifier(card) }
                    .randomOrNull()
                if (cardToJoker != null) {
                    cardToJoker.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }
        }

        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        game.enemyCResources.dropCardFromHand(hand.withIndex().minBy {
            when (it.value.rank) {
                Rank.JOKER -> 7
                Rank.JACK -> 6
                Rank.QUEEN -> 4
                Rank.KING -> 5
                Rank.ACE -> 3
                else -> it.value.rank.value
            }
        }.index)
    }
}