package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.crooker
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemyCrooker : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.crooker
    override val isEven: Boolean
        get() = false

    override fun createDeck(): CResources = CResources(CustomDeck())

    override val maxBets: Int
        get() = 6
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 25

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {

    }
}