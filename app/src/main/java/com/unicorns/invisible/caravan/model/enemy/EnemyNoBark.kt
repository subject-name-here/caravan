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
data object EnemyNoBark : EnemyPve {
    override fun getNameId() = R.string.no_bark

    override fun isEven() = false

    override fun createDeck() = CResources(CustomDeck(CardBack.STANDARD, false).apply {
        removeAll(toList().filter { it.rank.value < 5 })
        Suit.entries.forEach { suit ->
            add(Card(Rank.JACK, suit, CardBack.GOMORRAH, true))
            add(Card(Rank.JACK, suit, CardBack.GOMORRAH, false))
        }
        add(Card(Rank.JOKER, Suit.HEARTS, CardBack.GOMORRAH, false))
        add(Card(Rank.JOKER, Suit.CLUBS, CardBack.GOMORRAH, false))
        add(Card(Rank.JOKER, Suit.HEARTS, CardBack.GOMORRAH, true))
        add(Card(Rank.JOKER, Suit.CLUBS, CardBack.GOMORRAH, true))
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