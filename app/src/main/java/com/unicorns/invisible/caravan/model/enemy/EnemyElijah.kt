package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable


@Serializable
class EnemyElijah : EnemyPvEWithBank() {
    override val nameId
        get() = R.string.elijah
    override val isEven
        get() = false

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(CardBack.STANDARD, CardBack.VAULT_21, CardBack.SIERRA_MADRE).forEach { back ->
            listOf(0, 1).forEach { isAlt ->
                Suit.entries.forEach { suit ->
                    add(CardNumber(RankNumber.SIX, suit, back, 0))
                    add(CardNumber(RankNumber.TEN, suit, back, 0))
                    add(CardFaceSuited(RankFace.KING, suit, back, 0))
                    add(CardFaceSuited(RankFace.JACK, suit, back, 0))
                }
                add(CardJoker(CardJoker.Number.ONE, back, 0))
                add(CardJoker(CardJoker.Number.TWO, back, 0))
            }
        }
    })

    override var bank: Int = 0
    override val maxBank: Int
        get() = 60
    override val bet: Int
        get() = 10

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}