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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.Date


object StrategyCheckFuture : Strategy {
    // Map of pairs: for Game S the best move is: if we make move KEY, our enemy gets outcome VALUE
    private val reses = mutableMapOf<String, Pair<Outcome, MoveResponse>>()
    private fun createJob(
        game: Game,
        moveResponse: MoveResponse,
        isEnemy: Boolean,
        depth: Int
    ): Job {
        return runBlocking {
            launch(Dispatchers.Unconfined) {
                val s = GameRecord(game, isEnemy).serializeToString()
                if (game.isGameOver == if (isEnemy) 1 else -1) {
                    reses[s] = Outcome.VICTORY_SOON to moveResponse
                    return@launch
                } else {
                    val gameCopy = game.copy()
                    makeMoveResponseOnGame(gameCopy, moveResponse, isEnemy)
                    if (isEnemy) {
                        checkPlayerMoves(gameCopy, depth - 1)
                    } else {
                        selectMove(gameCopy, depth - 1)
                    }
                    reses[s] = Outcome.NOT_FILLED to moveResponse
                }
            }
        }
    }

    private val tasks = ArrayList<Job>(10000)
    override fun move(game: Game): Boolean {
        Log.i("Ulysses", "Started predicting...")
        selectMove(game.copy(), 3)

        runBlocking {
            withContext(Dispatchers.Unconfined) {
                while (tasks.isNotEmpty()) {
                    Log.i("Ulysses", "Tasks size: ${tasks.size}")
                    synchronized(tasks) {
                        tasks.removeAll(tasks.filter { it.isCompleted }.toSet())
                    }
                    Log.i("Ulysses", "Tasks size: ${tasks.size}")
                    delay(95L)
                }
            }
        }

        buildTree(game.copy(), true, 3)

        Log.i("Ulysses", hits.toString())
        Log.i("Ulysses", reses.size.toString())

        val result = reses[GameRecord(game.copy(), true).toString()]
        if (result == null) {
            Log.i("Ulysses", "TREE FAILED")
            return false
        }

        when (result.first) {
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

            Outcome.NOT_FILLED -> {
                Log.i("Ulysses", "TREE FAILD!!!")
                return false
            }
        }

        makeMoveResponseOnGame(game, result.second, true)

        return true
    }

    private val playerHand = Rank.entries.map { rank ->
        if (rank == Rank.JOKER || rank == Rank.KING || rank == Rank.JACK) {
            listOf(Card(rank, Suit.HEARTS, CardBack.STANDARD, false))
        } else {
            Suit.entries.map { suit ->
                Card(rank, suit, CardBack.STANDARD, false)
            }
        }
    }.flatten()

    private fun makeMoveResponseOnGame(game: Game, move: MoveResponse, isEnemy: Boolean) {
        val caravans = if (isEnemy) game.enemyCaravans else game.playerCaravans
        val hand = if (isEnemy) game.enemyCResources.hand else playerHand
        when (move.moveCode) {
            1 -> {
                if (move.caravanCode !in caravans.indices || caravans[move.caravanCode].isEmpty()) {
                    throw Exception("CORRUPTED 1")
                }
                caravans[move.caravanCode].dropCaravan()
            }
            2 -> {
                if (move.handCardNumber !in hand.indices) {
                    game.isCorrupted = true
                    throw Exception("CORRUPTED 2")
                }
                if (isEnemy) {
                    game.enemyCResources.dropCardFromHand(move.handCardNumber)
                }
            }
            3 -> {
                if (move.handCardNumber !in hand.indices || move.caravanCode !in caravans.indices) {
                    throw Exception("CORRUPTED 3.1")
                }
                val card = if (isEnemy) game.enemyCResources.removeFromHand(move.handCardNumber) else hand[move.handCardNumber]
                if (!caravans[move.caravanCode].canPutCardOnTop(card)) {
                    game.isCorrupted = true
                    throw Exception("CORRUPTED 3.2")
                }
                caravans[move.caravanCode].putCardOnTop(card)
            }
            4 -> {
                if (move.handCardNumber !in hand.indices) {
                    throw Exception("CORRUPTED 4.1")
                }
                val card = if (isEnemy) game.enemyCResources.removeFromHand(move.handCardNumber) else hand[move.handCardNumber]

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
                        move.caravanCode !in caravans.indices ||
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
    }

    private fun checkMoves(game: Game, depth: Int, isEnemy: Boolean) {
        if (depth <= 0) {
            return
        }
        game.isPlayerTurn = isEnemy
        val hand = if (isEnemy)
            game.copy().enemyCResources.hand
        else
            playerHand

        val time = Date().time

        fun processCaravan(caravan: IndexedValue<Caravan>, isEnemyCaravan: Boolean) {
            if (isEnemy == isEnemyCaravan && caravan.value.getValue() > 0) {
                runBlocking { launch(Dispatchers.Unconfined) {
                    val gameCopy = game.copy()
                    val caravanFromCopy =
                        (if (isEnemyCaravan) gameCopy.enemyCaravans else gameCopy.playerCaravans)[caravan.index]
                    caravanFromCopy.dropCaravan()
                    gameCopy.checkOnGameOver()
                    tasks.add(createJob(
                        game, MoveResponse(
                            moveCode = 1,
                            caravanCode = caravan.index,
                        ), isEnemy, depth
                    ))
                } }
            }

            hand.withIndex().forEach { (cardIndex, card) ->
                if (card.isFace()) {
                    caravan.value.cards.withIndex().forEach { (potentialCardIndex, potentialCard) ->
                        if (potentialCard.canAddModifier(card)) {
                            if (card.rank == Rank.JOKER) {
                                runBlocking { launch(Dispatchers.Unconfined) {
                                    val gameCopy2 = game.copy()
                                    val cardModified =
                                        (if (isEnemyCaravan) gameCopy2.enemyCaravans else gameCopy2.playerCaravans)[caravan.index].cards[potentialCardIndex]
                                    cardModified.addModifier(card)
                                    gameCopy2.putJokerOntoCard(cardModified.card)
                                    cardModified.deactivateJoker()
                                    gameCopy2.checkOnGameOver()
                                    val task = createJob(
                                        game, MoveResponse(
                                            moveCode = 4,
                                            caravanCode = if (isEnemyCaravan) caravan.index else -3 + caravan.index,
                                            cardInCaravanNumber = potentialCardIndex,
                                            handCardNumber = cardIndex
                                        ), isEnemy, depth
                                    )
                                    tasks.add(task)
                                } }
                            } else {
                                runBlocking { launch(Dispatchers.Unconfined) {
                                    val gameCopy = game.copy()
                                    val caravanFromCopy =
                                        (if (isEnemyCaravan) gameCopy.enemyCaravans else gameCopy.playerCaravans)[caravan.index]
                                    caravanFromCopy.cards[potentialCardIndex].addModifier(card)
                                    if (card.rank == Rank.JACK) {
                                        caravanFromCopy.removeAllJackedCards()
                                    }
                                    gameCopy.checkOnGameOver()
                                    val task = createJob(
                                        game, MoveResponse(
                                            moveCode = 4,
                                            caravanCode = if (isEnemyCaravan) caravan.index else -3 + caravan.index,
                                            cardInCaravanNumber = potentialCardIndex,
                                            handCardNumber = cardIndex
                                        ), isEnemy, depth
                                    )
                                    tasks.add(task)
                                } }
                            }
                        }
                    }
                } else if (isEnemy == isEnemyCaravan) {
                    if (caravan.value.canPutCardOnTop(card)) {
                        runBlocking { launch(Dispatchers.Unconfined) {
                            val gameCopy = game.copy()
                            val caravanFromCopy =
                                (if (isEnemyCaravan) gameCopy.enemyCaravans else gameCopy.playerCaravans)[caravan.index]
                            caravanFromCopy.putCardOnTop(card)
                            gameCopy.checkOnGameOver()

                            val task = createJob(
                                game, MoveResponse(
                                    moveCode = 3,
                                    caravanCode = caravan.index,
                                    handCardNumber = cardIndex
                                ), isEnemy, depth
                            )
                            tasks.add(task)
                        } }
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

        if (isEnemy) {
            hand.indices.forEach {
                val task = createJob(
                    game, MoveResponse(
                        moveCode = 2,
                        handCardNumber = it
                    ), true, depth
                )
                tasks.add(task)
            }
        } else {
            val task = createJob(
                game, MoveResponse(
                    moveCode = 2,
                    handCardNumber = 0
                ), false, depth
            )
            tasks.add(task)
        }

        if (depth >= 2) {
            Log.i("Ulysses:time", (Date().time - time).toString() + " " + depth.toString())
        }
    }


    private fun checkPlayerMoves(game: Game, depth: Int) {
        val key = GameRecord(game, false).serializeToString()
        val gameString = synchronized(reses) { reses[key] }
        if (gameString != null) {
            return
        }
        synchronized(tasks) {
            checkMoves(game, depth, false)
        }
    }

    private fun selectMove(game: Game, depth: Int) {
        val key = GameRecord(game, true).serializeToString()
        val gameString = synchronized(reses) { reses[key] }
        if (gameString != null) {
            return
        }
        synchronized(tasks) {
            checkMoves(game, depth, true)
        }
    }

    var hits = 0
    private fun buildTree(game: Game, isEnemy: Boolean, depth: Int): Outcome {
        val res = reses[GameRecord(game, isEnemy).serializeToString()]
        if (res == null || depth <= 0) {
            throw Exception("EEEEEEE")
        }
        if (res.first != Outcome.NOT_FILLED) {
            return res.first
        }

        val outcomes = mutableListOf<Outcome>()
        hits++
        val hand = if (isEnemy)
            game.copy().enemyCResources.hand
        else
            playerHand

        val time = Date().time

        fun processCaravan(caravan: IndexedValue<Caravan>, isEnemyCaravan: Boolean) {
            if (isEnemy == isEnemyCaravan && caravan.value.getValue() > 0) {
                val copy = game.copy()
                makeMoveResponseOnGame(
                    copy, MoveResponse(
                        moveCode = 1,
                        caravanCode = caravan.index,
                    ), isEnemy
                )
                outcomes.add(buildTree(copy, !isEnemy, depth - 1))
            }

            hand.withIndex().forEach { (cardIndex, card) ->
                if (card.isFace()) {
                    caravan.value.cards.withIndex().forEach { (potentialCardIndex, potentialCard) ->
                        if (potentialCard.canAddModifier(card)) {
                            val copy = game.copy()
                            makeMoveResponseOnGame(
                                copy, MoveResponse(
                                    moveCode = 4,
                                    caravanCode = if (isEnemyCaravan) caravan.index else -3 + caravan.index,
                                    cardInCaravanNumber = potentialCardIndex,
                                    handCardNumber = cardIndex
                                ), isEnemy
                            )
                            copy.processJoker()
                            copy.processJacks()
                            outcomes.add(buildTree(copy, !isEnemy, depth - 1))
                        }
                    }
                } else if (isEnemy == isEnemyCaravan) {
                    if (caravan.value.canPutCardOnTop(card)) {
                        val copy = game.copy()
                        makeMoveResponseOnGame(
                            copy, MoveResponse(
                                moveCode = 3,
                                caravanCode = caravan.index,
                                handCardNumber = cardIndex
                            ), isEnemy
                        )
                        outcomes.add(buildTree(copy, !isEnemy, depth - 1))
                    }
                }
            }
        }

        runBlocking {
            game.enemyCaravans.mapIndexed { index, caravan ->
                launch(Dispatchers.Unconfined) {
                    processCaravan(IndexedValue(index, caravan), true)
                }
            }
            game.playerCaravans.mapIndexed { index, caravan ->
                launch(Dispatchers.Unconfined) {
                    processCaravan(IndexedValue(index, caravan), false)
                }
            }
        }

        if (isEnemy) {
            hand.indices.forEach {
                val copy = game.copy()
                makeMoveResponseOnGame(copy, MoveResponse(
                    moveCode = 2,
                    handCardNumber = it
                ), true)
                outcomes.add(buildTree(copy, false, depth - 1))
            }
        } else {
            val copy = game.copy()
            makeMoveResponseOnGame(copy, MoveResponse(
                moveCode = 2,
                handCardNumber = 0
            ), false)
            outcomes.add(buildTree(copy, true, depth - 1))
        }

        if (depth >= 2) {
            Log.i("Ulysses:time2", (Date().time - time).toString() + " " + depth.toString())
        }

        return when {
            outcomes.any { it == Outcome.VICTORY_SOON } -> Outcome.VICTORY_SOON
            outcomes.all { it == Outcome.DEFEAT_SOON } -> Outcome.DEFEAT_SOON
            else -> Outcome.UNKNOWN
        }
    }


    @Serializable
    class GameRecord(private val caravans: List<Caravan>, private val isEnemy: Boolean) {
        constructor(game: Game, isEnemy: Boolean) : this(game.playerCaravans + game.enemyCaravans, isEnemy)

        fun serializeToString(): String {
            val result = StringBuilder()
            result.append(isEnemy.toString())
            result.append(" || ")
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
                result.append("| ")
            }
            return result.toString()
        }
    }

    enum class Outcome {
        NOT_FILLED,
        UNKNOWN,
        DEFEAT_SOON,
        VICTORY_SOON;
    }
}