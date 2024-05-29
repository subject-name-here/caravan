package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
sealed class Enemy {
    abstract suspend fun makeMove(game: Game)
    abstract fun createDeck(): CResources
    open fun getRewardBack(): CardBack? = null
    open fun isAlt() = false
}