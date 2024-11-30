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
data object EnemyTower6 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(
            CardBack.SIERRA_MADRE, CardBack.TOPS, CardBack.GOMORRAH,
            CardBack.ULTRA_LUXE, CardBack.LUCKY_38, CardBack.VAULT_21
        ).forEach { back ->
            Suit.entries.forEach { suit ->
                add(Card(Rank.SIX, suit, back, true))
                add(Card(Rank.TEN, suit, back, true))
                add(Card(Rank.KING, suit, back, true))
            }
            add(Card(Rank.JOKER, Suit.HEARTS, back, true))
            add(Card(Rank.JOKER, Suit.CLUBS, back, true))
        }
    })

    override fun makeMove(game: Game) {
        EnemyUlysses.makeMove(game)
    }
}