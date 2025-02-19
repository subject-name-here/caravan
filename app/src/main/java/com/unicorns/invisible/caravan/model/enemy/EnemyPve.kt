package com.unicorns.invisible.caravan.model.enemy

import kotlinx.serialization.Serializable


@Serializable
sealed interface EnemyPve : Enemy {
    fun getNameId(): Int

    fun isEven(): Boolean

    fun getBank(): Int
    fun refreshBank()

    fun getBet(): Int?
    fun retractBet()
    fun addReward(reward: Int)
}