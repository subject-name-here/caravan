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


@Serializable
data object EnemySnuffles : EnemyPve {
    override fun getNameId() = R.string.snuffles
    override fun isEven() = false

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(CardBack.LUCKY_38, CardBack.GOMORRAH, CardBack.ULTRA_LUXE, CardBack.TOPS).forEach { back ->
            Rank.entries.forEach { rank ->
                if (rank == Rank.JOKER) {
                    add(Card(Rank.JOKER, Suit.HEARTS, back, false))
                    add(Card(Rank.JOKER, Suit.CLUBS, back, false))
                } else {
                    listOf(Suit.CLUBS, Suit.SPADES).forEach { suit ->
                        add(Card(rank, suit, back, false))
                    }
                }
            }
        }
        Suit.entries.forEach { suit ->
            add(Card(Rank.TEN, suit, CardBack.NUCLEAR, false))
        }
        add(Card(Rank.KING, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.KING, Suit.CLUBS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.KING, Suit.DIAMONDS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.KING, Suit.SPADES, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.JACK, Suit.SPADES, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.QUEEN, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
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