package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank


object StrategyCareful : Strategy {
    override fun move(game: Game): Boolean {
        val caravanToValue = game.enemyCaravans.filter { !it.isEmpty() }.map { it to it.getValue() }.sortedBy { it.second }
        if (caravanToValue.firstOrNull()?.second in (1..5)) {
            caravanToValue.first().first.dropCaravan()
            return true
        }
        val hand = game.enemyCResources.hand
        hand.withIndex().sortedBy { if (it.value.rank != Rank.QUEEN) it.value.rank.value else 9 }.forEach { cardWithIndex ->
            val (cardIndex, card) = cardWithIndex

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedBy { it.getValue() }.forEachIndexed { _, caravan ->
                    if (caravan.getValue() + card.rank.value <= 21 && caravan.canPutCardOnTop(card)) {
                        caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                        return true
                    }
                }
            }

            if (card.rank == Rank.QUEEN) {
                val possibleQueenCaravans = game.enemyCaravans
                    .filter { c ->
                        c.size >= 2 &&
                                hand.all { !c.canPutCardOnTop(it) } &&
                                c.cards.last().canAddModifier(card)
                    }
                if (possibleQueenCaravans.isNotEmpty()) {
                    possibleQueenCaravans
                        .random()
                        .cards
                        .last()
                        .addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return true
                }
            }

            if (card.rank == Rank.JACK) {
                val caravan = game.playerCaravans.filter { !it.isEmpty() }.maxByOrNull { it.getValue() }
                val cardToJack = caravan?.cards?.maxBy { it.getValue() }
                if (cardToJack != null && cardToJack.canAddModifier(card)) {
                    cardToJack.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return true
                }
            }
        }

        hand.withIndex().sortedBy { it.value.rank.value }.forEach { cardWithIndex ->
            val (cardIndex, card) = cardWithIndex

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedBy { it.getValue() }.forEachIndexed { caravanIndex, caravan ->
                    if (caravan.getValue() + card.rank.value <= 26 && caravan.canPutCardOnTop(card)) {
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
                            return true
                        }
                    }
                }
            }
        }

        return false
    }
}