package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructive
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyRush
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyTime
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.random.Random


@Serializable
data object EnemyPriestess : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.DECK_13, true).apply {
        add(Card(Rank.ACE, Suit.HEARTS, CardBack.UNPLAYABLE, true))

        add(Card(Rank.ACE, Suit.HEARTS, CardBack.WILD_WASTELAND, true))
        add(Card(Rank.ACE, Suit.CLUBS, CardBack.WILD_WASTELAND, true))

        add(Card(Rank.KING, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.KING, Suit.CLUBS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.KING, Suit.DIAMONDS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.KING, Suit.SPADES, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.JACK, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.JACK, Suit.CLUBS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.JACK, Suit.DIAMONDS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.JACK, Suit.SPADES, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.QUEEN, Suit.HEARTS, CardBack.WILD_WASTELAND, false))

        Rank.entries.forEach { rank ->
            Suit.entries.forEach { suit ->
                if ((0..2).random() == 0) {
                    add(Card(rank, suit, CardBack.DECK_13, false))
                }
            }
        }
    })
    override fun getRewardBack() = CardBack.DECK_13
    override fun isAlt(): Boolean = true

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filter { !it.isSpecial() }.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        fun check(p0: Int, e0: Int): Float {
            return when {
                p0 in (21..26) && (p0 > e0 || e0 > 26) -> 2f
                p0 > 11 && (e0 != 26 || e0 == p0) -> 0.5f
                else -> 0f
            }
        }

        val score = game.playerCaravans.indices.map {
            check(
                game.playerCaravans[it].getValue(),
                game.enemyCaravans[it].getValue()
            )
        }

        val specials = hand.withIndex().filter { it.value.isSpecial() }
        specials.forEach { (index, special) ->
            when (special.getWildWastelandCardType()) {
                Card.WildWastelandCardType.CAZADOR -> {
                    val candidate = game.playerCaravans
                        .filter { it.getValue() in (1..26) }
                        .filter { !it.cards.any { card -> card.isProtectedByMuggy } }
                        .sortedByDescending { it.cards.size }
                        .maxByOrNull { it.getValue() }
                        ?.cards
                        ?.filter { it.canAddModifier(special) }
                        ?.maxByOrNull { it.getValue() }
                    if (candidate != null) {
                        candidate.addModifier(game.enemyCResources.removeFromHand(index))
                        game.wildWastelandSound()
                        return
                    }
                }
                Card.WildWastelandCardType.DIFFICULT_PETE -> {
                    if (score.sum() > 2f) {
                        val candidate = (game.playerCaravans + game.enemyCaravans)
                            .flatMap { it.cards }
                            .firstOrNull { it.canAddModifier(special) }
                        if (candidate != null) {
                            candidate.addModifier(game.enemyCResources.removeFromHand(index))
                            game.wildWastelandSound()
                            return
                        }
                    }
                }
                Card.WildWastelandCardType.FEV -> {
                    if (score.sum() > 2f) {
                        val candidate = game.playerCaravans
                            .flatMap { it.cards }
                            .sortedBy { it.card.rank.value }
                            .firstOrNull { it.canAddModifier(special) }
                        if (candidate != null) {
                            candidate.addModifier(game.enemyCResources.removeFromHand(index))
                            game.wildWastelandSound()
                            return
                        }
                    }
                }
                Card.WildWastelandCardType.MUGGY -> {
                    val candidate1 = game.enemyCaravans
                        .filter { it.cards.any { card -> card.canAddModifier(special) } }
                        .filter { it.getValue() in (21..26) }
                        .maxByOrNull { it.getValue() }
                        ?.cards?.find { it.canAddModifier(special) }
                    val candidate2 = game.playerCaravans
                        .filter { it.cards.any { card -> card.canAddModifier(special) } }
                        .filter { it.getValue() > 26 }
                        .maxByOrNull { it.getValue() }
                        ?.cards?.find { it.canAddModifier(special) }
                    if (candidate1 != null) {
                        candidate1.addModifier(game.enemyCResources.removeFromHand(index))
                        game.wildWastelandSound()
                        return
                    } else if (candidate2 != null) {
                        candidate2.addModifier(game.enemyCResources.removeFromHand(index))
                        game.wildWastelandSound()
                        return
                    }
                }
                Card.WildWastelandCardType.YES_MAN -> {
                    val candidate = game.enemyCaravans
                        .filter { !it.isEmpty() }
                        .filter { it.cards.any { card -> card.canAddModifier(special) } }
                        .maxByOrNull { abs(26 - it.getValue()) }
                        ?.cards?.find { it.canAddModifier(special) }
                    if (candidate != null) {
                        candidate.addModifier(game.enemyCResources.removeFromHand(index))
                        game.wildWastelandSound()
                        return
                    }
                }
                Card.WildWastelandCardType.UFO -> {
                    if (score.sum() > 2f) {
                        val candidate = (game.playerCaravans + game.enemyCaravans)
                            .flatMap { it.cards }
                            .firstOrNull { it.canAddModifier(special) }
                        if (candidate != null) {
                            candidate.addModifier(game.enemyCResources.removeFromHand(index))
                            game.wildWastelandSound()
                            return
                        }
                    }
                }
                null -> {
                    if (special.isAlt && special.back == CardBack.WILD_WASTELAND) {
                        if (score.sum() > 2f) {
                            val candidate = game.enemyCaravans
                                .flatMap { it.cards }
                                .shuffled()
                                .firstOrNull { it.canAddModifier(special) }
                            if (candidate != null) {
                                candidate.addModifier(game.enemyCResources.removeFromHand(index))
                                game.nukeBlownSound()
                                return
                            }
                        }
                    }
                }
            }
        }

        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val playersReadyCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }

        hand.withIndex().filter { !it.value.isSpecial() }.shuffled().sortedByDescending {
            if (playersReadyCaravans.isNotEmpty()) {
                when (it.value.rank) {
                    Rank.JOKER -> 38
                    Rank.JACK, Rank.KING -> 30
                    Rank.TEN, Rank.NINE, Rank.SEVEN, Rank.SIX -> 20
                    Rank.QUEEN -> 4
                    else -> it.value.rank.value
                }
            } else {
                when (it.value.rank) {
                    Rank.TEN, Rank.NINE, Rank.SEVEN, Rank.SIX -> 20
                    Rank.QUEEN, Rank.ACE -> 4
                    Rank.JACK, Rank.KING, Rank.JOKER -> 2
                    else -> it.value.rank.value
                }
            }
        }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.JACK) {
                val caravan =
                    game.playerCaravans.withIndex().filter { it.value.getValue() in (12..26) }
                        .randomOrNull()
                val cardToJack = caravan?.value?.cards?.maxByOrNull { it.getValue() }
                if (cardToJack != null && cardToJack.canAddModifier(card)) {
                    val futureValue = caravan.value.getValue() - cardToJack.getValue()
                    val enemyValue = game.enemyCaravans[caravan.index].getValue()
                    if (!(checkMoveOnDefeat(
                            game,
                            caravan.index
                        ) && enemyValue in (21..26) && (enemyValue > futureValue || futureValue > 26))
                    ) {
                        cardToJack.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
            if (card.rank == Rank.KING) {
                val caravan = game.playerCaravans.minBy { abs(26 - it.getValue()) }
                val caravanIndex = game.playerCaravans.indexOf(caravan)
                val cardToKing = caravan.cards
                    .filter { caravan.getValue() + it.getValue() > 26 }
                    .maxByOrNull { it.getValue() }

                if (cardToKing != null && cardToKing.canAddModifier(card)) {
                    val futureValue = caravan.getValue() + cardToKing.getValue()
                    val enemyValue = game.enemyCaravans[caravanIndex].getValue()
                    if (!(checkMoveOnDefeat(
                            game,
                            caravanIndex
                        ) && enemyValue in (21..26) && (enemyValue > futureValue || futureValue > 26))
                    ) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }

                game.enemyCaravans
                    .flatMap { c -> c.cards.map { it to c } }
                    .sortedByDescending { it.first.getValue() }
                    .forEach {
                        if (it.second.getValue() + it.first.getValue() in (13..26)) {
                            if (it.first.canAddModifier(card)) {
                                val futureValue = it.second.getValue() + it.first.getValue()
                                val playerValue = caravan.getValue()
                                if (!(checkMoveOnDefeat(
                                        game,
                                        caravanIndex
                                    ) && futureValue in (21..26) && (futureValue > playerValue || playerValue > 26))
                                ) {
                                    it.first.addModifier(
                                        game.enemyCResources.removeFromHand(
                                            cardIndex
                                        )
                                    )
                                    return
                                }
                            }
                        }
                    }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans
                    .sortedByDescending { it.getValue() }
                    .forEachIndexed { caravanIndex, caravan ->
                        if (caravan.getValue() + card.rank.value <= 26 && caravan.canPutCardOnTop(
                                card
                            )
                        ) {
                            if (!(checkMoveOnDefeat(
                                    game,
                                    caravanIndex
                                ) && caravan.getValue() + card.rank.value in (21..26))
                            ) {
                                caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                                return
                            }
                        }
                    }
            }

            if (card.rank == Rank.QUEEN) {
                val possibleQueenCaravans = game.enemyCaravans
                    .filter { c ->
                        c.size >= 2 && c.getValue() < 21 && hand.all { !c.canPutCardOnTop(it) } && c.cards.last()
                            .canAddModifier(card)
                    }
                if (possibleQueenCaravans.isNotEmpty()) {
                    possibleQueenCaravans
                        .random()
                        .cards
                        .last()
                        .addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }

            if (card.rank == Rank.JOKER) {
                if (StrategyJoker.move(game)) {
                    game.jokerPlayedSound()
                    return
                }
            }

            if (card.rank == Rank.JACK && overWeightCaravans.isNotEmpty()) {
                val enemyCaravan = overWeightCaravans.random()
                val cardToDelete = enemyCaravan.cards.maxBy { it.getValue() }
                if (cardToDelete.canAddModifier(card)) {
                    cardToDelete.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        game.enemyCResources.dropCardFromHand(hand.withIndex().minBy {
            if (it.value.isSpecial()) {
                15
            } else when (it.value.rank) {
                Rank.QUEEN -> 0
                else -> it.value.rank.value
            }
        }.index)
    }
}