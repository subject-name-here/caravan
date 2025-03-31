package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.easy_pete
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemyEasyPete : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.easy_pete
    override val isEven: Boolean
        get() = true

    override fun createDeck(): CResources = CResources(CustomDeck())

    override var bank: Int = 0
    override val maxBank: Int
        get() = 21
    override val bet: Int
        get() = min(bank, 7)

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}