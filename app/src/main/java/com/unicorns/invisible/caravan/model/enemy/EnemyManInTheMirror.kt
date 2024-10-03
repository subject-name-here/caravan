package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable


@Serializable
data object EnemyManInTheMirror : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck())

    override fun makeMove(game: Game) {
        EnemyBestest.makeMove(game)
    }
}