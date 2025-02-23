package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.model.challenge.ChallengePlay188
import com.unicorns.invisible.caravan.model.challenge.ChallengeWin5Games
import com.unicorns.invisible.caravan.model.enemy.EnemyAlice
import com.unicorns.invisible.caravan.model.enemy.EnemyBenny
import com.unicorns.invisible.caravan.model.enemy.EnemyCrooker
import com.unicorns.invisible.caravan.model.enemy.EnemyDrMobius
import com.unicorns.invisible.caravan.model.enemy.EnemyEasyPete
import com.unicorns.invisible.caravan.model.enemy.EnemyElijah
import com.unicorns.invisible.caravan.model.enemy.EnemyFisto
import com.unicorns.invisible.caravan.model.enemy.EnemyGloria
import com.unicorns.invisible.caravan.model.enemy.EnemyHanlon
import com.unicorns.invisible.caravan.model.enemy.EnemyJoshua
import com.unicorns.invisible.caravan.model.enemy.EnemyLuc10
import com.unicorns.invisible.caravan.model.enemy.EnemyMadnessCardinal
import com.unicorns.invisible.caravan.model.enemy.EnemyNash
import com.unicorns.invisible.caravan.model.enemy.EnemyNoBark
import com.unicorns.invisible.caravan.model.enemy.EnemyOliver
import com.unicorns.invisible.caravan.model.enemy.EnemySignificantOther
import com.unicorns.invisible.caravan.model.enemy.EnemySnuffles
import com.unicorns.invisible.caravan.model.enemy.EnemyTabitha
import com.unicorns.invisible.caravan.model.enemy.EnemyTheManInTheMirror
import com.unicorns.invisible.caravan.model.enemy.EnemyUlysses
import com.unicorns.invisible.caravan.model.enemy.EnemyVeronica
import com.unicorns.invisible.caravan.model.enemy.EnemyVictor
import com.unicorns.invisible.caravan.model.enemy.EnemyViqueen
import com.unicorns.invisible.caravan.model.enemy.EnemyVulpes
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
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

    @EncodeDefault
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

    val ownedDecks
        get() = availableCards.filterNot { it.isAlt }.map { it.back }.distinct()
    val ownedDecksAlt
        get() = availableCards.filter { it.isAlt }.map { it.back }.distinct()

    fun getCustomDeckCopy(): CustomDeck {
        val deck = CustomDeck()
        val alts = getAltDecksChosenMap()
        getCurrentCustomDeck().toList().forEach {
            if (it.isAlt == alts[it.back]) {
                deck.add(Card(it.rank, it.suit, it.back, it.isAlt))
            }
        }
        return deck
    }

    @EncodeDefault
    val ownedStyles = mutableSetOf(Style.PIP_BOY)

    @EncodeDefault
    var styleId: Int = Style.PIP_BOY.ordinal

    // TODO: a) more stats, and b) correctly count all the old one.
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
    var animationSpeed = AnimationSpeed.NORMAL

    @EncodeDefault
    var capsInHand = 150
    @EncodeDefault
    var tickets = 5

    @EncodeDefault
    var dailyHash = 0
    @EncodeDefault
    var challengesNew: MutableList<Challenge> = mutableListOf()
    fun updateChallenges() {
        challengesNew = Challenge.initChallenges(dailyHash)
    }

    @EncodeDefault
    val challengesInf: MutableList<Challenge> = mutableListOf(
        ChallengeWin5Games(),
        ChallengePlay188()
    )

    @EncodeDefault
    var towerLevel: Int = 0

    @EncodeDefault
    var storyProgress = 0
    @EncodeDefault
    var altStoryProgress = 0
    @EncodeDefault
    var storyCompleted = false

    @EncodeDefault
    val activatedPrizes = HashSet<Int>()

    @EncodeDefault
    var betaReward = false
    @EncodeDefault
    var papaSmurfActive = false
    @EncodeDefault
    var isHeroic = false
    @EncodeDefault
    var isRadioUsesPseudonyms = false

    @EncodeDefault
    val enemiesGroups = listOf(
        listOf(
            EnemyOliver,
            EnemyVeronica,
            EnemyVictor,
            EnemyHanlon,
            EnemyUlysses,
            EnemyBenny
        ),
        listOf(
            EnemyNoBark,
            EnemyNash,
            EnemyTabitha,
            EnemyVulpes,
            EnemyElijah,
            EnemyCrooker
        ),
        listOf(
            EnemySnuffles,
            EnemyEasyPete,
            EnemyTheManInTheMirror,
            EnemyMadnessCardinal,
            EnemyDrMobius,
            EnemyLuc10
        ),
        listOf(
            EnemyGloria,
            EnemyFisto,
            EnemySignificantOther,
            EnemyViqueen,
            EnemyJoshua,
            EnemyAlice
        )

    )
    fun updateEnemiesBanks() {
        val enemies = enemiesGroups.flatten()
        enemies.forEach { enemy ->
            enemy.refreshBank()
        }
    }

    // This one is for Snuffles bank update!
    var table: Int = 0

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