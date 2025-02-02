package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.CardDropSelect
import com.unicorns.invisible.caravan.model.enemy.strategy.DropSelection
import com.unicorns.invisible.caravan.model.enemy.strategy.SelectCard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropCaravan
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropCard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInitStage
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.model.trading.GomorrahTrader
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.utils.checkMoveOnDefeat
import com.unicorns.invisible.caravan.utils.checkMoveOnShouldYouDoSmth
import kotlinx.serialization.Serializable
import kotlin.math.abs


@Serializable
data object EnemyMadnessCardinal : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.MADNESS, false).apply {
        add(Card(Rank.ACE, Suit.HEARTS, CardBack.ENCLAVE, true))
        add(Card(Rank.ACE, Suit.CLUBS, CardBack.ENCLAVE, true))
        add(Card(Rank.ACE, Suit.DIAMONDS, CardBack.ENCLAVE, true))
        add(Card(Rank.ACE, Suit.SPADES, CardBack.ENCLAVE, true))

        add(Card(Rank.KING, Suit.HEARTS, CardBack.MADNESS, true))
        add(Card(Rank.KING, Suit.CLUBS, CardBack.MADNESS, true))
        add(Card(Rank.KING, Suit.DIAMONDS, CardBack.MADNESS, true))
        add(Card(Rank.KING, Suit.SPADES, CardBack.MADNESS, true))
        add(Card(Rank.JACK, Suit.HEARTS, CardBack.MADNESS, true))
        add(Card(Rank.JACK, Suit.CLUBS, CardBack.MADNESS, true))
        add(Card(Rank.QUEEN, Suit.SPADES, CardBack.MADNESS, true))
    })

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            StrategyInitStage(SelectCard.MAX_TO_RANDOM).move(game)
            return
        }

        // 2) If not and if player is abt to win, destroy player ready and almost ready caravans (on right columns!)
        var isLosingAny = false
        game.enemyCaravans.withIndex().forEach { (caravanIndex, caravan) ->
            val isLosing = checkMoveOnDefeat(game, caravanIndex) || checkMoveOnShouldYouDoSmth(game, caravanIndex)
            if (isLosing) {
                isLosingAny = true
            }
        }


        val specials = hand.withIndex().filter { !it.value.isOrdinary() }
        specials.forEach { (index, special) ->
            when (special.getWildWastelandCardType()) {
                Card.WildWastelandCardType.CAZADOR -> {
                    val candidate = game.playerCaravans
                        .filter { it.getValue() in (11..26) }
                        .filter { !it.cards.any { card ->
                            val mods = card.modifiersCopy()
                            card.isProtectedByMuggy || mods.any { mod ->
                                mod.getWildWastelandCardType() == Card.WildWastelandCardType.CAZADOR
                            }
                        } }
                        .maxByOrNull { it.size }
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
                    val candidate = (game.playerCaravans + game.enemyCaravans)
                        .flatMap { it.cards }
                        .firstOrNull { it.canAddModifier(special) }
                    if (candidate != null && isLosingAny) {
                        candidate.addModifier(game.enemyCResources.removeFromHand(index))
                        game.wildWastelandSound()
                        return
                    }
                }
                Card.WildWastelandCardType.FEV -> {
                    val candidate = game.playerCaravans
                        .flatMap { it.cards }
                        .sortedBy { it.card.rank.value }
                        .firstOrNull { it.canAddModifier(special) && it.card.rank.value <= 4 }
                    if (candidate != null) {
                        candidate.addModifier(game.enemyCResources.removeFromHand(index))
                        game.wildWastelandSound()
                        return
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
                    val candidate = (game.playerCaravans + game.enemyCaravans)
                        .flatMap { it.cards }
                        .firstOrNull { it.canAddModifier(special) }
                    if (candidate != null && isLosingAny) {
                        candidate.addModifier(game.enemyCResources.removeFromHand(index))
                        game.wildWastelandSound()
                        return
                    }
                }
                null -> {
                    if (special.isNuclear()) {
                        val candidate = game.enemyCaravans
                            .filter { !it.isEmpty() }
                            .filter { it.cards.any { card -> card.canAddModifier(special) } }
                            .maxByOrNull { abs(26 - it.getValue()) }
                            ?.cards?.find { it.canAddModifier(special) }
                        if (candidate != null && isLosingAny) {
                            candidate.addModifier(game.enemyCResources.removeFromHand(index))
                            game.nukeBlownSound()
                            return
                        }
                    }
                }
            }
        }

        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val playersReadyCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }

        hand.withIndex().filter { it.value.isOrdinary() }.shuffled().sortedByDescending {
            if (playersReadyCaravans.isNotEmpty()) {
                when (it.value.rank) {
                    Rank.JOKER -> 38
                    Rank.JACK, Rank.KING -> 30
                    Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN, Rank.SIX -> 20
                    Rank.QUEEN -> 2
                    else -> it.value.rank.value
                }
            } else {
                when (it.value.rank) {
                    Rank.TEN, Rank.NINE, Rank.EIGHT, Rank.SEVEN, Rank.SIX -> 20
                    Rank.QUEEN -> 3
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
                    .filter { caravan.getValue() + it.getValue() !in (21..26) }
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
                        if (caravan.getValue() + card.rank.value <= 26 && caravan.canPutCardOnTop(card)) {
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
                if (StrategyJokerSimple.move(game)) {
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

        if (StrategyDropCaravan(DropSelection.RANDOM).move(game)) {
            return
        }

        StrategyDropCard(CardDropSelect.MIN_VALUE).move(game)
    }

    override fun onVictory() {
        save.traders.filterIsInstance<GomorrahTrader>().forEach { it.cardinalDefeated = true }
    }
}