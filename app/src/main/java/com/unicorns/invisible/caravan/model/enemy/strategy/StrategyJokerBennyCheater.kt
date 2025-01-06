package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.EnemyBenny
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.json
import kotlinx.serialization.encodeToString


data object StrategyJokerBennyCheater : Strategy {
    override fun move(game: Game): Boolean {
        if (game.enemy !is EnemyBenny || game.enemy.cheatCounter >= 2) {
            return false
        }
        val card = Card(Rank.JOKER, Suit.entries[game.enemy.cheatCounter], CardBack.TOPS, false)
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val perfectCaravans = game.enemyCaravans.filter { it.getValue() in 21..26 }
        val playersOverWeightCaravans = game.playerCaravans.filter { it.getValue() > 26 }
        val playersReadyCaravans = game.playerCaravans.filter { it.getValue() in 21..26 }

        val cards = (game.playerCaravans + game.enemyCaravans)
            .flatMap { it.cards }
            .sortedByDescending { if (it.card.rank == Rank.ACE) 10 else it.card.rank.value }
        val gameCopyString = json.encodeToString(game)
        fun joke(potentialCardToJoker: CardWithModifier): Int {
            val gameCopy = json.decodeFromString<Game>(gameCopyString)
            val cardInCopy =
                (gameCopy.playerCaravans + gameCopy.enemyCaravans).flatMap { it.cards }.find {
                    potentialCardToJoker.card.rank == it.card.rank && potentialCardToJoker.card.suit == it.card.suit
                }
            if (cardInCopy?.canAddModifier(card) == true) {
                cardInCopy.addModifier(card)
                gameCopy.processJoker()
                gameCopy.checkOnGameOver()
                var score = 0
                game.enemyCaravans.forEachIndexed { index, caravan ->
                    val copyCaravan = gameCopy.enemyCaravans[index]
                    val diff = caravan.size - copyCaravan.size
                    when (caravan) {
                        in overWeightCaravans -> {
                            score += diff
                        }
                        in perfectCaravans -> {
                            score -= diff * 3
                        }
                        else -> {
                            score -= diff
                        }
                    }
                }
                game.playerCaravans.forEachIndexed { index, caravan ->
                    val copyCaravan = gameCopy.playerCaravans[index]
                    val diff = caravan.size - copyCaravan.size
                    when (caravan) {
                        in playersOverWeightCaravans -> {
                            score -= diff
                        }
                        in playersReadyCaravans -> {
                            score += diff * 3
                        }
                        else -> {
                            score += diff
                        }
                    }
                }

                return score
            }
            return 0
        }

        val jokerApplicant = cards.maxByOrNull { potentialCardToJoker ->
            joke(potentialCardToJoker)
        }
        if (jokerApplicant != null && joke(jokerApplicant) >= 3) {
            jokerApplicant.addModifier(card)
            return true
        }

        return false
    }
}