package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingRuiner
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.model.trading.SierraMadreTrader
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.utils.checkMoveOnDefeat
import com.unicorns.invisible.caravan.utils.checkMoveOnPossibleVictory
import com.unicorns.invisible.caravan.utils.checkMoveOnProbableDefeat
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
data object EnemyElijah : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(
            CardBack.STANDARD,
            CardBack.LUCKY_38,
            CardBack.ULTRA_LUXE,
            CardBack.GOMORRAH,
            CardBack.TOPS,
            CardBack.VAULT_21
        ).forEach { back ->
            Suit.entries.forEach { suit ->
                add(Card(Rank.SIX, suit, back, true))
                add(Card(Rank.TEN, suit, back, true))
                add(Card(Rank.KING, suit, back, true))
            }
            add(Card(Rank.JOKER, Suit.HEARTS, back, true))
            add(Card(Rank.JOKER, Suit.CLUBS, back, true))
        }
    })

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val under26Caravans = game.enemyCaravans.filterIndexed { index, it ->
            it.getValue() in (21..25) && game.playerCaravans[index].getValue() >= it.getValue()
        }

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.first { it.cards.isEmpty() }
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        fun checkAnyReady(p0: Int, e0: Int): Int {
            return when {
                p0 in (21..26) && (p0 > e0 || e0 > 26) -> 1
                e0 in (21..26) && (e0 > p0 || p0 > 26) -> 1
                else -> 0
            }
        }

        val kings = hand.withIndex().filter { it.value.rank == Rank.KING }
        if (kings.isNotEmpty()) {
            val king = kings.random().value
            game.enemyCaravans.withIndex().shuffled().forEach { (caravanIndex, enemyCaravan) ->
                enemyCaravan.cards.forEach { card ->
                    val futureValue = enemyCaravan.getValue() + card.getValue()
                    val playerValue = game.playerCaravans[caravanIndex].getValue()
                    if (
                        card.canAddModifier(king) &&
                        checkMoveOnPossibleVictory(game, caravanIndex) &&
                        futureValue in (21..26) && (futureValue > playerValue || playerValue > 26)
                    ) {
                        card.addModifier(king)
                        return
                    }
                }
            }
        }

        hand.withIndex().filter { !it.value.isFace() }.forEach { (cardIndex, card) ->
            game.enemyCaravans.withIndex().sortedByDescending { it.value.getValue() }
                .forEach { (caravanIndex, caravan) ->
                    val futureValue = caravan.getValue() + card.rank.value
                    val playerValue = game.playerCaravans[caravanIndex].getValue()
                    if (
                        caravan.canPutCardOnTop(card) &&
                        checkMoveOnPossibleVictory(game, caravanIndex) &&
                        futureValue in (21..26) && (futureValue > playerValue || playerValue > 26)
                    ) {
                        caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
        }

        if (Random.nextBoolean() && StrategyJoker.move(game)) {
            game.jokerPlayedSound()
            return
        }

        if ((0..2).sumOf {
                checkAnyReady(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue())
            } >= 2) {
            if (StrategyKingRuiner.move(game)) {
                return
            }
        }

        if (kings.isNotEmpty()) {
            val (cardIndex, card) = kings.random()
            game.enemyCaravans.withIndex().shuffled().forEach { (caravanIndex, enemyCaravan) ->
                if (enemyCaravan.getValue() in listOf(10, 16)) {
                    val ten = enemyCaravan.cards.find {
                        it.card.rank == Rank.TEN && it.getValue() == 10 && it.canAddModifier(card)
                    }
                    if (ten != null && !(checkMoveOnProbableDefeat(
                            game,
                            caravanIndex
                        ) && enemyCaravan.getValue() == 16)
                    ) {
                        ten.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
        }

        hand.withIndex().filter { !it.value.isFace() }.forEach { (cardIndex, card) ->
            game.enemyCaravans.withIndex().sortedByDescending { it.value.getValue() }
                .forEach { (caravanIndex, caravan) ->
                    if (caravan.size < 2 && caravan.getValue() + card.rank.value <= 26 && caravan.canPutCardOnTop(
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

        if (Random.nextBoolean() && StrategyJoker.move(game)) {
            return
        }
        if (Random.nextBoolean() && StrategyKingRuiner.move(game)) {
            return
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }
        if (under26Caravans.isNotEmpty()) {
            under26Caravans.minBy { it.getValue() }.dropCaravan()
            return
        }

        val handSorted = hand.withIndex().sortedBy { it.value.rank.value }
        game.enemyCResources.dropCardFromHand(handSorted.first().index)
    }

    override fun onVictory() {
        save.traders.filterIsInstance<SierraMadreTrader>().forEach { it.elijahBeaten++ }
    }
}