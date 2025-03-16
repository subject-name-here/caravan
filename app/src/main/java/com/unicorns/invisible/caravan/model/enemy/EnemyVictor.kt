package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemyVictor : EnemyPve {
    override fun getNameId() = R.string.pve_enemy_victor
    override fun isEven() = true

    override fun createDeck() = CResources(CardBack.LUCKY_38, 0)

    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 50 }
    override fun getBet(): Int { return min(bank, 25) }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}