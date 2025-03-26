package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemyElijah : EnemyPvEWithBank() {
    override val nameId
        get() = R.string.elijah
    override val isEven
        get() = false

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(CardBack.STANDARD, CardBack.VAULT_21_DAY, CardBack.SIERRA_MADRE_DIRTY).forEach { back ->
            listOf(0, 1).forEach { isAlt ->
                Suit.entries.forEach { suit ->
                    add(CardNumber(RankNumber.SIX, suit, back))
                    add(CardNumber(RankNumber.TEN, suit, back))
                    add(CardFaceSuited(RankFace.KING, suit, back))
                    add(CardFaceSuited(RankFace.JACK, suit, back))
                }
                add(CardJoker(CardJoker.Number.ONE, back))
                add(CardJoker(CardJoker.Number.TWO, back))
            }
        }
    })

    override var bank: Int = 0
    override val maxBank: Int
        get() = 60
    override val bet: Int
        get() = min(bank, 10)

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}