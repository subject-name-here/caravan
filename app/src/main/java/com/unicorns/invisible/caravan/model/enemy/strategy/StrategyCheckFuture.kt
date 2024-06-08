package com.unicorns.invisible.caravan.model.enemy.strategy

import android.os.Build
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

object StrategyCheckFuture : Strategy {
    val strategies = listOf(
        StrategyDestructive,
        StrategyRush,
        StrategyCareful,
        StrategyTime,
    )
    override fun move(game: Game): Boolean {
        strategies.toList().forEach {
            val copy = game.copy()
            it.move(copy)
            copy.processJacks()
            copy.processJoker()
            copy.checkOnGameOver()
            if (copy.isGameOver == -1) {
                it.move(game)
                return true
            } else {
                if (checkPlayerMoves(copy)) {
                    it.move(game)
                    game.saySomething(R.string.pve_enemy_best, R.string.ulysses_predict)
                    return true
                }
            }
        }

        return false
    }

    private fun checkPlayerMoves(game: Game): Boolean {
        val cards = Rank.entries.map { Card(it, Suit.entries.random(), CardBack.STANDARD, false) }

        fun cardJob(card: Card): Boolean {
            if (card.rank == Rank.QUEEN) {
                if (!checkMyMoves(game.copy())) {
                    return false
                }
            } else if (card.isFace()) {
                var gameCopy2 = game.copy()
                (gameCopy2.enemyCaravans + gameCopy2.playerCaravans).flatMap { it.cards }
                    .sortedByDescending { it.getValue() }
                    .filter { it.canAddModifier(card) }
                    .forEach { potentialCard ->
                        potentialCard.addModifier(card)
                        gameCopy2.processJacks()
                        gameCopy2.processJoker()
                        gameCopy2.checkOnGameOver()
                        if (gameCopy2.isGameOver == 1 || !checkMyMoves(gameCopy2)) {
                            return false
                        }
                        gameCopy2 = game.copy()
                    }
            } else {
                (0..2).forEach { caravanIndex ->
                    val copy = game.copy()
                    if (copy.playerCaravans[caravanIndex].canPutCardOnTop(card)) {
                        copy.playerCaravans[caravanIndex].putCardOnTop(card)
                        copy.processJacks()
                        copy.processJoker()
                        copy.checkOnGameOver()
                        if (copy.isGameOver == 1 || !checkMyMoves(copy)) {
                            return false
                        }
                    }
                }
            }
            return true
        }

        return runBlocking {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cards.parallelStream().map { cardJob(it) }.allMatch { it }
            } else {
                cards.map { card ->
                    CoroutineScope(Dispatchers.Unconfined).async {
                        cardJob(card)
                    }
                }.map { it.await() }.all { it }
            }
        }
    }

    private fun checkMyMoves(game: Game): Boolean {
        return strategies.toList().any {
            val copy = game.copy()
            it.move(copy)
            copy.processJoker()
            copy.processJacks()
            copy.checkOnGameOver()
            copy.isGameOver == -1
        }
    }
}