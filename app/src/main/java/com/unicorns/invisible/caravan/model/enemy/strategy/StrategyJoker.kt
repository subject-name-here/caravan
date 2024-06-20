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
                    var score = -3
                    game.enemyCaravans.forEachIndexed { index, caravan ->
                        val copyCaravan = gameCopy.enemyCaravans[index]
                        if (caravan.getValue() > 26 && caravan.getValue() > copyCaravan.getValue()) {
                            if (copyCaravan.getValue() <= 26) {
                                score += if (copyCaravan.getValue() in (21..26)) {
                                    5
                                } else {
                                    2
                                }
                            } else {
                                score++
                            }
                        } else if (caravan.getValue() > copyCaravan.getValue()) {
                            if (caravan.getValue() in (21..26)) {
                                score -= 5
                            } else {
                                score--
                            }
                        }
                    }
                    game.playerCaravans.forEachIndexed { index, caravan ->
                        val copyCaravan = gameCopy.playerCaravans[index]
                        if (caravan.getValue() > 26 && caravan.getValue() > copyCaravan.getValue()) {
                            if (copyCaravan.getValue() <= 26) {
                                score -= if (copyCaravan.getValue() in (21..26)) {
                                    6
                                } else {
                                    2
                                }
                            } else {
                                score--
                            }
                        } else if (caravan.getValue() > copyCaravan.getValue()) {
                            if (caravan.getValue() in (21..26)) {
                                score += if (copyCaravan.getValue() !in (21..26)) {
                                    5
                                } else {
                                    1
                                }
                            }
                        }
                    }
                    val overWeightCaravansCopy =
                        gameCopy.enemyCaravans.filter { it.getValue() > 26 }
                    val perfectCaravansCopy =
                        gameCopy.enemyCaravans.filter { it.getValue() in (21..26) }
                    val playersReadyCaravansCopy =
                        gameCopy.playerCaravans.filter { it.getValue() in (21..26) }
                    val score2 = (perfectCaravansCopy.size - perfectCaravans.size) * 3 +
                            overWeightCaravans.size - overWeightCaravansCopy.size +
                            playersReadyCaravans.size - playersReadyCaravansCopy.size

                    if (gameCopy.isGameOver == 1) {
                        return 0
                    } else if (gameCopy.isGameOver == -1) {
                        return 1234567
                    }

                    return if (score2 > 0 && score > 0) score + score2 * 2 else 0
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