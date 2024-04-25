package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Deck
import kotlinx.serialization.Serializable


@Serializable
sealed class Enemy {
    abstract suspend fun makeMove(game: Game)
    abstract fun createDeck(): Deck
}