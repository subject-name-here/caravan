package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable

@Serializable
data object EnemyCaesar : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        CardBack.classicDecks.forEach { back ->
            Rank.entries.forEach { rank ->
                if (rank == Rank.JOKER) {
                    add(Card(Rank.JOKER, Suit.HEARTS, back, false))
                    add(Card(Rank.JOKER, Suit.CLUBS, back, false))
                } else if (rank.value in (4..10)) {
                    listOf(Suit.SPADES).forEach { suit ->
                        add(Card(rank, suit, back, false))
                    }
                } else if (rank.isFace() && rank != Rank.QUEEN) {
                    Suit.entries.forEach { suit ->
                        add(Card(rank, suit, back, false))
                    }
                }
            }
        }
    })
    override fun getRewardBack() = null

    override fun makeMove(game: Game) {
        EnemyBetter.makeMove(game)
    }
}