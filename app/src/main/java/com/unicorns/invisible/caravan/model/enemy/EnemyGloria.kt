package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemyGloria : EnemyPve {
    override fun getNameId() = R.string.gloria_van_graff
    override fun isEven() = false

    override fun createDeck(): CResources = CResources(CustomDeck())

    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 125 }
    override fun getBet(): Int { return min(bank, 25) }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}