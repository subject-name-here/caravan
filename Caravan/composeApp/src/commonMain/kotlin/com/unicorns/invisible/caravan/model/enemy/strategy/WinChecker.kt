package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game


class Guild(var v1: Int, var v2: Int, var v3: Int)
class State(val player: Guild, val enemy: Guild)

fun gameToState(game: Game): State {
    return State(
        Guild(
            game.playerCaravans[0].getValue(),
            game.playerCaravans[1].getValue(),
            game.playerCaravans[2].getValue(),
        ),
        Guild(
            game.enemyCaravans[0].getValue(),
            game.enemyCaravans[1].getValue(),
            game.enemyCaravans[2].getValue(),
        )
    )
}

fun checkTheOutcome(state: State): Int {
    val o1 = check(state.player.v1, state.enemy.v1)
    val o2 = check(state.player.v1, state.enemy.v1)
    val o3 = check(state.player.v1, state.enemy.v1)
    if (o1 == 0 || o2 == 0 || o3 == 0) {
         return 0
    }
    return if (o1 + o2 + o3 > 0) {
        1
    } else {
        -1
    }
}

private fun check(vp: Int, ve: Int): Int {
    return if (vp in (21..26)) {
        if (vp == ve) {
            0
        } else if (ve > vp && ve in (21..26)) {
            -1
        } else {
            1
        }
    } else if (ve in (21..26)) {
        -1
    } else {
        0
    }
}