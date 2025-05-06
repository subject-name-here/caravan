package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck

data object EnemyTower6 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck())

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        EnemyTower3.makeMove(game, speed)
    }
}