package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable


@Serializable
data object EnemySwank : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(CardBack.TOPS, CardBack.GOMORRAH, CardBack.ULTRA_LUXE).forEach { back ->
            Suit.entries.forEach { suit ->
                add(Card(Rank.SEVEN, suit, back))
                add(Card(Rank.NINE, suit, back))
                add(Card(Rank.TEN, suit, back))
                add(Card(Rank.JACK, suit, back))
                add(Card(Rank.QUEEN, suit, back))
            }
        }
    })
    override fun getRewardBack() = CardBack.TOPS
    override fun isAlt(): Boolean {
        return true
    }

    override suspend fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy {
                when (it.rank.value) {
                    10 -> 3
                    7 -> 2
                    else -> 1
                }
            }
            val caravan = game.enemyCaravans.first { it.cards.isEmpty() }
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        hand.withIndex().sortedByDescending { it.value.rank.value }.forEach { (cardIndex, card) ->
            if (!card.isFace()) {
                val caravan = game.enemyCaravans.filter {
                    it.getValue() + card.rank.value in listOf(7, 9, 10, 16, 17, 19, 26)
                }.minByOrNull { it.getValue() + card.rank.value }
                if (caravan != null) {
                    if (caravan.canPutCardOnTop(card)) {
                        caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }

            if (card.rank == Rank.QUEEN) {
                val possibleQueenCaravans = game.enemyCaravans
                    .filter {
                        it.size == 2 &&
                                it.cards.last().card.rank != Rank.NINE &&
                                it.cards.last().canAddModifier(card) &&
                                !it.cards.last().isQueenReversingSequence()
                    }
                if (possibleQueenCaravans.isNotEmpty()) {
                    possibleQueenCaravans
                        .random()
                        .cards
                        .last()
                        .addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }

            if (card.rank == Rank.JACK) {
                val caravansWithKingOwners = game.enemyCaravans
                    .filter { it.cards.any { card -> card.getValue() > card.card.rank.value } }
                if (caravansWithKingOwners.isNotEmpty()) {
                    val caravan = caravansWithKingOwners.random()
                    val kingOwner = caravan.cards.find { it.getValue() > it.card.rank.value }
                    if (kingOwner != null && kingOwner.canAddModifier(card)) {
                        kingOwner.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }

                val possibleQueenCaravans = game.enemyCaravans
                    .filter {
                        it.size == 2 &&
                                it.cards.last().card.rank == Rank.NINE &&
                                it.cards.last().isQueenReversingSequence()
                    }
                if (possibleQueenCaravans.isNotEmpty()) {
                    possibleQueenCaravans
                        .random()
                        .cards
                        .last()
                        .addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        game.enemyCResources.removeFromHand(hand.indices.random())
    }
}