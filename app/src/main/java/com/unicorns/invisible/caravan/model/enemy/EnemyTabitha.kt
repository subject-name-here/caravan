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
import kotlin.math.min


@Serializable
class EnemyTabitha : EnemyPve {
    override fun getNameId() = R.string.tabitha

    override fun isEven() = false

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(
            CardBack.STANDARD, CardBack.TOPS, CardBack.GOMORRAH,
            CardBack.ULTRA_LUXE, CardBack.LUCKY_38, CardBack.VAULT_21
        ).forEach { back ->
            Rank.entries.forEach { rank ->
                if (rank == Rank.JOKER) {
                    add(Card(Rank.JOKER, Suit.HEARTS, back, false))
                    add(Card(Rank.JOKER, Suit.CLUBS, back, false))
                } else if (!rank.isFace() && rank != Rank.ACE) {
                    add(Card(rank, Suit.DIAMONDS, back, false))
                } else if (rank == Rank.JACK) {
                    add(Card(rank, Suit.SPADES, back, false))
                }
            }
        }
    })

    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 40 }
    override fun getBet(): Int { return min(bank, 20) }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override suspend fun makeMove(game: Game, delay: Long) {}
}