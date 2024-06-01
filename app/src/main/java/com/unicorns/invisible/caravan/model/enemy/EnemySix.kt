package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructive
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
            add(Card(Rank.JOKER, Suit.HEARTS, back, true))
            add(Card(Rank.JOKER, Suit.CLUBS, back, true))
        }
    })
    override fun getRewardBack() = CardBack.VAULT_21
    override fun isAlt() = true

    override suspend fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val under26Caravans = game.enemyCaravans.filterIndexed { index, it ->
            it.getValue() in (21..25) && game.playerCaravans[index].getValue() >= it.getValue()
        }

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.first { it.cards.isEmpty() }
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        fun check(p0: Int, e0: Int): Float {
            return when {
                p0 in (21..26) && (p0 > e0 || e0 > 26) -> 2f
                p0 in (11..26) && (e0 != 26 || e0 == p0) -> 0.5f
                else -> 0f
            }
        }

        fun checkAnyReady(p0: Int, e0: Int): Float {
            return when {
                p0 in (21..26) && (p0 > e0 || e0 > 26) -> 2f
                e0 in (21..26) && (e0 > p0 || p0 > 26) -> 2f
                else -> 0f
            }
        }

        if (
            game.enemyCaravans.indices.map { checkAnyReady(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue()) }.sum() > 3.9f
        ) {
            if (StrategyDestructive.move(game)) {
                return
            }
        }

        val kings = hand.withIndex().filter { it.value.rank == Rank.KING }
        if (kings.isNotEmpty()) {
            val (cardIndex, card) = kings.random()
            game.enemyCaravans.withIndex().shuffled().forEach { (caravanIndex, enemyCaravan) ->
                if (enemyCaravan.getValue() in listOf(10, 16)) {
                    val ten = enemyCaravan.cards.find { it.card.rank == Rank.TEN && it.getValue() == 10 && it.canAddModifier(card) }
                    if (ten != null) {
                        val otherCaravansIndices = game.enemyCaravans.indices.filter { it != caravanIndex }
                        val score = otherCaravansIndices.map {
                            check(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue())
                        }.sum()
                        if (!(score > 2.4f && enemyCaravan.getValue() == 16)) {
                            ten.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }
        }

        hand.withIndex().filter { !it.value.isFace() }.forEach { (cardIndex, card) ->
            game.enemyCaravans.withIndex().sortedByDescending { it.value.getValue() }.forEach { (caravanIndex, caravan) ->
                if (caravan.size < 2 && caravan.getValue() + card.rank.value <= 26 && caravan.canPutCardOnTop(card)) {
                    val otherCaravansIndices = game.enemyCaravans.indices.filter { it != caravanIndex }
                    val score = otherCaravansIndices.map {
                        check(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue())
                    }.sum()
                    if (!(score > 2.4f && caravan.getValue() + card.rank.value in (21..26))) {
                        caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
        }

        if (kings.isNotEmpty()) {
            val (cardIndex, card) = kings.random()
            game.playerCaravans.shuffled().forEach { playerCaravan ->
                if (playerCaravan.getValue() >= 14) {
                    val cardToKing = playerCaravan.cards
                        .filter {
                            it.canAddModifier(card) &&
                                    (playerCaravan.getValue() + it.getValue() > 26) &&
                                    (playerCaravan.getValue() - it.getValue() !in (21..26))
                        }
                        .randomOrNull()
                    if (cardToKing != null) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }
        if (under26Caravans.isNotEmpty()) {
            under26Caravans.random().dropCaravan()
            return
        }

        val numbers = hand.withIndex().filter { !it.value.isFace() }
        if (numbers.isNotEmpty()) {
            game.enemyCResources.removeFromHand(numbers.random().index)
        } else {
            game.enemyCResources.removeFromHand(hand.indices.random())
        }
    }
}