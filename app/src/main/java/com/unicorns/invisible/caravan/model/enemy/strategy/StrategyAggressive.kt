package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.json
import kotlinx.serialization.encodeToString

class StrategyAggressive : Strategy {
    override fun move(game: Game): Boolean {
        val hand = game.enemyCResources.hand

        val jack = hand.withIndex().find { it.value.rank == Rank.JACK }
        if (jack != null) {
            val caravan = game.playerCaravans.filter { !it.isEmpty() }.maxByOrNull { it.getValue() }
            val cardToJack = caravan?.cards?.maxBy { it.getValue() }
            if (cardToJack != null && cardToJack.canAddModifier(jack.value)) {
                cardToJack.addModifier(game.enemyCResources.removeFromHand(jack.index))
                return true
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
                    return true
                }
            }
        }

        val joker = hand.withIndex().find { it.value.rank == Rank.JOKER }
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val playersReadyCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }
        if (joker != null) {
            val (cardIndex, card) = joker
            val cards = (game.playerCaravans + game.enemyCaravans).flatMap { it.cards }
            val gameCopyString = json.encodeToString(game)

            fun joke(potentialCardToJoker: CardWithModifier): Int {
                val gameCopy = json.decodeFromString<Game>(gameCopyString)
                val cardInCopy = (gameCopy.playerCaravans + gameCopy.enemyCaravans).flatMap { it.cards }.find {
                    potentialCardToJoker.card.rank == it.card.rank && potentialCardToJoker.card.suit == it.card.suit
                }
                if (cardInCopy?.canAddModifier(card) == true) {
                    cardInCopy.addModifier(card)
                    val overWeightCaravansCopy = gameCopy.enemyCaravans.filter { it.getValue() > 26 }
                    val playersReadyCaravansCopy = gameCopy.playerCaravans.filter { it.getValue() in (21..26) }
                    if (overWeightCaravansCopy.size < overWeightCaravans.size || playersReadyCaravansCopy.size < playersReadyCaravans.size) {
                        return 1
                    }
                }
                return 0
            }

            val cardToJoker = cards.maxByOrNull { potentialCardToJoker ->
                joke(potentialCardToJoker)
            }
            if (cardToJoker != null && joke(cardToJoker) > 0) {
                cardToJoker.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                return true
            }
        }

        return false
    }
}