package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.SelectCard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInitStage
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueen
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.utils.checkMoveOnDefeat
import com.unicorns.invisible.caravan.utils.checkMoveOnImminentVictory
import com.unicorns.invisible.caravan.utils.checkMoveOnPossibleVictory
import com.unicorns.invisible.caravan.utils.checkMoveOnProbableDefeat
import com.unicorns.invisible.caravan.utils.checkMoveOnShouldYouDoSmth
import kotlinx.serialization.Serializable
import kotlin.math.max


@Serializable
data object EnemyUlysses : Enemy {
    override fun createDeck() = CResources(CardBack.VAULT_21, true)
    override fun getBankNumber() = 4

    override fun makeMove(game: Game) {
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val underWeightCaravans = game.enemyCaravans.filter { it.getValue() < 21 }
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            StrategyInitStage(SelectCard.MIN_TO_RANDOM).move(game)
            return
        }

        // 1) Check if we have winning move
        game.enemyCaravans.withIndex().forEach {
            val isWinningMovePossible = checkMoveOnPossibleVictory(game, it.index)
            val rivalCaravanValue = game.playerCaravans[it.index].getValue()
            val lowerBound = max(21, rivalCaravanValue + 1)
            if (isWinningMovePossible) {
                // If caravan is overweight, check on Jacks
                val jack = hand.withIndex().find { card -> card.value.rank == Rank.JACK }
                if (jack != null) {
                    it.value.cards
                        .filter { card -> card.canAddModifier(jack.value) }
                        .sortedBy { card -> card.getValue() }
                        .forEach { card ->
                            if (it.value.getValue() - card.getValue() in (lowerBound..26)) {
                                card.addModifier(game.enemyCResources.removeFromHand(jack.index))
                                return
                            }
                        }
                    if (checkMoveOnImminentVictory(game, it.index) && rivalCaravanValue > 26 || it.value.getValue() == rivalCaravanValue) {
                        game.playerCaravans.forEach { playerCaravan ->
                            playerCaravan.cards.forEach { card ->
                                if (playerCaravan.getValue() - card.getValue() < 21) {
                                    card.addModifier(game.enemyCResources.removeFromHand(jack.index))
                                    return
                                }
                            }
                        }
                    }
                }

                // If caravan is underweight, check on Kings
                val king = hand.withIndex().find { card -> card.value.rank == Rank.KING }
                if (king != null) {
                    it.value.cards
                        .filter { card -> card.canAddModifier(king.value) }
                        .sortedBy { card -> card.getValue() }
                        .forEach { card ->
                            if (it.value.getValue() + card.getValue() in (lowerBound..26)) {
                                card.addModifier(game.enemyCResources.removeFromHand(king.index))
                                return
                            }
                        }
                    if (checkMoveOnImminentVictory(game, it.index) && rivalCaravanValue < 21) {
                        game.playerCaravans.forEach { playerCaravan ->
                            playerCaravan.cards.forEach { card ->
                                if (playerCaravan.getValue() + card.getValue() > 26 || it.value.getValue() == rivalCaravanValue) {
                                    card.addModifier(game.enemyCResources.removeFromHand(king.index))
                                    return
                                }
                            }
                        }
                    }
                }

                // Put a card on top!
                hand.withIndex()
                    .filter { card -> !card.value.isFace() }
                    .forEach { (cardIndex, card) ->
                        if (it.value.getValue() + card.rank.value in (lowerBound..26) && it.value.canPutCardOnTop(card)) {
                            it.value.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }


                // maybe even drop a caravan!
                if (rivalCaravanValue == it.value.getValue() && checkMoveOnImminentVictory(game, it.index)) {
                    it.value.dropCaravan()
                    return
                }
            }
        }


        // 2) If not and if player is abt to win, destroy player ready and almost ready caravans (on right columns!)
        game.enemyCaravans.withIndex().forEach { (caravanIndex, caravan) ->
            val isLosing = checkMoveOnDefeat(game, caravanIndex) || checkMoveOnShouldYouDoSmth(game, caravanIndex)
            if (isLosing) {
                hand.withIndex()
                    .filter { it.value.rank == Rank.KING }
                    .forEach { (kingIndex, king) ->
                        game.playerCaravans
                            .withIndex()
                            .filter { it.index != caravanIndex }
                            .forEach { (_, otherCaravan) ->
                                otherCaravan.cards.withIndex()
                                    .filter { it.value.canAddModifier(king) }
                                    .sortedByDescending { it.value.getValue() }
                                    .forEach {
                                        if (otherCaravan.getValue() + it.value.getValue() > 26) {
                                            it.value.addModifier(game.enemyCResources.removeFromHand(kingIndex))
                                            return
                                        }
                                    }
                            }
                    }

                hand.withIndex()
                    .filter { it.value.rank == Rank.JACK }
                    .forEach { (jackIndex, jack) ->
                        game.playerCaravans
                            .filter { it.getValue() >= 11 }
                            .sortedByDescending { if (it.getValue() > 26) 0 else it.getValue() }
                            .forEach { otherCaravan ->
                                otherCaravan.cards.withIndex()
                                    .filter { it.value.canAddModifier(jack) }
                                    .sortedByDescending {
                                        if (caravan.getValue() !in (21..26)) {
                                            it.value.getValue() + 100
                                        } else {
                                            it.value.getValue()
                                        }
                                    }
                                    .forEach {
                                        if (otherCaravan.getValue() - it.value.getValue() < 21) {
                                            it.value.addModifier(game.enemyCResources.removeFromHand(jackIndex))
                                            return
                                        }
                                    }
                            }
                    }

                if (StrategyJokerSimple.move(game)) {
                    game.jokerPlayedSound()
                    return
                }

                // Try creating a draw!
                hand.withIndex()
                    .filter { !it.value.isFace()  }
                    .sortedByDescending { it.value.rank.value }
                    .forEach { (cardIndex, card) ->
                        game.enemyCaravans
                            .withIndex()
                            .filter { it.index != caravanIndex }
                            .forEach { (otherCaravanIndex, otherCaravan) ->
                                if (
                                    otherCaravan.getValue() + card.rank.value == game.playerCaravans[otherCaravanIndex].getValue() &&
                                    otherCaravan.canPutCardOnTop(card)
                                ) {
                                    otherCaravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                                    return
                                }

                                // Try overselling player's caravan
                                if (
                                    otherCaravan.getValue() + card.rank.value > game.playerCaravans[otherCaravanIndex].getValue() &&
                                    otherCaravan.getValue() + card.rank.value <= 26 &&
                                    otherCaravan.canPutCardOnTop(card)
                                ) {
                                    otherCaravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                                    return
                                }
                            }
                    }

                // 2.75) Maybe try a Queen!
                val playerCaravan = game.playerCaravans[caravanIndex]
                hand.withIndex().filter { it.value.rank == Rank.QUEEN }.forEach { (cardIndex, card) ->
                    if (playerCaravan.size >= 2) {
                        val last = playerCaravan.cards.last().card.rank.value
                        val preLast = playerCaravan.cards[playerCaravan.cards.lastIndex - 1].card.rank.value
                        if (playerCaravan.cards.last().canAddModifier(card)) {
                            val isRev = playerCaravan.cards.last().isQueenReversingSequence()
                            val isAscending = preLast < last && !isRev || preLast > last && isRev
                            if (isAscending && last <= 3) {
                                playerCaravan.cards.last().addModifier(game.enemyCResources.removeFromHand(cardIndex))
                                return
                            }
                            val isDescending = preLast > last && !isRev || preLast < last && isRev
                            if (isDescending && last >= 8) {
                                playerCaravan.cards.last().addModifier(game.enemyCResources.removeFromHand(cardIndex))
                                return
                            }
                        }
                    }
                }
            }
        }

        // 4) only then try to put card on our caravan.
        game.enemyCaravans
            .withIndex()
            .filter { it.value.getValue() < 26 }
            .sortedByDescending {
                val ourValue = it.value.getValue()
                val playerValue = game.playerCaravans[it.index].getValue()
                val isLastCaravanInContention = checkMoveOnPossibleVictory(game, it.index)
                if (isLastCaravanInContention) {
                    150 + ourValue
                } else if (ourValue !in (21..26) && playerValue !in (21..26)) {
                    ourValue
                } else if (ourValue !in (21..26) && playerValue in (21..26)) {
                    ourValue + 100
                } else if (ourValue in (21..26) && playerValue !in (21..26)) {
                    playerValue
                } else {
                    when {
                        ourValue > playerValue -> ourValue - playerValue
                        ourValue == playerValue -> 50
                        else -> 100
                    }
                }
            }
            .forEach { (caravanIndex, caravan) ->
                val mult = if (caravan.size >= 2) {
                    val last = caravan.cards.last().card.rank.value
                    val preLast = caravan.cards[caravan.cards.lastIndex - 1].card.rank.value
                    if (preLast > last) {
                        -1
                    } else {
                        1
                    }
                } else if (caravan.size == 1) {
                    if (caravan.cards.last().card.rank.value <= 5) {
                        1
                    } else {
                        -1
                    }
                } else {
                    -1
                }
                hand.withIndex().filter { !it.value.isFace() || it.value.rank == Rank.QUEEN }
                    .sortedBy { if (it.value.rank == Rank.QUEEN) 5 * mult else it.value.rank.value * mult }
                    .forEach { (cardIndex, card) ->
                        if (caravan.getValue() + card.rank.value <= 26 &&
                            caravan.canPutCardOnTop(card) &&
                            !(checkMoveOnProbableDefeat(game, caravanIndex) && caravan.getValue() + card.rank.value in (21..26)) &&
                            !(checkMoveOnDefeat(game, caravanIndex) && caravan.getValue() + card.rank.value in (21..26))
                        ) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
            }


        // 3) Try king on underweightCaravans and jacks on overweightCaravans.
        val jack = hand.withIndex().find { it.value.rank == Rank.JACK }
        if (jack != null) {
            overWeightCaravans.withIndex().sortedBy { it.value.getValue() }.forEach {
                it.value.cards
                    .filter { card -> card.canAddModifier(jack.value) }
                    .sortedBy { card -> card.getValue() }
                    .forEach { card ->
                        val afterJackValue = it.value.getValue() - card.getValue()
                        if (
                            afterJackValue <= 26 &&
                            !(checkMoveOnProbableDefeat(game, it.index) && afterJackValue in (21..26))
                        ) {
                            card.addModifier(game.enemyCResources.removeFromHand(jack.index))
                            return
                        }
                    }
            }
        }

        val king = hand.withIndex().find { it.value.rank == Rank.KING }
        if (king != null) {
            underWeightCaravans.withIndex().filter { it.value.getValue() > 10 }.sortedByDescending { it.value.getValue() }.forEach {
                it.value.cards
                    .filter { card -> card.canAddModifier(king.value) }
                    .sortedByDescending { card -> card.getValue() }
                    .forEach { card ->
                        val afterKingValue = it.value.getValue() + card.getValue()
                        if (
                            afterKingValue <= 26 &&
                            !(checkMoveOnProbableDefeat(game, it.index) && afterKingValue in (21..26))
                        ) {
                            card.addModifier(game.enemyCResources.removeFromHand(king.index))
                            return
                        }
                    }
            }
        }

        if (StrategyQueen.move(game)) {
            return
        }

        if (StrategyJokerSimple.move(game)) {
            game.jokerPlayedSound()
            return
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.maxBy { it.getValue() }.dropCaravan()
            return
        }

        game.enemyCResources.dropCardFromHand(hand.withIndex().minByOrNull { (_, cardInHand) ->
            when (cardInHand.rank) {
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