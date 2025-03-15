package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlin.random.Random


data object EnemyStory1 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.SIERRA_MADRE, true))

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val cardIndex = hand.withIndex().filter { !it.value.isModifier() }.random().index
            val caravan = game.enemyCaravans.first { it.size == 0 }
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
            return
        }

        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan(speed)
            return
        }

        hand.withIndex().filter { Random.nextBoolean() }.forEach { (cardIndex, card) ->
            if (!card.rank.isFace()) {
                game.enemyCaravans.shuffled().forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
                            return
                        }
                    }
                }
            }
            if (card.rank == Rank.JACK) {
                val caravan = game.enemyCaravans.filter { it.getValue() > 26 }.randomOrNull()
                if (caravan != null) {
                    val cardToAdd = caravan.cards.maxBy { it.getValue() }
                    if (cardToAdd.canAddModifier(card)) {
                        cardToAdd.addModifier(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
                        return
                    }
                }
            }
            if (card.rank == Rank.KING) {
                val caravan =
                    game.enemyCaravans.filter { it.getValue() < 21 }.randomOrNull()
                if (caravan != null) {
                    val cardToKing = caravan.cards.filter { it.canAddModifier(card) }
                        .filter { caravan.getValue() + it.getValue() <= 26 }
                        .maxByOrNull { it.card.rank.value }
                    if (cardToKing != null) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex, speed), speed)
                        return
                    }
                }
            }
        }

        game.enemyCResources.dropCardFromHand(hand.indices.random(), speed)
    }
}