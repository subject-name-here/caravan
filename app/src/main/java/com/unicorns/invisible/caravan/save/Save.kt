package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.challenge.Challenge
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
class Save(val isUsable: Boolean) {
    @EncodeDefault
    var selectedDeck: Pair<CardBack, Boolean> = CardBack.STANDARD to false

    @EncodeDefault
    val altDecksChosen = CardBack.entries.associateWith { false }.toMutableMap()

    @EncodeDefault
    val customDeck: CustomDeck = CustomDeck(CardBack.STANDARD, false)

    @EncodeDefault
    val availableCards: MutableSet<Card> = HashSet(CustomDeck(CardBack.STANDARD, false).toList())

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
    var capsInDeposit = 0

    @EncodeDefault
    var tickets = 3

    @EncodeDefault
    var animationSpeed = AnimationSpeed.NORMAL

    @EncodeDefault
    var challengesHash = getCurrentDateHashCode()
    private fun getCurrentDateHashCode(): Int {
        return SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(Date()).hashCode()
    }
    @EncodeDefault
    var challenges: MutableList<Challenge> = Challenge.initChallenges(challengesHash)
    fun updateChallenges() {
        val currentHash = getCurrentDateHashCode()
        if (currentHash != challengesHash) {
            challengesHash = currentHash
            challenges = Challenge.initChallenges(challengesHash)
        }
    }

    @EncodeDefault
    var towerLevel: Int = 0

    @EncodeDefault
    var storyChaptersProgress = 0
    @EncodeDefault
    var altStoryChaptersProgress = 0

    @EncodeDefault
    var prize1Activated = false
    @EncodeDefault
    var prize2Activated = false
}