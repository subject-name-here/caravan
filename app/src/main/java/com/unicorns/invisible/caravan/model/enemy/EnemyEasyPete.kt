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
import kotlin.math.min


@Serializable
class EnemyEasyPete : EnemyPve {
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

    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 21 }
    override fun getBet(): Int { return min(bank, 7) }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}