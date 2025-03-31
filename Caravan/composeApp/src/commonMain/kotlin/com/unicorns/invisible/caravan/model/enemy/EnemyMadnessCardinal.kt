package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.madness_cardinal
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardAtomic
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.WWType
import kotlinx.serialization.Serializable


@Serializable
class EnemyMadnessCardinal : EnemyPvENoBank() {
    override val nameId
        get() = Res.string.madness_cardinal
    override val isEven
        get() = false

    override var wins: Int = 0
    override var winsBlitz: Int = 0

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        repeat(4) {
            add(CardAtomic())
        }

        WWType.entries.forEach {
            add(CardWildWasteland(it))
        }
    })


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}