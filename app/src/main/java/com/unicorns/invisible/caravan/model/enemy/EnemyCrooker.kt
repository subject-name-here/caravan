package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.utils.checkMoveOnDefeat
import kotlinx.serialization.Serializable
import kotlin.math.abs


@Serializable
data object EnemyCrooker : Enemy {
    override fun getBankNumber() = 11
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(CardBack.VAULT_21, CardBack.LUCKY_38).forEach { back ->
            Rank.entries.forEach { rank ->
                if (rank == Rank.JOKER) {
                    add(Card(Rank.JOKER, Suit.HEARTS, back, true))
                    add(Card(Rank.JOKER, Suit.CLUBS, back, true))
                } else if (rank.value > 3) {
                    Suit.entries.forEach { suit ->
                        add(Card(rank, suit, back, true))
                    }
                }
            }
        }
    })
    override fun makeMove(game: Game) {
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val playersReadyCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        hand.withIndex().sortedByDescending {
            if (playersReadyCaravans.isNotEmpty()) {
                when (it.value.rank) {
                    Rank.JOKER -> 38
                    Rank.JACK, Rank.KING -> 30
                    Rank.TEN, Rank.NINE, Rank.SEVEN, Rank.SIX -> 20
                    Rank.QUEEN -> 4
                    else -> it.value.rank.value
                }
            } else {
                when (it.value.rank) {
                    Rank.TEN, Rank.NINE, Rank.SEVEN, Rank.SIX -> 20
                    Rank.QUEEN, Rank.ACE -> 4
                    Rank.JACK, Rank.KING, Rank.JOKER -> 2
                    else -> it.value.rank.value
                }
            }
        }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.JACK) {
                val caravan =
                    game.playerCaravans.withIndex().filter { it.value.getValue() in (12..26) }
                        .randomOrNull()
                val cardToJack = caravan?.value?.cards?.maxByOrNull { it.getValue() }
                if (cardToJack != null && cardToJack.canAddModifier(card)) {
                    val futureValue = caravan.value.getValue() - cardToJack.getValue()
                    val enemyValue = game.enemyCaravans[caravan.index].getValue()
                    if (!(checkMoveOnDefeat(
                            game,
                            caravan.index
                        ) && enemyValue in (21..26) && (enemyValue > futureValue || futureValue > 26))
                    ) {
                        cardToJack.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
            if (card.rank == Rank.KING) {
                val caravan = game.playerCaravans.minBy { abs(26 - it.getValue()) }
                val caravanIndex = game.playerCaravans.indexOf(caravan)
                val cardToKing = caravan.cards
                    .filter { caravan.getValue() + it.getValue() !in (21..26) }
                    .maxByOrNull { it.getValue() }

                if (cardToKing != null && cardToKing.canAddModifier(card)) {
                    val futureValue = caravan.getValue() + cardToKing.getValue()
                    val enemyValue = game.enemyCaravans[caravanIndex].getValue()
                    if (!(checkMoveOnDefeat(
                            game,
                            caravanIndex
                        ) && enemyValue in (21..26) && (enemyValue > futureValue || futureValue > 26))
                    ) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }

                game.enemyCaravans
                    .flatMap { c -> c.cards.map { it to c } }
                    .sortedByDescending { it.first.getValue() }
                    .forEach {
                        if (it.second.getValue() + it.first.getValue() in (13..26)) {
                            if (it.first.canAddModifier(card)) {
                                val futureValue = it.second.getValue() + it.first.getValue()
                                val playerValue = caravan.getValue()
                                if (!(checkMoveOnDefeat(
                                        game,
                                        caravanIndex
                                    ) && futureValue in (21..26) && (futureValue > playerValue || playerValue > 26))
                                ) {
                                    it.first.addModifier(
                                        game.enemyCResources.removeFromHand(
                                            cardIndex
                                        )
                                    )
                                    return
                                }
                            }
                        }
                    }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans
                    .sortedByDescending { it.getValue() }
                    .forEachIndexed { caravanIndex, caravan ->
                        if (caravan.getValue() + card.rank.value <= 26 && caravan.canPutCardOnTop(card)) {
                            if (!(checkMoveOnDefeat(
                                    game,
                                    caravanIndex
                                ) && caravan.getValue() + card.rank.value in (21..26))
                            ) {
                                caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                                return
                            }
                        }
                    }
            }

            if (card.rank == Rank.QUEEN) {
                val possibleQueenCaravans = game.enemyCaravans
                    .filter { c ->
                        c.size >= 2 && c.getValue() < 21 && hand.all { !c.canPutCardOnTop(it) } && c.cards.last()
                            .canAddModifier(card)
                    }
                if (possibleQueenCaravans.isNotEmpty()) {
                    possibleQueenCaravans
                        .random()
                        .cards
                        .last()
                        .addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }

            if (card.rank == Rank.JOKER) {
                if (StrategyJokerSimple.move(game)) {
                    game.jokerPlayedSound()
                    return
                }
            }

            if (card.rank == Rank.JACK && overWeightCaravans.isNotEmpty()) {
                val enemyCaravan = overWeightCaravans.random()
                val cardToDelete = enemyCaravan.cards.maxBy { it.getValue() }
                if (cardToDelete.canAddModifier(card)) {
                    cardToDelete.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        game.enemyCResources.dropCardFromHand(hand.withIndex().minBy {
            when (it.value.rank) {
                Rank.QUEEN -> 0
                else -> it.value.rank.value
            }
        }.index)
    }
}