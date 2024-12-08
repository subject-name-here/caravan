package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game


class StrategyInitStage(private val selection: SelectCard) : Strategy {
    override fun move(game: Game): Boolean {
        val hand = game.enemyCResources.hand

        when (selection) {
            SelectCard.MIN_TO_RANDOM -> {
                val card = hand.filter { !it.isFace() }.minBy { it.rank.value }
                val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
                caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            }
            SelectCard.MAX_TO_RANDOM -> {
                val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
                val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
                caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            }
            SelectCard.MAX_TO_LTR -> {
                val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
                val caravan = game.enemyCaravans.first { it.cards.isEmpty() }
                caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            }
            SelectCard.RANDOM_TO_RANDOM -> {
                val cardIndex = hand.withIndex().filter { !it.value.isFace() }.random().index
                val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
                caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
            }
            SelectCard.RANDOM_TO_LTR -> {
                val cardIndex = hand.withIndex().filter { !it.value.isFace() }.random().index
                val caravan = game.enemyCaravans.first { it.cards.isEmpty() }
                caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
            }
        }

        return true
    }
}

enum class SelectCard {
    MIN_TO_RANDOM,
    MAX_TO_RANDOM,
    RANDOM_TO_RANDOM,
    MAX_TO_LTR,
    RANDOM_TO_LTR,
}