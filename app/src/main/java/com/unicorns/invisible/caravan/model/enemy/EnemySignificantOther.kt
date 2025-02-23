package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
data object EnemySignificantOther : EnemyPve {
    var back: CardBack = CardBack.STANDARD
    var isAlt: Boolean = false

    override fun getNameId() = R.string.enemy_significant_other
    override fun isEven() = true

    override fun createDeck(): CResources {
        return CResources(back, isAlt)
    }
    override fun getBank(): Int { return 0 }
    override fun refreshBank() {}
    override fun getBet(): Int? { return 0 }
    override fun retractBet() {}
    override fun addReward(reward: Int) {}
    override fun makeMove(game: Game) {}
}