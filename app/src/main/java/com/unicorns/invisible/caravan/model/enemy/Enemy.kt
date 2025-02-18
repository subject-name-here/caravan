package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
sealed interface Enemy {
    fun getNameId(): Int

    fun createDeck(): CResources
    fun isEven(): Boolean

    fun getBank(): Int
    fun refreshBank()

    fun getBet(): Int?
    fun retractBet()
    fun addReward(reward: Int)

    fun startBattle()
    fun addVictory()

    fun isSpeedOverriding(): Boolean = false

    fun makeMove(game: Game)
}