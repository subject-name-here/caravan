package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.model.challenge.ChallengeBeatEnemies
import com.unicorns.invisible.caravan.model.challenge.ChallengeDoNotPlayCards
import com.unicorns.invisible.caravan.model.challenge.ChallengePlayCard
import com.unicorns.invisible.caravan.model.challenge.ChallengeWinByDiscard
import com.unicorns.invisible.caravan.model.challenge.ChallengeWinByPlayingJoker
import com.unicorns.invisible.caravan.model.enemy.EnemyBenny
import com.unicorns.invisible.caravan.model.enemy.EnemyBestest
import com.unicorns.invisible.caravan.model.enemy.EnemyBetter
import com.unicorns.invisible.caravan.model.enemy.EnemyEasy
import com.unicorns.invisible.caravan.model.enemy.EnemyHard
import com.unicorns.invisible.caravan.model.enemy.EnemyHouse
import com.unicorns.invisible.caravan.model.enemy.EnemyMedium
import com.unicorns.invisible.caravan.model.enemy.EnemyNash
import com.unicorns.invisible.caravan.model.enemy.EnemyNoBark
import com.unicorns.invisible.caravan.model.enemy.EnemySecuritron38
import com.unicorns.invisible.caravan.model.enemy.EnemySix
import com.unicorns.invisible.caravan.model.enemy.EnemySwank
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random


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
    var styleId: Int = 2

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
    var caps = 0
    @EncodeDefault
    var tickets = 0

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
        var flag = false
        CardBack.playableBacks.forEach { back ->
            val cardBought = (3..15).random()
            val cardBoughtAlt = (2..11).random()
            if (back to true in soldCards) {
                val oldPrice = getCardPrice(Card(Rank.ACE, Suit.HEARTS, back, true))
                soldCards[back to true] = (soldCards[back to true]!! - cardBoughtAlt).coerceAtLeast(0)
                val newPrice = getCardPrice(Card(Rank.ACE, Suit.HEARTS, back, true))
                if (oldPrice != newPrice) {
                    flag = true
                }
            }
            if (back to false in soldCards) {
                val oldPrice = getCardPrice(Card(Rank.ACE, Suit.HEARTS, back, false))
                soldCards[back to false] = (soldCards[back to false]!! - cardBought).coerceAtLeast(0)
                val newPrice = getCardPrice(Card(Rank.ACE, Suit.HEARTS, back, false))
                if (oldPrice != newPrice) {
                    flag = true
                }
            }
        }
        return flag
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

                CardBack.DECK_13 -> when (soldAlready) {
                    in (0..9) -> 10
                    in (10..19) -> 9
                    in (20..29) -> 8
                    in (30..39) -> 7
                    in (40..49) -> 5
                    in (50..59) -> 4
                    in (60..69) -> 3
                    else -> 1
                }
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

                CardBack.UNPLAYABLE -> 0
                CardBack.WILD_WASTELAND -> 0

                CardBack.LUCKY_38, CardBack.VAULT_21, CardBack.DECK_13 -> (30 - soldAlready / 5).coerceAtLeast(1)
            }
        }
    }

    @EncodeDefault
    val ownedStyles = mutableSetOf(Style.DESERT, Style.PIP_BOY)

    @EncodeDefault
    var animationSpeed = AnimationSpeed.NORMAL

    @EncodeDefault
    var challengesHash = getCurrentDateHashCode()
    private fun getCurrentDateHashCode(): Int {
        return SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(Date()).hashCode()
    }

    @EncodeDefault
    var challenges: MutableList<Challenge> = initChallenges()
    fun updateChallenges() {
        val currentHash = getCurrentDateHashCode()
        if (currentHash != challengesHash) {
            challengesHash = currentHash
            challenges = initChallenges()
        }
    }
    private fun initChallenges(): MutableList<Challenge> {
        val rand = Random(challengesHash)
        val challenges = mutableListOf<Challenge>()

        val rank = Rank.entries.random(rand)
        challenges.add(ChallengePlayCard(rank))

        val code = (1..7).random(rand)
        val enemies = when (code) {
            1 -> {
                listOf(EnemyBestest)
            }
            2 -> {
                listOf(EnemySix)
            }
            3 -> {
                listOf(EnemyHouse)
            }
            4 -> {
                listOf(EnemyBetter, EnemySecuritron38)
            }
            5 -> {
                listOf(EnemyNoBark, EnemyMedium)
            }
            6 -> {
                listOf(EnemyNash, EnemyEasy)
            }
            7 -> {
                listOf(EnemyHard, EnemyBenny, EnemySwank)
            }
            else -> listOf()
        }
        challenges.add(ChallengeBeatEnemies(enemies, code))

        val challenge = when (val code2 = (1..8).random()) {
            8 -> ChallengeWinByDiscard()
            7 -> ChallengeWinByPlayingJoker()
            else -> ChallengeDoNotPlayCards(code2)
        }
        challenges.add(challenge)

        return challenges
    }

    @EncodeDefault
    var towerLevel: Int = 0
    @EncodeDefault
    var isTowerFree: Boolean = false
    @EncodeDefault
    var isGameRigged: Boolean = false
    @EncodeDefault
    var isEnclaveThemeAvailable: Boolean = false

    @EncodeDefault
    var secretMode: Boolean = false

    @EncodeDefault
    var storyChaptersProgress = 0
}