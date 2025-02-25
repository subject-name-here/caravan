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

    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 30 }
    override fun getBet(): Int { return min(bank, 10) }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override fun makeMove(game: Game) {}
}