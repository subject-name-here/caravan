package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game


interface Strategy {
    fun move(game: Game)
}