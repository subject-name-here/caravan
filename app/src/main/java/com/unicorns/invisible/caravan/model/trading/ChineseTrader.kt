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
data object ChineseTrader : Trader {
    var is1921Entered = false
    override fun isOpen() = is1921Entered &&
            save.storyChaptersProgress >= 9 && save.altStoryChaptersProgress >= 1

    override fun openingCondition() = R.string.chinese_trader_condition

    override fun getName(): Int = R.string.chinese_trader_name

    override fun getCards(): List<Pair<Card, Int>> {
        val rand = Random(save.challengesHash)
        val deck1 = CustomDeck(CardBack.CHINESE, false)
        val cards1 = deck1.toList().shuffled(rand).take(6)
        return cards1.map { card -> card to save.getPriceOfCard(card) }
    }

    override fun getStyles(): List<Style> = emptyList()
}