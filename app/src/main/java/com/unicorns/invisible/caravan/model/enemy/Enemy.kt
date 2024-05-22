package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable


@Serializable
sealed class Enemy {
    abstract suspend fun makeMove(game: Game)
    abstract fun createDeck(): CResources
    open fun getRewardDeck(): CustomDeck? = null
}