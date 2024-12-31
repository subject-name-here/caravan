package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable


@Serializable
data object EnemyStory8 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.STANDARD, true))

    override fun makeMove(game: Game) {
        EnemyHanlon.makeMove(game)
    }
}