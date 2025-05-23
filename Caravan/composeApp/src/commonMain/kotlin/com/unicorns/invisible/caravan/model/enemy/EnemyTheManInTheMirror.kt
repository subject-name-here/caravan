package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.man_in_the_mirror
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable


@Serializable
class EnemyTheManInTheMirror : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.man_in_the_mirror
    override val isEven
        get() = false
    override val level: Int
        get() = 3

    override fun createDeck() = CResources(CustomDeck())

    override val maxBets: Int
        get() = 8
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 11

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}