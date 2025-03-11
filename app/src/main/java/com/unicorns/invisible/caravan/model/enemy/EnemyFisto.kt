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
class EnemyFisto : EnemyPve {
    override fun getNameId() = R.string.fisto
    override fun isEven() = false

    override fun createDeck(): CResources {
        return CResources(CustomDeck(CardBack.GOMORRAH, false).apply {
            listOf(
                CardBack.VAULT_21,
                CardBack.TOPS,
                CardBack.LUCKY_38,
                CardBack.ULTRA_LUXE,
                CardBack.STANDARD
            ).forEach { back ->
                add(Card(Rank.JOKER, Suit.HEARTS, back, false))
                add(Card(Rank.JOKER, Suit.CLUBS, back, false))
            }

            add(Card(
                Card.WildWastelandCardType.YES_MAN.rank,
                Card.WildWastelandCardType.YES_MAN.suit,
                CardBack.WILD_WASTELAND,
                false
            ))
            add(Card(
                Card.WildWastelandCardType.MUGGY.rank,
                Card.WildWastelandCardType.MUGGY.suit,
                CardBack.WILD_WASTELAND,
                false
            ))
        })
    }
    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 30 }
    override fun getBet(): Int { return min(bank, 15) }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override suspend fun makeMove(game: Game, delay: Long) {}
}