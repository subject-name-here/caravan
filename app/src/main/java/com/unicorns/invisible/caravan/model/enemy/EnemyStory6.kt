package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable


data object EnemyStory6 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.STANDARD, false))

    override fun makeMove(game: Game) {
        EnemyVeronica.makeMove(game)
    }
}