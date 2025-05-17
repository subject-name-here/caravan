package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game


class Guild(var v1: Int, var v2: Int, var v3: Int) {
    operator fun get(index: Int): Int {
        return when (index) {
            0 -> v1
            1 -> v2
            2 -> v3
            else -> throw NoSuchElementException()
        }
    }
    operator fun set(index: Int, v: Int) {
        when (index) {
            0 -> { v1 = v }
            1 -> { v2 = v }
            2 -> { v3 = v }
            else -> throw NoSuchElementException()
        }
    }
}
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
    IMMINENT_PLAYER_VICTORY,
    GAME_ON;

    fun isPlayerMoveWins(): Boolean = this in listOf(GAME_ON, IMMINENT_PLAYER_VICTORY)
    fun isEnemyMoveWins(): Boolean = this in listOf(GAME_ON, IMMINENT_ENEMY_VICTORY)
}
fun checkOnResult(state: State, checkIndex: Int? = null): GamePossibleResult {
    val outcome = checkTheOutcome(state)
    if (outcome == -1) {
        return GamePossibleResult.IMMINENT_ENEMY_VICTORY
    } else if (outcome == 1) {
        return GamePossibleResult.IMMINENT_PLAYER_VICTORY
    }

    fun checkCaravans(e0: Int, p0: Int): Boolean {
        return (
            e0 in (21..26) && (e0 > p0 || p0 > 26) ||
            p0 in (21..26) && (p0 > e0 || e0 > 26)
        )
    }
    var score = 0
    (0..2).forEach {
        if (checkCaravans(state.enemy[it], state.player[it])) {
            score++
        }
    }
    if (score < 2) {
        return GamePossibleResult.UNKNOWN
    }

    val index = (0..2).find { !checkCaravans(state.enemy[it], state.player[it]) }!!
    if (checkIndex != null && index != checkIndex) {
        return GamePossibleResult.UNKNOWN
    }
    if (state.enemy[index] < 11 && state.player[index] < 11) {
        return GamePossibleResult.UNKNOWN
    }
    val contCaravanResult = when {
        state.enemy[index] >= 11 && state.player[index] >= 11 -> {
            0
        }
        state.enemy[index] >= 11 -> {
            -1
        }
        state.player[index] >= 11 -> {
            1
        }
        else -> throw Exception()
    }

    val otherIndices = (0..2).filter { it != index }
    val otherResults = otherIndices.map { check(state.player[it], state.enemy[it]) }
    return when {
        otherResults[0] == 1 && otherResults[1] == 1 -> GamePossibleResult.IMMINENT_PLAYER_VICTORY
        otherResults[0] == -1 && otherResults[1] == -1 -> GamePossibleResult.IMMINENT_ENEMY_VICTORY
        contCaravanResult == 0 -> GamePossibleResult.GAME_ON
        contCaravanResult == 1 -> GamePossibleResult.IMMINENT_PLAYER_VICTORY
        contCaravanResult == -1 -> GamePossibleResult.IMMINENT_ENEMY_VICTORY
        else -> throw Exception()
    }
}

fun canJokerCrashTheParty(state: State): Boolean {
    fun checkCaravans(e0: Int, p0: Int): Boolean {
        return e0 >= 21 || p0 >= 21
    }
    var score = 0
    (0..2).forEach {
        if (checkCaravans(state.enemy[it], state.player[it])) {
            score++
        }
    }
    if (score < 2) {
        return false
    }
    if (score == 3) {
        return (0..2).count { state.player[it] >= 21 } >= 2
    }

    val index = (0..2).find { !checkCaravans(state.enemy[it], state.player[it]) }!!
    if (state.enemy[index] < 21 && state.player[index] < 21) {
        return false
    }
    val contCaravanResult = when {
        state.enemy[index] >= 21 && state.player[index] >= 21 -> {
            0
        }
        state.enemy[index] >= 21 -> {
            -1
        }
        state.player[index] >= 21 -> {
            1
        }
        else -> throw Exception()
    }

    val otherIndices = (0..2).filter { it != index }
    val otherResults = otherIndices.map { state.player[it] >= 21 }
    return when {
        otherResults[0] && otherResults[1] -> true
        (otherResults[0] || otherResults[1]) && contCaravanResult != -1 -> true
        else -> false
    }
}
