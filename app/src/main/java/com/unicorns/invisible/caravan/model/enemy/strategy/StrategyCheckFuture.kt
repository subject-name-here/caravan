package com.unicorns.invisible.caravan.model.enemy.strategy

import android.util.Log
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.multiplayer.MoveResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.concurrent.RecursiveTask


object StrategyCheckFuture : Strategy {
    private val gamesToOutcomes = mutableSetOf<Pair<String, Outcome>>()
    override fun move(game: Game): Boolean {
        Log.i("Ulysses", "Started predicting...")
        val chosenMove = selectMove(game.copy(), 5)

        val outcome = chosenMove.second
        when (outcome) {
            Outcome.UNKNOWN -> {
                Log.i("Ulysses", "Unknown...")
                return false
            }
            Outcome.VICTORY_SOON -> {
                game.saySomething(R.string.pve_enemy_best, R.string.feels_like_i_am_losing)
                return false
            }
            Outcome.DEFEAT_SOON -> {
                game.saySomething(R.string.pve_enemy_best, R.string.ulysses_predict)
            }
        }

        val move = chosenMove.first
        when (move.moveCode) {
            1 -> {
                if (move.caravanCode !in game.enemyCaravans.indices || game.enemyCaravans[move.caravanCode].isEmpty()) {
                    throw Exception("CORRUPTED 1")
                }
                game.enemyCaravans[move.caravanCode].dropCaravan()
            }
            2 -> {
                if (move.handCardNumber !in game.enemyCResources.hand.indices) {
                    game.isCorrupted = true
                    throw Exception("CORRUPTED 2")
                }
                game.enemyCResources.dropCardFromHand(move.handCardNumber)
            }
            3 -> {
                if (move.handCardNumber !in game.enemyCResources.hand.indices || move.caravanCode !in game.enemyCaravans.indices) {
                    throw Exception("CORRUPTED 3.1")
                }
                val card = game.enemyCResources.removeFromHand(move.handCardNumber)
                if (!game.enemyCaravans[move.caravanCode].canPutCardOnTop(card)) {
                    game.isCorrupted = true
                    throw Exception("CORRUPTED 3.2")
                }
                game.enemyCaravans[move.caravanCode].putCardOnTop(card)
            }
            4 -> {
                if (move.handCardNumber !in game.enemyCResources.hand.indices) {
                    throw Exception("CORRUPTED 4.1")
                }
                val card = game.enemyCResources.removeFromHand(move.handCardNumber)

                val cardInCaravan = if (move.caravanCode < 0) {
                    val playersCaravan = 3 + move.caravanCode
                    if (
                        playersCaravan !in game.playerCaravans.indices ||
                        move.cardInCaravanNumber !in game.playerCaravans[playersCaravan].cards.indices
                    ) {
                        throw Exception("CORRUPTED 4.2")
                    }
                    game.playerCaravans[playersCaravan].cards[move.cardInCaravanNumber]
                } else {
                    if (
                        move.caravanCode !in game.enemyCaravans.indices ||
                        move.cardInCaravanNumber !in game.enemyCaravans[move.caravanCode].cards.indices
                    ) {
                        throw Exception("CORRUPTED 4.3")
                    }
                    game.enemyCaravans[move.caravanCode].cards[move.cardInCaravanNumber]
                }
                if (!cardInCaravan.canAddModifier(card)) {
                    throw Exception("CORRUPTED 4.4")
                }
                cardInCaravan.addModifier(card)
            }
        }

        return true
    }


    // Map of pairs: if we make move KEY, our enemy gets outcome VALUE
    private fun checkMoves(game: Game, depth: Int, isEnemy: Boolean): ArrayList<Pair<MoveResponse, Outcome>> {
        if (depth <= 0) {
            return arrayListOf(MoveResponse() to Outcome.UNKNOWN, MoveResponse() to Outcome.UNKNOWN)
        }

        game.isPlayerTurn = isEnemy
        val hand = if (isEnemy)
            game.copy().enemyCResources.hand
        else
            Rank.entries.map { rank ->
                if (rank == Rank.JOKER || rank == Rank.KING || rank == Rank.JACK) {
                    listOf(Card(rank, Suit.HEARTS, CardBack.STANDARD, false))
                } else {
                    Suit.entries.map { suit ->
                        Card(rank, suit, CardBack.STANDARD, false)
                    }
                }
            }.flatten()

        val time = Date().time
        val reses = ArrayList<Pair<MoveResponse, Outcome>>()
        class AddToSmallReses(val gameCopy: Game, val moveResponse: MoveResponse) : RecursiveTask<Unit>() {
            override fun compute() {
                reses.add(moveResponse to if (gameCopy.isGameOver == if (isEnemy) 1 else -1) {
                    Outcome.VICTORY_SOON
                } else {
                    if (isEnemy) {
                        checkPlayerMoves(gameCopy, depth - 1)
                    } else {
                        selectMove(gameCopy, depth - 1).second
                    }
                })
            }
        }
        val tasks = mutableListOf<AddToSmallReses>()
        val jobs = mutableListOf<Job>()
        fun processCaravan(caravan: IndexedValue<Caravan>, isEnemyCaravan: Boolean) {
            runBlocking {
                if (isEnemy == isEnemyCaravan && caravan.value.getValue() > 0) {
                    jobs.add(launch(context = Dispatchers.Unconfined) {
                        val gameCopy = game.copy()
                        val caravanFromCopy =
                            (if (isEnemyCaravan) gameCopy.enemyCaravans else gameCopy.playerCaravans)[caravan.index]
                        caravanFromCopy.dropCaravan()
                        gameCopy.checkOnGameOver()
                        tasks.add(AddToSmallReses(
                            gameCopy, MoveResponse(
                                moveCode = 1,
                                caravanCode = caravan.index,
                            )
                        ).also { it.fork() })
                    })
                }

                hand.withIndex().forEach { (cardIndex, card) ->
                    if (card.isFace()) {
                        caravan.value.cards.withIndex().forEach { (potentialCardIndex, potentialCard) ->
                            if (potentialCard.canAddModifier(card)) {
                                if (card.rank == Rank.JOKER) {
                                    jobs.add(launch(context = Dispatchers.Unconfined) {
                                        val gameCopy2 = game.copy()
                                        val cardModified =
                                            (if (isEnemyCaravan) gameCopy2.enemyCaravans else gameCopy2.playerCaravans)[caravan.index].cards[potentialCardIndex]
                                        cardModified.addModifier(card)
                                        gameCopy2.putJokerOntoCard(cardModified.card)
                                        cardModified.deactivateJoker()
                                        gameCopy2.checkOnGameOver()
                                        tasks.add(AddToSmallReses(
                                            gameCopy2, MoveResponse(
                                                moveCode = 4,
                                                caravanCode = if (isEnemyCaravan) caravan.index else -3 + caravan.index,
                                                cardInCaravanNumber = caravan.value.cards.lastIndex,
                                                handCardNumber = cardIndex
                                            )
                                        ).also { it.fork() })
                                    })
                                } else {
                                    jobs.add(launch(context = Dispatchers.Unconfined) {
                                        val gameCopy = game.copy()
                                        val caravanFromCopy =
                                            (if (isEnemyCaravan) gameCopy.enemyCaravans else gameCopy.playerCaravans)[caravan.index]
                                        caravanFromCopy.cards[potentialCardIndex].addModifier(card)
                                        if (card.rank == Rank.JACK) {
                                            caravanFromCopy.removeAllJackedCards()
                                        }
                                        gameCopy.checkOnGameOver()
                                        tasks.add(AddToSmallReses(
                                            gameCopy, MoveResponse(
                                                moveCode = 4,
                                                caravanCode = if (isEnemyCaravan) caravan.index else -3 + caravan.index,
                                                cardInCaravanNumber = caravan.value.cards.lastIndex,
                                                handCardNumber = cardIndex
                                            )
                                        ).also { it.fork() })
                                    })
                                }
                            }
                        }
                    } else if (isEnemy == isEnemyCaravan) {
                        if (caravan.value.canPutCardOnTop(card)) {
                            jobs.add(launch(context = Dispatchers.Unconfined) {
                                val gameCopy = game.copy()
                                val caravanFromCopy =
                                    (if (isEnemyCaravan) gameCopy.enemyCaravans else gameCopy.playerCaravans)[caravan.index]
                                caravanFromCopy.putCardOnTop(card)
                                gameCopy.checkOnGameOver()

                                tasks.add(AddToSmallReses(
                                    gameCopy, MoveResponse(
                                        moveCode = 3,
                                        caravanCode = caravan.index,
                                        handCardNumber = cardIndex
                                    )
                                ).also { it.fork() })
                            })
                        }
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
        runBlocking {
            jobs.joinAll()
        }
        tasks.map {
            it.join()
        }


        val resP0 = if (isEnemy) {
            checkPlayerMoves(game.copy(), depth - 1)
        } else {
            selectMove(game.copy(), depth - 1).second
        }
        reses.add(MoveResponse(
            moveCode = 2,
            handCardNumber = hand.indices.random()
        ) to resP0)

        if (depth >= 1) {
            Log.i("Ulysses:time", (Date().time - time).toString() + " " + depth.toString())
        }

        return reses
    }


    private fun checkPlayerMoves(game: Game, depth: Int): Outcome {
        game.isPlayerTurn = true
        val gameString = synchronized(gamesToOutcomes) {
            gamesToOutcomes.find { it.first == GameRecord(game).serializeToString() }
        }
        if (gameString != null) {
            return gameString.second
        }

        val reses = checkMoves(game, depth, false).map { it.second }

        if (depth > 3 || reses.toList().any { it != Outcome.UNKNOWN }) {
            Log.i("Ulysses:You", reses.toString())
        }

        val majorOutcome = reses.distinct().map { it to reses.count { r -> r == it } }.maxBy { it.second }
        return when {
            reses.count { it.isVictory() } == reses.size -> Outcome.DEFEAT_SOON
            reses.any { it == Outcome.DEFEAT_SOON } -> Outcome.VICTORY_SOON
            majorOutcome.second >= reses.size * 3 / 4 -> majorOutcome.first
            else -> Outcome.UNKNOWN
        }.also {
            synchronized(gamesToOutcomes) {
                gamesToOutcomes.add(GameRecord(game).serializeToString() to it)
            }
        }
    }

    private fun selectMove(game: Game, depth: Int): Pair<MoveResponse, Outcome> {
        val reses = checkMoves(game, depth, true)
        if (depth > 3 || reses.toList().any { it.second != Outcome.UNKNOWN }) {
            Log.i("Ulysses:Me", reses.toString())
        }

        val bestMoves = reses.filter { it.second == Outcome.DEFEAT_SOON }
        if (bestMoves.isNotEmpty()) {
            return bestMoves.map { it.first }.sortedBy { it.moveCode }[0] to Outcome.VICTORY_SOON
        }

        val unknownMoves = reses.filter { it.second == Outcome.UNKNOWN }
        if (unknownMoves.isNotEmpty()) {
            return unknownMoves.map { it.first }.sortedBy { it.moveCode }[0] to Outcome.UNKNOWN
        }

        return reses.random().first to Outcome.DEFEAT_SOON
    }

    @Serializable
    class GameRecord(private val caravans: List<Caravan>) {
        constructor(game: Game) : this(game.playerCaravans + game.enemyCaravans)

        fun serializeToString(): String {
            val result = StringBuilder()
            caravans.forEach {
                it.cards.forEach { cardWithModifiers ->
                    result.append("${cardWithModifiers.card.rank.value} ${cardWithModifiers.card.suit.ordinal} ")
                    cardWithModifiers.modifiersCopy().forEach { modifier ->
                        if (modifier.rank == Rank.QUEEN) {
                            result.append("${modifier.rank.value} ${modifier.suit.ordinal} ")
                        } else {
                            result.append("${modifier.rank.value} ")
                        }
                    }
                }
                result.append("|")
            }
            return result.toString()
        }
    }

    enum class Outcome {
        UNKNOWN,
        DEFEAT_SOON,
        VICTORY_SOON;

        fun isVictory(): Boolean {
            val res = this != UNKNOWN && this != DEFEAT_SOON
            return res
        }
    }
}