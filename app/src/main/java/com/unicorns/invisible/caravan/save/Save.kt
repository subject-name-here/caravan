package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.MainActivity
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
import com.unicorns.invisible.caravan.utils.playDailyCompleted
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random


@OptIn(ExperimentalSerializationApi::class)
@Serializable
class Save(val playerId: String?) {
    @EncodeDefault
    var selectedDeck: Pair<CardBack, Boolean> = CardBack.STANDARD to false

    @EncodeDefault
    val altDecksChosen = CardBack.entries.associateWith { false }.toMutableMap()

    @EncodeDefault
    val customDeck: CustomDeck = CustomDeck(CardBack.STANDARD, false)

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
        customDeck.toList().forEach {
            if (it.isAlt == altDecksChosen[it.back]) {
                deck.add(it)
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
    var capsInHand = 100

    @EncodeDefault
    var tickets = 3

    @EncodeDefault
    var animationSpeed = AnimationSpeed.NORMAL

    @EncodeDefault
    var challengesHash = getCurrentDateHashCode()
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
    var glitchDefeated = false

    @EncodeDefault
    var storyChaptersProgress = 0
    @EncodeDefault
    var altStoryChaptersProgress = 0

    @EncodeDefault
    var prize1Activated = false
    @EncodeDefault
    var prize2Activated = false
    @EncodeDefault
    var prize4Activated = false
    @EncodeDefault
    var papaSmurfActive = false
    @EncodeDefault
    var sixtyNineActive = false
    @EncodeDefault
    var isHeroic = false

    @EncodeDefault
    val enemyCapsLeft = HashMap<Int, Int>()
    fun updateDailyStats() {
        val random = Random(challengesHash)
        repeat(30) {
            enemyCapsLeft[it] = if (it % 6 == 5) {
                75
            } else {
                random.nextInt(15, 30)
            }
        }
    }

    @EncodeDefault
    var barterStat = 10
    @EncodeDefault
    var barterStatProgress = 0.0

    fun getPriceOfCard(card: Card): Int {
        val base = if (card.isAlt) 45 else 15
        val barterMult = 2.5 - barterStat.toDouble() / 50.0
        val rankMult = when (card.rank) {
            Rank.ACE -> 0.95
            Rank.TWO, Rank.THREE, Rank.FOUR -> 0.8
            Rank.FIVE, Rank.SIX, Rank.SEVEN -> 1.0
            Rank.EIGHT, Rank.NINE, Rank.TEN -> 1.2
            Rank.JACK -> 1.25
            Rank.QUEEN -> 1.1
            Rank.KING -> 1.75
            Rank.JOKER -> 2.5
        }
        val backCount = availableCards.count { c -> c.back == card.back && c.isAlt == card.isAlt }
        val rarityMult = (backCount + 26.0) / 52.0
        val bsMult = Random(challengesHash).nextDouble(0.8, 1.2)
        return (base.toDouble() * barterMult * rankMult * rarityMult * bsMult).toInt()
    }
    fun onCardBuying(activity: MainActivity) {
        barterStatProgress += Random.nextDouble(0.175, 0.225)
        if (barterStatProgress >= 1.0 && barterStat < 100) {
            barterStat++
            playDailyCompleted(activity)
            barterStatProgress = 0.0
        }
        saveData(activity)
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