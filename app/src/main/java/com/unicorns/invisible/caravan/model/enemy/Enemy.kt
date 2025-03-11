package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources


sealed interface Enemy {
    fun createDeck(): CResources

    fun isSpeedOverriding(): Boolean = false

    suspend fun makeMove(game: Game, delay: Long)

    fun onVictory(isBlitz: Boolean) {}
}