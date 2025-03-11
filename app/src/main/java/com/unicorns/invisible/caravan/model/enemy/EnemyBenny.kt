package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemyBenny : EnemyPve {
    override fun getNameId() = R.string.benny
    override fun createDeck() = CResources(CardBack.TOPS, true)
    override fun isEven() = true

    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 90 }
    override fun getBet(): Int { return min(bank, 45) }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override suspend fun makeMove(game: Game, delay: Long) {

    }
}