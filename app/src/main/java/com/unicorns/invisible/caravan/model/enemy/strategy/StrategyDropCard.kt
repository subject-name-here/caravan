package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank


class StrategyDropCard(private val select: CardDropSelect) : Strategy {
    override fun move(game: Game): Boolean {
        val hand = game.enemyCResources.hand

        val index = when (select) {
            CardDropSelect.MIN_VALUE -> {
                hand.withIndex().minBy { it.value.rank.value }.index
            }
            CardDropSelect.VERONICA_ORDER -> {
                hand.withIndex().minBy {
                    when (it.value.rank) {
                        Rank.JOKER -> 7
                        Rank.JACK -> 6
                        Rank.QUEEN -> 4
                        Rank.KING -> 5
                        Rank.ACE -> 3
                        else -> it.value.rank.value
                    }
                }.index
            }
            CardDropSelect.ULYSSES_ORDER -> {
                hand.withIndex().minBy {
                    if (it.value.isNuclear())
                        15
                    else when (it.value.rank) {
                        Rank.ACE -> 4
                        Rank.TWO -> 3
                        Rank.THREE -> 3
                        Rank.FOUR -> 4
                        Rank.FIVE -> 5
                        Rank.SIX -> 5
                        Rank.SEVEN -> 6
                        Rank.EIGHT -> 6
                        Rank.NINE -> 7
                        Rank.TEN -> 8
                        Rank.JACK -> 12
                        Rank.QUEEN -> 6
                        Rank.KING -> 13
                        Rank.JOKER -> 14
                    }
                }.index
            }
            CardDropSelect.MIN_VALUE_Q0 -> {
                hand.withIndex().minBy {
                    when (it.value.rank) {
                        Rank.QUEEN -> 0
                        else -> it.value.rank.value
                    }
                }.index
            }
            CardDropSelect.RANDOM -> {
                hand.indices.random()
            }
            CardDropSelect.NASH_ORDER -> {
                hand.withIndex().minBy {
                    when (it.value.rank) {
                        Rank.JACK -> 0
                        Rank.QUEEN -> 1
                        Rank.SIX -> 2
                        Rank.KING -> 3
                        else -> 0
                    }
                }.index
            }
        }

        game.enemyCResources.dropCardFromHand(index)
        return true
    }
}

enum class CardDropSelect {
    MIN_VALUE,
    VERONICA_ORDER, // Weird "faces between numbers" order
    ULYSSES_ORDER,  // The most optimal order
    MIN_VALUE_Q0,   // Queen's value is 0
    RANDOM,
    NASH_ORDER      // Special order for 6KKQ tactics
}
