package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
data object EnemyBenny : Enemy {
    override fun createDeck() = CResources(CardBack.TOPS, true)

    override fun makeMove(game: Game) {
        TODO("Not yet implemented")
    }
}