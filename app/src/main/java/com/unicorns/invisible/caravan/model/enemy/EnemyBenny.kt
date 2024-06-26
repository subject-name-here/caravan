package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructive
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyRush
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyTime
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.utils.playJokerSounds
import kotlinx.serialization.Serializable


@Serializable
data object EnemyBenny : Enemy() {
    override fun createDeck(): CResources = CResources(CardBack.TOPS, true)
    override fun getRewardBack() = null

    override fun makeMove(game: Game) {
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        if (StrategyJokerSimple.move(game)) {
            game.jokerPlayedSound()
            return
        }

        hand.withIndex().sortedBy { -it.value.rank.value }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.JACK) {
                val caravan = game.playerCaravans.filter { it.getValue() in (13..26) }
                    .maxByOrNull { it.getValue() }
                val cardToJack = caravan?.cards?.maxBy { it.getValue() }
                if (cardToJack != null && cardToJack.canAddModifier(card)) {
                    cardToJack.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
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

                game.enemyCaravans.filter { it.getValue() in (1..25) }.forEach { enemyCaravan ->
                    enemyCaravan.cards.sortedBy { -it.card.rank.value }.forEach { caravanCard ->
                        if (enemyCaravan.getValue() + caravanCard.getValue() in (16..26)) {
                            if (caravanCard.canAddModifier(card)) {
                                caravanCard.addModifier(
                                    game.enemyCResources.removeFromHand(cardIndex)
                                )
                                return
                            }
                        }
                    }
                }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedBy { -it.getValue() }.forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }

            if (card.rank == Rank.JACK && overWeightCaravans.isNotEmpty()) {
                val enemyCaravan = overWeightCaravans.random()
                val cardToDelete = enemyCaravan.cards.maxBy { it.getValue() }
                if (cardToDelete.canAddModifier(card)) {
                    cardToDelete.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }
        }

        hand.withIndex().filter { it.value.rank == Rank.QUEEN }.forEach { (cardIndex, card) ->
            val possibleQueenCaravans = game.enemyCaravans.withIndex()
                .filter { ci ->
                    val c = ci.value
                    c.size >= 2 && c.getValue() < 26 && hand.all { !c.canPutCardOnTop(it) } && c.cards.last().canAddModifier(card)
                }
            val caravan = possibleQueenCaravans.maxByOrNull { (_, caravan) ->
                if (caravan.getValue() in (21..26)) {
                    10
                } else {
                    caravan.getValue()
                }
            }
            if (caravan != null) {
                caravan.value.cards.last().addModifier(game.enemyCResources.removeFromHand(cardIndex))
                return
            }

            game.playerCaravans.forEach { playerCaravan ->
                if (playerCaravan.size >= 2) {
                    val last = playerCaravan.cards.last().card.rank.value
                    val preLast = playerCaravan.cards[playerCaravan.cards.lastIndex - 1].card.rank.value
                    if (playerCaravan.cards.last().canAddModifier(card)) {
                        val isRev = playerCaravan.cards.last().isQueenReversingSequence()
                        val isAscending = preLast < last && !isRev || preLast > last && isRev
                        if (isAscending && last <= 5) {
                            playerCaravan.cards.last().addModifier(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                        val isDescending = preLast > last && !isRev || preLast < last && isRev
                        if (isDescending && last >= 6) {
                            playerCaravan.cards.last().addModifier(game.enemyCResources.removeFromHand(cardIndex))
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

        game.enemyCResources.dropCardFromHand(hand.indices.random())
    }
}