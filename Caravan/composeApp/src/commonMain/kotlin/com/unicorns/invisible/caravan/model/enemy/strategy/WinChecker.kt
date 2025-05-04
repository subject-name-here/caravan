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

enum class GamePossibleResult {
    UNKNOWN,
    IMMINENT_ENEMY_VICTORY,
    POSSIBLE_PLAYER_VICTORY,
    ENEMY_VICTORY_IS_POSSIBLE,
    PLAYER_VICTORY_IS_POSSIBLE,
    GAME_ON,

}
fun checkOnResult(game: Game, caravanIndex: Int): GamePossibleResult {
    val otherCaravansIndices = game.enemyCaravans.indices.filter { it != caravanIndex }
    var score = 0
    fun checkIfWin(e0: Int, p0: Int) {
        if (e0 in (21..26) && (e0 > p0 || p0 > 26)) {
            score++
        }
    }
    otherCaravansIndices.forEach {
        checkIfWin(game.enemyCaravans[it].getValue(), game.playerCaravans[it].getValue())
    }
    if (score == 2) {
        return GamePossibleResult.IMMINENT_ENEMY_VICTORY
    }

    score = 0
    otherCaravansIndices.forEach {
        checkIfWin(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue())
    }
    if (score == 2) {
        return GamePossibleResult.POSSIBLE_PLAYER_VICTORY
    }

    score = 0
    otherCaravansIndices.forEach {
        checkIfWin(game.enemyCaravans[it].getValue(), game.playerCaravans[it].getValue())
    }
    if (score == 1) {
        otherCaravansIndices.forEach {
            checkIfWin(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue())
        }
        if (score == 2) {
            return when {
                game.enemyCaravans[caravanIndex].getValue() >= 11 && game.playerCaravans[caravanIndex].getValue() >= 11 -> {
                    GamePossibleResult.GAME_ON
                }
                game.enemyCaravans[caravanIndex].getValue() >= 11 -> {
                    GamePossibleResult.ENEMY_VICTORY_IS_POSSIBLE
                }
                game.playerCaravans[caravanIndex].getValue() >= 11 -> {
                    GamePossibleResult.PLAYER_VICTORY_IS_POSSIBLE
                }
                else -> GamePossibleResult.UNKNOWN
            }
        }
    }

    return GamePossibleResult.UNKNOWN
}
