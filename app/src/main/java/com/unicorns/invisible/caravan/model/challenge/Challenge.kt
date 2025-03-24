package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Card
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
sealed interface Challenge {
    fun processMove(move: Move, game: Game)
    fun processGameResult(game: Game)

    fun getName(activity: MainActivity): String
    fun getDescription(activity: MainActivity): String
    fun getProgress(): String
    fun reward(activity: MainActivity): List<Pair<String, () -> Unit>>

    fun isCompleted(): Boolean

    data class Move(
        val moveCode: Int,
        val handCard: Card? = null,
    )

    companion object {
        fun initChallenges(seed: Int): MutableList<Challenge> {
            val rand = Random(seed)
            val challenges = mutableListOf<Challenge>()

            challenges.add(ChallengeDailyAll())

            challenges.add(ChallengePlayCard((1..14).random(rand)))
            challenges.add(ChallengeDoNotPlayCards((1..7).random(rand))) // TODO: add more variations
            challenges.add(ChallengeBeatEnemies((1..8).random(rand))) // TODO: add more combinations

            val specialCode = (1..7).random(rand)  // TODO: add more interesting things!
            when (specialCode) {
                1 -> challenges.add(ChallengeWinByDiscard())
                2 -> challenges.add(ChallengeWinByPlayingJoker())
                3 -> challenges.add(ChallengeWinByMarriage())
                4 -> challenges.add(ChallengeAll26(26))
                5 -> challenges.add(ChallengeAll26(21))
                6 -> challenges.add(ChallengeAttrition())
                7 -> challenges.add(ChallengeWinWithEmptyDeck())
            }

            return challenges
        }
    }
}