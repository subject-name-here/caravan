package com.unicorns.invisible.caravan.model.enemy.strategy

import android.util.Log
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString


object StrategyCheckFuture : Strategy {
    val strategies = listOf(
        StrategyDestructive,
        StrategyRush,
        StrategyCareful,
        StrategyTime,
        StrategyJoker,
    )
    override fun move(game: Game): Boolean {
        strategies.toList().forEachIndexed { index, it ->
            val copy = game.copy()
            Log.i("Ulysses", "START$index")
            if (it.move(copy)) {
                copy.processJacks()
                copy.processJoker()
                copy.checkOnGameOver()
                if (copy.isGameOver == -1) {
                    it.move(game)
                    return true
                } else {
                    val outcome = checkPlayerMoves(copy, depth = 2)
                    when (outcome) {
                        Outcome.UNKNOWN -> {}
                        Outcome.DEFEAT_SOON -> {}
                        Outcome.VICTORY_NEXT_MOVE -> {
                            if (it.move(game)) {
                                game.saySomething(R.string.pve_enemy_best, R.string.ulysses_predict)
                                return true
                            }
                        }
                        Outcome.VICTORY_NEXT_TWO_MOVES -> {
                            if (it.move(game)) {
                                game.saySomething(R.string.pve_enemy_best, R.string.i_predict_your_certain_doom_next_in_two_moves)
                                return true
                            }
                        }
                    }
                }
            }
            Log.i("Ulysses", "END$index")
        }

        return false
    }


    private val gamesToOutcomes = mutableListOf<Pair<String, Outcome>>()
    private fun checkPlayerMoves(game: Game, depth: Int): Outcome {
        val gameString = gamesToOutcomes.find { it.first == GameRecord(game).serializeToString() }
        if (gameString != null) {
            Log.i("HIT", " THE CACHE!!!!")
            return gameString.second
        }

        var defeatFlag = false
        fun processCaravan(gameCopy: Game, isEnemy: Boolean, caravan: Caravan) = CoroutineScope(Dispatchers.Unconfined).async {
            val smallReses = mutableListOf<Outcome>()
            fun addToSmallReses() {
                if (gameCopy.isGameOver == 1) {
                    smallReses.add(Outcome.DEFEAT_SOON)
                    defeatFlag = true
                } else if (defeatFlag) {
                    smallReses.add(when (checkMyMoves(gameCopy, depth)) {
                        Outcome.UNKNOWN -> Outcome.UNKNOWN
                        Outcome.DEFEAT_SOON -> Outcome.DEFEAT_SOON
                        Outcome.VICTORY_NEXT_MOVE -> Outcome.VICTORY_NEXT_TWO_MOVES
                        Outcome.VICTORY_NEXT_TWO_MOVES -> Outcome.VICTORY_NEXT_TWO_MOVES
                    })
                }
            }

            val caravanSave = Caravan()
            caravanSave.copyFrom(caravan)
            if (!isEnemy) {
                Rank.entries.filter { !it.isFace() }.forEach { rank ->
                    val card = Card(rank, Suit.HEARTS, CardBack.STANDARD, false)
                    if (caravan.canPutCardOnTop(card)) {
                        caravan.putCardOnTop(card)
                        gameCopy.checkOnGameOver()

                        caravan.copyFrom(caravanSave)
                    }
                }

                if (caravan.getValue() > 0) {
                    caravan.dropCaravan()
                    gameCopy.checkOnGameOver()
                    addToSmallReses()
                    caravan.copyFrom(caravanSave)
                }
            }
            Rank.entries.filter { it == Rank.JACK || it == Rank.KING }.forEach { rank ->
                val card = Card(rank, Suit.HEARTS, CardBack.STANDARD, false)
                caravan.cards.forEach { potentialCard ->
                    if (potentialCard.canAddModifier(card)) {
                        potentialCard.addModifier(card)
                        gameCopy.processJacks()
                        gameCopy.checkOnGameOver()
                        addToSmallReses()
                        caravan.copyFrom(caravanSave)
                    }
                }
            }
            Suit.entries.forEach {
                val card = Card(Rank.QUEEN, it, CardBack.STANDARD, false)
                if (!caravan.isEmpty()) {
                    val potentialCard = caravan.cards.last()
                    if (potentialCard.canAddModifier(card)) {
                        potentialCard.addModifier(card)
                        addToSmallReses()
                        caravan.copyFrom(caravanSave)
                    }
                }
            }
            smallReses
        }

        val caravans = game.playerCaravans.map { it to false } + game.enemyCaravans.map { it to true }
        val reses = mutableListOf<Outcome>()
        val deferreds = caravans.map { (caravan, isEnemy) ->
            processCaravan(game.copy(), isEnemy, caravan)
        }
        runBlocking {
            reses.addAll(deferreds.awaitAll().flatten())
        }

        val gameCopy = game.copy()

        val card = Card(Rank.JOKER, Suit.HEARTS, CardBack.STANDARD, false)
        var gameCopy2 = gameCopy.copy()
        (gameCopy2.playerCaravans + gameCopy2.enemyCaravans).forEach { caravan ->
            caravan.cards.forEach { potentialCard ->
                if (potentialCard.canAddModifier(card)) {
                    potentialCard.addModifier(card)
                    gameCopy2.processJoker()
                    gameCopy2.checkOnGameOver()
                    if (gameCopy.isGameOver == 1) {
                        reses.add(Outcome.DEFEAT_SOON)
                    } else {
                        reses.add(when (checkMyMoves(gameCopy, depth)) {
                            Outcome.UNKNOWN -> Outcome.UNKNOWN
                            Outcome.DEFEAT_SOON -> Outcome.DEFEAT_SOON
                            Outcome.VICTORY_NEXT_MOVE -> Outcome.VICTORY_NEXT_TWO_MOVES
                            Outcome.VICTORY_NEXT_TWO_MOVES -> Outcome.VICTORY_NEXT_TWO_MOVES
                        })
                    }
                    gameCopy2 = gameCopy.copy()
                }
            }
        }

        reses.add(when (checkMyMoves(gameCopy, depth)) {
            Outcome.UNKNOWN -> Outcome.UNKNOWN
            Outcome.DEFEAT_SOON -> Outcome.DEFEAT_SOON
            Outcome.VICTORY_NEXT_MOVE -> Outcome.VICTORY_NEXT_TWO_MOVES
            Outcome.VICTORY_NEXT_TWO_MOVES -> Outcome.VICTORY_NEXT_TWO_MOVES
        })

        val majorOutcome = reses.distinct().map { it to reses.count { r -> r == it } }.maxBy { it.second }
        return when {
            reses.count { it.isVictory() } >= reses.size - 1 -> Outcome.VICTORY_NEXT_MOVE
            reses.any { it == Outcome.DEFEAT_SOON } -> Outcome.DEFEAT_SOON
            majorOutcome.second >= reses.size / 2 -> majorOutcome.first
            else -> Outcome.UNKNOWN
        }.also {
            gamesToOutcomes.add(GameRecord(game).serializeToString() to it)
        }
    }

    private fun checkMyMoves(game: Game, depth: Int): Outcome {
        val gameString = gamesToOutcomes.find { it.first == GameRecord(game).serializeToString() }
        if (gameString != null) {
            Log.i("HIT", " THE CACHE!!!!")
            return gameString.second
        }

        var victoryFlag = false
        val deferreds = strategies.map {
            CoroutineScope(Dispatchers.Unconfined).async {
                val copy = game.copy()
                it.move(copy)
                copy.processJoker()
                copy.processJacks()
                copy.checkOnGameOver()
                if (copy.isGameOver == -1) {
                    Log.i("Ulysses: Me", "Victorious soon!")
                    victoryFlag = true
                    Outcome.VICTORY_NEXT_MOVE
                } else {
                    if (victoryFlag) {
                        Outcome.VICTORY_NEXT_MOVE
                    } else if (depth == 1) {
                        Outcome.UNKNOWN
                    } else when (checkPlayerMoves(copy, depth - 1)) {
                        Outcome.UNKNOWN -> {
                            Outcome.UNKNOWN
                        }
                        Outcome.DEFEAT_SOON -> {
                            Outcome.DEFEAT_SOON
                        }
                        Outcome.VICTORY_NEXT_MOVE, Outcome.VICTORY_NEXT_TWO_MOVES -> {
                            Outcome.VICTORY_NEXT_TWO_MOVES
                        }
                    }
                }
            }
        }
        return runBlocking {
            deferreds.joinAll()
            val reses = deferreds.map { runBlocking { it.await() } }
            reses.run {
                when {
                    this.count { it == Outcome.DEFEAT_SOON } >= this.size - 1 -> Outcome.DEFEAT_SOON
                    this.any { it.isVictory() } -> Outcome.VICTORY_NEXT_MOVE
                    else -> Outcome.UNKNOWN
                }
            }.also {
                gamesToOutcomes.add(GameRecord(game).serializeToString() to it)
            }
        }

    }

    @Serializable
    class GameRecord(val caravans: List<Caravan>, val hand: List<Card>, val isPlayerTurn: Boolean) {
        constructor(game: Game) : this(game.playerCaravans + game.enemyCaravans, game.enemyCResources.hand, game.isPlayerTurn)

        fun serializeToString(): String {
            val result = StringBuilder()
            result.append("$isPlayerTurn ")
            hand.forEach {
                result.append("${it.rank.value} ${it.suit.ordinal} ")
            }
            result.append("\n")
            caravans.forEach {
                it.cards.forEach { cardWithModifiers ->
                    result.append("${cardWithModifiers.card.rank.value} ${cardWithModifiers.card.suit.ordinal} ")
                    cardWithModifiers.modifiersCopy().forEach { modifier ->
                        result.append("${modifier.rank.value} ${modifier.suit.ordinal} ")
                    }
                }
                result.append("\n")
            }
            return result.toString()
        }
    }

    enum class Outcome {
        UNKNOWN,
        DEFEAT_SOON,
        VICTORY_NEXT_MOVE,
        VICTORY_NEXT_TWO_MOVES;

        fun isVictory(): Boolean {
            return this != UNKNOWN && this != DEFEAT_SOON
        }
    }
}