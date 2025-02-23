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
import kotlin.math.abs


@Serializable
data object EnemyEasyPete : EnemyPve {
    override fun getNameId() = R.string.easy_pete
    override fun isEven() = true

    override fun createDeck() = CResources(CustomDeck().apply {
        Rank.entries.forEach { rank ->
            if (rank != Rank.JOKER) {
                Suit.entries.forEach { suit ->
                    add(Card(rank, suit, CardBack.STANDARD, false))
                }
            }
        }

        add(Card(Rank.ACE, Suit.HEARTS, CardBack.NUCLEAR, false))
        add(Card(Rank.ACE, Suit.CLUBS, CardBack.NUCLEAR, false))
        add(Card(
            Card.WildWastelandCardType.DIFFICULT_PETE.rank,
            Card.WildWastelandCardType.DIFFICULT_PETE.suit,
            CardBack.WILD_WASTELAND,
            false
        ))
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

    override fun makeMove(game: Game) {}
}