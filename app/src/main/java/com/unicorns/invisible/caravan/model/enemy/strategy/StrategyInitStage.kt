package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game


class StrategyInitStage(private val selection: SelectCard) : Strategy {
    override fun move(game: Game): Boolean {
        val hand = game.enemyCResources.hand

        when (selection) {
            SelectCard.MIN_TO_RANDOM -> {
                val card = hand.filter { !it.isFace() && !it.isNuclear() }.minBy { it.rank.value }
                val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
                caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            }
            SelectCard.MAX_TO_RANDOM -> {
                val card = hand.filter { !it.isFace() && !it.isNuclear() }.maxBy { it.rank.value }
                val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
                caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            }
            SelectCard.MAX_TO_LTR -> {
                val card = hand.filter { !it.isFace() && !it.isNuclear() }.maxBy { it.rank.value }
                val caravan = game.enemyCaravans.first { it.cards.isEmpty() }
                caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            }
            SelectCard.RANDOM_TO_RANDOM -> {
                val card = hand.filter { !it.isFace() && !it.isNuclear() }.random()
                val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
                caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            }
            SelectCard.RANDOM_TO_LTR -> {
                val card = hand.filter { !it.isFace() && !it.isNuclear() }.random()
                val caravan = game.enemyCaravans.first { it.cards.isEmpty() }
                caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
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