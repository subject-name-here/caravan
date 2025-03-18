package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemyLuc10 : EnemyPvEWithBank() {
    override val nameId
        get() = R.string.luc10
    override val isEven
        get() = true

    override fun createDeck() = CResources(CardBack.LUCKY_38, 1)

    override var bank: Int = 0
    override val maxBank: Int
        get() = 100
    override val bet: Int
        get() = 50

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}