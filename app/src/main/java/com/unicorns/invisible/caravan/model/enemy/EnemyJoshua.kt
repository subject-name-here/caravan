package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
data object EnemyJoshua : EnemyPve {
    override fun getNameId() = R.string.joshua_graham
    override fun isEven() = true

    override fun createDeck(): CResources {
        return CResources(CardBack.STANDARD, false)
    }
    override fun getBank(): Int { return 0 }
    override fun refreshBank() {}
    override fun getBet(): Int? { return 0 }
    override fun retractBet() {}
    override fun addReward(reward: Int) {}
    override fun makeMove(game: Game) {}
}