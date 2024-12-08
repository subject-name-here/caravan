package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
data object EnemyStory6 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck())

    override fun makeMove(game: Game) {
        TODO("Not yet implemented")
    }
}