package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.model.challenge.ChallengePlay188
import com.unicorns.invisible.caravan.model.challenge.ChallengeWin5Games
import com.unicorns.invisible.caravan.model.enemy.EnemyBenny
import com.unicorns.invisible.caravan.model.enemy.EnemyCaesar
import com.unicorns.invisible.caravan.model.enemy.EnemyCrooker
import com.unicorns.invisible.caravan.model.enemy.EnemyDrMobius
import com.unicorns.invisible.caravan.model.enemy.EnemyEasyPete
import com.unicorns.invisible.caravan.model.enemy.EnemyElijah
import com.unicorns.invisible.caravan.model.enemy.EnemyFisto
import com.unicorns.invisible.caravan.model.enemy.EnemyGloria
import com.unicorns.invisible.caravan.model.enemy.EnemyHanlon
import com.unicorns.invisible.caravan.model.enemy.EnemyLuc10
import com.unicorns.invisible.caravan.model.enemy.EnemyMadnessCardinal
import com.unicorns.invisible.caravan.model.enemy.EnemyNash
import com.unicorns.invisible.caravan.model.enemy.EnemyNoBark
import com.unicorns.invisible.caravan.model.enemy.EnemyOliver
import com.unicorns.invisible.caravan.model.enemy.EnemyPvEWithBank
import com.unicorns.invisible.caravan.model.enemy.EnemyRingo
import com.unicorns.invisible.caravan.model.enemy.EnemySalt
import com.unicorns.invisible.caravan.model.enemy.EnemySnuffles
import com.unicorns.invisible.caravan.model.enemy.EnemyTabitha
import com.unicorns.invisible.caravan.model.enemy.EnemyTheManInTheMirror
import com.unicorns.invisible.caravan.model.enemy.EnemyUlysses
import com.unicorns.invisible.caravan.model.enemy.EnemyVeronica
import com.unicorns.invisible.caravan.model.enemy.EnemyVictor
import com.unicorns.invisible.caravan.model.enemy.EnemyViqueen
import com.unicorns.invisible.caravan.model.enemy.EnemyVulpes
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.model.trading.*
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.math.max


// PlayerId == null => save is not loaded
// PlayerId == ""   => playerId is unknown
@OptIn(ExperimentalSerializationApi::class)
@Serializable
class Save(var playerId: String? = null) {
    @EncodeDefault
    var selectedDeck: Pair<CardBack, Int> = CardBack.STANDARD to 0

    private val customDeck: CollectibleDeck = CollectibleDeck(CardBack.STANDARD, 0)
    private val backNumbersChosen = CardBack.entries.associateWith { 0 }.toMutableMap()

    private val customDeck2: CollectibleDeck = CollectibleDeck(CardBack.STANDARD, 0)
    private val backNumbersChosen2 = CardBack.entries.associateWith { 0 }.toMutableMap()

    private val customDeck3: CollectibleDeck = CollectibleDeck(CardBack.STANDARD, 0)
    private val backNumbersChosen3 = CardBack.entries.associateWith { 0 }.toMutableMap()

    private val customDeck4: CollectibleDeck = CollectibleDeck(CardBack.STANDARD, 0)
    private val backNumbersChosen4 = CardBack.entries.associateWith { 0 }.toMutableMap()

    @EncodeDefault
    var activeCustomDeck = 1
    fun getCurrentCustomDeck(): CollectibleDeck = when (activeCustomDeck) {
        2 -> customDeck2
        3 -> customDeck3
        4 -> customDeck4
        else -> customDeck
    }
    fun getBackNumbersChosenMap() = when (activeCustomDeck) {
        2 -> backNumbersChosen2
        3 -> backNumbersChosen3
        4 -> backNumbersChosen4
        else -> backNumbersChosen
    }

    @EncodeDefault
    private val availableCards = CollectibleDeck(CardBack.STANDARD, 0)
    fun isCardAvailableAlready(it: CardWithPrice): Boolean {
        return it in availableCards
    }
    fun addCard(card: CardWithPrice) {
        availableCards.add(card)
    }

    val availableDecks
        get() = availableCards.toList().map { it.getBack() to it.getBackNumber() }.distinct()

    fun getCurrentDeckCopy(): CollectibleDeck {
        val deck = CollectibleDeck()
        getCurrentCustomDeck().toList().forEach {
            if (getBackNumbersChosenMap()[it.getBack()] == it.getBackNumber()) {
                deck.add(
                    when (it) {
                        is CardFaceSuited -> CardFaceSuited(it.rank, it.suit, it.getBack(), it.getBackNumber())
                        is CardJoker -> CardJoker(it.number, it.getBack(), it.getBackNumber())
                        is CardNumber -> CardNumber(it.rank, it.suit, it.getBack(), it.getBackNumber())
                    }
                )
            }
        }
        return deck
    }

    @EncodeDefault
    val ownedStyles = mutableSetOf(Style.PIP_BOY)

    @EncodeDefault
    var styleId: Int = Style.PIP_BOY.ordinal


    // STATS
    @EncodeDefault
    var gamesStarted = 0
    @EncodeDefault
    var gamesFinished = 0
    @EncodeDefault
    var wins = 0
    @EncodeDefault
    var pvpWins = 0

    @EncodeDefault
    var capsBet = 0
    @EncodeDefault
    var capsWon = 0
    @EncodeDefault
    var maxBetWon = 0
    @EncodeDefault
    var winsWithBet = 0

    @EncodeDefault
    var capsWasted = 0
    @EncodeDefault
    var chipsWasted = 0

    @EncodeDefault
    var challengesCompleted = 0

    @EncodeDefault
    var currentStrike: Int = 0
        set(value) {
            field = value
            maxStrike = max(maxStrike, value)
        }
    @EncodeDefault
    var maxStrike = 0
        private set


    // TODO: a) more stats, and b) correctly count all the old one.
    // END STATS


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
    var silverRushChips = 100
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
    var challengesConst1: MutableList<Challenge> = mutableListOf(

    )

    @EncodeDefault
    var towerLevel: Int = 0
    @EncodeDefault
    var cookCookMult: Int = 1
    @EncodeDefault
    var secondChances: Int = 0
    @EncodeDefault
    var towerBeaten: Boolean = false

    @EncodeDefault
    var storyProgress = 0
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
    val enemiesGroups2 = listOf(
        listOf(
            EnemyOliver(),
            EnemyVeronica(),
            EnemyVictor(),
            EnemyHanlon(),
            EnemyUlysses(),
            EnemyBenny()
        ),
        listOf(
            EnemyNoBark(),
            EnemyNash(),
            EnemyTabitha(),
            EnemyVulpes(),
            EnemyElijah(),
            EnemyCrooker()
        ),
        listOf(
            EnemySnuffles(),
            EnemyEasyPete(),
            EnemyTheManInTheMirror(),
            EnemyMadnessCardinal(),
            EnemyDrMobius(),
            EnemyLuc10()
        ),
        listOf(
            EnemyRingo(),
            EnemyFisto(),
            EnemyCaesar(),
            EnemyViqueen(),
            EnemySalt(),
            EnemyGloria(),
        )

    )
    fun updateEnemiesBanks() {
        val enemies = enemiesGroups2.flatten()
        enemies.filterIsInstance<EnemyPvEWithBank>().forEach { enemy ->
            enemy.bank = if (enemy is EnemySnuffles) {
                val fromTable = table
                table = 0
                fromTable
            } else {
                enemy.maxBank
            }
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