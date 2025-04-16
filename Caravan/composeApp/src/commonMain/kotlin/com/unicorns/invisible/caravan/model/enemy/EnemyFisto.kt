package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.fisto
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.WWType
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemyFisto : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.fisto
    override val isEven
        get() = false

    override fun createDeck(): CResources {
        return CResources(CustomDeck(CardBack.GOMORRAH).apply {
            listOf(
                CardBack.VAULT_21_DAY,
                CardBack.TOPS,
                CardBack.LUCKY_38,
                CardBack.ULTRA_LUXE,
                CardBack.STANDARD
            ).forEach { back ->
                add(CardJoker(CardJoker.Number.ONE, back))
                add(CardJoker(CardJoker.Number.TWO, back))
            }

            repeat(2) {
                add(CardWildWasteland(WWType.YES_MAN))
                add(CardWildWasteland(WWType.MUGGY))
            }
        })
    }

    override val maxBets: Int
        get() = 3
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 10

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}