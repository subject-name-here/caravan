package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.json
import kotlinx.serialization.encodeToString

object StrategyJoker : Strategy {
    override fun move(game: Game): Boolean {
        val hand = game.enemyCResources.hand

        val joker = hand.withIndex().find { it.value.rank == Rank.JOKER }
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val perfectCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val playersReadyCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }
        if (joker != null) {
            val card = joker.value
            val cards = (game.playerCaravans + game.enemyCaravans)
                .flatMap { it.cards }
                .sortedBy { if (it.card.rank == Rank.ACE) 10 else it.card.rank.value }
            val gameCopyString = json.encodeToString(game)
            fun joke(potentialCardToJoker: CardWithModifier): Int {
                val gameCopy = json.decodeFromString<Game>(gameCopyString)
                val cardInCopy = (gameCopy.playerCaravans + gameCopy.enemyCaravans).flatMap { it.cards }.find {
                    potentialCardToJoker.card.rank == it.card.rank && potentialCardToJoker.card.suit == it.card.suit
                }
                if (cardInCopy?.canAddModifier(card) == true) {
                    cardInCopy.addModifier(card)
                    gameCopy.processJoker()
                    val overWeightCaravansCopy = gameCopy.enemyCaravans.filter { it.getValue() > 26 }
                    val perfectCaravansCopy = gameCopy.enemyCaravans.filter { it.getValue() in (21..26) }
                    val playersReadyCaravansCopy = gameCopy.playerCaravans.filter { it.getValue() in (21..26) }
                    return (perfectCaravansCopy.size - perfectCaravans.size) * 3 +
                            overWeightCaravans.size - overWeightCaravansCopy.size +
                            playersReadyCaravans.size - playersReadyCaravansCopy.size
                }
                return 0
            }

            val jokerApplicant = cards.maxBy { potentialCardToJoker ->
                joke(potentialCardToJoker)
            }
            if (joke(jokerApplicant) > 0) {
                jokerApplicant.addModifier(game.enemyCResources.removeFromHand(joker.index))
                return true
            }
        }

        return false
    }
}