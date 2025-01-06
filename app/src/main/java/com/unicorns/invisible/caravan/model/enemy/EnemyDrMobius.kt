package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.SelectCard
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructive
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInitStage
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyRush
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
data object EnemyDrMobius : Enemy {
    override fun createDeck() = CResources(CustomDeck().apply {
        var cards = arrayOf<Card>()
        while (cards.count { card -> !card.isFace() } < 3) {
            cards = Array<Card>(8) { generateCard() }
        }
        cards.forEach { add(it) }
    })
    override fun getBankNumber() = 16

    override fun makeMove(game: Game) {
        makeMoveInner(game)
        if (game.enemyCResources.hand.size < 5) {
            game.enemyCResources.addOnTop(generateCard())
        }
    }

    private fun makeMoveInner(game: Game) {
        if (game.isInitStage()) {
            StrategyInitStage(SelectCard.RANDOM_TO_RANDOM).move(game)
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
        if (score.sum() > 2f || Random.nextBoolean() && score.sum() == 2f) {
            if (StrategyJoker.move(game)) {
                game.jokerPlayedSound()
                return
            }
            if (StrategyDestructive.move(game)) {
                return
            }
        }

        if (StrategyRush.move(game)) {
            return
        }
        if (StrategyJoker.move(game)) {
            game.jokerPlayedSound()
            return
        }

        EnemyUlysses.makeMove(game)
    }

    fun generateCard(): Card {
        val rank = Rank.entries.random()
        return if (rank == Rank.JOKER) {
            Card(rank, listOf(Suit.HEARTS, Suit.CLUBS).random(), CardBack.LUCKY_38, false)
        } else {
            Card(rank, Suit.entries.random(), CardBack.STANDARD, Random.nextBoolean())
        }
    }
}