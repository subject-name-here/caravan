package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable


@Serializable
data object EnemyFrank : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.ENCLAVE, false).apply {
        add(Card(Rank.ACE, Suit.HEARTS, CardBack.ENCLAVE, true))
        add(Card(Rank.ACE, Suit.CLUBS, CardBack.ENCLAVE, true))
        add(Card(Rank.ACE, Suit.DIAMONDS, CardBack.ENCLAVE, true))
    })

    override fun makeMove(game: Game) {
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val underWeightCaravans = game.enemyCaravans.filter { it.getValue() < 21 }
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filter { it.isOrdinary() }.filter { !it.isFace() }.minBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.maxBy { it.getValue() }.dropCaravan()
            return
        }

        // TODO!!!!!

        game.enemyCResources.dropCardFromHand(hand.withIndex().minByOrNull { (_, cardInHand) ->
            if (cardInHand.isNuclear())
                15
            else when (cardInHand.rank) {
                Rank.ACE -> 4
                Rank.TWO -> 3
                Rank.THREE -> 3
                Rank.FOUR -> 4
                Rank.FIVE -> 5
                Rank.SIX -> 5
                Rank.SEVEN -> 6
                Rank.EIGHT -> 6
                Rank.NINE -> 7
                Rank.TEN -> 8
                Rank.JACK -> 12
                Rank.QUEEN -> 6
                Rank.KING -> 13
                Rank.JOKER -> 14
            }
        }!!.index)
    }
}
