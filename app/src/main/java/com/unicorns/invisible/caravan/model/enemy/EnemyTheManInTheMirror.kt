package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
data object EnemyTheManInTheMirror : EnemyPve {
    override fun getNameId() = R.string.man_in_the_mirror
    override fun isEven() = false

    override fun createDeck() = CResources(CustomDeck())

    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 88 }
    override fun getBet(): Int { return min(bank, 11) }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override fun makeMove(game: Game) {}
}