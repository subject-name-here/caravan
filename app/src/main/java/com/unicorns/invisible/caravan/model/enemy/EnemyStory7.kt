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
data object EnemyStory7 : Enemy {
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
    })

    override fun makeMove(game: Game) {
        EnemyUlysses.makeMove(game)
    }
}