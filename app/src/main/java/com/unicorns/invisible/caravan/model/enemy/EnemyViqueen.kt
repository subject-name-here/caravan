package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
class EnemyViqueen : EnemyPvENoBank() {
    override val nameId
        get() = R.string.viqueen
    override val isEven
        get() = true

    override var wins: Int = 0
    override var winsBlitz: Int = 0

    override fun createDeck(): CResources {
        return CResources(CardBack.VIKING, 0)
    }
    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}