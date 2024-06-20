package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalSerializationApi::class)
@Serializable
class Save {
    @EncodeDefault
    var selectedDeck: Pair<CardBack, Boolean> = CardBack.STANDARD to false

    @EncodeDefault
    val availableDecks = CardBack.entries.associateWith { false }.toMutableMap().apply {
        this[CardBack.STANDARD] = true
    }

    @EncodeDefault
    val availableDecksAlt = CardBack.entries.associateWith { false }.toMutableMap().apply {
        this[CardBack.STANDARD] = true
    }

    @EncodeDefault
    val altDecksChosen = CardBack.entries.associateWith { false }.toMutableMap()

    @EncodeDefault
    var styleId: Int = 1

    @EncodeDefault
    val customDeck: CustomDeck = CustomDeck()
    var useCustomDeck: Boolean = false

    @EncodeDefault
    val availableCards: MutableSet<Card> = HashSet(CustomDeck(CardBack.STANDARD, false).toList())

    fun getCustomDeckCopy(): CustomDeck {
        val deck = CustomDeck()
        customDeck.toList().forEach {
            if (it.isAlt == altDecksChosen[it.back]) {
                deck.add(it)
            }
        }
        return deck
    }

    @EncodeDefault
    var gamesStarted = 0

    @EncodeDefault
    var gamesFinished = 0

    @EncodeDefault
    var wins = 0

    @EncodeDefault
    var radioVolume = 1f

    @EncodeDefault
    var soundVolume = 1f

    @EncodeDefault
    var ambientVolume = 1f

    var useCaravanIntro = true

    @EncodeDefault
    var caps = 10000

    private var previousDate = Date().time

    @EncodeDefault
    val soldCards = HashMap<Pair<CardBack, Boolean>, Int>()
    fun updateSoldCards(): Boolean {
        val currentDate = Date().time
        val prevDate = previousDate
        previousDate = currentDate

        val simpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
        if (simpleDateFormat.format(Date(currentDate))
                .equals(simpleDateFormat.format(Date(prevDate)))
        ) {
            return false
        }
        CardBack.entries.forEach { back ->
            val cardBought = (3..15).random()
            val cardBoughtAlt = (2..11).random()
            if (back to true in soldCards) {
                soldCards[back to true] = (soldCards[back to true]!! - cardBought).coerceAtLeast(0)
            }
            if (back to false in soldCards) {
                soldCards[back to false] =
                    (soldCards[back to false]!! - cardBoughtAlt).coerceAtLeast(0)
            }
        }
        return true
    }

    fun getCardPrice(card: Card): Int {
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

                CardBack.LUCKY_38, CardBack.VAULT_21 -> (30 - soldAlready / 5).coerceAtLeast(1)
            }
        }
    }

    @EncodeDefault
    val ownedStyles = mutableSetOf(Style.DESERT, Style.PIP_BOY)

    @EncodeDefault
    var animationLengthTick = 380L
}