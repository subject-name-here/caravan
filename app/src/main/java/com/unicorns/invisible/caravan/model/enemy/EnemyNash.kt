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
                add(Card(Rank.SIX, suit, back))
                add(Card(Rank.JACK, suit, back))
                add(Card(Rank.KING, suit, back))
            }
        }
    })
    override fun getRewardBack() = CardBack.ULTRA_LUXE
    override fun isAlt(): Boolean {
        return true
    }

    override suspend fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 } +
                game.enemyCaravans.filter { !it.isEmpty() && it.cards[0].modifiersCopy().size >= 3 }

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.first { it.cards.isEmpty() }
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        val jack = hand.withIndex().find { it.value.rank == Rank.JACK }
        if (jack != null) {
            val caravan = game.playerCaravans.filter { !it.isEmpty() }.maxByOrNull { it.getValue() }
            val cardToJack = caravan?.cards?.maxBy { it.getValue() }
            if (cardToJack != null && cardToJack.canAddModifier(jack.value)) {
                cardToJack.addModifier(game.enemyCResources.removeFromHand(jack.index))
                return
            }
        }

        val six = hand.withIndex().find { it.value.rank == Rank.SIX }
        if (six != null) {
            val emptyCaravans = game.enemyCaravans.shuffled().filter { it.isEmpty() }
            if (emptyCaravans.isNotEmpty()) {
                val caravan = emptyCaravans[0]
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
                val caravan = underWeightCaravans[0]
                val card = caravan.cards.getOrNull(0)
                if (card != null && card.canAddModifier(king.value)) {
                    card.addModifier(game.enemyCResources.removeFromHand(king.index))
                    return
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        game.enemyCResources.removeFromHand(hand.indices.random())
    }
}