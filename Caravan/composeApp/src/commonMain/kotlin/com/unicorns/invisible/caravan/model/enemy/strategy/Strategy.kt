package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game


interface Strategy {
    suspend fun move(game: Game, speed: AnimationSpeed): Boolean
}