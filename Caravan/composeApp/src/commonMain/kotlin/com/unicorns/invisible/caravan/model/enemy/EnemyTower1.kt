package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck


data object EnemyTower1 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck())


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
//        val hand = game.enemyCResources.hand
//
//        if (game.isInitStage()) {
//            val cardIndex = hand.withIndex().filter { !it.value.isModifier() }.random().index
//            val caravan = game.enemyCaravans.first { it.size == 0 }
//            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
//            return
//        }
//
//        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
//        if (overWeightCaravans.isNotEmpty()) {
//            overWeightCaravans.random().dropCaravan(speed)
//            return
//        }
//
//        hand.withIndex().sortedByDescending {
//            when (it.value.rank.value) {
//                11 -> 8
//                else -> it.value.rank.value
//            }
//        }.forEach { (cardIndex, card) ->
//            if (!card.rank.isFace()) {
//                game.enemyCaravans.shuffled().forEach { caravan ->
//                    if (caravan.getValue() + card.rank.value <= 26) {
//                        if (caravan.canPutCardOnTop(card)) {
//                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
//                            return
//                        }
//                    }
//                }
//            }
//
//            if (Random.nextBoolean()) {
//                if (card.rank == Rank.KING) {
//                    val caravan =
//                        game.playerCaravans.filter { it.getValue() in (21..26) }.randomOrNull()
//                    if (caravan != null) {
//                        val cardToKing = caravan.cards.filter { it.canAddModifier(card) }
//                            .maxByOrNull { it.card.rank.value }
//                        if (cardToKing != null && cardToKing.canAddModifier(card)) {
//                            cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
//                            return
//                        }
//                    }
//                }
//                if (card.rank == Rank.JACK) {
//                    val caravan =
//                        game.playerCaravans.filter { it.getValue() in (21..26) }.randomOrNull()
//                    if (caravan != null) {
//                        val cardToAdd = caravan.cards.maxBy { it.getValue() }
//                        if (cardToAdd.canAddModifier(card)) {
//                            cardToAdd.addModifier(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
//                            return
//                        }
//                    }
//                }
//            }
//        }

        // game.enemyCResources.dropCardFromHand(hand.indices.random(), speed)
    }
}