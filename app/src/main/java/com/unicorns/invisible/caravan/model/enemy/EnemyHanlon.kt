package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
class EnemyHanlon : EnemyPve {
    override fun getNameId() = R.string.pve_enemy_chief_hanlon
    override fun isEven() = true

    override fun createDeck() = CResources(CardBack.NCR, false)

    override fun getBank() = 0
    override fun refreshBank() {}
    override fun getBet() = null
    override fun retractBet() {}
    override fun addReward(reward: Int) {}

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}