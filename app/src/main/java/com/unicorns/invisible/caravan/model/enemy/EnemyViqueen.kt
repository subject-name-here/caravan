package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
class EnemyViqueen : EnemyPve {
    override fun getNameId() = R.string.viqueen
    override fun isEven() = true

    override fun createDeck(): CResources {
        return CResources(CardBack.VIKING, 0)
    }
    override fun getBank(): Int { return 0 }
    override fun refreshBank() {}
    override fun getBet(): Int? { return null }
    override fun retractBet() {}
    override fun addReward(reward: Int) {}
    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}