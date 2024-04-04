package com.unicorns.invisible.caravan.model.actions

import com.unicorns.invisible.caravan.model.Game


fun dropPlayerCaravan(game: Game, num: Int) {
    game.playerCaravans[num].dropCaravan()
}
fun dropEnemyCaravan(game: Game, num: Int) {
    game.enemyCaravans[num].dropCaravan()
}