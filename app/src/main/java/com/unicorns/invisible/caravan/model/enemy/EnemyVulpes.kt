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
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.trading.GomorrahTrader
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.utils.checkMoveOnDefeat
import kotlinx.serialization.Serializable


@Serializable
data object EnemyVulpes : EnemyPve {
    override fun getNameId() = R.string.vulpes
    override fun isEven() = false

    override fun getBank(): Int { return 0 }
    override fun refreshBank() {}
    override fun getBet(): Int? { return null }
    override fun retractBet() {}
    override fun addReward(reward: Int) {}

    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.LUCKY_38, true).apply {
        removeAll(toList().filter { it.rank.value < 5 && it.rank.value != Rank.QUEEN.value })
    })

    override fun makeMove(game: Game) {
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val playersReadyCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            StrategyInitStage(SelectCard.MAX_TO_RANDOM).move(game)
            return
        }

        hand.withIndex().sortedByDescending { it.value.rank.value }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.JOKER) {
                if (StrategyJoker.move(game)) {
                    game.jokerPlayedSound()
                    return
                }
            }

            if (card.rank == Rank.KING) {
                val caravanToOverweight = game.playerCaravans.filter { it.getValue() > 13 }
                if (caravanToOverweight.isNotEmpty()) {
                    caravanToOverweight.sortedBy { it.getValue() }.forEach { caravan ->
                        val cardToKing = caravan.cards.filter { it.canAddModifier(card) }
                            .maxByOrNull { it.getValue() }
                        if (cardToKing != null && caravan.getValue() + cardToKing.getValue() > 26) {
                            cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
                game.enemyCaravans.withIndex().shuffled().forEach { (index, enemyCaravan) ->
                    enemyCaravan.cards.sortedByDescending { it.card.rank.value }
                        .forEach { caravanCard ->
                            if (caravanCard.canAddModifier(card)
                                && enemyCaravan.getValue() + caravanCard.getValue() in (21..26)
                                && !checkMoveOnDefeat(game, index)
                            ) {
                                caravanCard.addModifier(
                                    game.enemyCResources.removeFromHand(cardIndex)
                                )
                                return
                            }
                        }
                }
            }

            if (card.rank == Rank.JACK) {
                if (playersReadyCaravans.isNotEmpty()) {
                    val caravanToAttack = playersReadyCaravans.maxBy { it.getValue() }
                    val cardToJack = caravanToAttack.cards.maxBy { it.getValue() }
                    if (cardToJack.canAddModifier(card)) {
                        cardToJack.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }

                if (overWeightCaravans.isNotEmpty()) {
                    val enemyCaravan = overWeightCaravans.minBy { it.getValue() }
                    val cardToDelete = enemyCaravan.cards.maxBy { it.getValue() }
                    if (cardToDelete.canAddModifier(card)) {
                        cardToDelete.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans.withIndex().sortedBy { it.value.getValue() }
                    .forEach { (caravanIndex, caravan) ->
                        if (caravan.getValue() + card.rank.value <= 26 && caravan.canPutCardOnTop(
                                card
                            )
                        ) {
                            if (!checkMoveOnDefeat(game, caravanIndex)) {
                                caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                                return
                            }
                        }
                    }
            }
        }

        if (StrategyDropCaravan(DropSelection.MAX_WEIGHT).move(game)) {
            return
        }

        StrategyDropCard(CardDropSelect.MIN_VALUE).move(game)
    }
}