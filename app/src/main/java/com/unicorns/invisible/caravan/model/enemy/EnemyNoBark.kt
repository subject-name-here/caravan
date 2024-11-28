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
data object EnemyNoBark : Enemy {
    override fun getBankNumber() = 6

    override fun createDeck() = CResources(CustomDeck(CardBack.STANDARD, false).apply {
        removeAll(toList().filter { it.rank.value < 5 || it.rank == Rank.JOKER })
        listOf(CardBack.GOMORRAH).forEach { back ->
            Suit.entries.forEach { suit ->
                add(Card(Rank.JACK, suit, back, true))
                add(Card(Rank.JACK, suit, back, false))
            }
        }
    })


    override fun makeMove(game: Game) {
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        // TODO

        game.enemyCResources.dropCardFromHand(hand.withIndex().minByOrNull {
            when (it.value.rank) {
                Rank.QUEEN -> 0
                else -> it.value.rank.value
            }
        }!!.index)
    }
}