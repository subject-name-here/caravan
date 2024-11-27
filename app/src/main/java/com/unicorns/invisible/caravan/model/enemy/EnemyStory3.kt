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
class EnemyStory3 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.TOPS, false).apply {
        removeAll(toList().filter { it.isFace() && it.rank != Rank.JACK })
    })

    private var cazadorsAdded = false
    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filter { it.isOrdinary() }.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        } else if (!cazadorsAdded) {
            cazadorsAdded = true
            game.enemyCResources.addOnTop(Card(Rank.QUEEN, Suit.HEARTS, CardBack.MADNESS, true))
        }

        val specials = hand.withIndex().filter { it.value.isWildWasteland() }
        specials.forEach { (index, special) ->
            when (special.getWildWastelandCardType()) {
                Card.WildWastelandCardType.CAZADOR -> {
                    val candidate = game.playerCaravans
                        .filter { it.getValue() in (11..26) }
                        .filter { !it.cards.flatMap { card -> card.modifiersCopy() }.any { mod -> mod.isWildWasteland() } }
                        .maxByOrNull { it.size }
                        ?.cards
                        ?.filter { it.canAddModifier(special) }
                        ?.maxByOrNull { it.getValue() }
                    if (candidate != null) {
                        candidate.addModifier(game.enemyCResources.removeFromHand(index))
                        game.wildWastelandSound()
                        return
                    }
                }
                else -> {}
            }
        }


        hand.withIndex().filter { it.value.isOrdinary() }.shuffled().forEach { (cardIndex, card) ->
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
                val caravan =
                    game.playerCaravans.filter { it.getValue() in (21..26) }.randomOrNull()
                if (caravan != null) {
                    val cardToAdd = caravan.cards.maxBy { it.getValue() }
                    if (cardToAdd.canAddModifier(card)) {
                        cardToAdd.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
        }

        game.enemyCResources.dropCardFromHand(hand.withIndex().minByOrNull {
            if (it.value.isWildWasteland()) {
                15
            } else {
                when (it.value.rank) {
                    Rank.ACE -> 3
                    Rank.TWO -> 2
                    Rank.THREE -> 2
                    Rank.FOUR -> 3
                    Rank.FIVE -> 3
                    Rank.SIX -> 4
                    Rank.SEVEN -> 5
                    Rank.EIGHT -> 5
                    Rank.NINE -> 5
                    Rank.TEN -> 5
                    else -> 6
                }
            }
        }!!.index)
    }
}