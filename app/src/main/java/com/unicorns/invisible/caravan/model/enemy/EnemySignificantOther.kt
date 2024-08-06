package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueen
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class EnemySignificantOther(
    val deck: CustomDeck,
    @Transient val speaker: (Int) -> Unit = {}
) : Enemy() {
    override fun createDeck(): CResources = CResources(deck)

    @Transient
    private val linesSaid = ArrayList<Int>()
    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val cardIndex = hand.withIndex().filter { !it.value.isFace() }.random().index
            val caravan = game.enemyCaravans.first { it.size == 0 }
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
            return
        }

        if (checkIfPlayerNeedsHelp(game)) {
            helpPlayer(game)
            checkIfPlayerNeedsHelp(game)
            return
        }

        if (checkIfFulfilled(game)) {
            whenFulfilled(game)
            return
        }

        checkIfEqualSold(game)
        routineMove(game)
        checkIfEqualSold(game)
        checkIfFulfilled(game)
    }

    private fun helpPlayer(game: Game) {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }

        hand.withIndex().shuffled().forEach { (cardIndex, card) ->
            if (!card.rank.isFace()) {
                game.enemyCaravans.shuffled().forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 20) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }

            if (card.rank == Rank.KING) {
                game.playerCaravans.shuffled().forEach { playerCaravan ->
                    val cardToKing = playerCaravan.cards
                        .filter {
                            it.canAddModifier(card) && playerCaravan.getValue() + it.getValue() <= 26
                        }
                        .maxByOrNull { it.getValue() }
                    if (cardToKing != null) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.maxBy { it.getValue() }.dropCaravan()
            return
        }

        game.enemyCResources.dropCardFromHand(hand.indices.random())
    }
    private fun checkIfPlayerNeedsHelp(game: Game): Boolean {
        if (1 in linesSaid) {
            return false
        }
        val playerValues = game.playerCaravans.map { it.getValue() }
        if (playerValues.any { it in (21..26) }) {
            speaker(1)
            linesSaid.add(1)
            return false
        }
        return true
    }

    private fun checkIfFulfilled(game: Game): Boolean {
        val playerValues = game.playerCaravans.map { it.getValue() }
        val enemyValues = game.enemyCaravans.map { it.getValue() }
        val isFulfilled = (0..2).all { playerValues[it] == enemyValues[it] && playerValues[it] in (21..26) }
        if (isFulfilled && 3 !in linesSaid) {
            speaker(3)
            linesSaid.add(3)
        }
        return isFulfilled
    }
    private fun whenFulfilled(game: Game) {
        val hand = game.enemyCResources.hand
        game.enemyCResources.dropCardFromHand(hand.indices.random())
    }

    private fun checkIfEqualSold(game: Game) {
        val playerValues = game.playerCaravans.map { it.getValue() }
        val enemyValues = game.enemyCaravans.map { it.getValue() }
        val isEqualSold = (0..2).any { playerValues[it] == enemyValues[it] && playerValues[it] in (21..26) }
        if (isEqualSold && 2 !in linesSaid) {
            speaker(2)
            linesSaid.add(2)
        }
    }
    private fun routineMove(game: Game) {
        val hand = game.enemyCResources.hand
        game.enemyCaravans.withIndex().forEach {
            val rivalCaravanValue = game.playerCaravans[it.index].getValue()

            val jack = hand.withIndex().find { card -> card.value.rank == Rank.JACK }
            if (jack != null) {
                it.value.cards
                    .filter { card -> card.canAddModifier(jack.value) }
                    .sortedBy { card -> card.getValue() }
                    .forEach { card ->
                        if (it.value.getValue() - card.getValue() == rivalCaravanValue) {
                            card.addModifier(game.enemyCResources.removeFromHand(jack.index))
                            return
                        }
                    }
            }

            val king = hand.withIndex().find { card -> card.value.rank == Rank.KING }
            if (king != null) {
                it.value.cards
                    .filter { card -> card.canAddModifier(king.value) }
                    .sortedBy { card -> card.getValue() }
                    .forEach { card ->
                        if (it.value.getValue() + card.getValue() == rivalCaravanValue) {
                            card.addModifier(game.enemyCResources.removeFromHand(king.index))
                            return
                        }
                    }
                game.playerCaravans[it.index].cards
                    .filter { card -> card.canAddModifier(king.value) }
                    .sortedBy { card -> card.getValue() }
                    .forEach { card ->
                        if (rivalCaravanValue + card.getValue() == it.value.getValue()) {
                            card.addModifier(game.enemyCResources.removeFromHand(king.index))
                            return
                        }
                    }
            }
            hand.withIndex()
                .filter { card -> !card.value.isFace() }
                .forEach { (cardIndex, card) ->
                    if (it.value.getValue() + card.rank.value == rivalCaravanValue && it.value.canPutCardOnTop(card)) {
                        it.value.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }

            if (rivalCaravanValue == 0) {
                it.value.dropCaravan()
                return
            }
        }

        game.enemyCaravans.withIndex().forEach {
            val rivalCaravanValue = game.playerCaravans[it.index].getValue()

            val king = hand.withIndex().find { card -> card.value.rank == Rank.KING }
            if (king != null) {
                it.value.cards
                    .filter { card -> card.canAddModifier(king.value) }
                    .sortedByDescending { card -> card.getValue() }
                    .forEach { card ->
                        if (it.value.getValue() + card.getValue() <= rivalCaravanValue) {
                            card.addModifier(game.enemyCResources.removeFromHand(king.index))
                            return
                        }
                    }
            }
        }

        game.enemyCaravans
            .withIndex()
            .filter { it.value.getValue() < 26 }
            .shuffled()
            .forEach { (caravanIndex, caravan) ->
                hand.withIndex().filter { !it.value.isSpecial() }.filter { !it.value.isFace() }
                    .sortedByDescending { it.value.rank.value }
                    .forEach { (cardIndex, card) ->
                        val futureValue = caravan.getValue() + card.rank.value
                        if (futureValue <= game.playerCaravans[caravanIndex].getValue() &&
                            futureValue <= 26 &&
                            caravan.canPutCardOnTop(card)
                        ) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
            }

        if (StrategyQueen.move(game)) {
            return
        }

        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.maxBy { it.getValue() }.dropCaravan()
            return
        }

        game.enemyCResources.dropCardFromHand(hand.indices.random())
    }
}