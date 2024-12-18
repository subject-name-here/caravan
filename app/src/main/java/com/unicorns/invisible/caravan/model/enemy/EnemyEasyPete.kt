package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.utils.checkMoveOnDefeat
import kotlinx.serialization.Serializable
import kotlin.math.abs


@Serializable
data object EnemyEasyPete : Enemy {
    override fun createDeck() = CResources(CardBack.STANDARD, false)

    override fun getBankNumber() = 13

    override fun makeMove(game: Game) {
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        // TODO: use Difficult Pete cards

        hand.withIndex().shuffled().sortedByDescending {
            when (it.value.rank) {
                Rank.JACK -> 30
                Rank.KING, Rank.TEN, Rank.NINE, Rank.SEVEN, Rank.SIX -> 20
                Rank.QUEEN -> 0
                else -> it.value.rank.value
            }
        }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.JACK) {
                val caravan =
                    game.playerCaravans.withIndex().filter { it.value.getValue() in (10..26) }
                        .randomOrNull()
                val cardToJack = caravan?.value?.cards?.maxByOrNull { it.getValue() }
                if (cardToJack != null && cardToJack.canAddModifier(card) &&
                    !(checkMoveOnDefeat(
                        game,
                        caravan.index
                    ) && caravan.value.getValue() == game.enemyCaravans[caravan.index].getValue() && caravan.value.getValue() in (21..26))
                ) {
                    cardToJack.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }
            if (card.rank == Rank.KING) {
                game.enemyCaravans.withIndex()
                    .flatMap { c -> c.value.cards.map { it to c } }
                    .sortedByDescending { (it.second.value.getValue() + it.first.getValue()) / 2 }
                    .forEach {
                        if (it.second.value.getValue() + it.first.getValue() in (12..26) && it.first.canAddModifier(
                                card
                            ) &&
                            !(
                                    checkMoveOnDefeat(game, it.second.index) &&
                                            it.second.value.getValue() == game.playerCaravans[it.second.index].getValue() &&
                                            it.second.value.getValue() in (21..26)
                                    )
                        ) {
                            it.first.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedByDescending { it.getValue() }
                    .forEachIndexed { caravanIndex, caravan ->
                        if (caravan.getValue() + card.rank.value <= 26 && caravan.canPutCardOnTop(
                                card
                            )
                        ) {
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
                        .maxBy { abs(6 - it.cards.last().card.rank.value) }
                        .cards
                        .last()
                        .addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }

            if (card.rank == Rank.JOKER) {
                if (StrategyJoker.move(game)) {
                    game.jokerPlayedSound()
                    return
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        game.enemyCResources.dropCardFromHand(hand.withIndex().minByOrNull {
            when (it.value.rank) {
                Rank.QUEEN -> 0
                else -> it.value.rank.value
            }
        }!!.index)
    }
}