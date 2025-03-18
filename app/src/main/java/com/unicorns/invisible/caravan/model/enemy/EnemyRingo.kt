package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemyRingo : EnemyPvEWithBank() {
    override val nameId
        get() = R.string.ringo
    override val isEven
        get() = true

    override fun createDeck(): CResources {
        return CResources(CustomDeck(CardBack.TOPS, 0))
    }

    override var bank: Int = 0
    override val maxBank: Int
        get() = 45
    override val bet: Int
        get() = 15

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}