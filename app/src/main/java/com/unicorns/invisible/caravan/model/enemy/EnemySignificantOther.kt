package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
class EnemySignificantOther : Enemy {
    var back: CardBack = CardBack.STANDARD
    var backNumber: Int = 0

    override fun createDeck() = CResources(back, backNumber)
    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}