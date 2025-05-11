package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.tabitha
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructiveClever
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropAllButFace
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.checkIfEnemyVictoryIsClose
import com.unicorns.invisible.caravan.model.enemy.strategy.checkOnResult
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlin.math.abs


@Serializable
class EnemyTabitha : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.tabitha
    override val isEven
        get() = false
    override val level: Int
        get() = 6
    override val isAvailable: Boolean
        get() = true

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        val backs = listOf(
            CardBack.STANDARD_UNCOMMON,
            CardBack.ULTRA_LUXE,
            CardBack.LUCKY_38,
            CardBack.GOMORRAH,
            CardBack.TOPS,
            CardBack.VAULT_21_NIGHT
        )
        val suit = Suit.DIAMONDS
        backs.forEach { back ->
            RankNumber.entries.forEach { rank ->
                if (rank != RankNumber.ACE) {
                    add(CardNumber(rank, suit, back))
                }
            }
            Suit.entries.forEach { s ->
                add(CardFaceSuited(RankFace.JACK, s, back))
                add(CardFaceSuited(RankFace.KING, s, back))
            }

            add(CardJoker(CardJoker.Number.ONE, back))
            add(CardJoker(CardJoker.Number.TWO, back))
        }
    })

    override val maxBets: Int
        get() = 2
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 25

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.MAX_FIRST_TO_RANDOM).move(game, speed)
            return
        }

        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val playersReadyCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }
        val hand = game.enemyCResources.hand

        game.enemyCaravans.withIndex().forEach { (index, _) ->
            if (checkIfEnemyVictoryIsClose(gameToState(game), index)) {
                if (StrategyDestructiveClever().move(game, speed)) {
                    return
                }
            }
        }

        val jack = hand.find { it is CardFace && it.rank == RankFace.JACK }
        val aceOfDiamonds = game.playerCaravans.flatMap { it.cards }
            .find { it.card.rank == RankNumber.ACE && it.card.suit == Suit.DIAMONDS }
        if (jack != null && aceOfDiamonds != null && aceOfDiamonds.canAddModifier(jack as CardModifier)) {
            val state = gameToState(game)
            val index = game.playerCaravans.indexOfFirst { aceOfDiamonds in it.cards }
            when (index) {
                0 -> state.player.v1 -= aceOfDiamonds.getValue()
                1 -> state.player.v2 -= aceOfDiamonds.getValue()
                2 -> state.player.v3 -= aceOfDiamonds.getValue()
            }

            if (checkTheOutcome(state) != 1 && (0..2).none { checkOnResult(state, it).isPlayerMoveWins() }) {
                val jackIndex = hand.indexOf(jack)
                aceOfDiamonds.addModifier(
                    game.enemyCResources.removeFromHand(
                        jackIndex,
                        speed
                    ) as CardFace, speed
                )
                return
            }
        }


        hand.withIndex().shuffled().sortedByDescending {
            val card = it.value
            if (playersReadyCaravans.isNotEmpty()) {
                when (card) {
                    is CardBase -> {
                        when (card.rank) {
                            RankNumber.TEN, RankNumber.NINE, RankNumber.SEVEN, RankNumber.SIX -> 20
                            else -> card.rank.value
                        }
                    }

                    is CardFace -> {
                        when (card.rank) {
                            RankFace.JOKER -> 38
                            RankFace.JACK, RankFace.KING -> 30
                            else -> card.rank.value
                        }
                    }

                    else -> {
                        0
                    }
                }
            } else {
                when (card) {
                    is CardBase -> {
                        when (card.rank) {
                            RankNumber.TEN, RankNumber.NINE, RankNumber.SEVEN, RankNumber.SIX -> 20
                            RankNumber.ACE -> 4
                            else -> card.rank.value
                        }
                    }

                    is CardModifier -> {
                        2
                    }
                }
            }
        }.forEach { (cardIndex, card) ->
            if (card is CardFace && card.rank == RankFace.JACK) {
                val caravan = game.playerCaravans.withIndex()
                    .filter { it.value.getValue() in (12..26) }
                    .maxByOrNull { v -> v.value.getValue() }
                val cardToJack = caravan?.value?.cards?.maxByOrNull { it.getValue() }
                if (cardToJack != null && cardToJack.canAddModifier(card)) {
                    val state = gameToState(game)
                    val indexC = game.playerCaravans.withIndex()
                        .first { cardToJack in it.value.cards }.index
                    when (indexC) {
                        0 -> state.player.v1 -= cardToJack.getValue()
                        1 -> state.player.v2 -= cardToJack.getValue()
                        2 -> state.player.v3 -= cardToJack.getValue()
                    }
                    if (checkTheOutcome(state) != 1 && (0..2).none { checkOnResult(state, it).isPlayerMoveWins() }) {
                        cardToJack.addModifier(
                            game.enemyCResources.removeFromHand(
                                cardIndex,
                                speed
                            ) as CardModifier, speed
                        )
                        return
                    }
                }
            }
            if (card is CardFace && card.rank == RankFace.KING) {
                val caravan = game.playerCaravans.maxBy { abs(26 - it.getValue()) }
                val cardToKing = caravan.cards
                    .filter { caravan.getValue() + it.getValue() > 26 }
                    .maxByOrNull { it.getValue() }

                if (cardToKing != null && cardToKing.canAddModifier(card)) {
                    val state = gameToState(game)
                    val indexC = game.playerCaravans.indexOf(caravan)
                    when (indexC) {
                        0 -> state.player.v1 -= cardToKing.getValue()
                        1 -> state.player.v2 -= cardToKing.getValue()
                        2 -> state.player.v3 -= cardToKing.getValue()
                    }
                    if (checkTheOutcome(state) != 1 && (0..2).none { checkOnResult(state, it).isPlayerMoveWins() }) {
                        cardToKing.addModifier(
                            game.enemyCResources.removeFromHand(
                                cardIndex,
                                speed
                            ) as CardModifier, speed
                        )
                        return
                    }
                }

                game.enemyCaravans
                    .flatMap { c -> c.cards.map { it to c } }
                    .sortedByDescending { it.first.getValue() }
                    .forEach {
                        if (it.second.getValue() + it.first.getValue() in (13..26)) {
                            if (it.first.canAddModifier(card)) {
                                val state = gameToState(game)
                                val indexC = game.enemyCaravans.withIndex()
                                    .first { it2 -> it.first in it2.value.cards }.index
                                when (indexC) {
                                    0 -> state.enemy.v1 += it.first.getValue()
                                    1 -> state.enemy.v2 += it.first.getValue()
                                    2 -> state.enemy.v3 += it.first.getValue()
                                }
                                if (checkTheOutcome(state) != 1 && (0..2).none { checkOnResult(state, it).isPlayerMoveWins() }) {
                                    it.first.addModifier(
                                        game.enemyCResources.removeFromHand(
                                            cardIndex,
                                            speed
                                        ) as CardModifier, speed
                                    )
                                    return
                                }
                            }
                        }
                    }
            }

            if (card is CardBase) {
                game.enemyCaravans
                    .sortedByDescending { it.getValue() }
                    .forEachIndexed { indexC, caravan ->
                        if (caravan.getValue() + card.rank.value <= 26 && caravan.canPutCardOnTop(card)) {
                            val state = gameToState(game)
                            when (indexC) {
                                0 -> state.enemy.v1 += card.rank.value
                                1 -> state.enemy.v2 += card.rank.value
                                2 -> state.enemy.v3 += card.rank.value
                            }
                            if (checkTheOutcome(state) != 1 && (0..2).none { checkOnResult(state, it).isPlayerMoveWins() }) {
                                caravan.putCardOnTop(
                                    game.enemyCResources.removeFromHand(
                                        cardIndex,
                                        speed
                                    ) as CardBase, speed
                                )
                                return
                            }
                        }
                    }
            }

            if (card is CardJoker) {
                if (StrategyJokerSimple(cardIndex).move(game, speed)) {
                    return
                }
            }

            if (card is CardFace && card.rank == RankFace.JACK && overWeightCaravans.isNotEmpty()) {
                val enemyCaravan = overWeightCaravans.random()
                val cardToDelete = enemyCaravan.cards.maxBy { it.getValue() }
                if (cardToDelete.canAddModifier(card)) {
                    val state = gameToState(game)
                    val indexC = game.enemyCaravans.indexOf(enemyCaravan)
                    when (indexC) {
                        0 -> state.enemy.v1 -= cardToDelete.getValue()
                        1 -> state.enemy.v2 -= cardToDelete.getValue()
                        2 -> state.enemy.v3 -= cardToDelete.getValue()
                    }
                    if (checkTheOutcome(state) != 1 && (0..2).none { checkOnResult(state, it).isPlayerMoveWins() }) {
                        cardToDelete.addModifier(
                            game.enemyCResources.removeFromHand(
                                cardIndex,
                                speed
                            ) as CardModifier, speed
                        )
                        return
                    }
                }
            }
        }

        game.enemyCaravans.forEachIndexed { indexC, caravan ->
            if (caravan.getValue() > 26) {
                caravan.dropCaravan(speed)
                return
            }
        }

        StrategyDropAllButFace(RankFace.JACK).move(game, speed)
    }
}