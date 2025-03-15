package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
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
class EnemyMadnessCardinal : EnemyPve {
    override fun getNameId() = R.string.madness_cardinal
    override fun isEven() = false

    override fun getBank() = 0
    override fun refreshBank() {}
    override fun getBet() = null
    override fun retractBet() {}
    override fun addReward(reward: Int) {}

    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.MADNESS, false).apply {
        listOf(CardBack.STANDARD, CardBack.SIERRA_MADRE, CardBack.VIKING,).forEach { back ->
            addAll(CustomDeck(back, false))
            if (back.hasAlt()) {
                addAll(CustomDeck(back, true))
            }

            add(Card(Rank.ACE, Suit.HEARTS, CardBack.NUCLEAR, false))
            add(Card(Rank.ACE, Suit.CLUBS, CardBack.NUCLEAR, false))
            add(Card(Rank.ACE, Suit.DIAMONDS, CardBack.NUCLEAR, false))
            add(Card(Rank.ACE, Suit.SPADES, CardBack.NUCLEAR, false))

            add(Card(Rank.KING, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
            add(Card(Rank.KING, Suit.CLUBS, CardBack.WILD_WASTELAND, false))
            add(Card(Rank.KING, Suit.DIAMONDS, CardBack.WILD_WASTELAND, false))
            add(Card(Rank.KING, Suit.SPADES, CardBack.WILD_WASTELAND, false))
            add(Card(Rank.JACK, Suit.SPADES, CardBack.WILD_WASTELAND, false))
            add(Card(Rank.QUEEN, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
        }
    })


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}