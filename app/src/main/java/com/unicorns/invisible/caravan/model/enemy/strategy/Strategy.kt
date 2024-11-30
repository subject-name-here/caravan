package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game


// TODO: extract much more
// TODO: revisit enemies AI!!!
interface Strategy {
    fun move(game: Game): Boolean
}