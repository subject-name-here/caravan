package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.CardDropSelect
import com.unicorns.invisible.caravan.model.enemy.strategy.DropSelection
import com.unicorns.invisible.caravan.model.enemy.strategy.SelectCard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructive
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropCaravan
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropCard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInitStage
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.utils.checkMoveOnDefeat
import com.unicorns.invisible.caravan.utils.checkMoveOnPossibleVictory
import kotlinx.serialization.Serializable


@Serializable
data object EnemyNash : Enemy {
    override fun getBankNumber() = 7
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(
            CardBack.TOPS,
            CardBack.GOMORRAH,
            CardBack.ULTRA_LUXE,
            CardBack.LUCKY_38
        ).forEach { back ->
            Suit.entries.forEach { suit ->
                add(Card(Rank.SIX, suit, back, false))
                add(Card(Rank.JACK, suit, back, true))
                add(Card(Rank.QUEEN, suit, back, true))
                add(Card(Rank.KING, suit, back, false))
            }
        }
    })

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }.toSet() +
                game.enemyCaravans
                    .filter {
                        !it.isEmpty() &&
                                it.cards[0]
                                    .modifiersCopy()
                                    .filter { modifier -> modifier.rank != Rank.KING }.size >= 2
                    }
                    .toSet()

        if (game.isInitStage()) {
            StrategyInitStage(SelectCard.RANDOM_TO_LTR).move(game)
            return
        }

        if (checkMoveOnDefeat(game, -1)) {
            if (StrategyDestructive.move(game)) {
                return
            }
        }

        game.enemyCaravans.withIndex().forEach { (index, caravan) ->
            if (checkMoveOnPossibleVictory(game, index)) {
                val king = hand.withIndex().find { it.value.rank == Rank.KING }
                if (king != null && !caravan.isEmpty() && caravan.getValue() < 21 && caravan !in overWeightCaravans) {
                    val card = caravan.cards[0]
                    if (card.canAddModifier(king.value) && !(checkMoveOnDefeat(
                            game,
                            index
                        ) && caravan.getValue() == 12)
                    ) {
                        card.addModifier(game.enemyCResources.removeFromHand(king.index))
                        return
                    }
                }
            }
        }

        val jack = hand.withIndex().find { it.value.rank == Rank.JACK }
        if (jack != null) {
            game.playerCaravans.forEachIndexed { _, caravan ->
                val six = caravan.cards.find { it.card.rank == Rank.SIX }
                if (six != null && six.canAddModifier(jack.value)) {
                    six.addModifier(game.enemyCResources.removeFromHand(jack.index))
                    return
                }
            }
            fun putJack(caravan: Caravan, index: Int, cardToJack: CardWithModifier): Boolean {
                val futureValue = caravan.getValue() - cardToJack.getValue()
                val enemyValue = game.enemyCaravans[index].getValue()
                if (!(checkMoveOnDefeat(
                        game,
                        index
                    ) && enemyValue in (21..26) && (enemyValue > futureValue || futureValue > 26))
                ) {
                    cardToJack.addModifier(game.enemyCResources.removeFromHand(jack.index))
                    return true
                }
                return false
            }
            game.playerCaravans.withIndex()
                .filter { it.value.getValue() in (24..26) }
                .sortedByDescending { it.value.getValue() }
                .forEach { (index, caravan) ->
                    val cardToJack = caravan.cards.filter { it.canAddModifier(jack.value) }
                        .maxByOrNull { it.getValue() }
                    if (cardToJack != null) {
                        if (putJack(caravan, index, cardToJack)) {
                            return
                        }
                    }
                }
            if ((0..11).random() <= 1) {
                game.playerCaravans.withIndex()
                    .filter { it.value.getValue() <= 26 }
                    .sortedByDescending { it.value.getValue() }
                    .forEach { (index, caravan) ->
                        val cardToJack = caravan.cards.filter { it.canAddModifier(jack.value) }
                            .maxByOrNull { it.getValue() }
                        if (cardToJack != null) {
                            if (putJack(caravan, index, cardToJack)) {
                                return
                            }
                        }
                    }
            }
        }

        val six = hand.withIndex().find { it.value.rank == Rank.SIX }
        val king = hand.withIndex().find { it.value.rank == Rank.KING }
        val queen = hand.withIndex().find { it.value.rank == Rank.QUEEN }
        game.enemyCaravans.forEachIndexed { index, caravan ->
            if (six != null && caravan.isEmpty()) {
                if (caravan.canPutCardOnTop(six.value)) {
                    caravan.putCardOnTop(game.enemyCResources.removeFromHand(six.index))
                    return
                }
            }

            if (king != null && !caravan.isEmpty() && caravan.getValue() < 21 && caravan !in overWeightCaravans) {
                val card = caravan.cards[0]
                if (card.canAddModifier(king.value) && !(checkMoveOnDefeat(
                        game,
                        index
                    ) && caravan.getValue() == 12)
                ) {
                    card.addModifier(game.enemyCResources.removeFromHand(king.index))
                    return
                }
            }

            if (queen != null && !caravan.isEmpty() && caravan !in overWeightCaravans) {
                val card = caravan.cards[0]
                if (card.modifiersCopy()
                        .count { it.rank == Rank.QUEEN } == 0 && card.canAddModifier(queen.value)
                ) {
                    card.addModifier(game.enemyCResources.removeFromHand(queen.index))
                    return
                }
            }
        }

        if (StrategyDropCaravan(DropSelection.RANDOM).move(game)) {
            return
        }

        if (jack != null) {
            game.playerCaravans.withIndex()
                .filter { it.value.getValue() in (1..26) }
                .sortedByDescending { it.value.getValue() }
                .forEach { (index, caravan) ->
                    val cardToJack = caravan.cards.filter { it.canAddModifier(jack.value) }
                        .maxByOrNull { it.getValue() }
                    if (cardToJack != null) {
                        val futureValue = caravan.getValue() - cardToJack.getValue()
                        val enemyValue = game.enemyCaravans[index].getValue()
                        if (!(checkMoveOnDefeat(
                                game,
                                index
                            ) && enemyValue in (21..26) && (enemyValue > futureValue || futureValue > 26))
                        ) {
                            cardToJack.addModifier(game.enemyCResources.removeFromHand(jack.index))
                            return
                        }
                    }
                }
        }

        StrategyDropCard(CardDropSelect.NASH_ORDER).move(game)
    }
}