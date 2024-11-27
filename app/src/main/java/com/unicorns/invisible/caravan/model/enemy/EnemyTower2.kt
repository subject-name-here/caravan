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
data object EnemyTower2 : Enemy {
    override fun createDeck() = CResources(CustomDeck().apply {
        listOf(CardBack.TOPS, CardBack.GOMORRAH, CardBack.ULTRA_LUXE, CardBack.LUCKY_38)
            .forEach { back ->
                Suit.entries.forEach { suit ->
                    add(Card(Rank.SIX, suit, back, false))
                    add(Card(Rank.JACK, suit, back, true))
                    add(Card(Rank.QUEEN, suit, back, true))
                    add(Card(Rank.KING, suit, back, false))
                }
            }
    })

    override fun makeMove(game: Game) {
        TODO("Not yet implemented")
    }
}