package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.elijah
import com.unicorns.invisible.caravan.AnimationSpeed
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
        get() = Res.string.elijah
    override val isEven
        get() = false
    override val level: Int
        get() = 3

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

    override val maxBets: Int
        get() = 6
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 10

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}