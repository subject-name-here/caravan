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
data object EnemyGloria : EnemyPve {
    override fun getNameId() = R.string.gloria_van_graff
    override fun isEven() = false

    override fun createDeck(): CResources {
        return CResources(CustomDeck().apply {
            listOf(
                CardBack.STANDARD,
                CardBack.VAULT_21,
                CardBack.TOPS,
                CardBack.ULTRA_LUXE,
                CardBack.GOMORRAH,
                CardBack.LUCKY_38,
            ).forEach { back ->
                Suit.entries.forEach { suit ->
                    add(Card(Rank.JACK, suit, back, true))
                    add(Card(Rank.KING, suit, back, true))
                }

                Rank.entries.filter { !it.isFace() }.forEach { rank ->
                    add(Card(rank, Suit.SPADES, back, true))
                }
            }
        })
    }
    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 125 }
    override fun getBet(): Int { return min(bank, 25) }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override fun makeMove(game: Game) {}
}