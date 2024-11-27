package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable


@Serializable
data object EnemyTheManInTheMirror : Enemy {
    override fun createDeck() = CResources(CustomDeck())

    override fun makeMove(game: Game) {
        TODO("Not yet implemented")
    }
}