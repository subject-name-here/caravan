package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable


@Serializable
class EnemySignificantOther : Enemy {
    override fun createDeck() = CResources(save.selectedDeck)
    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}