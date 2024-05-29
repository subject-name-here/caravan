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
data object EnemySix : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        CardBack.entries.forEach { back ->
            Suit.entries.forEach { suit ->
                add(Card(Rank.SIX, suit, back, true))
                add(Card(Rank.TEN, suit, back, true))
                add(Card(Rank.KING, suit, back, true))
            }
        }
    })
    override fun getRewardBack() = CardBack.VAULT_21
    override fun isAlt(): Boolean {
        return true
    }

    override suspend fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.first { it.cards.isEmpty() }
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        hand.withIndex().sortedBy { -it.value.rank.value }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.KING) {
                game.enemyCaravans.forEachIndexed { caravanIndex, enemyCaravan ->
                    if (enemyCaravan.getValue() in listOf(10, 16)) {
                        val ten = enemyCaravan.cards.find { it.card.rank == Rank.TEN && it.getValue() == 10 }
                        if (ten != null && ten.canAddModifier(card)) {
                            val otherCaravansIndices = game.enemyCaravans.indices.filter { it != caravanIndex }
                            var score = 0
                            fun check(p0: Int, e0: Int) {
                                if (p0 in (21..26) && (p0 > e0 || e0 > 26)) {
                                    score++
                                }
                            }
                            otherCaravansIndices.forEach {
                                check(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue())
                            }
                            if (!(score == 2 && enemyCaravan.getValue() == 16)) {
                                ten.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                                return
                            }
                        }
                    }
                }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedBy { -it.getValue() }.forEachIndexed { caravanIndex, caravan ->
                    if (caravan.size < 2 && caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.canPutCardOnTop(card)) {
                            val otherCaravansIndices = game.enemyCaravans.indices.filter { it != caravanIndex }
                            var score = 0
                            fun check(p0: Int, e0: Int) {
                                if (p0 in (21..26) && (p0 > e0 || e0 > 26)) {
                                    score++
                                }
                            }
                            otherCaravansIndices.forEach {
                                check(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue())
                            }
                            if (!(score == 2 && caravan.getValue() + card.rank.value in (21..26))) {
                                caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                                return
                            }
                        }
                    }
                }
            }

            if (card.rank == Rank.KING) {
                game.playerCaravans.forEach { playerCaravan ->
                    if (playerCaravan.getValue() in (13..26)) {
                        val cardToKing = playerCaravan.cards
                            .filter { it.getValue() + playerCaravan.getValue() > 26 }
                            .minByOrNull { it.getValue() }
                        if (cardToKing != null && cardToKing.canAddModifier(card)) {
                            cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
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

        game.enemyCResources.removeFromHand(hand.indices.random())
    }
}