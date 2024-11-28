package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
sealed interface Enemy {
    fun createDeck(): CResources
    fun getBankNumber(): Int = -1

    fun makeMove(game: Game)

    fun onVictory() {}
}