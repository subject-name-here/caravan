package com.unicorns.invisible.caravan.utils

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.Save
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun Save.updateSoldCards(): Boolean {
    val currentDate = Date().time
    val prevDate = previousDate
    previousDate = currentDate

    val simpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
    if (simpleDateFormat.format(Date(currentDate))
            .equals(simpleDateFormat.format(Date(prevDate)))
    ) {
        return false
    }
    var flag = false
    CardBack.playableBacks.forEach { back ->
        val cardBought = (3..15).random() to false
        val cardBoughtAlt = (2..11).random() to true
        listOf(cardBought, cardBoughtAlt).forEach { (amount, isAlt) ->
            if (back to isAlt in soldCards) {
                val oldPrice = getCardPrice(Card(Rank.ACE, Suit.HEARTS, back, isAlt))
                soldCards[back to isAlt] = (soldCards[back to isAlt]!! - amount).coerceAtLeast(0)
                val newPrice = getCardPrice(Card(Rank.ACE, Suit.HEARTS, back, isAlt))
                if (oldPrice != newPrice) {
                    flag = true
                }
            }
        }
    }
    return flag
}

fun Save.getCardPrice(card: Card): Int {
    val soldAlready = soldCards[card.back to card.isAlt] ?: 0
    return if (!card.isAlt) {
        when (card.back) {
            CardBack.STANDARD -> 0
            CardBack.TOPS, CardBack.ULTRA_LUXE, CardBack.GOMORRAH -> when (soldAlready) {
                in (0..9) -> 10
                in (10..19) -> 8
                in (20..29) -> 6
                in (30..39) -> 4
                in (40..49) -> 2
                else -> 1
            }

            CardBack.LUCKY_38 -> when (soldAlready) {
                in (0..9) -> 10
                in (10..19) -> 9
                in (20..29) -> 7
                in (30..39) -> 5
                in (40..49) -> 3
                else -> 1
            }

            CardBack.VAULT_21 -> when (soldAlready) {
                in (0..9) -> 10
                in (10..19) -> 9
                in (20..29) -> 8
                in (30..39) -> 6
                in (40..49) -> 4
                in (50..59) -> 3
                else -> 1
            }

            CardBack.DECK_13 -> 0
            CardBack.UNPLAYABLE -> 0
            CardBack.WILD_WASTELAND -> 0
        }
    } else {
        when (card.back) {
            CardBack.STANDARD -> when (soldAlready) {
                in (0..9) -> 30
                in (10..19) -> 25
                in (20..29) -> 20
                in (30..39) -> 15
                in (40..49) -> 10
                in (50..59) -> 5
                in (60..69) -> 3
                else -> 1
            }

            CardBack.ULTRA_LUXE, CardBack.GOMORRAH -> when (soldAlready) {
                in (0..9) -> 30
                in (10..19) -> 20
                in (20..29) -> 15
                in (30..39) -> 10
                in (40..49) -> 5
                in (50..59) -> 3
                else -> 1
            }

            CardBack.TOPS -> when (soldAlready) {
                in (0..8) -> 30
                in (9..16) -> 25
                in (17..24) -> 20
                in (25..32) -> 15
                in (33..40) -> 10
                in (41..48) -> 8
                in (49..56) -> 5
                in (57..64) -> 3
                else -> 1
            }

            CardBack.LUCKY_38, CardBack.VAULT_21, CardBack.DECK_13 -> (30 - soldAlready / 5).coerceAtLeast(1)

            CardBack.UNPLAYABLE -> 0
            CardBack.WILD_WASTELAND -> 0
        }
    }
}