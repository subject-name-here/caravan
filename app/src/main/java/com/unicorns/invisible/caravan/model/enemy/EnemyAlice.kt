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
import kotlin.math.min


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

    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 100 }
    override fun getBet(): Int { return min(bank, 25) }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override fun makeMove(game: Game) {


    }
}