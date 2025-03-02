package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
class EnemySignificantOther : Enemy {
    var back: CardBack = CardBack.STANDARD
    var isAlt: Boolean = false

    override fun createDeck() = CResources(back, isAlt)
    override fun makeMove(game: Game) {}
}