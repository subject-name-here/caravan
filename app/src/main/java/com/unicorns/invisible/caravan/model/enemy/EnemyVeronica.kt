package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemyVeronica : EnemyPve {
    override fun getNameId() = R.string.pve_enemy_veronica
    override fun isEven() = true

    override fun createDeck() = CResources(CardBack.ULTRA_LUXE, false)

    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 30 }
    override fun getBet(): Int { return min(bank, 15) }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}