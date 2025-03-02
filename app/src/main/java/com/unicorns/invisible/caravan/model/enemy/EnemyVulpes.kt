package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable


@Serializable
class EnemyVulpes : EnemyPve {
    override fun getNameId() = R.string.vulpes
    override fun isEven() = false

    override fun getBank(): Int { return 0 }
    override fun refreshBank() {}
    override fun getBet(): Int? { return null }
    override fun retractBet() {}
    override fun addReward(reward: Int) {}

    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.LEGION, false).apply {
        removeAll(toList().filter { it.rank.value < 5 && it.rank.value != Rank.QUEEN.value })
    })

    override fun makeMove(game: Game) {}
}