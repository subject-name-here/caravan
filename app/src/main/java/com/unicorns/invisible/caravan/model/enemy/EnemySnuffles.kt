package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.random.Random


@Serializable
data object EnemySnuffles : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        CardBack.classicDecks.forEach { back ->
            Rank.entries.forEach { rank ->
                if (rank == Rank.JOKER) {
                    add(Card(Rank.JOKER, Suit.HEARTS, back, false))
                    add(Card(Rank.JOKER, Suit.CLUBS, back, false))
                } else {
                    listOf(Suit.CLUBS, Suit.SPADES).forEach { suit ->
                        add(Card(rank, suit, back, false))
                    }
                }
            }
        }
        Rank.entries.forEach { rank ->
            if (rank.value <= 4) {
                Suit.entries.forEach { suit ->
                    add(Card(rank, suit, CardBack.WILD_WASTELAND, true))
                }
            }
        }
        add(Card(Rank.KING, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.KING, Suit.CLUBS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.KING, Suit.DIAMONDS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.KING, Suit.SPADES, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.JACK, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.JACK, Suit.CLUBS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.JACK, Suit.DIAMONDS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.JACK, Suit.SPADES, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.QUEEN, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
    })
    override fun getRewardBack() = null

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filter { !it.isSpecial() }.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        val specials = hand.withIndex().filter { it.value.isSpecial() }
        specials.forEach { (index, special) ->
            when (special.getWildWastelandCardType()) {
                Card.WildWastelandCardType.CAZADOR -> {
                    val candidate = game.playerCaravans
                        .filter { it.getValue() in (11..26) }
                        .filter { !it.cards.any { card -> card.isProtectedByMuggy } }
                        .maxByOrNull { it.size }
                        ?.cards
                        ?.filter { it.canAddModifier(special) }
                        ?.maxByOrNull { it.getValue() }
                    if (candidate != null && Random.nextBoolean()) {
                        candidate.addModifier(game.enemyCResources.removeFromHand(index))
                        game.wildWastelandSound()
                        return
                    }
                }
                Card.WildWastelandCardType.DIFFICULT_PETE -> {
                    val candidate = (game.playerCaravans + game.enemyCaravans)
                        .flatMap { it.cards }
                        .firstOrNull { it.canAddModifier(special) }
                    if (candidate != null && Random.nextBoolean()) {
                        candidate.addModifier(game.enemyCResources.removeFromHand(index))
                        game.wildWastelandSound()
                        return
                    }
                }
                Card.WildWastelandCardType.FEV -> {
                    val candidate = game.playerCaravans
                        .flatMap { it.cards }
                        .sortedBy { it.card.rank.value }
                        .firstOrNull { it.canAddModifier(special) }
                    if (candidate != null && Random.nextBoolean()) {
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
                    if (candidate1 != null && Random.nextBoolean()) {
                        candidate1.addModifier(game.enemyCResources.removeFromHand(index))
                        game.wildWastelandSound()
                        return
                    } else if (candidate2 != null && Random.nextBoolean()) {
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
                    if (candidate != null && Random.nextBoolean()) {
                        candidate.addModifier(game.enemyCResources.removeFromHand(index))
                        game.wildWastelandSound()
                        return
                    }
                }
                Card.WildWastelandCardType.UFO -> {
                    val candidate = (game.playerCaravans + game.enemyCaravans)
                        .flatMap { it.cards }
                        .firstOrNull { it.canAddModifier(special) }
                    if (candidate != null && Random.nextBoolean()) {
                        candidate.addModifier(game.enemyCResources.removeFromHand(index))
                        game.wildWastelandSound()
                        return
                    }
                }
                null -> {
                    if (special.isAlt && special.back == CardBack.WILD_WASTELAND) {
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


        hand.withIndex().filter { !it.value.isSpecial() }.shuffled().forEach { (cardIndex, card) ->
            if (!card.rank.isFace()) {
                game.enemyCaravans.shuffled().forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }
            if (card.rank == Rank.JACK) {
                val caravan =
                    game.playerCaravans.filter { it.getValue() in (21..26) }.randomOrNull()
                if (caravan != null) {
                    val cardToAdd = caravan.cards.maxBy { it.getValue() }
                    if (cardToAdd.canAddModifier(card)) {
                        cardToAdd.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
            if (card.rank == Rank.KING) {
                val caravan =
                    game.playerCaravans.filter { it.getValue() in (21..26) }.randomOrNull()
                if (caravan != null) {
                    val cardToKing = caravan.cards.filter { it.canAddModifier(card) }
                        .maxByOrNull { it.card.rank.value }
                    if (cardToKing != null && cardToKing.canAddModifier(card)) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }

            if (card.rank == Rank.QUEEN) {
                val possibleQueenCaravans = game.enemyCaravans
                    .filter { c ->
                        c.size >= 2 &&
                                hand.all { !c.canPutCardOnTop(it) } &&
                                c.cards.last().canAddModifier(card)
                    }
                if (possibleQueenCaravans.isNotEmpty()) {
                    possibleQueenCaravans
                        .first()
                        .cards
                        .last()
                        .addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }

            if (StrategyJokerSimple.move(game)) {
                game.jokerPlayedSound()
                return
            }
        }

        game.enemyCResources.dropCardFromHand(hand.withIndex().minByOrNull {
            if (it.value.isSpecial()) {
                15
            } else {
                when (it.value.rank) {
                    Rank.ACE -> 3
                    Rank.TWO -> 2
                    Rank.THREE -> 2
                    Rank.FOUR -> 3
                    Rank.FIVE -> 3
                    Rank.SIX -> 4
                    Rank.SEVEN -> 5
                    Rank.EIGHT -> 5
                    Rank.NINE -> 5
                    Rank.TEN -> 5
                    Rank.JACK -> 6
                    Rank.QUEEN -> 4
                    Rank.KING -> 6
                    Rank.JOKER -> 7
                }
            }
        }!!.index)
    }
}