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
data object EnemyNash : EnemyPve {
    override fun getNameId() = R.string.johnson_nash
    override fun isEven() = false

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(
            CardBack.STANDARD,
            CardBack.GOMORRAH,
            CardBack.ULTRA_LUXE,
            CardBack.TOPS
        ).forEach { back ->
            Suit.entries.forEach { suit ->
                add(Card(Rank.SIX, suit, back, false))
                add(Card(Rank.JACK, suit, back, false))
                add(Card(Rank.QUEEN, suit, back, false))
                add(Card(Rank.KING, suit, back, false))
            }
        }
        Suit.entries.forEach { suit ->
            add(Card(Rank.SIX, suit, CardBack.LUCKY_38, false))
            add(Card(Rank.KING, suit, CardBack.LUCKY_38, false))
            add(Card(Rank.QUEEN, suit, CardBack.LUCKY_38, false))
            add(Card(Rank.KING, suit, CardBack.VAULT_21, false))
        }
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