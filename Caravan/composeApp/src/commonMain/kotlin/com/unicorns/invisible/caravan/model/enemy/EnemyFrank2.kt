package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueenToSelf
import com.unicorns.invisible.caravan.model.enemy.strategy.canJokerCrashTheParty
import com.unicorns.invisible.caravan.model.enemy.strategy.checkOnResult
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFBomb
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardNuclear
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlin.random.Random


data object EnemyFrank2 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.ENCLAVE).apply {
        removeAll { it is CardFaceSuited && it.suit == Suit.HEARTS }
        removeAll { it is CardBase && it.rank.value < 3 }
        removeAll { it is CardJoker }
        add(CardFBomb())
    })

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val underWeightCaravans = game.enemyCaravans.filter { it.getValue() < 21 }
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.MAX_FIRST_TO_RANDOM).move(game, speed)
            return
        }

        // 1) Check if we have winning move
        if (checkOnResult(gameToState(game)).isEnemyMoveWins()) {
            val jack = hand.filterIsInstance<CardFace>().find { card -> card.rank == RankFace.JACK }
            if (jack != null) {
                val jackIndex = hand.indexOf(jack)
                game.enemyCaravans.withIndex().forEach { caravan ->
                    caravan.value.cards
                        .filter { card -> card.canAddModifier(jack) }
                        .sortedBy { card -> card.getValue() }
                        .forEach { card ->
                            val state = gameToState(game)
                            state.enemy[caravan.index] -= card.getValue()
                            if (checkTheOutcome(state) == -1) {
                                card.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardModifier, speed)
                                return
                            }
                        }
                }

                game.playerCaravans.withIndex()
                    .sortedByDescending { it.value.getValue() }
                    .forEach { caravan ->
                        caravan.value.cards
                            .filter { card -> card.canAddModifier(jack) }
                            .sortedBy { card -> card.getValue() }
                            .forEach { card ->
                                val state = gameToState(game)
                                state.player[caravan.index] -= card.getValue()
                                if (checkTheOutcome(state) == -1) {
                                    card.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardModifier, speed)
                                    return
                                }
                            }
                    }

                val king = hand.filterIsInstance<CardFace>().find { card -> card.rank == RankFace.KING }
                if (king != null) {
                    val kingIndex = hand.indexOf(king)
                    game.enemyCaravans.withIndex().forEach { caravan ->
                        caravan.value.cards
                            .filter { card -> card.canAddModifier(king) }
                            .sortedBy { card -> card.getValue() }
                            .forEach { card ->
                                val state = gameToState(game)
                                state.enemy[caravan.index] += card.getValue()
                                if (checkTheOutcome(state) == -1) {
                                    card.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardModifier, speed)
                                    return
                                }
                            }
                    }
                    game.playerCaravans.withIndex()
                        .sortedByDescending { it.value.getValue() }
                        .forEach { caravan ->
                            caravan.value.cards
                                .filter { card -> card.canAddModifier(king) }
                                .sortedBy { card -> card.getValue() }
                                .forEach { card ->
                                    val state = gameToState(game)
                                    state.player[caravan.index] += card.getValue()
                                    if (checkTheOutcome(state) == -1) {
                                        card.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardModifier, speed)
                                        return
                                    }
                                }
                        }
                }

                // Put a card on top!
                game.enemyCaravans.withIndex().shuffled().forEach { caravan ->
                    hand.filterIsInstance<CardNumber>()
                        .filter { caravan.value.canPutCardOnTop(it) }
                        .forEach { card ->
                            val cardIndex = hand.indexOf(card)
                            val state = gameToState(game)
                            state.enemy[caravan.index] += card.rank.value
                            if (checkTheOutcome(state) == -1) {
                                caravan.value.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
                                return
                            }
                        }

                    // maybe even drop a caravan!
                    val state = gameToState(game)
                    state.enemy[caravan.index] = 0
                    if (checkTheOutcome(state) == -1) {
                        caravan.value.dropCaravan(speed)
                        return
                    }
                }
            }
        }

        // 2) If not and if player is abt to win, destroy player ready and almost ready caravans (on right columns!)
        val isLosing = checkOnResult(gameToState(game)).isPlayerMoveWins() || canJokerCrashTheParty(
            gameToState(game)
        )
        if (isLosing) {
            hand.filterIsInstance<CardFace>()
                .filter { it.rank == RankFace.JACK }
                .forEach { jack ->
                    val jackIndex = hand.indexOf(jack)
                    game.playerCaravans
                        .sortedByDescending { it.getValue() }
                        .forEach { otherCaravan ->
                            otherCaravan.cards.withIndex()
                                .filter { it.value.canAddModifier(jack) }
                                .forEach {
                                    val state = gameToState(game)
                                    val indexC = game.playerCaravans.indexOf(otherCaravan)
                                    state.player[indexC] -= it.value.getValue()
                                    if (!checkOnResult(state).isPlayerMoveWins() && !canJokerCrashTheParty(state)) {
                                        it.value.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardModifier, speed)
                                        return
                                    }
                                }
                        }
                    game.enemyCaravans
                        .forEach { otherCaravan ->
                            otherCaravan.cards.withIndex()
                                .filter { it.value.canAddModifier(jack) }
                                .forEach {
                                    val state = gameToState(game)
                                    val indexC = game.enemyCaravans.indexOf(otherCaravan)
                                    state.enemy[indexC] -= it.value.getValue()
                                    if (!checkOnResult(state).isPlayerMoveWins() && !canJokerCrashTheParty(state)) {
                                        it.value.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardModifier, speed)
                                        return
                                    }
                                }
                        }
                }

            hand.filterIsInstance<CardFace>()
                .filter { it.rank == RankFace.KING }
                .forEach { king ->
                    val kingIndex = hand.indexOf(king)
                    game.playerCaravans
                        .sortedByDescending { it.getValue() }
                        .forEach { otherCaravan ->
                            otherCaravan.cards.withIndex()
                                .filter { it.value.canAddModifier(king) }
                                .sortedByDescending { it.value.getValue() }
                                .forEach {
                                    val state = gameToState(game)
                                    val indexC = game.playerCaravans.indexOf(otherCaravan)
                                    state.player[indexC] += it.value.getValue()
                                    if (!checkOnResult(state).isPlayerMoveWins() && !canJokerCrashTheParty(state)) {
                                        it.value.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardModifier, speed)
                                        return
                                    }
                                }
                        }
                    game.enemyCaravans.forEach { otherCaravan ->
                        otherCaravan.cards.withIndex()
                            .filter { it.value.canAddModifier(king) }
                            .sortedByDescending { it.value.getValue() }
                            .forEach {
                                val state = gameToState(game)
                                val indexC = game.enemyCaravans.indexOf(otherCaravan)
                                state.enemy[indexC] += it.value.getValue()
                                if (!checkOnResult(state).isPlayerMoveWins() && !canJokerCrashTheParty(state)) {
                                    it.value.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardModifier, speed)
                                    return
                                }
                            }
                    }
                }

            // Try creating a draw!
            hand.filterIsInstance<CardNumber>()
                .sortedByDescending { it.rank.value }
                .forEach { card ->
                    val cardIndex = hand.indexOf(card)
                    game.enemyCaravans.withIndex()
                        .forEach { (otherCaravanIndex, otherCaravan) ->
                            val state = gameToState(game)
                            state.enemy[otherCaravanIndex] += card.rank.value
                            if (!checkOnResult(state).isPlayerMoveWins() && !canJokerCrashTheParty(state)) {
                                otherCaravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
                                return
                            }
                        }
                }

            val joker = hand.indexOfFirst { it is CardJoker }
            if (joker != -1) {
                if (StrategyJokerSimple(joker, isHard = true).move(game, speed)) {
                    return
                }
            }

            game.enemyCaravans.withIndex()
                .forEach { (otherCaravanIndex, otherCaravan) ->
                    val state = gameToState(game)
                    state.enemy[otherCaravanIndex] = 0
                    if (!checkOnResult(state).isPlayerMoveWins() && !canJokerCrashTheParty(state)) {
                        otherCaravan.dropCaravan(speed)
                        return
                    }
                }

            hand.filterIsInstance<CardNuclear>()
                .forEach { bomb ->
                    val bombIndex = hand.indexOf(bomb)
                    val caravans = game.enemyCaravans.withIndex().shuffled()
                    val caravanToSave = if (caravans.all { it.value.getValue() > 26 }) {
                        caravans.minBy { it.value.getValue() }
                    } else {
                        caravans.filter {
                            it.value.getValue() <= 26 && it.value.cards.any { card -> card.canAddModifier(bomb) }
                        }.maxByOrNull { it.value.getValue() }
                    }
                    if (caravanToSave == null) {
                        val card = game.playerCaravans.flatMap { it.cards }.shuffled().find { it.canAddModifier(bomb) }
                        if (card != null) {
                            card.addModifier(game.enemyCResources.removeFromHand(bombIndex, speed) as CardModifier, speed)
                            game.enemyCResources.addOnTop(CardFBomb())
                            return
                        }
                    } else if (caravanToSave.value.getValue() > 0) {
                        caravanToSave.value.cards.shuffled().forEach {
                            if (it.canAddModifier(bomb)) {
                                it.addModifier(game.enemyCResources.removeFromHand(bombIndex, speed) as CardModifier, speed)
                                game.enemyCResources.addOnTop(CardFBomb())
                                return
                            }
                        }
                    }
                }

            // 2.75) Maybe try a Queen!
            game.playerCaravans.forEach { playerCaravan ->
                hand.filterIsInstance<CardFace>().filter { it.rank == RankFace.QUEEN }.forEach { card ->
                    val cardIndex = hand.indexOf(card)
                    if (playerCaravan.size >= 2) {
                        val last = playerCaravan.cards.last().card.rank.value
                        val preLast = playerCaravan.cards[playerCaravan.cards.lastIndex - 1].card.rank.value
                        if (playerCaravan.cards.last().canAddModifier(card)) {
                            val isRev = playerCaravan.cards.last().isQueenReversingSequence()
                            val isAscending = preLast < last && !isRev || preLast > last && isRev
                            if (isAscending && last <= 3) {
                                playerCaravan.cards.last().addModifier(game.enemyCResources.removeFromHand(cardIndex, speed) as CardModifier, speed)
                                return
                            }
                            val isDescending = preLast > last && !isRev || preLast < last && isRev
                            if (isDescending && last >= 8) {
                                playerCaravan.cards.last().addModifier(game.enemyCResources.removeFromHand(cardIndex, speed) as CardModifier, speed)
                                return
                            }
                        }
                    }
                }
            }
        }

        // 4) only then try to put card on our caravan.
        game.enemyCaravans
            .withIndex()
            .filter { it.value.getValue() < 26 }
            .sortedByDescending {
                val ourValue = it.value.getValue()
                val playerValue = game.playerCaravans[it.index].getValue()
                val isLastCaravanInContention = checkOnResult(gameToState(game), it.index).isEnemyMoveWins()
                if (isLastCaravanInContention) {
                    150 + ourValue
                } else if (ourValue !in (21..26) && playerValue !in (21..26)) {
                    ourValue
                } else if (ourValue !in (21..26) && playerValue in (21..26)) {
                    100 + ourValue
                } else if (ourValue in (21..26) && playerValue !in (21..26)) {
                    playerValue
                } else {
                    when {
                        ourValue > playerValue -> ourValue - playerValue
                        ourValue == playerValue -> 50
                        else -> 100
                    }
                }
            }
            .forEach { (caravanIndex, caravan) ->
                val mult = if (caravan.size >= 2) {
                    val last = caravan.cards.last().card.rank.value
                    val preLast = caravan.cards[caravan.cards.lastIndex - 1].card.rank.value
                    if (preLast > last) {
                        -1
                    } else {
                        1
                    }
                } else if (caravan.size == 1) {
                    if (caravan.cards.last().card.rank.value <= 5) {
                        1
                    } else {
                        -1
                    }
                } else {
                    -1
                }
                hand.filterIsInstance<CardNumber>()
                    .sortedBy { it.rank.value * mult }
                    .forEach { card ->
                        val cardIndex = hand.indexOf(card)
                        val newSum = caravan.getValue() + card.rank.value
                        val state = gameToState(game)
                        state.enemy[caravanIndex] += card.rank.value
                        val isStateBad = checkOnResult(state).isPlayerMoveWins() || canJokerCrashTheParty(state)
                        val wasStateBad = checkOnResult(gameToState(game)).isPlayerMoveWins() || canJokerCrashTheParty(
                            gameToState(game)
                        )
                        if (newSum <= 26 && caravan.canPutCardOnTop(card) && (!isStateBad || wasStateBad) && checkTheOutcome(state) != 1) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
                            return
                        }
                    }
            }


        // 3) Try king on underweightCaravans and jacks on overweightCaravans.
        val jack = hand.filterIsInstance<CardFace>().find { it.rank == RankFace.JACK }
        if (jack != null) {
            val jackIndex = hand.indexOf(jack)
            overWeightCaravans.withIndex().sortedBy { it.value.getValue() }.forEach {
                it.value.cards
                    .filter { card -> card.canAddModifier(jack) }
                    .sortedBy { card -> card.getValue() }
                    .forEach { card ->
                        val state = gameToState(game)
                        state.enemy[it.index] -= card.getValue()
                        val isStateBad = checkOnResult(state).isPlayerMoveWins() || canJokerCrashTheParty(state)
                        val wasStateBad = checkOnResult(gameToState(game)).isPlayerMoveWins() || canJokerCrashTheParty(
                            gameToState(game)
                        )
                        if (state.enemy[it.index] <= 26 && (!isStateBad || wasStateBad) && checkTheOutcome(state) != 1) {
                            card.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardModifier, speed)
                            return
                        }
                    }
            }

            if (hand.filterIsInstance<CardFace>().filter { it.rank == RankFace.JACK }.size > 1) {
                game.playerCaravans
                    .filter { it.getValue() <= 26 }
                    .sortedByDescending { it.getValue() }
                    .forEach { otherCaravan ->
                        otherCaravan.cards.withIndex()
                            .filter {
                                it.value.canAddModifier(jack) && it.value.modifiersCopy().any { mod ->
                                    mod is CardFace && mod.rank == RankFace.KING
                                }
                            }
                            .sortedByDescending { it.value.getValue() }
                            .forEach {
                                val state = gameToState(game)
                                val indexC = game.playerCaravans.indexOf(otherCaravan)
                                state.player[indexC] -= it.value.getValue()
                                val isStateBad = checkOnResult(state).isPlayerMoveWins() || canJokerCrashTheParty(state)
                                val wasStateBad = checkOnResult(gameToState(game)).isPlayerMoveWins() || canJokerCrashTheParty(
                                    gameToState(game)
                                )
                                if ((!isStateBad || wasStateBad) && checkTheOutcome(state) != 1) {
                                    it.value.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardModifier, speed)
                                    return
                                }
                            }
                    }
            }
        }

        val king = hand.filterIsInstance<CardFace>().find { it.rank == RankFace.KING }
        if (king != null) {
            val kingIndex = hand.indexOf(king)
            underWeightCaravans.withIndex().filter { it.value.getValue() > 10 }.sortedByDescending { it.value.getValue() }.forEach {
                it.value.cards
                    .filter { card -> card.canAddModifier(king) }
                    .sortedByDescending { card -> card.getValue() }
                    .forEach { card ->
                        val state = gameToState(game)
                        state.enemy[it.index] += card.getValue()
                        val isStateBad = checkOnResult(state).isPlayerMoveWins() || canJokerCrashTheParty(state)
                        val wasStateBad = checkOnResult(gameToState(game)).isPlayerMoveWins() || canJokerCrashTheParty(
                            gameToState(game)
                        )
                        if (state.enemy[it.index] <= 26 && (!isStateBad || wasStateBad) && checkTheOutcome(state) != 1) {
                            card.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardModifier, speed)
                            return
                        }
                    }
            }

            if (hand.filterIsInstance<CardFace>().filter { it.rank == RankFace.KING }.size > 1) {
                game.playerCaravans
                    .sortedByDescending { if (it.getValue() <= 26) it.getValue() else 40 - it.getValue() }
                    .forEach { otherCaravan ->
                        otherCaravan.cards.withIndex()
                            .filter { it.value.canAddModifier(king) }
                            .sortedByDescending { it.value.getValue() }
                            .forEach {
                                val state = gameToState(game)
                                val indexC = game.playerCaravans.indexOf(otherCaravan)
                                state.player[indexC] += it.value.getValue()
                                val isStateBad = checkOnResult(state).isPlayerMoveWins() || canJokerCrashTheParty(state)
                                val wasStateBad = checkOnResult(gameToState(game)).isPlayerMoveWins() || canJokerCrashTheParty(
                                    gameToState(game)
                                )
                                if (state.player[indexC] > 26 && (!isStateBad || wasStateBad) && checkTheOutcome(state) != 1) {
                                    it.value.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardModifier, speed)
                                    return
                                }
                            }
                    }
            }
        }

        val joker = hand.indexOfFirst { it is CardFace && it.rank == RankFace.JOKER }
        if (Random.nextBoolean() && joker != -1) {
            if (StrategyJokerSimple(joker).move(game, speed)) {
                return
            }
        }

        val queen = hand.indexOfFirst { it is CardFace && it.rank == RankFace.QUEEN }
        if (queen != -1) {
            if (StrategyQueenToSelf(queen).move(game, speed)) {
                return
            }
        }

        game.enemyCaravans.sortedByDescending { it.getValue() }.forEach { caravan ->
            if (caravan.getValue() > 26) {
                caravan.dropCaravan(speed)
                return
            }
        }

        val card = hand.minBy { c ->
            when (c) {
                is CardBase -> {
                    when (c.rank) {
                        RankNumber.ACE -> 4
                        RankNumber.TWO -> 3
                        RankNumber.THREE -> 3
                        RankNumber.FOUR -> 4
                        RankNumber.FIVE -> 5
                        RankNumber.SIX -> 5
                        RankNumber.SEVEN -> 6
                        RankNumber.EIGHT -> 6
                        RankNumber.NINE -> 7
                        RankNumber.TEN -> 8
                    }
                }
                is CardFace -> {
                    when (c.rank) {
                        RankFace.JACK -> 12
                        RankFace.QUEEN -> 4
                        RankFace.KING -> 13
                        RankFace.JOKER -> 14
                    }
                }
                else -> 15
            }
        }
        game.enemyCResources.dropCardFromHand(hand.indexOf(card), speed)
    }
}