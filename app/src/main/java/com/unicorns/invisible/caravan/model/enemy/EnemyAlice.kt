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
data object EnemyAlice : EnemyPve {
    override fun getNameId() = R.string.alice_mclafferty
    override fun isEven() = false

    override fun createDeck(): CResources {
        return CResources(CustomDeck().apply {
            listOf(CardBack.NCR, CardBack.VAULT_21, CardBack.STANDARD).forEach { back ->
                listOf(Suit.CLUBS, Suit.SPADES, Suit.DIAMONDS).forEach { suit ->
                    Rank.entries.forEach { rank ->
                        if (rank != Rank.JOKER) {
                            add(Card(rank, suit, back, false))
                            if (back.hasAlt()) {
                                add(Card(rank, suit, back, true))
                            }
                        }
                    }
                }
            }
        })
    }
    override fun getBank(): Int { return 0 }
    override fun refreshBank() {}
    override fun getBet(): Int? { return 0 }
    override fun retractBet() {}
    override fun addReward(reward: Int) {}
    override fun makeMove(game: Game) {}
}