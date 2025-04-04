package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck


data object EnemyTower3 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.STANDARD).apply {
        // removeAll(toList().filter { it.rank == Rank.JOKER })
    })

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
//        val hand = game.enemyCResources.hand
//        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
//
//        if (game.isInitStage()) {
//            val cardIndex = hand.withIndex().filter { !it.value.isModifier() }.minBy { it.value.rank.value }.index
//            val caravan = game.enemyCaravans.first { it.size == 0 }
//            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
//            return
//        }
//
//        hand.withIndex().forEach { (cardIndex, card) ->
//            if (card.rank == Rank.JACK) {
//                val caravan = game.playerCaravans.filter { it.getValue() in (16..26) }
//                    .maxByOrNull { it.getValue() }
//                val cardToJack = caravan?.cards?.maxBy { it.getValue() }
//                if (cardToJack != null && cardToJack.canAddModifier(card)) {
//                    cardToJack.addModifier(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
//                    return
//                }
//            }
//            if (card.rank == Rank.KING) {
//                val caravan =
//                    game.playerCaravans.filter { it.getValue() in (21..26) }.randomOrNull()
//                if (caravan != null) {
//                    val cardToKing = caravan.cards.maxByOrNull { it.getValue() }
//                    if (cardToKing != null && cardToKing.canAddModifier(card)) {
//                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
//                        return
//                    }
//                }
//
//                game.enemyCaravans.filter { it.getValue() in (1..25) }.forEach { enemyCaravan ->
//                    enemyCaravan.cards.sortedBy { -it.card.rank.value }.forEach { caravanCard ->
//                        if (enemyCaravan.getValue() + caravanCard.getValue() in (16..26)) {
//                            if (caravanCard.canAddModifier(card)) {
//                                caravanCard.addModifier(
//                                    game.enemyCResources.removeFromHand(cardIndex, speed), speed
//                                )
//                                return
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (!card.rank.isFace()) {
//                game.enemyCaravans.sortedBy { -it.getValue() }.forEach { caravan ->
//                    if (caravan.getValue() + card.rank.value <= 26) {
//                        if (caravan.canPutCardOnTop(card)) {
//                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
//                            return
//                        }
//                    }
//                }
//            }
//
//            if (card.rank == Rank.JACK && overWeightCaravans.isNotEmpty()) {
//                val enemyCaravan = overWeightCaravans.random()
//                val cardToDelete = enemyCaravan.cards.maxBy { it.getValue() }
//                if (cardToDelete.canAddModifier(card)) {
//                    cardToDelete.addModifier(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
//                    return
//                }
//            }
//
//
//            if (card.rank == Rank.QUEEN) {
//                val possibleQueenCaravans = game.enemyCaravans
//                    .filter { c ->
//                        c.size >= 2 && c.getValue() < 21 && hand.all { !c.canPutCardOnTop(it) } && c.cards.last()
//                            .canAddModifier(card)
//                    }
//                if (possibleQueenCaravans.isNotEmpty()) {
//                    possibleQueenCaravans
//                        .random()
//                        .cards
//                        .last()
//                        .addModifier(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
//                    return
//                }
//            }
//        }
//
//        if (overWeightCaravans.isNotEmpty()) {
//            overWeightCaravans.random().dropCaravan(speed)
//            return
//        }
//
//        game.enemyCResources.dropCardFromHand(hand.indices.random(), speed)
    }
}