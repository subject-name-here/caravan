package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
sealed interface Enemy {
    fun createDeck(): CResources
    fun refreshBank(amount: Int)
    fun getBank(): Int

    fun startBattle()
    fun addVictory()

    fun isSpeedOverriding(): Boolean = false

    fun makeMove(game: Game)
}