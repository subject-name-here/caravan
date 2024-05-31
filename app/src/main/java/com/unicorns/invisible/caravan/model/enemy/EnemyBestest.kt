package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyAggressive
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyRush
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.multiplayer.MoveResponse
import com.unicorns.invisible.caravan.save.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlin.math.abs


@Serializable
data object EnemyBestest : Enemy() {
    override fun createDeck(): CResources = CResources(CardBack.VAULT_21, false)
    override fun getRewardBack() = CardBack.VAULT_21

    override suspend fun makeMove(game: Game) {
        if (game.isInitStage()) {
            val hand = game.enemyCResources.hand
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        if (game.enemyCResources.hand.size <= 4) {
            if (StrategyRush.move(game)) {
                return
            }
            if (StrategyAggressive.move(game)) {
                return
            }
        } else {
            val gameCopyString = json.encodeToString(game)
            val depth = 0
            val theBest = checkAllMoves(gameCopyString, depth)
            if (theBest < 0.5f) {
                val best2 = (0..2).map { caravanIndex ->
                    val gameCopy2 = json.decodeFromString<Game>(gameCopyString)
                    if (gameCopy2.enemyCaravans[caravanIndex].getValue() > 0) {
                        gameCopy2.enemyCaravans[caravanIndex].dropCaravan()
                        if (gameCopy2.isGameOver == 1) {
                            return@map 1f to caravanIndex
                        } else if (!gameCopy2.isOver()) {
                            return@map checkAllPlayerMoves(json.encodeToString(gameCopy2), depth) to caravanIndex
                        } else {
                            return@map 0f to caravanIndex
                        }
                    } else {
                        return@map 1f to caravanIndex
                    }
                }.minBy { it.first }
                if (abs(best2.first - theBest) < 0.001) {
                    if (game.enemyCaravans[best2.second].getValue() > 0) {
                        game.enemyCaravans[best2.second].dropCaravan()
                        return
                    }
                }

                game.enemyCResources.hand.withIndex().shuffled().map { (cardIndex, card) ->
                    val res = mutableListOf<MoveResponse>()
                    if (card.isFace()) {
                        game.enemyCaravans.forEachIndexed { caravanIndex, caravan ->
                            caravan.cards.forEachIndexed { index, _ ->
                                res.add(MoveResponse(
                                    moveCode = 4,
                                    caravanCode = caravanIndex,
                                    handCardNumber = cardIndex,
                                    cardInCaravanNumber = index
                                ))
                            }
                        }
                        game.playerCaravans.forEachIndexed { caravanIndex, caravan ->
                            caravan.cards.forEachIndexed { index, _ ->
                                res.add(MoveResponse(
                                    moveCode = 4,
                                    caravanCode = -3 + caravanIndex,
                                    handCardNumber = cardIndex,
                                    cardInCaravanNumber = index
                                ))
                            }
                        }
                    } else {
                        repeat(3) {
                            res.add(MoveResponse(
                                moveCode = 3,
                                caravanCode = it,
                                handCardNumber = cardIndex
                            ))
                        }
                    }
                    res
                }.flatten().forEach { move ->
                    val gameCopy2 = json.decodeFromString<Game>(gameCopyString)
                    if (move.moveCode == 3) {
                        val card = gameCopy2.enemyCResources.removeFromHand(move.handCardNumber)
                        if (gameCopy2.enemyCaravans[move.caravanCode].canPutCardOnTop(card)) {
                            gameCopy2.enemyCaravans[move.caravanCode].putCardOnTop(card)
                            if (abs(checkAllPlayerMoves(json.encodeToString(gameCopy2), depth) - theBest) < 0.005) {
                                game.enemyCaravans[move.caravanCode].putCardOnTop(game.enemyCResources.removeFromHand(move.handCardNumber))
                                return
                            }
                        }
                    } else {
                        val card = gameCopy2.enemyCResources.removeFromHand(move.handCardNumber)

                        val cardInCaravan = if (move.caravanCode < 0) {
                            val playersCaravan = 3 + move.caravanCode
                            gameCopy2.playerCaravans[playersCaravan].cards[move.cardInCaravanNumber]
                        } else {
                            gameCopy2.enemyCaravans[move.caravanCode].cards[move.cardInCaravanNumber]
                        }
                        if (cardInCaravan.canAddModifier(card)) {
                            cardInCaravan.addModifier(card)
                            if (abs(checkAllPlayerMoves(json.encodeToString(gameCopy2), depth) - theBest) < 0.005) {
                                game.enemyCaravans[move.caravanCode].putCardOnTop(game.enemyCResources.removeFromHand(move.handCardNumber))
                                return
                            }
                        }
                    }
                }
            }
        }

        game.enemyCResources.removeFromHand(game.enemyCResources.hand.withIndex().minByOrNull {
            when (it.value.rank) {
                Rank.QUEEN -> 0
                Rank.JACK, Rank.KING, Rank.JOKER -> 11
                else -> it.value.rank.value
            }
        }!!.index)
    }

    private fun checkMove(gameCopyString2: String, card: Card, depth: Int): Float {
        var cnt = 0
        var result = 0
        if (card.isFace()) {
            val gameCopy = json.decodeFromString<Game>(gameCopyString2)
            (gameCopy.playerCaravans + gameCopy.enemyCaravans).flatMap { it.cards }
                .filter { it.canAddModifier(card) }
                .forEach { potentialCard ->
                    val gameCopy2 = json.decodeFromString<Game>(gameCopyString2)
                    val cardInCopy = (gameCopy2.playerCaravans + gameCopy2.enemyCaravans).flatMap { it.cards }.find {
                        potentialCard.card.rank == it.card.rank && potentialCard.card.suit == it.card.suit && potentialCard.card.back == it.card.back
                    }
                    if (cardInCopy?.canAddModifier(card) == true) {
                        cnt++
                        cardInCopy.addModifier(card)
                        val cards2 = (gameCopy2.playerCaravans + gameCopy2.enemyCaravans).flatMap { it.cards }
                        if (cards2.any { it.hasJacks() || it.hasActiveJoker }) {
                            gameCopy2.processJacks()
                            gameCopy2.processJoker()
                        }
                        if (gameCopy2.isGameOver == 1) {
                            result++
                        } else if (!gameCopy2.isOver()) {
                            if (checkAllPlayerMoves(json.encodeToString(gameCopy2), depth) > 0.49f) {
                                result++
                            }
                        }
                    }
                }
        } else {
            (0..2).forEach { caravanIndex ->
                val gameCopy = json.decodeFromString<Game>(gameCopyString2)
                if (gameCopy.enemyCaravans[caravanIndex].canPutCardOnTop(card)) {
                    cnt++
                    gameCopy.enemyCaravans[caravanIndex].putCardOnTop(card)
                    if (gameCopy.isGameOver == 1) {
                        result++
                    } else if (!gameCopy.isOver()) {
                        if (checkAllPlayerMoves(json.encodeToString(gameCopy), depth) > 0.49f) {
                            result++
                        }
                    }
                }
            }
        }

        return result.toFloat() / cnt.toFloat()
    }

    private fun checkAllMoves(gameCopyString: String, depth: Int): Float {
        val gameCopy = json.decodeFromString<Game>(gameCopyString)

        val best = gameCopy.enemyCResources.hand.shuffled().minOf { card ->
            checkMove(gameCopyString, card, depth)
        }

        val best2 = (0..2).minOf { caravanIndex ->
            val gameCopy2 = json.decodeFromString<Game>(gameCopyString)
            if (gameCopy2.enemyCaravans[caravanIndex].getValue() > 0) {
                gameCopy2.enemyCaravans[caravanIndex].dropCaravan()
                if (gameCopy2.isGameOver == 1) {
                    return@minOf 1f
                } else if (!gameCopy2.isOver()) {
                    return@minOf checkAllPlayerMoves(json.encodeToString(gameCopy2), depth)
                } else {
                    return@minOf 0f
                }
            } else {
                return@minOf 1f
            }
        }

        val best3 = checkAllPlayerMoves(json.encodeToString(gameCopyString), depth)

        return listOf(best, best2, best3).min()
    }

    // Is there a move that makes player win.
    private fun checkAllPlayerMoves(gameCopyString2: String, depth: Int): Float {
        if (depth <= 0) {
            return 0.33f
        }

        var cnt = 0
        var result = 0
        CustomDeck(CardBack.STANDARD, false).takeRandom(9).toList().shuffled().forEach { card ->
            if (card.isFace()) {
                val gameCopy = json.decodeFromString<Game>(gameCopyString2)
                (gameCopy.playerCaravans + gameCopy.enemyCaravans).flatMap { it.cards }
                    .filter { it.canAddModifier(card) }
                    .forEach { potentialCard ->
                        val gameCopy2 = json.decodeFromString<Game>(gameCopyString2)
                        val cardInCopy = (gameCopy2.playerCaravans + gameCopy2.enemyCaravans).flatMap { it.cards }.find {
                            potentialCard.card.rank == it.card.rank && potentialCard.card.suit == it.card.suit && potentialCard.card.back == it.card.back
                        }
                        if (cardInCopy?.canAddModifier(card) == true) {
                            cardInCopy.addModifier(card)
                            cnt++
                            val cards2 = (gameCopy2.playerCaravans + gameCopy2.enemyCaravans).flatMap { it.cards }
                            if (cards2.any { it.hasJacks() || it.hasActiveJoker }) {
                                gameCopy2.processJacks()
                                gameCopy2.processJoker()
                            }
                            if (gameCopy2.isGameOver == 1) {
                                result++
                            } else if (!gameCopy2.isOver()) {
                                val possibility = checkAllMoves(json.encodeToString(gameCopy2), depth - 1)
                                if (possibility > 0.51f) {
                                    result++
                                }
                            }
                        }
                    }
            } else {
                (0..2).forEach { caravanIndex ->
                    val gameCopy = json.decodeFromString<Game>(gameCopyString2)
                    if (gameCopy.playerCaravans[caravanIndex].canPutCardOnTop(card)) {
                        cnt++
                        gameCopy.playerCaravans[caravanIndex].putCardOnTop(card)
                        if (gameCopy.isGameOver == 1) {
                            result++
                        } else if (!gameCopy.isOver()) {
                            val possibility = checkAllMoves(json.encodeToString(gameCopy), depth - 1)
                            if (possibility > 0.51f) {
                                result++
                            }
                        }
                    }
                }
            }
        }

        (0..2).forEach { caravanIndex ->
            val gameCopy = json.decodeFromString<Game>(gameCopyString2)
            if (gameCopy.playerCaravans[caravanIndex].getValue() > 0) {
                cnt++
                gameCopy.playerCaravans[caravanIndex].dropCaravan()
                if (gameCopy.isGameOver == 1) {
                    result++
                } else if (!gameCopy.isOver()) {
                    val possibility = checkAllMoves(json.encodeToString(gameCopy), depth - 1)
                    if (possibility > 0.49f) {
                        result++
                    }
                }
            }

        }

        val possibility = checkAllMoves(gameCopyString2, depth - 1)
        if (possibility > 0.49f) {
            result++
        }

        return result.toFloat() / cnt.toFloat()
    }
}