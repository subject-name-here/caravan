package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.man_in_the_mirror
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemyTheManInTheMirror : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.man_in_the_mirror
    override val isEven
        get() = false

    override fun createDeck() = CResources(CustomDeck())

    override var bank: Int = 0
    override val maxBank: Int
        get() = 88
    override val bet: Int
        get() = min(bank, 11)

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}