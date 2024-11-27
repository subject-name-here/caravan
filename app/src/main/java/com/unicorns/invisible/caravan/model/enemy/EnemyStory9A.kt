package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck


data object EnemyStory9A : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck())

    override fun makeMove(game: Game) {
        TODO("Not yet implemented")
    }
}