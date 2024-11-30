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
data object EnemyTower9 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(CardBack.TOPS, CardBack.ULTRA_LUXE, CardBack.GOMORRAH, CardBack.LUCKY_38, CardBack.VAULT_21)
            .forEach { back ->
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

    override fun makeMove(game: Game) {
        EnemyUlysses.makeMove(game)
    }
}