package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable


@Serializable
class EnemyMedium : Enemy() {
    override suspend fun makeMove(game: Game) {
        val deck = game.enemyDeck
        // TODO!!
    }
}