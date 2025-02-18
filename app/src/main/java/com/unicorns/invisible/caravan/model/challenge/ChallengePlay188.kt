package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import kotlinx.serialization.Serializable


@Serializable
class ChallengePlay188 : Challenge {
    private var cardsPlayed = 0

    override fun processMove(move: Challenge.Move, game: Game) {
        if (move.moveCode == 3 || move.moveCode == 4) {
            cardsPlayed++
        }
    }

    override fun processGameResult(game: Game) {}

    override fun getName(activity: MainActivity): String {
        return activity.getString(R.string.play_188_challenge)
    }

    override fun getDescription(activity: MainActivity): String {
        return activity.getString(R.string.play_188_challenge_descr)
    }

    override fun getProgress(): String {
        return "${cardsPlayed} / 188"
    }

    override fun isCompleted(): Boolean {
        return cardsPlayed >= 188
    }

    override fun restartChallenge() {
        cardsPlayed = 0
    }
}