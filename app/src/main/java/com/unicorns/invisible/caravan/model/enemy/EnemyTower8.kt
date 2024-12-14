package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.CardDropSelect
import com.unicorns.invisible.caravan.model.enemy.strategy.DropSelection
import com.unicorns.invisible.caravan.model.enemy.strategy.SelectCard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropCaravan
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropCard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInitStage
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
data object EnemyTower8 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(
            CardBack.TOPS, CardBack.GOMORRAH, CardBack.ULTRA_LUXE, CardBack.VAULT_21
        ).forEach { back ->
            Rank.entries.forEach { rank ->
                if (rank == Rank.JOKER) {
                    add(Card(Rank.JOKER, Suit.HEARTS, back, false))
                } else if (!rank.isFace()) {
                    listOf(Suit.HEARTS).forEach { suit ->
                        add(Card(rank, suit, back, false))
                    }
                } else if (rank != Rank.QUEEN) {
                    Suit.entries.forEach { suit ->
                        add(Card(rank, suit, back, false))
                    }
                }
            }
        }
    })

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            StrategyInitStage(SelectCard.RANDOM_TO_RANDOM).move(game)
            return
        }

        hand.withIndex().shuffled().sortedByDescending {
            when (it.value.rank) {
                Rank.JACK, Rank.KING, Rank.JOKER -> 30
                Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN, Rank.SIX -> 20
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

            if (StrategyJokerSimple.move(game)) {
                game.jokerPlayedSound()
                return
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedByDescending { it.getValue() }
                    .forEachIndexed { caravanIndex, caravan ->
                        if (caravan.getValue() + card.rank.value <= 26 &&
                            caravan.canPutCardOnTop(card)
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
        }

        if (StrategyDropCaravan(DropSelection.RANDOM).move(game)) {
            return
        }

        StrategyDropCard(CardDropSelect.RANDOM).move(game)
    }
}