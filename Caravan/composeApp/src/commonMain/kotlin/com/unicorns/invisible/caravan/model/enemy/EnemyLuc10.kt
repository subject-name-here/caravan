package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.luc10
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
class EnemyLuc10 : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.luc10
    override val isEven
        get() = true
    override val level: Int
        get() = 4

    override fun createDeck() = CResources(CardBack.LUCKY_38_SPECIAL)

    override val maxBets: Int
        get() = 3
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 50

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}