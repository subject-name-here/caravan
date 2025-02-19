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
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
data object EnemyVeronica : EnemyPve {
    override fun getNameId() = R.string.pve_enemy_veronica
    override fun isEven() = true

    override fun createDeck() = CResources(CardBack.ULTRA_LUXE, false)

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
            StrategyInitStage(SelectCard.RANDOM_TO_RANDOM).move(game)
            return
        }

        hand.withIndex().sortedByDescending {
            when (it.value.rank) {
                Rank.JOKER -> 7
                Rank.JACK -> 6
                Rank.QUEEN -> 4
                Rank.KING -> 5
                Rank.ACE -> 3
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
            if (card.rank == Rank.JACK && Random.nextBoolean()) {
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
            if (card.rank == Rank.KING && Random.nextBoolean()) {
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
                    .filter { it.size >= 2 }
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
                val cardToJoker = (game.playerCaravans + game.enemyCaravans)
                    .flatMap { it.cards }
                    .filter { it.canAddModifier(card) }
                    .randomOrNull()
                if (cardToJoker != null) {
                    cardToJoker.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    game.jokerPlayedSound()
                    return
                }
            }
        }

        if (StrategyDropCaravan(DropSelection.RANDOM).move(game)) {
            return
        }

        StrategyDropCard(CardDropSelect.VERONICA_ORDER).move(game)
    }
}