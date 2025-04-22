package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.salt
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemySalt : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.salt
    override val isEven
        get() = true
    override val level: Int
        get() = 2
    override val isAvailable: Boolean
        get() = true

    override fun createDeck(): CResources = CResources(CustomDeck())

    override val maxBets: Int
        get() = 4
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 10

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}