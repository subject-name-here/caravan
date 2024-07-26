package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlin.math.abs


@Serializable
data object EnemyStory9A : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        Rank.entries.forEach { rank ->
            if (rank == Rank.JOKER) {
                add(Card(rank, Suit.HEARTS, CardBack.VAULT_21, true))
                add(Card(rank, Suit.CLUBS,  CardBack.VAULT_21, true))
                add(Card(rank, Suit.HEARTS, CardBack.VAULT_21, false))
                add(Card(rank, Suit.CLUBS,  CardBack.VAULT_21, false))
            } else {
                Suit.entries.forEach { suit ->
                    add(Card(rank, suit, CardBack.VAULT_21, true))
                    add(Card(rank, suit, CardBack.VAULT_21, false))
                }
            }
        }
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
        add(Card(Rank.QUEEN, Suit.SPADES, CardBack.WILD_WASTELAND, false))
    })
    override fun getRewardBack() = null

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
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


        EnemyCaesar.makeMove(game)
    }
}