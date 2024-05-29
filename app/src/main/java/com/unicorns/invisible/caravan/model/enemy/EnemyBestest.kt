package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
data object EnemyBestest : Enemy() {
    override fun createDeck(): CResources = CResources(CardBack.VAULT_21)
    override fun getRewardBack() = CardBack.VAULT_21

    override suspend fun makeMove(game: Game) {
        // TODO
    }
}