package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
data object EnemyLuc10 : EnemyPve {
    override fun getNameId() = R.string.luc10
    override fun isEven() = true

    override fun createDeck() = CResources(CardBack.LUCKY_38, true)

    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 100 }
    override fun getBet(): Int { return min(bank, 50) }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override fun makeMove(game: Game) {}
}