package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.pve_enemy_chief_hanlon
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable


@Serializable
class EnemyHanlon : EnemyPvENoBank() {
    override val nameId
        get() = Res.string.pve_enemy_chief_hanlon
    override val isEven
        get() = true

    override var wins: Int = 0
    override var winsBlitz: Int = 0

    override fun createDeck() = CResources(CardBack.NCR)

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}