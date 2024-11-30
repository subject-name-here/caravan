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
data object EnemyTower4 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.STANDARD, false).apply {
        addAll(CustomDeck(CardBack.STANDARD, true))
        removeAll(toList().filter { it.rank == Rank.JOKER })
    })

    override fun makeMove(game: Game) {
        EnemyTower3.makeMove(game)
    }
}