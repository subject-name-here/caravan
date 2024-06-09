package com.unicorns.invisible.caravan.model.enemy.strategy

import android.util.Log
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.multiplayer.MoveResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.max
import kotlin.math.min


object StrategyCheckFuture : Strategy {
    var playerHand = mutableListOf<Card>()
    // Map of pairs: for Game S the best move is: if we make move KEY, our enemy gets outcome VALUE
    private val reses = ConcurrentHashMap<String, Pair<Outcome, MoveResponse>>(10000)
    private val tasks = ConcurrentLinkedQueue<() -> Unit>()
    var pool = emptyList<Job>()
    fun startPool() {
        pool = (0..3).map {
            CoroutineScope(Dispatchers.Unconfined).launch {
                while (isActive) {
                    val time = Date().time
                    val times = (tasks.size / 16).coerceAtMost(8192) + 4
                    repeat(times) {
                        tasks.poll()?.invoke() ?: return@repeat
                    }
                    if (tasks.size > 0) {
                        Log.i("Ulysses", (Date().time - time).toString() + " " + tasks.size)
                    }

                    delay(95L + (0..38).random())
                }
            }
        }
    }
    fun stopPool() {
        pool.forEach { it.cancel() }
    }

//
//    private fun createJob(
//        game: Game,
//        moveResponse: MoveResponse,
//        isEnemy: Boolean,
//        depth: Int
//    ) {
//        val s = GameRecord(game, isEnemy).serializeToString()
//        if (game.isGameOver == if (isEnemy) 1 else -1) {
//            reses[s] = Outcome.VICTORY_SOON to moveResponse
//        } else if (depth <= 0) {
//            reses[s] = Outcome.UNKNOWN to moveResponse
//        } else {
//            val gameCopy = game.copy()
//            makeMoveResponseOnGame(gameCopy, moveResponse, isEnemy)
//            if (moveResponse.moveCode == 4) {
//                gameCopy.processJacks()
//                gameCopy.processJoker()
//            }
//            if (isEnemy) {
//                checkPlayerMoves(gameCopy, depth - 1)
//            } else {
//                selectMove(gameCopy, depth - 1)
//            }
//        }
//    }


    var depth = 2
    override fun move(game: Game): Boolean {
        Log.i("Ulysses", "Started predicting...")
//        selectMove(game.copy(), depth)
//
//        runBlocking {
//            withContext(Dispatchers.Default) {
//                while (tasks.isNotEmpty()) {
//                    delay(380L)
//                }
//            }
//        }
        Log.i("Ulysses", "Started building tree...")
        buildTree(game.copy(), true, depth)

        Log.i("Ulysses", reses.size.toString())
        Log.i("Ulysses", reses.filter { it.value.first != Outcome.UNKNOWN }.size.toString())

        val result = reses[GameRecord(game.copy(), true).serializeToString()]
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
        }

        makeMoveResponseOnGame(game, result.second, true)

        return true
    }

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
            0 -> {
                throw Exception("CORRUPTED 000-000-000")
            }
        }
    }
//
//    private fun checkMoves(game: Game, depth: Int, isEnemy: Boolean) {
//        game.isPlayerTurn = isEnemy
//        val hand = if (isEnemy)
//            game.enemyCResources.hand
//        else
//            playerHand
//
//        fun processCaravan(caravan: IndexedValue<Caravan>, isEnemyCaravan: Boolean) {
//            if (isEnemy == isEnemyCaravan && caravan.value.getValue() > 0) {
//                tasks.add {
//                    val gameCopy = game.copy()
//                    val caravanFromCopy = (if (isEnemyCaravan) gameCopy.enemyCaravans else gameCopy.playerCaravans)[caravan.index]
//                    caravanFromCopy.dropCaravan()
//                    gameCopy.checkOnGameOver()
//                    createJob(
//                        game, MoveResponse(
//                            moveCode = 1,
//                            caravanCode = caravan.index,
//                        ), isEnemy, depth
//                    )
//                }
//            }
//            hand
//                .withIndex()
//                .filter { if (it.value.isFace()) true else caravan.value.canPutCardOnTop(it.value) }
//                .forEach { (cardIndex, card) ->
//                    if (card.rank == Rank.JOKER) {
//                        caravan.value.cards.withIndex()
//                            .filter { it.value.canAddModifier(card) }
//                            .forEach { (potentialCardIndex, _) ->
//                                tasks.add {
//                                    val gameCopy2 = game.copy()
//                                    val cardModified =
//                                        (if (isEnemyCaravan) gameCopy2.enemyCaravans else gameCopy2.playerCaravans)[caravan.index].cards[potentialCardIndex]
//                                    cardModified.addModifier(card)
//                                    gameCopy2.putJokerOntoCard(cardModified.card)
//                                    cardModified.deactivateJoker()
//                                    gameCopy2.checkOnGameOver()
//                                    createJob(
//                                        game, MoveResponse(
//                                            moveCode = 4,
//                                            caravanCode = if (isEnemyCaravan) caravan.index else -3 + caravan.index,
//                                            cardInCaravanNumber = potentialCardIndex,
//                                            handCardNumber = cardIndex
//                                        ), isEnemy, depth
//                                    )
//                                }
//                            }
//                    } else if (card.isFace()) {
//                        tasks.add {
//                            caravan.value.cards.withIndex()
//                                .filter { it.value.canAddModifier(card) }
//                                .forEach { (potentialCardIndex, _) ->
//                                    val gameCopy = game.copy()
//                                    val caravanFromCopy =
//                                        (if (isEnemyCaravan) gameCopy.enemyCaravans else gameCopy.playerCaravans)[caravan.index]
//                                    caravanFromCopy.cards[potentialCardIndex].addModifier(card)
//                                    if (card.rank == Rank.JACK) {
//                                        caravanFromCopy.removeAllJackedCards()
//                                    }
//                                    gameCopy.checkOnGameOver()
//                                    createJob(
//                                        game, MoveResponse(
//                                            moveCode = 4,
//                                            caravanCode = if (isEnemyCaravan) caravan.index else -3 + caravan.index,
//                                            cardInCaravanNumber = potentialCardIndex,
//                                            handCardNumber = cardIndex
//                                        ), isEnemy, depth
//                                    )
//                                }
//                        }
//                    } else if (isEnemy == isEnemyCaravan) {
//                        tasks.add {
//                            val gameCopy = game.copy()
//                            val caravanFromCopy =
//                                (if (isEnemyCaravan) gameCopy.enemyCaravans else gameCopy.playerCaravans)[caravan.index]
//                            caravanFromCopy.putCardOnTop(card)
//                            gameCopy.checkOnGameOver()
//
//                            createJob(
//                                game, MoveResponse(
//                                    moveCode = 3,
//                                    caravanCode = caravan.index,
//                                    handCardNumber = cardIndex
//                                ), isEnemy, depth
//                            )
//                        }
//                    }
//                }
//        }
//
//        game.enemyCaravans.mapIndexed { index, caravan ->
//            processCaravan(IndexedValue(index, caravan), true)
//        }
//        game.playerCaravans.mapIndexed { index, caravan ->
//            processCaravan(IndexedValue(index, caravan), false)
//        }
//
//        tasks.add {
//            if (isEnemy) {
//                hand.indices.forEach {
//                    createJob(
//                        game, MoveResponse(
//                            moveCode = 2,
//                            handCardNumber = it
//                        ), true, depth
//                    )
//                }
//            } else {
//                createJob(
//                    game, MoveResponse(
//                        moveCode = 2,
//                        handCardNumber = 0
//                    ), false, depth
//                )
//            }
//        }
//    }


    private fun buildTree(game: Game, isEnemy: Boolean, depth: Int): Pair<Outcome, MoveResponse> {
        val res = reses[GameRecord(game, isEnemy).serializeToString()]
//        if (depth <= 0 && res == null) {
//            Log.i("Ulysses", "TREE WEIRD EEEEEE $depth $isEnemy")
//            return Outcome.UNKNOWN to MoveResponse()
//        }
        if (res != null) {
            return res
        }

        if (depth <= 0) {
            val s = GameRecord(game, isEnemy).serializeToString()
            if (game.isGameOver == if (isEnemy) 1 else -1) {
                reses[s] = Outcome.VICTORY_SOON to MoveResponse()
            } else {
                reses[s] = Outcome.UNKNOWN to MoveResponse()
            }
            return reses[s]!!
        }

        val outcomes = mutableListOf<Pair<Outcome, MoveResponse>>()
        val hand = if (isEnemy)
            game.copy().enemyCResources.hand
        else
            playerHand

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

        game.enemyCaravans.mapIndexed { index, caravan ->
            processCaravan(IndexedValue(index, caravan), true)
        }
        game.playerCaravans.mapIndexed { index, caravan ->
            processCaravan(IndexedValue(index, caravan), false)
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

        val best = outcomes.filter { it.first == Outcome.VICTORY_SOON }
        val result = when {
            best.isNotEmpty() -> best.minByOrNull { it.second.moveCode }!!
            outcomes.all { it.first == Outcome.DEFEAT_SOON } -> outcomes.first()
            else -> outcomes.filter { it.first == Outcome.UNKNOWN }.random()
        }
        reses[GameRecord(game, isEnemy).serializeToString()] = result
        return result
    }


    @Serializable
    class GameRecord(private val caravans: List<Caravan>, private val isEnemyMove: Boolean, private val hand: List<Card>) {
        constructor(game: Game, isEnemyMove: Boolean) : this(game.playerCaravans + game.enemyCaravans, isEnemyMove, game.enemyCResources.hand)

        fun serializeToString(): String {
            val result = StringBuilder()
            result.append("$isEnemyMove ")
            hand.sortedBy { it.rank.ordinal * Suit.entries.size + it.suit.ordinal }.forEach {
                result.append(cardToString(it))
            }
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
        UNKNOWN,
        DEFEAT_SOON,
        VICTORY_SOON;
    }
}