package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.GamePossibleResult
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyQueenToSelf
import com.unicorns.invisible.caravan.model.enemy.strategy.checkOnResult
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardAtomic
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.saveGlobal
import kotlin.math.max


data object EnemyFrank : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.ENCLAVE).apply {
        val times = if (saveGlobal.towerBeaten) 4 else 3
        repeat(times) { add(CardAtomic()) }
    })

    private fun checkMoveOnProbableDefeat(game: Game, caravanIndex: Int): Boolean {
        val otherCaravansIndices = game.enemyCaravans.indices.filter { it != caravanIndex }
        var score = 0
        fun check(p0: Int, e0: Int) {
            if (p0 >= 11 && e0 != 26) {
                score++
            }
        }
        otherCaravansIndices.forEach {
            check(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue())
        }
        var score2 = 0
        fun check2(p0: Int, e0: Int) {
            if (p0 in (21..26) && (p0 > e0 || e0 > 26) || e0 in (21..26) && (e0 > p0 || p0 > 26)) {
                score2++
            }
        }
        otherCaravansIndices.forEach {
            check2(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue())
        }
        return score >= 1 && score2 >= 1
    }

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val underWeightCaravans = game.enemyCaravans.filter { it.getValue() < 21 }
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.MIN_FIRST_TO_RANDOM).move(game, speed)
            return
        }

        // 1) Check if we have winning move
        game.enemyCaravans.withIndex().forEach { caravan ->
            val isWinningMovePossible = checkOnResult(game, caravan.index) in listOf(
                GamePossibleResult.GAME_ON,
                GamePossibleResult.IMMINENT_ENEMY_VICTORY,
                GamePossibleResult.ENEMY_VICTORY_IS_POSSIBLE
            )
            val rivalCaravanValue = game.playerCaravans[caravan.index].getValue()
            val lowerBound = max(21, rivalCaravanValue + 1)
            if (isWinningMovePossible) {
                // If caravan is overweight, check on Jacks
                val jack = hand.filterIsInstance<CardFace>().find { card -> card.rank == RankFace.JACK }
                if (jack != null) {
                    val jackIndex = hand.indexOf(jack)
                    caravan.value.cards
                        .filter { card -> card.canAddModifier(jack) }
                        .sortedBy { card -> card.getValue() }
                        .forEach { card ->
                            if (caravan.value.getValue() - card.getValue() in (lowerBound..26)) {
                                card.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardModifier, speed)
                                return
                            }
                        }
                    if (
                        checkOnResult(game, caravan.index) == GamePossibleResult.IMMINENT_ENEMY_VICTORY &&
                        rivalCaravanValue > 26 || caravan.value.getValue() == rivalCaravanValue
                    ) {
                        game.playerCaravans.forEach { playerCaravan ->
                            playerCaravan.cards.forEach { card ->
                                if (playerCaravan.getValue() - card.getValue() < 21) {
                                    card.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardModifier, speed)
                                    return
                                }
                            }
                        }
                    }
                }

                // If caravan is underweight, check on Kings
                val king = hand.filterIsInstance<CardFace>().find { card -> card.rank == RankFace.KING }
                if (king != null) {
                    val kingIndex = hand.indexOf(king)
                    caravan.value.cards
                        .filter { card -> card.canAddModifier(king) }
                        .sortedBy { card -> card.getValue() }
                        .forEach { card ->
                            if (caravan.value.getValue() + card.getValue() in (lowerBound..26)) {
                                card.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardModifier, speed)
                                return
                            }
                        }
                    if (checkOnResult(game, caravan.index) == GamePossibleResult.IMMINENT_ENEMY_VICTORY && rivalCaravanValue < 21) {
                        game.playerCaravans.forEach { playerCaravan ->
                            playerCaravan.cards.forEach { card ->
                                if (playerCaravan.getValue() + card.getValue() > 26 || caravan.value.getValue() == rivalCaravanValue) {
                                    card.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardModifier, speed)
                                    return
                                }
                            }
                        }
                    }
                }

                // Put a card on top!
                hand.filterIsInstance<CardNumber>()
                    .forEach { card ->
                        val cardIndex = hand.indexOf(card)
                        if (caravan.value.getValue() + card.rank.value in (lowerBound..26) && caravan.value.canPutCardOnTop(card)) {
                            caravan.value.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
                            return
                        }
                    }


                // maybe even drop a caravan!
                if (rivalCaravanValue == caravan.value.getValue() && checkOnResult(game, caravan.index) == GamePossibleResult.IMMINENT_ENEMY_VICTORY) {
                    caravan.value.dropCaravan(speed)
                    return
                }
            }
        }


        // 2) If not and if player is abt to win, destroy player ready and almost ready caravans (on right columns!)
        game.enemyCaravans.withIndex().forEach { (caravanIndex, caravan) ->
            val isLosing = checkOnResult(game, caravanIndex) in listOf(
                GamePossibleResult.IMMINENT_PLAYER_VICTORY,
                GamePossibleResult.PLAYER_VICTORY_IS_POSSIBLE,
                GamePossibleResult.GAME_ON
            )
            if (isLosing) {
                hand.filterIsInstance<CardAtomic>()
                    .forEach { bomb ->
                        val bombIndex = hand.indexOf(bomb)
                        val caravans = game.enemyCaravans.withIndex().reversed()
                        val caravanToSave = if (caravans.all { it.value.getValue() > 26 }) {
                            caravans.minBy { it.value.getValue() }
                        } else {
                            caravans.filter { it.value.getValue() <= 26 }.maxBy { it.value.getValue() }
                        }
                        if (caravanToSave.value.getValue() > 0) {
                            caravanToSave.value.cards.reversed().forEach {
                                if (it.canAddModifier(bomb)) {
                                    it.addModifier(game.enemyCResources.removeFromHand(bombIndex, speed) as CardModifier, speed)
                                    return
                                }
                            }
                        }
                    }

                hand
                    .filterIsInstance<CardFace>()
                    .filter { it.rank == RankFace.KING }
                    .forEach { king ->
                        val kingIndex = hand.indexOf(king)
                        game.playerCaravans
                            .withIndex()
                            .filter { it.index != caravanIndex }
                            .forEach { (_, otherCaravan) ->
                                otherCaravan.cards.withIndex()
                                    .filter { it.value.canAddModifier(king) }
                                    .sortedByDescending { it.value.getValue() }
                                    .forEach {
                                        if (otherCaravan.getValue() + it.value.getValue() > 26) {
                                            it.value.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardModifier, speed)
                                            return
                                        }
                                    }
                            }
                    }

                hand.withIndex()
                    .filterIsInstance<CardFace>()
                    .filter { it.rank == RankFace.JACK }
                    .forEach { jack ->
                        val jackIndex = hand.indexOf(jack)
                        game.playerCaravans
                            .filter { it.getValue() >= 11 }
                            .sortedByDescending { if (it.getValue() > 26) 0 else it.getValue() }
                            .forEach { otherCaravan ->
                                otherCaravan.cards.withIndex()
                                    .filter { it.value.canAddModifier(jack) }
                                    .sortedByDescending {
                                        if (caravan.getValue() !in (21..26)) {
                                            it.value.getValue() + 100
                                        } else {
                                            it.value.getValue()
                                        }
                                    }
                                    .forEach {
                                        if (otherCaravan.getValue() - it.value.getValue() < 21) {
                                            it.value.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardModifier, speed)
                                            return
                                        }
                                    }
                            }
                    }

                val joker = hand.indexOfFirst { it is CardFace && it.rank == RankFace.JOKER }
                if (joker != -1) {
                    if (StrategyJokerSimple(joker).move(game, speed)) {
                        return
                    }
                }

                // Try creating a draw!
                hand.filterIsInstance<CardNumber>()
                    .sortedByDescending { it.rank.value }
                    .forEach { card ->
                        val cardIndex = hand.indexOf(card)
                        game.enemyCaravans
                            .withIndex()
                            .filter { it.index != caravanIndex }
                            .forEach { (otherCaravanIndex, otherCaravan) ->
                                if (
                                    otherCaravan.getValue() + card.rank.value == game.playerCaravans[otherCaravanIndex].getValue() &&
                                    otherCaravan.canPutCardOnTop(card)
                                ) {
                                    otherCaravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
                                    return
                                }

                                // Try overselling player's caravan
                                if (
                                    otherCaravan.getValue() + card.rank.value > game.playerCaravans[otherCaravanIndex].getValue() &&
                                    otherCaravan.getValue() + card.rank.value <= 26 &&
                                    otherCaravan.canPutCardOnTop(card)
                                ) {
                                    otherCaravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
                                    return
                                }
                            }
                    }

                // 2.75) Maybe try a Queen!
                val playerCaravan = game.playerCaravans[caravanIndex]
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


        if (game.playerCResources.deckSize >= game.enemyCResources.deckSize) {
            if (overWeightCaravans.isNotEmpty()) {
                overWeightCaravans.maxBy { it.getValue() }.dropCaravan(speed)
                return
            }
            val caravan = game.enemyCaravans
                .filter { it.getValue() < 11 && !it.isEmpty() }
                .minByOrNull { it.getValue() }
            if (caravan != null) {
                caravan.dropCaravan(speed)
                return
            }

            if (game.enemyCResources.deckSize == 0) {
                val caravanC = game.enemyCaravans
                    .filter { !it.isEmpty() }
                    .minByOrNull { it.getValue() }
                if (caravanC != null) {
                    caravanC.dropCaravan(speed)
                    return
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
                val isLastCaravanInContention = checkOnResult(game, it.index) in listOf(
                    GamePossibleResult.IMMINENT_ENEMY_VICTORY,
                    GamePossibleResult.ENEMY_VICTORY_IS_POSSIBLE
                )
                if (isLastCaravanInContention) {
                    150 + ourValue
                } else if (ourValue !in (21..26) && playerValue !in (21..26)) {
                    ourValue
                } else if (ourValue !in (21..26) && playerValue in (21..26)) {
                    ourValue + 100
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
                        if (newSum <= 26 &&
                            caravan.canPutCardOnTop(card) &&
                            !(checkMoveOnProbableDefeat(game, caravanIndex) && newSum in (21..26)) &&
                            !(checkOnResult(game, caravanIndex) == GamePossibleResult.IMMINENT_PLAYER_VICTORY && newSum in (21..26))
                        ) {
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
                        val afterJackValue = it.value.getValue() - card.getValue()
                        if (
                            afterJackValue <= 26 &&
                            !(checkMoveOnProbableDefeat(game, it.index) && afterJackValue in (21..26))
                        ) {
                            card.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardModifier, speed)
                            return
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
                        val afterKingValue = it.value.getValue() + card.getValue()
                        if (
                            afterKingValue <= 26 &&
                            !(checkMoveOnProbableDefeat(game, it.index) && afterKingValue in (21..26))
                        ) {
                            card.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardModifier, speed)
                            return
                        }
                    }
            }
        }

        val queen = hand.indexOfFirst { it is CardFace && it.rank == RankFace.QUEEN }
        if (queen != -1) {
            if (StrategyQueenToSelf(queen).move(game, speed)) {
                return
            }
        }

        val joker = hand.indexOfFirst { it is CardFace && it.rank == RankFace.JOKER }
        if (joker != -1) {
            if (StrategyJokerSimple(joker).move(game, speed)) {
                return
            }
        }

        game.enemyCaravans.sortedByDescending { it.getValue() }.forEachIndexed { indexC, caravan ->
            if (caravan.getValue() > 26) {
                val state = gameToState(game)
                when (indexC) {
                    0 -> state.enemy.v1 = 0
                    1 -> state.enemy.v2 = 0
                    2 -> state.enemy.v3 = 0
                }
                if (checkTheOutcome(state) != 1) {
                    caravan.dropCaravan(speed)
                    return
                }
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
                        RankFace.QUEEN -> 6
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
