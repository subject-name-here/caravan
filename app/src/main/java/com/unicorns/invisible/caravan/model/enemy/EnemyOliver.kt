package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructive
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyRush
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyTime
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable


@Serializable
data object EnemyOliver : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        CardBack.classicDecks.forEach { back ->
            Rank.entries.forEach { rank ->
                if (rank == Rank.JOKER) {
                    add(Card(Rank.JOKER, Suit.HEARTS, back, false))
                    add(Card(Rank.JOKER, Suit.CLUBS, back, false))
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
    override fun getRewardBack() = null

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        fun check(p0: Int, e0: Int): Float {
            return when {
                p0 in (21..26) && (p0 > e0 || e0 > 26) -> 2f
                p0 > 11 && (e0 != 26 || e0 == p0) -> 0.5f
                else -> 0f
            }
        }

        val score = game.playerCaravans.indices.map {
            check(
                game.playerCaravans[it].getValue(),
                game.enemyCaravans[it].getValue()
            )
        }
        if (2f !in score) {
            if (StrategyRush.move(game)) {
                return
            }
        } else if (score.sum() > 2f) {
            if (StrategyJoker.move(game)) {
                game.jokerPlayedSound()
                return
            }
            if (StrategyDestructive.move(game)) {
                return
            }
        }

        if (StrategyTime.move(game)) {
            return
        }

        EnemyHard.makeMove(game)
    }
}