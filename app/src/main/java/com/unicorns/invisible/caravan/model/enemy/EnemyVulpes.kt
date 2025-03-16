package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
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

    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.FNV_FACTION, 1).apply {
        removeAll {
            it is CardNumber && it.rank.value <= 5 || it is CardFace && it.rank == RankFace.QUEEN
        }
    })

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}