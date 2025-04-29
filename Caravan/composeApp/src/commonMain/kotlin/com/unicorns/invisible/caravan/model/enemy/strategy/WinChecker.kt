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
    val o2 = check(state.player.v2, state.enemy.v2)
    val o3 = check(state.player.v3, state.enemy.v3)
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

fun checkIfPlayerVictoryIsClose(state: State): Boolean {
    val o1 = check(state.player.v1, state.enemy.v1)
    val o2 = check(state.player.v2, state.enemy.v2)
    val o3 = check(state.player.v3, state.enemy.v3)
    val isAllDifferent =
        o1 == 0 && state.player.v1 >= 11 && o2 * o3 != 0 ||
                o2 == 0 && state.player.v2 >= 11 && o1 * o3 != 0 ||
                o3 == 0 && state.player.v3 >= 11 && o1 * o2 != 0
    return o1 + o2 + o3 >= 2 || isAllDifferent
}

fun checkIfEnemyVictoryIsClose(state: State, index: Int): Boolean {
    val o1 = check(state.player.v1, state.enemy.v1)
    val o2 = check(state.player.v2, state.enemy.v2)
    val o3 = check(state.player.v3, state.enemy.v3)
    return when (index) {
        0 -> {
            o2 != 0 && o3 != 0
        }
        1 -> {
            o1 != 0 && o3 != 0
        }
        else -> {
            o1 != 0 && o2 != 0
        }
    }
}
