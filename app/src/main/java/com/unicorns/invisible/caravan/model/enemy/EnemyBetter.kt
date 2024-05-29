package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable


@Serializable
data object EnemyBetter : Enemy() {
    override fun createDeck(): CResources = CResources(CardBack.LUCKY_38, false)
    override fun getRewardBack() = CardBack.LUCKY_38

    override suspend fun makeMove(game: Game) {
        // TODO
    }
}