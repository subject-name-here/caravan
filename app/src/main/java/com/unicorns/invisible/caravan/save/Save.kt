package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.trading.ChineseTrader
import com.unicorns.invisible.caravan.model.trading.EnclaveTrader
import com.unicorns.invisible.caravan.model.trading.GomorrahTrader
import com.unicorns.invisible.caravan.model.trading.Lucky38Trader
import com.unicorns.invisible.caravan.model.trading.SierraMadreTrader
import com.unicorns.invisible.caravan.model.trading.TopsTrader
import com.unicorns.invisible.caravan.model.trading.Trader
import com.unicorns.invisible.caravan.model.trading.UltraLuxeTrader
import com.unicorns.invisible.caravan.model.trading.Vault21Trader
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random


// PlayerId == null => save is not loaded
// PlayerId == ""   => playerId is unknown
@OptIn(ExperimentalSerializationApi::class)
@Serializable
class Save(var playerId: String? = null) {
    @EncodeDefault
    var selectedDeck: Pair<CardBack, Boolean> = CardBack.STANDARD to false

    private val customDeck: CustomDeck = CustomDeck(CardBack.STANDARD, false)
    private val altDecksChosen = CardBack.entries.associateWith { false }.toMutableMap()

    private val customDeck2: CustomDeck = CustomDeck(CardBack.STANDARD, false)
    private val altDecksChosen2 = CardBack.entries.associateWith { false }.toMutableMap()

    private val customDeck3: CustomDeck = CustomDeck(CardBack.STANDARD, false)
    private val altDecksChosen3 = CardBack.entries.associateWith { false }.toMutableMap()

    private val customDeck4: CustomDeck = CustomDeck(CardBack.STANDARD, false)
    private val altDecksChosen4 = CardBack.entries.associateWith { false }.toMutableMap()

    var activeCustomDeck = 1
    fun getCurrentCustomDeck(): CustomDeck = when (activeCustomDeck) {
        2 -> customDeck2
        3 -> customDeck3
        4 -> customDeck4
        else -> customDeck
    }
    fun getAltDecksChosenMap() = when (activeCustomDeck) {
        2 -> altDecksChosen2
        3 -> altDecksChosen3
        4 -> altDecksChosen4
        else -> altDecksChosen
    }

    @EncodeDefault
    private val availableCards: MutableSet<Card> = HashSet(CustomDeck(CardBack.STANDARD, false).toList())
    fun isCardAvailableAlready(it: Card): Boolean {
        return availableCards.any { ac ->
            ac.rank == it.rank && ac.suit == it.suit && ac.back == it.back && ac.isAlt == it.isAlt
        }
    }
    fun addCard(card: Card) {
        if (!isCardAvailableAlready(card)) {
            availableCards.add(card)
        }
    }
    fun clearAvailableCards() {
        availableCards.clear()
    }

    val ownedDecks
        get() = availableCards.filterNot { it.isAlt }.map { it.back }.distinct()
    val ownedDecksAlt
        get() = availableCards.filter { it.isAlt }.map { it.back }.distinct()

    fun getCustomDeckCopy(): CustomDeck {
        val deck = CustomDeck()
        getCurrentCustomDeck().toList().forEach {
            if (it.isAlt == getAltDecksChosenMap()[it.back]) {
                deck.add(Card(it.rank, it.suit, it.back, it.isAlt))
            }
        }
        return deck
    }

    @EncodeDefault
    val ownedStyles = mutableSetOf(Style.PIP_BOY)

    @EncodeDefault
    var styleId: Int = Style.PIP_BOY.ordinal

    @EncodeDefault
    var gamesStarted = 0
    @EncodeDefault
    var gamesFinished = 0
    @EncodeDefault
    var wins = 0
    @EncodeDefault
    var capsBet = 0
    @EncodeDefault
    var capsWon = 0

    @EncodeDefault
    var radioVolume = 1f
    @EncodeDefault
    var soundVolume = 1f
    @EncodeDefault
    var ambientVolume = 1f

    var useCaravanIntro = true
    var playRadioInBack = false

    @EncodeDefault
    var capsInHand = 150

    @EncodeDefault
    var tickets = 5

    @EncodeDefault
    var animationSpeed = AnimationSpeed.NORMAL

    @EncodeDefault
    var challengesHash = 0
    fun getCurrentDateHashCode(): Int {
        return SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(Date()).hashCode()
    }
    @EncodeDefault
    var challenges: MutableList<Challenge> = Challenge.initChallenges(challengesHash)
    fun updateChallenges() {
        challenges = Challenge.initChallenges(challengesHash)
    }

    @EncodeDefault
    var towerLevel: Int = 0

    @EncodeDefault
    var storyProgress = 0
    @EncodeDefault
    var altStoryProgress = 0

    @EncodeDefault
    var prize1Activated = false
    @EncodeDefault
    var prize2Activated = false
    @EncodeDefault
    var prize3Activated = false
    @EncodeDefault
    var prize4Activated = false
    @EncodeDefault
    var betaReward = false
    @EncodeDefault
    var papaSmurfActive = false
    @EncodeDefault
    var sixtyNineActive = false
    @EncodeDefault
    var isHeroic = false

    @EncodeDefault
    val enemyCapsLeft = Array<Int>(30) { 0 }
    fun updateDailyStats() {
        val random = Random(challengesHash)
        repeat(30) {
            enemyCapsLeft[it] = if (it % 6 == 5) {
                90
            } else {
                random.nextInt(15, 20)
            }
        }
    }

    fun getPriceOfCard(card: Card): Int {
        val base = if (card.isAlt) 30 else 10
        val rankMult = when (card.rank) {
            Rank.ACE, Rank.SIX, Rank.QUEEN -> 1.0
            Rank.TWO, Rank.THREE -> 0.8
            Rank.FOUR, Rank.FIVE -> 0.9
            Rank.SEVEN, Rank.EIGHT, Rank.NINE -> 1.1
            Rank.TEN -> 1.2
            Rank.JACK -> 1.3
            Rank.KING -> 1.4
            Rank.JOKER -> 1.5
        }
        return (base.toDouble() * rankMult).toInt()
    }

    @EncodeDefault
    val traders = listOf<Trader>(
        UltraLuxeTrader(),
        TopsTrader(),
        GomorrahTrader(),
        Lucky38Trader(),
        Vault21Trader(),
        SierraMadreTrader(),
        EnclaveTrader(),
        ChineseTrader(),
    )
}