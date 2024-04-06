package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable


@Serializable
data object EnemyMedium : Enemy() {
    override suspend fun makeMove(game: Game) {
        val deck = game.enemyDeck
        // TODO!!
    }
}