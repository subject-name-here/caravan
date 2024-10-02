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
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
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

    var previousDate = Date().time

    @EncodeDefault
    val soldCards = HashMap<Pair<CardBack, Boolean>, Int>()

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

        challenges.add(ChallengeBeatEnemies((1..7).random(rand)))

        val challenge = when (val code2 = (1..8).random(rand)) {
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
    var soOpen: Boolean = false

    @EncodeDefault
    var storyChaptersProgress = 0
    @EncodeDefault
    var altStoryChaptersProgress = 0

    @EncodeDefault
    var prize1Activated = false
    @EncodeDefault
    var prize2Activated = false
}