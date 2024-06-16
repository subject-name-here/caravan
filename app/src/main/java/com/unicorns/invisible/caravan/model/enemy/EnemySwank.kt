package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructive
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
data object EnemySwank : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(CardBack.TOPS, CardBack.GOMORRAH, CardBack.ULTRA_LUXE).forEach { back ->
            Suit.entries.forEach { suit ->
                add(Card(Rank.SEVEN, suit, back, true))
                add(Card(Rank.NINE, suit, back, true))
                add(Card(Rank.TEN, suit, back, true))
                add(Card(Rank.JACK, suit, back, true))
                add(Card(Rank.QUEEN, suit, back, true))
            }
            add(Card(Rank.JOKER, Suit.HEARTS, back, true))
            add(Card(Rank.JOKER, Suit.CLUBS, back, true))
        }
    })
    override fun getRewardBack() = CardBack.TOPS
    override fun isAlt(): Boolean {
        return true
    }

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy {
                when (it.rank.value) {
                    10 -> 3
                    7 -> 2
                    else -> 1
                }
            }
            val caravan = game.enemyCaravans.first { it.cards.isEmpty() }
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        fun check(pc: Caravan, e0: Int): Float {
            val p0 = pc.getValue()
            return when {
                p0 in (21..26) && (p0 > e0 || e0 > 26) -> 2f
                p0 in (11..26) || pc.cards.any { pc.getValue() - it.getValue() in (21..26) } -> 0.5f
                else -> 0f
            }
        }
        val score = game.playerCaravans.indices.map { check(game.playerCaravans[it], game.enemyCaravans[it].getValue()) }
        if (score.sum() > 2.4f) {
            if (StrategyJoker.move(game)) {
                game.jokerPlayedSound()
                return
            }
            if (StrategyDestructive.move(game)) {
                return
            }
        }

        hand.withIndex().sortedByDescending { it.value.rank.value }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.QUEEN) {
                val possibleQueenCaravans = game.enemyCaravans
                    .filter {
                        it.size == 2 &&
                                it.cards.last().card.rank != Rank.NINE &&
                                !it.cards.last().isQueenReversingSequence() &&
                                it.cards.last().canAddModifier(card)
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

            if (card.rank == Rank.JACK) {
                val kingOwners = game.enemyCaravans
                    .filterIndexed { i, caravan ->
                        val e0 = caravan.getValue()
                        val p0 = game.playerCaravans[i].getValue()
                        !(e0 in (21..26) && (e0 >= p0 || p0 > 26))
                    }
                    .flatMap { it.cards }
                    .filter { c -> c.getValue() > c.card.rank.value && c.canAddModifier(card) }
                if (kingOwners.isNotEmpty()) {
                    kingOwners.random().addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }

                val possibleQueenCaravans = game.enemyCaravans
                    .filter {
                        it.size == 2 &&
                                it.cards.last().card.rank == Rank.NINE &&
                                it.cards.last().isQueenReversingSequence() &&
                                it.cards.last().canAddModifier(card)
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

            if (!card.isFace()) {
                val list = when (card.rank.value) {
                    7 -> listOf(7, 26)
                    9 -> listOf(16, 19)
                    10 -> listOf(10, 26)
                    else -> listOf()
                }
                val caravan = game.enemyCaravans.withIndex().filter {
                    it.value.getValue() + card.rank.value in list
                }.minByOrNull { it.value.getValue() + card.rank.value }
                if (caravan != null) {
                    if (
                        caravan.value.canPutCardOnTop(card) &&
                        !(checkMoveOnDefeat(game, caravan.index) && caravan.value.getValue() + card.rank.value in (21..26))
                    ) {
                        caravan.value.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
        }

        hand.withIndex().shuffled().forEach { (cardIndex, card) ->
            if (!card.isFace()) {
                val caravan = game.enemyCaravans.withIndex().filter {
                    it.value.getValue() + card.rank.value in listOf(7, 9, 10, 16, 17, 19, 26)
                }.minByOrNull { it.value.getValue() + card.rank.value }
                if (caravan != null) {
                    if (
                        caravan.value.canPutCardOnTop(card) &&
                        !(checkMoveOnDefeat(game, caravan.index) && caravan.value.getValue() + card.rank.value in (21..26))
                    ) {
                        caravan.value.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
        }


        hand.withIndex().sortedByDescending { it.value.rank.value }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.QUEEN) {
                if (Random.nextBoolean()) {
                    val top = game.playerCaravans
                        .filter { it.size > 2 && it.cards.last().canAddModifier(card) }
                        .randomOrNull()
                        ?.cards
                        ?.last()
                    if (top != null) {
                        top.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        game.enemyCResources.dropCardFromHand(hand.indices.random())
    }
}