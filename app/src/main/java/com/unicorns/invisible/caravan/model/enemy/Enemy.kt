package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable


@Serializable
sealed class Enemy {
    abstract suspend fun makeMove(game: Game)
}