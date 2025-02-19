package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.CardDropSelect
import com.unicorns.invisible.caravan.model.enemy.strategy.DropSelection
import com.unicorns.invisible.caravan.model.enemy.strategy.SelectCard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropCaravan
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropCard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInitStage
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable


data object EnemyTower4 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.STANDARD, false).apply {
        addAll(CustomDeck(CardBack.STANDARD, true))
        removeAll(toList().filter { it.rank == Rank.JOKER })
    })

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            StrategyInitStage(SelectCard.MAX_TO_LTR).move(game)
            return
        }

        hand.withIndex().sortedByDescending {
            when (it.value.rank) {
                Rank.JACK -> 14
                Rank.QUEEN -> 2
                Rank.KING -> 15
                Rank.ACE -> 6
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
                    .filter { it.size >= 2 }
                    .randomOrNull()
                if (caravan != null) {
                    val cardToQueen = caravan.cards.last()
                    if (cardToQueen.canAddModifier(card)) {
                        cardToQueen.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
        }

        if (StrategyDropCaravan(DropSelection.MAX_WEIGHT).move(game)) {
            return
        }

        StrategyDropCard(CardDropSelect.VERONICA_ORDER).move(game)
    }
}