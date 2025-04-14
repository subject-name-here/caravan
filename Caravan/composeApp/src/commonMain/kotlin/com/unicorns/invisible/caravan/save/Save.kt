package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.model.challenge.ChallengePlay188
import com.unicorns.invisible.caravan.model.challenge.ChallengeWin6Games
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
import kotlin.math.max


// PlayerId == null => save is not loaded
// PlayerId == ""   => playerId is unknown
@OptIn(ExperimentalSerializationApi::class)
@Serializable
class Save(var playerId: String? = null) {
    @EncodeDefault
    var selectedDeck: CardBack = CardBack.STANDARD

    private val customDeck: CollectibleDeck = CollectibleDeck(CardBack.STANDARD)
    private val customDeck2: CollectibleDeck = CollectibleDeck(CardBack.STANDARD)
    private val customDeck3: CollectibleDeck = CollectibleDeck(CardBack.STANDARD)
    private val customDeck4: CollectibleDeck = CollectibleDeck(CardBack.STANDARD)

    @EncodeDefault
    var activeCustomDeck = 1
    fun getCurrentCustomDeck(): CollectibleDeck = when (activeCustomDeck) {
        2 -> customDeck2
        3 -> customDeck3
        4 -> customDeck4
        else -> customDeck
    }

    @EncodeDefault
    private val availableCards = CollectibleDeck(CardBack.STANDARD)
    fun isCardAvailableAlready(it: CardWithPrice): Boolean {
        return it in availableCards
    }
    fun addCard(card: CardWithPrice) {
        availableCards.add(card)
    }

    val availableDecks
        get() = availableCards.toList().map { it.getBack() }.distinct()

    fun getCurrentDeckCopy(): CollectibleDeck {
        val deck = CollectibleDeck()
        getCurrentCustomDeck().toList().forEach {
            deck.add(
                when (it) {
                    is CardFaceSuited -> CardFaceSuited(it.rank, it.suit, it.getBack())
                    is CardJoker -> CardJoker(it.number, it.getBack())
                    is CardNumber -> CardNumber(it.rank, it.suit, it.getBack())
                }
            )
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
    var enemiesDefeated = ArrayList<String>()
    @EncodeDefault
    var maxStrike = 0
        private set


    // TODO: more stats
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
    var sierraMadreChips = 75
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
        ChallengeWin6Games(),
        ChallengePlay188()
    )
    @EncodeDefault
    var challenges1: MutableList<Challenge> = mutableListOf(

    )
    @EncodeDefault
    var challenges2: MutableList<Challenge> = mutableListOf(

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
}