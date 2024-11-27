package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
data object EnemyVictor : Enemy {
    override fun createDeck() = CResources(CardBack.LUCKY_38, false)

    override fun makeMove(game: Game) {
        TODO("Not yet implemented")
    }
}