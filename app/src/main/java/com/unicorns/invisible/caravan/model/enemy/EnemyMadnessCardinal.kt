package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable


@Serializable
data object EnemyMadnessCardinal : Enemy {
    override fun createDeck() = CResources(CardBack.MADNESS, false)

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val cardIndex = hand.withIndex().filter { !it.value.isFace() }.random().index
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
            return
        }

        // TODO: make them cleverer than Snuffles

        game.enemyCResources.dropCardFromHand(hand.withIndex().minBy {
            when (it.value.rank) {
                Rank.JOKER -> 7
                Rank.JACK -> 6
                Rank.QUEEN -> 4
                Rank.KING -> 5
                Rank.ACE -> 3
                else -> it.value.rank.value
            }
        }.index)
    }
}