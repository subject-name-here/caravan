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
data object EnemyNash : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(CardBack.TOPS, CardBack.GOMORRAH, CardBack.ULTRA_LUXE, CardBack.LUCKY_38).forEach { back ->
            Suit.entries.forEach { suit ->
                add(Card(Rank.SIX, suit, back, false))
                add(Card(Rank.JACK, suit, back, true))
                add(Card(Rank.KING, suit, back, false))
            }
        }
    })
    override fun getRewardBack() = CardBack.ULTRA_LUXE
    override fun isAlt(): Boolean {
        return true
    }

    override suspend fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }.toSet() +
                game.enemyCaravans
                    .filter {
                        !it.isEmpty() &&
                                it.cards[0]
                                    .modifiersCopy()
                                    .filter { modifier -> modifier.rank != Rank.KING }
                                    .size >= 2
                    }
                    .toSet()

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.random()
            val caravan = game.enemyCaravans.first { it.cards.isEmpty() }
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        if ((0..2).random() > 0) {
            val jack = hand.withIndex().find { it.value.rank == Rank.JACK }
            if (jack != null) {
                val caravan = game.playerCaravans.filter { !it.isEmpty() }.maxByOrNull { it.getValue() }
                val cardToJack = caravan?.cards?.maxBy { it.getValue() }
                if (cardToJack != null && cardToJack.canAddModifier(jack.value)) {
                    cardToJack.addModifier(game.enemyCResources.removeFromHand(jack.index))
                    return
                }
            }
        }

        val six = hand.withIndex().find { it.value.rank == Rank.SIX }
        if (six != null) {
            val emptyCaravans = game.enemyCaravans.shuffled().filter { it.isEmpty() }
            if (emptyCaravans.isNotEmpty()) {
                val caravan = emptyCaravans.random()
                if (caravan.canPutCardOnTop(six.value)) {
                    caravan.putCardOnTop(game.enemyCResources.removeFromHand(six.index))
                    return
                }
            }
        }

        val king = hand.withIndex().find { it.value.rank == Rank.KING }
        if (king != null) {
            val underWeightCaravans = game.enemyCaravans.shuffled().filter { !it.isEmpty() && it.getValue() < 21 }
            if (underWeightCaravans.isNotEmpty()) {
                val caravan = underWeightCaravans.random()
                val card = caravan.cards[0]
                if (card.canAddModifier(king.value)) {
                    card.addModifier(game.enemyCResources.removeFromHand(king.index))
                    return
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        game.enemyCResources.removeFromHand(hand.withIndex().sortedBy {
            when (it.value.rank) {
                Rank.SIX -> 0
                Rank.KING -> 2
                else -> 1
            }
        }.first().index)
    }
}