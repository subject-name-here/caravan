package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import kotlinx.serialization.Serializable


@Serializable
class EnemyVulpes : EnemyPvENoBank() {
    override val nameId
        get() = R.string.vulpes
    override val isEven
        get() = false

    override var wins: Int = 0
    override var winsBlitz: Int = 0

    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.LEGION).apply {
        removeAll {
            it is CardNumber && it.rank.value <= 5 || it is CardFace && it.rank == RankFace.QUEEN
        }
    })

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}