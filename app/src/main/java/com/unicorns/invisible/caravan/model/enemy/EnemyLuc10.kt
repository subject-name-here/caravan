package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.SelectCard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInitStage
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.model.trading.SierraMadreTrader
import com.unicorns.invisible.caravan.model.trading.TopsTrader
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.utils.checkMoveOnDefeat
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
data object EnemyLuc10 : Enemy {
    override fun createDeck() = CResources(CardBack.LUCKY_38, true)
    override fun getBankNumber() = 17

    override fun makeMove(game: Game) {
        if (game.isInitStage()) {
            StrategyInitStage(SelectCard.RANDOM_TO_RANDOM).move(game)
            return
        }
        val playerScores = game.playerCaravans.map { it.getValue() }.withIndex()
        val hand = game.enemyCResources.hand.withIndex()

        val playerReadyCaravans = playerScores.filter { it.value in (21..26) }.shuffled()
        if (playerReadyCaravans.isNotEmpty()) {
            if (StrategyJoker.move(game)) {
                game.jokerPlayedSound()
                return
            }
        }

        val kings = hand.filter { it.value.rank == Rank.KING }
        val jacks = hand.filter { it.value.rank == Rank.JACK }
        playerReadyCaravans.forEach { (caravanIndex, caravanValue) ->
            val caravan = game.playerCaravans[caravanIndex]
            if (kings.isNotEmpty()) {
                val (kingIndex, king) = kings.first()
                val cardToKing = caravan.cards
                    .filter { caravanValue + it.getValue() > 26 }
                    .maxByOrNull { it.getValue() }
                if (cardToKing != null && cardToKing.canAddModifier(king)) {
                    val futureValue = caravanValue + cardToKing.getValue()
                    val enemyValue = game.enemyCaravans[caravanIndex].getValue()
                    if (!(checkMoveOnDefeat(game, caravanIndex) && enemyValue in (21..26) && futureValue > 26)) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(kingIndex))
                        return
                    }
                }
            }
        }
        playerReadyCaravans.forEach {
            if (jacks.isNotEmpty()) {
                val (jackIndex, jack) = jacks.first()

                val caravan = game.playerCaravans[it.index]
                val cardToJack = caravan.cards.maxByOrNull { it.getValue() }
                if (cardToJack != null && cardToJack.canAddModifier(jack)) {
                    val futureValue = it.value - cardToJack.getValue()
                    val enemyValue = game.enemyCaravans[it.index].getValue()
                    if (!(checkMoveOnDefeat(game, it.index) && enemyValue in (21..26) && enemyValue > futureValue)) {
                        cardToJack.addModifier(game.enemyCResources.removeFromHand(jackIndex))
                        return
                    }
                }
            }
        }

        EnemyBenny.makeMove(game)

        // The deck is L-U-C-K-Y!

        if (playerReadyCaravans.isNotEmpty()) {
            Suit.entries.forEach { suit ->
                val ranks = if (Random.nextBoolean()) {
                    listOf(Rank.JACK, Rank.KING)
                } else {
                    listOf(Rank.KING, Rank.JACK)
                }
                ranks.forEach {
                    if (game.enemyCResources.moveOnTop(it, suit)) {
                        return
                    }
                }
            }
            if (Random.nextBoolean()) {
                if (
                    game.enemyCResources.moveOnTop(Rank.JOKER, Suit.HEARTS) ||
                    game.enemyCResources.moveOnTop(Rank.JOKER, Suit.CLUBS)
                ) {
                    return
                }
            }
        }

        game.enemyCaravans.sortedByDescending { it.getValue() }.forEach { caravan ->
            Suit.entries.shuffled().forEach { suit ->
                Rank.entries.reversed().filter { !it.isFace() }.forEach { rank ->
                    if (caravan.canPutCardOnTop(Card(rank, suit, CardBack.STANDARD, false))) {
                        if (caravan.getValue() + rank.value <= 26) {
                            if (game.enemyCResources.moveOnTop(rank, suit)) {
                                return
                            }
                        }
                    }
                }
            }
        }

        Suit.entries.shuffled().forEach { suit ->
            if (game.enemyCResources.moveOnTop(Rank.QUEEN, suit)) {
                return
            }
        }
    }

    override fun onVictory() {
        save.traders.filterIsInstance<TopsTrader>().forEach { it.isLuc10Defeated = true }
    }
}