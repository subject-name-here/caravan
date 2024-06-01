package com.unicorns.invisible.caravan.model.enemy.strategy

import android.util.Log
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.EnemyHard
import com.unicorns.invisible.caravan.model.enemy.EnemyNoBark
import com.unicorns.invisible.caravan.model.enemy.EnemySecuritron38
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object StrategyCheckFuture : Strategy {
    val strategies = listOf(
        object : Strategy {
            override fun move(game: Game): Boolean {
                EnemySecuritron38.makeMove(game)
                return true
            }
        },
        object : Strategy {
            override fun move(game: Game): Boolean {
                EnemyHard.makeMove(game)
                return true
            }
        },
        object : Strategy {
            override fun move(game: Game): Boolean {
                EnemyNoBark.makeMove(game)
                return true
            }
        },
        StrategyDestructive,
        StrategyRush,
        StrategyCareful,
        StrategyTime,
    )
    override fun move(game: Game): Boolean {
        val threshold = 0.05f
        strategies.toList().forEach {
            val copy = game.copy()
            it.move(copy)
            copy.processJacks()
            copy.processJoker()
            copy.checkOnGameOver()
            if (copy.isGameOver == -1) {
                it.move(game)
                Log.i("Ulysses", "I predict victory.")
                return true
            } else {
                val res = checkPlayerMoves(game)
                if (res > threshold) {
                    it.move(game)
                    Log.i("Ulysses", "I predict victory.")
                    return true
                }
            }
        }

        return false
    }

    private fun checkPlayerMoves(game: Game): Float {
        var res = 0
        var cnt = 0
        var block = false
        val cards = Rank.entries.map { Card(it, Suit.entries.random(), CardBack.STANDARD, false) }
        runBlocking {
            val jobs = cards.map { card ->
                CoroutineScope(Dispatchers.Unconfined).launch {
                    if (card.rank == Rank.QUEEN) {
                        cnt++
                        if (checkMyMoves(game.copy())) {
                            res++
                        }
                    } else if (card.isFace()) {
                        var gameCopy2 = game.copy()
                        (gameCopy2.playerCaravans + gameCopy2.enemyCaravans).flatMap { it.cards }
                            .filter { it.canAddModifier(card) }
                            .forEach { potentialCard ->
                                cnt++
                                potentialCard.addModifier(card)
                                gameCopy2.processJacks()
                                gameCopy2.processJoker()
                                gameCopy2.checkOnGameOver()
                                if (gameCopy2.isGameOver == 1) {
                                    block = true
                                    return@launch
                                }
                                if (checkMyMoves(gameCopy2)) {
                                    res++
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
                                cnt++
                                if (copy.isGameOver == 1) {
                                    block = true
                                    return@launch
                                }
                                if (checkMyMoves(copy)) {
                                    res++
                                }
                            }
                        }
                    }
                }
            }
            jobs.joinAll()
        }

        if (block) {
            Log.i("Ulysses", "I predict defeat.")
            return 0f
        }

        Log.i("Ulysses", "I predict $res / $cnt.")
        return res.toFloat() / cnt.toFloat()
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