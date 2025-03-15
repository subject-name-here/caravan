package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources


data object EnemyTower3A : Enemy {
    override fun createDeck(): CResources {
        return CResources(CardBack.LUCKY_38, true)
    }

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {

    }
}