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
import com.unicorns.invisible.caravan.utils.playFanfares
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.math.max
import kotlin.math.min


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
    var pvpGames = 0
    @EncodeDefault
    var pvpWins = 0

    @EncodeDefault
    var capsBet = 0
    @EncodeDefault
    var capsWon = 0
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
    var lvl = 1
        private set
    fun needXpToNextLevel() = when (lvl) {
        1, 2 -> 500 * lvl
        3, 4, 5, 6 -> 750 * lvl
        else -> 1000 * lvl
    }
    @EncodeDefault
    var xp = 0
        private set
    fun increaseXp(add: Int) {
        xp += add
        while (xp >= needXpToNextLevel()) {
            xp -= needXpToNextLevel()
            lvl++
            updateEnemiesBanks()
            // TODO: show message
            playFanfares()
        }
    }
    fun increaseXpFromDefeatingEnemy(enemyLvl: Int, mult: Int): Int {
        val xp = max(25, 150 - 50 * (min(lvl, 6) - enemyLvl)) * mult
        increaseXp(xp)
        return xp
    }
    fun increaseXpFromLosingToEnemy(enemyLvl: Int, mult: Int): Int {
        val xp = max(5, 20 - 5 * (min(lvl, 6) - enemyLvl)) * mult
        increaseXp(xp)
        return xp
    }

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
    val enemiesGroups3 = listOf(
        listOf(
            EnemyOliver(),
            EnemyVeronica(),
            EnemyVictor(),
            EnemyHanlon(),
            EnemyUlysses(),
            EnemyBenny()
        ),
        listOf(
            EnemyNash(),
            EnemyNoBark(),
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
        val enemies = enemiesGroups3.flatten()
        enemies.filterIsInstance<EnemyPvEWithBank>().forEach { enemy ->
            enemy.curBets = if (enemy is EnemySnuffles) {
                val fromTable = table
                table = 0
                fromTable / 5
            } else {
                enemy.maxBets
            }
        }
    }

    // This one is for Snuffles bank update!
    var table: Int = 0
}