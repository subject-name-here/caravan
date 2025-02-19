package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
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
import com.unicorns.invisible.caravan.utils.checkMoveOnDefeat
import com.unicorns.invisible.caravan.utils.checkMoveOnShouldYouDoSmth
import kotlinx.serialization.Serializable
import kotlin.math.abs


@Serializable
data object EnemyEasyPete : EnemyPve {
    override fun getNameId() = R.string.easy_pete
    override fun isEven() = true

    override fun createDeck() = CResources(CustomDeck().apply {
        Rank.entries.forEach { rank ->
            if (rank != Rank.JOKER) {
                Suit.entries.forEach { suit ->
                    add(Card(rank, suit, CardBack.TOPS, false))
                }
            }
        }

        add(Card(Rank.ACE, Suit.CLUBS, CardBack.ENCLAVE, true))
        add(Card(Rank.ACE, Suit.SPADES, CardBack.ENCLAVE, true))
        add(Card(Rank.KING, Suit.SPADES, CardBack.MADNESS, true))
    })

    override fun getBank(): Int {
        return 0
    }

    override fun refreshBank() {

    }

    override fun getBet(): Int? {
        return 0
    }

    override fun retractBet() {

    }

    override fun addReward(reward: Int) {

    }

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            StrategyInitStage(SelectCard.MAX_TO_LTR).move(game)
            return
        }

        var isLosingAny = false
        game.enemyCaravans.withIndex().forEach { (caravanIndex, _) ->
            val isLosing = checkMoveOnDefeat(game, caravanIndex) || checkMoveOnShouldYouDoSmth(game, caravanIndex)
            if (isLosing) {
                isLosingAny = true
            }
        }

        val specials = hand.withIndex().filter { !it.value.isOrdinary() }
        specials.forEach { (index, special) ->
            when (special.getWildWastelandType()) {
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
                else -> {}
            }
        }


        hand.withIndex().filter { it.value.isOrdinary() }.sortedByDescending {
            when (it.value.rank) {
                Rank.JOKER -> 14
                Rank.JACK -> 8
                Rank.QUEEN -> 5
                Rank.KING -> 12
                else -> it.value.rank.value
            }
        }.forEach { (cardIndex, card) ->
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
                val caravans = game.playerCaravans.filter { it.getValue() in (21..26) }
                if (caravans.isNotEmpty()) {
                    val caravan = caravans.maxBy { it.getValue() }
                    val cardToAdd = caravan.cards.maxBy { it.getValue() }
                    if (cardToAdd.canAddModifier(card)) {
                        cardToAdd.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
            if (card.rank == Rank.KING) {
                val caravans = game.playerCaravans.filter { it.getValue() in (21..26) }
                if (caravans.isNotEmpty()) {
                    val caravan = caravans.maxBy { it.getValue() }
                    val cardToKing = caravan.cards.filter { it.canAddModifier(card) }
                        .maxByOrNull { it.card.rank.value }
                    if (cardToKing != null && cardToKing.canAddModifier(card)) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }

            if (card.rank == Rank.QUEEN) {
                val caravan = game.playerCaravans
                    .filter { it.size >= 3 }
                    .randomOrNull()
                if (caravan != null) {
                    val cardToQueen = caravan.cards.last()
                    if (cardToQueen.canAddModifier(card)) {
                        cardToQueen.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }

            if (card.rank == Rank.JOKER) {
                if (StrategyJokerSimple.move(game)) {
                    game.jokerPlayedSound()
                    return
                }
            }
        }

        if (StrategyDropCaravan(DropSelection.MAX_WEIGHT).move(game)) {
            return
        }

        StrategyDropCard(CardDropSelect.MIN_VALUE).move(game)
    }
}