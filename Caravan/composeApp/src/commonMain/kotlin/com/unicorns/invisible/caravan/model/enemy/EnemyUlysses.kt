package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.pve_enemy_ulysses
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
class EnemyUlysses : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.pve_enemy_ulysses
    override val isEven
        get() = true
    override val level: Int
        get() = 6
    override val isAvailable: Boolean
        get() = true

    override fun createDeck() = CResources(CardBack.VAULT_21_NIGHT)

    override val maxBets: Int
        get() = 4
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 15

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        EnemyFrank.makeMove(game, speed)
    }
}