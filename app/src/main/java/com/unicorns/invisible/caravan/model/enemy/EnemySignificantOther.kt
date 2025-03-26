package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
class EnemySignificantOther : Enemy {
    var back: CardBack = CardBack.STANDARD

    override fun createDeck() = CResources(back)
    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}