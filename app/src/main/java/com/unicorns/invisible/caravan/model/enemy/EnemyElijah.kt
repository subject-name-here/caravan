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
data object EnemyElijah : EnemyPve {
    override fun getNameId() = R.string.elijah
    override fun isEven() = false

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(CardBack.STANDARD, CardBack.VAULT_21, CardBack.SIERRA_MADRE).forEach { back ->
            listOf(true, false).forEach { isAlt ->
                Suit.entries.forEach { suit ->
                    add(Card(Rank.SIX, suit, back, isAlt))
                    add(Card(Rank.TEN, suit, back, isAlt))
                    add(Card(Rank.KING, suit, back, isAlt))
                    add(Card(Rank.JACK, suit, back, isAlt))
                }
                add(Card(Rank.JOKER, Suit.HEARTS, back, isAlt))
                add(Card(Rank.JOKER, Suit.CLUBS, back, isAlt))
            }
        }
    })

    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 60 }
    override fun getBet(): Int { return min(bank, 20) }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override fun makeMove(game: Game) {}
}