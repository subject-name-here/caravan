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
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.random.Random


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
        Log.i("Ulysses", "Started the tree...")
        buildTree(game, true, depth, listOf())

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
        } else {
            Log.i("Ulysses", "Failed to make a move...")
        }
        // TODO: remove all unknowns?
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

    private fun buildTree(game: Game, isEnemy: Boolean, depth: Int, usedCards: List<Card?>) {
        val s = GameRecord(game, isEnemy).serializeToString()
        if (s in reses.keys().toList()) {
            return
        }
        if (game.isGameOver != 0) {
            reses[s] = (when {
                game.isGameOver == 1 && isEnemy -> Outcome.DEFEAT_SOON
                game.isGameOver == 1 && !isEnemy -> Outcome.VICTORY_SOON
                game.isGameOver == -1 && !isEnemy -> Outcome.DEFEAT_SOON
                else -> Outcome.VICTORY_SOON
            }) to Move()
        }
        if (depth == 0) {
            reses[s] = Outcome.UNKNOWN to Move()
        }


        val edges = mutableListOf<Pair<Move, Card?>>()

        val hand = if (isEnemy) game.enemyCResources.hand else getPlayerHand(game)
        fun processCaravan(caravan: IndexedValue<Caravan>, isEnemyCaravan: Boolean) {
            if (isEnemy == isEnemyCaravan && caravan.value.getValue() > 0) {
                val move = Move(
                    moveCode = 1,
                    caravanCode = caravan.index,
                )
                edges.add(move to null)
            }

            hand.withIndex().forEach { (_, card) ->
                if (card.isFace()) {
                    caravan.value.cards.withIndex().forEach { (potentialCardIndex, potentialCard) ->
                        if (potentialCard.canAddModifier(card)) {
                            val move = Move(
                                moveCode = 4,
                                caravanCode = if (isEnemyCaravan) caravan.index else -3 + caravan.index,
                                cardInCaravanNumber = potentialCardIndex,
                                handCardRank = card.rank.ordinal,
                                handCardSuit = card.suit.ordinal
                            )
                            edges.add(move to card)
                        }
                    }
                } else if (isEnemy == isEnemyCaravan) {
                    if (caravan.value.canPutCardOnTop(card)) {
                        val move = Move(
                            moveCode = 3,
                            caravanCode = caravan.index,
                            handCardRank = card.rank.ordinal,
                            handCardSuit = card.suit.ordinal
                        )
                        edges.add(move to card)
                    }
                }
            }
        }

        game.enemyCaravans.mapIndexed { index, caravan ->
            processCaravan(IndexedValue(index, caravan), true)
        }
        game.playerCaravans.mapIndexed { index, caravan ->
            processCaravan(IndexedValue(index, caravan), false)
        }

        hand.forEach {
            val move = Move(
                moveCode = 2,
                handCardRank = it.rank.ordinal,
                handCardSuit = it.suit.ordinal,
            )
            edges.add(move to it)
        }

        val outcomeToMove = mutableListOf<Pair<Outcome, Move>>()
        edges.forEach { e ->
            val copy = game.copy()
            val card = e.second
            // Here we heavily use the fact that both Ulysses & player have deck o' 54.
            if (card == null || usedCards.none { it?.rank == card.rank && it.suit == card.suit }) {
                makeMoveResponseOnGame(copy, e.first, isEnemy)
                copy.processJacks()
                copy.processJoker()
                copy.checkOnGameOver()
                val son = GameRecord(copy, !isEnemy).serializeToString()
                buildTree(copy, !isEnemy, depth - 1, usedCards.toMutableList() + card)
                outcomeToMove.add(reses[son]!!.first to e.first)
            }
        }

        val outcomesGrouped = outcomeToMove.groupBy { it.first }
        val bestest = outcomesGrouped[Outcome.DEFEAT_SOON] ?: emptyList()
        val okay = outcomesGrouped[Outcome.UNKNOWN] ?: emptyList()
        val bad = outcomesGrouped[Outcome.VICTORY_SOON] ?: emptyList()
        val random = Random(22229)
        reses[s] = when {
            bestest.isNotEmpty() -> {
                Outcome.VICTORY_SOON to bestest.random(random).second
            }
            okay.isEmpty() -> {
                Outcome.DEFEAT_SOON to bad.random(random).second
            }
            else -> {
                if (bad.size >= outcomeToMove.size * 0.9) {
                    Outcome.DEFEAT_SOON to okay.random(random).second
                } else {
                    Outcome.UNKNOWN to okay.random(random).second
                }
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
    }

    data class Move(
        val moveCode: Int = 0,
        val caravanCode: Int = 0,
        val cardInCaravanNumber: Int = 0,
        val handCardRank: Int = 0,
        val handCardSuit: Int = 0,
    )
}