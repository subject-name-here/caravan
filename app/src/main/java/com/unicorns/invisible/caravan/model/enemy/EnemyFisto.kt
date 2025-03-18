package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.model.primitives.WWType
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemyFisto : EnemyPvEWithBank() {
    override val nameId
        get() = R.string.fisto
    override val isEven
        get() = false

    override fun createDeck(): CResources {
        return CResources(CustomDeck(CardBack.GOMORRAH, 0).apply {
            listOf(
                CardBack.VAULT_21,
                CardBack.TOPS,
                CardBack.LUCKY_38,
                CardBack.ULTRA_LUXE,
                CardBack.STANDARD
            ).forEach { back ->
                add(CardJoker(CardJoker.Number.ONE, back, 0))
                add(CardJoker(CardJoker.Number.TWO, back, 0))
            }

            repeat(2) {
                add(CardWildWasteland(WWType.YES_MAN))
                add(CardWildWasteland(WWType.MUGGY))
            }
        })
    }

    override var bank: Int = 0
    override val maxBank: Int
        get() = 30
    override val bet: Int
        get() = 10

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}