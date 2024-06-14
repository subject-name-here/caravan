package com.unicorns.invisible.caravan.model.enemy.strategy

import android.util.Log
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap


object StrategyCheckFuture : Strategy {
    // For the KEY game state we have VALUE.first outcome as most probable, and the advised move is VALUE.second.
    val reses = ConcurrentHashMap<String, Pair<Outcome, Move>>(10000)
    private fun getPlayerHand(game: Game): List<Card> {
        val hand = (game.playerCResources.hand + game.playerCResources.getDeckCopy())
            .sortedBy { it.rank.ordinal * Suit.entries.size + it.suit.ordinal }
            .toMutableList()
        return hand
    }

    private const val depth = 4 // DO NOT CHANGE!!!! Algorithm is sharpened for 4!
    override fun move(game: Game): Boolean {
        Log.i("Ulysses", "Started searching for terminals...")
        findTerminals(game.copy(), true)
        Log.i("Ulysses", "Propagating terminals...")
        propagateTerminals(game.copy(), true, depth)

        Log.i("Ulysses1.5", "full: " + reses.size.toString())
        Log.i("Ulysses1.5", "vics: " + reses.filter { it.value.first == Outcome.VICTORY_SOON }.size.toString())
        Log.i("Ulysses1.5", "defs: " + reses.filter { it.value.first == Outcome.DEFEAT_SOON }.size.toString())

        val result = reses[GameRecord(game.copy(), true).serializeToString()]!!
        val moveRes = makeMoveResponseOnGame(game, result.second, true)

        if (moveRes) {
            when (result.first) {
                Outcome.DEFEAT_SOON -> {
                    game.saySomething(R.string.pve_enemy_best, R.string.feels_like_i_am_losing)
                }
                Outcome.UNKNOWN -> {
                    Log.i("Ulysses", "Unknown...")
                }
                Outcome.VICTORY_SOON -> {
                    game.saySomething(R.string.pve_enemy_best, R.string.ulysses_predict)
                }
            }
        }
        return moveRes
    }

    private fun makeMoveResponseOnGame(game: Game, move: Move, isEnemy: Boolean): Boolean {
        val caravans = if (isEnemy) game.enemyCaravans else game.playerCaravans
        val hand = if (isEnemy) game.enemyCResources.hand else getPlayerHand(game)
        when (move.moveCode) {
            1 -> {
                if (move.caravanCode !in caravans.indices || caravans[move.caravanCode].isEmpty()) {
                    return false
                }
                caravans[move.caravanCode].dropCaravan()
            }
            2 -> {
                val handCard = hand.indexOfFirst { it.rank.ordinal == move.handCardRank && it.suit.ordinal == move.handCardSuit }
                if (handCard == -1) {
                    return false
                }
                if (isEnemy) {
                    game.enemyCResources.dropCardFromHand(handCard)
                }
            }
            3 -> {
                val handCard = hand.indexOfFirst { it.rank.ordinal == move.handCardRank && it.suit.ordinal == move.handCardSuit }
                if (handCard == -1) {
                    return false
                }
                val card = hand[handCard]
                if (!caravans[move.caravanCode].canPutCardOnTop(card)) {
                    return false
                }
                caravans[move.caravanCode].putCardOnTop(game.enemyCResources.removeFromHand(handCard))
            }
            4 -> {
                val handCard = hand.indexOfFirst { it.rank.ordinal == move.handCardRank && it.suit.ordinal == move.handCardSuit }
                if (handCard == -1) {
                    return false
                }
                val card = hand[handCard]

                val cardInCaravan = if (move.caravanCode < 0) {
                    val playersCaravan = 3 + move.caravanCode
                    if (
                        playersCaravan !in game.playerCaravans.indices ||
                        move.cardInCaravanNumber !in game.playerCaravans[playersCaravan].cards.indices
                    ) {
                        return false
                    }
                    game.playerCaravans[playersCaravan].cards[move.cardInCaravanNumber]
                } else {
                    if (
                        move.caravanCode !in caravans.indices ||
                        move.cardInCaravanNumber !in game.enemyCaravans[move.caravanCode].cards.indices
                    ) {
                        return false
                    }
                    game.enemyCaravans[move.caravanCode].cards[move.cardInCaravanNumber]
                }
                if (!cardInCaravan.canAddModifier(card)) {
                    return false
                }
                cardInCaravan.addModifier(game.enemyCResources.removeFromHand(handCard))
            }
            0 -> {
                throw Exception("CORRUPTED 000-000-000")
            }
        }
        return true
    }

    fun findTerminals(game: Game, isEnemy: Boolean) {
        val playerHand: MutableList<Card?> = getPlayerHand(game).toMutableList()
        playerHand.add(null)
        val enemyHand: MutableList<Card?> = game.copy().enemyCResources.hand.toMutableList()
        enemyHand.add(null)
        val hand1 = if (isEnemy) enemyHand else playerHand
        val hand2 = if (isEnemy) playerHand else enemyHand

        hand1.indices.forEach { c11 ->
            val card11 = hand1[c11]
            hand2.indices.forEach { c21 ->
                val card21 = hand2[c21]
                hand1.indices.forEach { c12 ->
                    if (c12 != c11) {
                        val card12 = hand1[c12]
                        (hand1.indices.toMutableList() - c21).forEach { c22 ->
                            if (c22 != c21) {
                                val card22 = hand2[c22]
                                markTerminals(game.copy(), listOf(card11, card21, card12, card22), true)
                            }
                        }
                    }
                }
            }
        }
    }

    data class MutablePair<A, B>(val first: A, var second: B)
    // If value.second == -1, it means the move is Victorious. Otherwise, it shows how many DEFEAT_SOON outcomes are among the moves.
    private val visitedToMoves = ConcurrentHashMap<String, MutablePair<ArrayList<Move>, Int>>(2000)
    private fun markTerminals(game: Game, cards: List<Card?>, isEnemy: Boolean) {
        val s = GameRecord(game, isEnemy).serializeToString()
        visitedToMoves.putIfAbsent(s, MutablePair(arrayListOf(), 0))

        if (game.isGameOver != 0) {
            reses[s] = when {
                game.isGameOver == 1 && isEnemy -> Outcome.DEFEAT_SOON to Move()
                game.isGameOver == -1 && isEnemy -> Outcome.VICTORY_SOON to Move()
                game.isGameOver == 1 && !isEnemy -> Outcome.VICTORY_SOON to Move()
                else -> Outcome.DEFEAT_SOON to Move()
            }
            Log.i("Ulysses", "GO: ${cards.size}, go=${game.isGameOver}")
            return
        }
        if (cards.isEmpty()) {
            return
        }

        val jobs = ArrayList<Job>(128)
        fun addJob(copy: Game, move: Move) {
            if (visitedToMoves[s]!!.first.contains(move)) {
                return
            }

            val task = {
                markTerminals(copy, cards.drop(1), !isEnemy)
                visitedToMoves[s]!!.first.add(move)
                val son = GameRecord(copy, !isEnemy).serializeToString()
                if (reses[son]?.first == Outcome.DEFEAT_SOON) {
                    Log.i("Ulysses", "Son is Defeat")
                    reses[s] = Outcome.VICTORY_SOON to move
                    visitedToMoves[s]?.second = -1
                } else if (reses[son]?.first == Outcome.VICTORY_SOON) {
                    Log.i("Ulysses", "Son is Victory")
                    if (visitedToMoves[s]?.second != -1) {
                        visitedToMoves[s]?.second?.inc()
                    }
                }
            }

            jobs.add(CoroutineScope(Dispatchers.Unconfined).launch { task() })
        }

        val card = cards.first()
        if (card?.isFace() == true) {
            fun processCaravan(caravan: Caravan, index: Int) {
                caravan.cards.withIndex()
                    .filter {
                        if (card.rank == Rank.QUEEN && it.index < caravan.size - 2) {
                            return@filter false
                        }
                        it.value.canAddModifier(card)
                    }
                    .forEach { (potentialCardIndex, _) ->
                        val copy = game.copy()
                        val move = Move(
                            moveCode = 4,
                            caravanCode = index,
                            cardInCaravanNumber = potentialCardIndex,
                            handCardRank = card.rank.ordinal,
                            handCardSuit = card.suit.ordinal
                        )
                        makeMoveResponseOnGame(copy, move, isEnemy)
                        copy.processJoker()
                        copy.processJacks()
                        copy.checkOnGameOver()
                        addJob(copy, move)
                    }
            }
            game.enemyCaravans.forEachIndexed { index, caravan ->
                processCaravan(caravan, index)
            }

            game.playerCaravans.forEachIndexed { index, caravan ->
                processCaravan(caravan, -3 + index)
            }
        } else if (card == null) {
            (if (isEnemy) game.enemyCaravans else game.playerCaravans).forEachIndexed { index, caravan ->
                if (!caravan.isEmpty()) {
                    val copy = game.copy()
                    val move = Move(
                        moveCode = 1,
                        caravanCode = index,
                    )
                    makeMoveResponseOnGame(copy, move, isEnemy)
                    copy.checkOnGameOver()
                    addJob(copy, move)
                }
            }
        } else {
            (if (isEnemy) game.enemyCaravans else game.playerCaravans).forEachIndexed { index, _ ->
                val copy = game.copy()
                val move = Move(
                    moveCode = 3,
                    caravanCode = index,
                    handCardRank = card.rank.ordinal,
                    handCardSuit = card.suit.ordinal
                )
                makeMoveResponseOnGame(copy, move, isEnemy)
                copy.checkOnGameOver()
                addJob(copy, move)
            }
        }
        if (card != null) {
            val copy = game.copy()
            val move = Move(
                moveCode = 2,
                handCardRank = card.rank.ordinal,
                handCardSuit = card.suit.ordinal
            )
            makeMoveResponseOnGame(copy, move, true)
            addJob(copy, move)
        }

        runBlocking { jobs.joinAll() }
    }

    private fun propagateTerminals(game: Game, isEnemy: Boolean, depth: Int) {
        if (depth == 0) {
            return
        }
        val s = GameRecord(game, isEnemy).serializeToString()

        val moves = visitedToMoves[s]!!.first
        val flag = visitedToMoves[s]!!.second
        if (flag == -1 || reses.containsKey(s)) {
            return
        } else {
            val outcomes = arrayListOf<Pair<Outcome?, Move>>()
            moves.forEach { move ->
                val copy = game.copy()
                makeMoveResponseOnGame(copy, move, isEnemy)
                copy.processJoker()
                copy.processJacks()
                copy.checkOnGameOver()
                propagateTerminals(copy, !isEnemy, depth - 1)
                val res = reses[GameRecord(copy, !isEnemy).serializeToString()]
                outcomes.add(res?.first?.reversed() to move)
            }
            val outcomesGroup = outcomes.groupBy { it.first }
            val okayMove = outcomesGroup[null]?.firstOrNull()
            if (flag >= moves.size * 0.9f) {
                reses[s] = Outcome.DEFEAT_SOON to (okayMove?.second ?: outcomes.first().second)
            } else {
                reses[s] = Outcome.UNKNOWN to okayMove!!.second
            }
        }
    }

    @Serializable
    class GameRecord(private val caravans: List<Caravan>, private val isEnemyMove: Boolean) {
        constructor(game: Game, isEnemyMove: Boolean) : this(game.playerCaravans + game.enemyCaravans, isEnemyMove)

        fun serializeToString(): String {
            val result = StringBuilder()
            result.append("$isEnemyMove ")
            result.append("|| ")
            caravans.forEach {
                caravanToString(result, it)
                result.append("| ")
            }
            return result.toString()
        }
    }

    private fun caravanToString(result: StringBuilder, it: Caravan) {
        it.cards.forEach { cardWithModifiers ->
            result.append(cardToString(cardWithModifiers.card))
            cardWithModifiers.modifiersCopy().forEach { modifier ->
                cardToString(modifier)
            }
        }
    }
    private fun cardToString(card: Card): String {
        return if (!card.isFace() || card.rank == Rank.QUEEN) {
            "${card.rank.ordinal} ${card.suit.ordinal} "
        } else {
            "${card.rank.ordinal} "
        }
    }

    enum class Outcome {
        DEFEAT_SOON,
        UNKNOWN,
        VICTORY_SOON;

        fun reversed(): Outcome? {
            return when (this) {
                DEFEAT_SOON -> VICTORY_SOON
                VICTORY_SOON -> DEFEAT_SOON
                UNKNOWN -> null
            }
        }
    }

    data class Move(
        val moveCode: Int = 0,
        val caravanCode: Int = 0,
        val cardInCaravanNumber: Int = 0,
        val handCardRank: Int = 0,
        val handCardSuit: Int = 0,
    )
}