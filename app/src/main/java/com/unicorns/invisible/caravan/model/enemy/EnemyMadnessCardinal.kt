package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardAtomic
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.WWType
import kotlinx.serialization.Serializable


@Serializable
class EnemyMadnessCardinal : EnemyPve {
    override fun getNameId() = R.string.madness_cardinal
    override fun isEven() = false

    override fun getBank() = 0
    override fun refreshBank() {}
    override fun getBet() = null
    override fun retractBet() {}
    override fun addReward(reward: Int) {}

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        repeat(4) {
            add(CardAtomic())
        }

        WWType.entries.forEach {
            add(CardWildWasteland(it))
        }
    })


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}