package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable


@Serializable
data object EnemyCrooker : EnemyPve {
    override fun getNameId() = R.string.crooker
    override fun isEven() = false

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(CardBack.NCR, CardBack.LUCKY_38, CardBack.VAULT_21).forEach { back ->
            addAll(CustomDeck(back, back.hasAlt()))
        }
    })

    override fun getBank(): Int {
        return 0
    }

    override fun refreshBank() {

    }

    override fun getBet(): Int? {
        return 0
    }

    override fun retractBet() {

    }

    override fun addReward(reward: Int) {

    }

    override fun makeMove(game: Game) {}
}