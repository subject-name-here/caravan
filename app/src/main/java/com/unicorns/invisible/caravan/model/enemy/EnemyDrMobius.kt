package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlin.math.min
import kotlin.random.Random


@Serializable
class EnemyDrMobius : EnemyPvEWithBank() {
    override val nameId
        get() = R.string.dr_mobius
    override val isEven: Boolean
        get() = false

    override fun createDeck() = CResources(CustomDeck().apply {
        repeat(3) {
            add(generateCardFace())
        }
        repeat(5) {
            add(generateCardNumber())
        }
    })

    override var bank: Int = 0
    override val maxBank: Int
        get() = 80
    override val bet: Int
        get() = if (bank == 0) 0 else min(bank, 8)

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        makeMoveInner(game)
        if (game.enemyCResources.hand.size < 5) {
            game.enemyCResources.addOnTop(if (Random.nextInt(14) >= 10) generateCardFace() else generateCardNumber())
        }
    }

    private fun makeMoveInner(game: Game) {

    }

    fun generateCardFace(): CardFace {
        val rank = RankFace.entries.random()
        val deckIndex = CardBack.STANDARD.nameIdWithBackFileName.indices.random()
        return if (rank == RankFace.JOKER) {
            CardJoker(CardJoker.Number.entries.random(), CardBack.STANDARD, deckIndex)
        } else {
            CardFaceSuited(rank, Suit.entries.random(), CardBack.STANDARD, deckIndex)
        }
    }

    fun generateCardNumber(): CardNumber {
        val rank = RankNumber.entries.random()
        val deckIndex = CardBack.STANDARD.nameIdWithBackFileName.indices.random()
        return CardNumber(rank, Suit.entries.random(), CardBack.STANDARD, deckIndex)
    }
}