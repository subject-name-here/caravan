package com.unicorns.invisible.caravan.model.trading

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
data object EnclaveTrader : Trader {
    var isTowerBeaten = false
    override fun isOpen() = isTowerBeaten

    override fun openingCondition() = R.string.enclave_trader_cond

    override fun getName(): Int = R.string.enclave_trader_name

    override fun getCards(): List<Pair<Card, Int>> {
        val rand = Random(save.challengesHash)
        val deck1 = CustomDeck(CardBack.ENCLAVE, false)
        val cards1 = deck1.toList().shuffled(rand).take(9)
        return cards1.map { card -> card to save.getPriceOfCard(card) }
    }

    override fun getStyles(): List<Style> = emptyList()
}