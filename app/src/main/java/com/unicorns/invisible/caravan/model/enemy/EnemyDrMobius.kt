package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
data object EnemyDrMobius : EnemyPve {
    override fun getNameId() = R.string.dr_mobius
    override fun isEven() = false

    override fun createDeck() = CResources(CustomDeck().apply {
        var cards = arrayOf<Card>()
        while (cards.count { card -> !card.isModifier() } < 3) {
            cards = Array<Card>(8) { generateCard() }
        }
        cards.forEach { add(it) }
    })

    override fun getBank(): Int {
        return 0
    }

    override fun refreshBank() {

    }

    override fun getBet(): Int? {
        return 0
    }

    override fun retractBet() {

    }

    override fun addReward(reward: Int) {

    }

    override fun makeMove(game: Game) {
        makeMoveInner(game)
        if (game.enemyCResources.hand.size < 5) {
            game.enemyCResources.addOnTop(generateCard())
        }
    }

    private fun makeMoveInner(game: Game) {

    }

    fun generateCard(): Card {
        val rank = Rank.entries.random()
        return if (rank == Rank.JOKER) {
            Card(rank, listOf(Suit.HEARTS, Suit.CLUBS).random(), CardBack.LUCKY_38, false)
        } else {
            Card(rank, Suit.entries.random(), CardBack.STANDARD, Random.nextBoolean())
        }
    }
}