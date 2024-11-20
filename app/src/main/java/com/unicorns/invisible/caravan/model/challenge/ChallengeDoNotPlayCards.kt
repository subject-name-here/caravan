package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class ChallengeDoNotPlayCards(private val code: Int) : Challenge {
    @Transient
    private var notPlayed = true
    override fun processMove(move: Challenge.Move, game: Game) {
        if (!game.isInitStage()) {
            if (move.moveCode in listOf(3, 4)) {
                val playedCard = move.handCard ?: return
                val predicate: () -> Boolean = when (code) {
                    1 -> {
                        { playedCard.rank in listOf(Rank.TWO, Rank.FOUR, Rank.SIX, Rank.EIGHT, Rank.TEN) }
                    }
                    2 -> {
                        { playedCard.rank in listOf(Rank.THREE, Rank.FIVE, Rank.SEVEN, Rank.NINE) }
                    }
                    3 -> {
                        { playedCard.rank == Rank.JACK }
                    }
                    4 -> {
                        { playedCard.rank == Rank.KING }
                    }
                    5 -> {
                        { playedCard.suit == Suit.HEARTS || playedCard.suit == Suit.DIAMONDS }
                    }
                    6 -> {
                        { playedCard.suit == Suit.SPADES || playedCard.suit == Suit.CLUBS }
                    }
                    7 -> {
                        { playedCard.rank in listOf(Rank.FOUR, Rank.SIX, Rank.EIGHT, Rank.NINE, Rank.TEN) }
                    }
                    else -> {
                        { true }
                    }
                }
                if (playedCard.isOrdinary() && playedCard.rank != Rank.JOKER && predicate()) {
                    notPlayed = false
                }
            }
        } else {
            notPlayed = true
        }
    }

    private var completed = false
    override fun processGameResult(game: Game) {
        if (game.isGameOver == 1) {
            if (notPlayed) {
                completed = true
            }
        }
    }

    override fun getName(activity: MainActivity): String {
        return when (code) {
            1 -> activity.getString(R.string.play_only_odd)
            2 -> activity.getString(R.string.play_only_even)
            3 -> activity.getString(R.string.no_jacks)
            4 -> activity.getString(R.string.no_kings)
            5 -> activity.getString(R.string.only_black)
            6 -> activity.getString(R.string.only_red)
            7 -> activity.getString(R.string.father_pucci)
            else -> ""
        }
    }

    override fun getDescription(activity: MainActivity): String {
        return when (code) {
            1 -> activity.getString(R.string.play_only_odd_descr)
            2 -> activity.getString(R.string.play_only_even_descr)
            3 -> activity.getString(R.string.no_jacks_descr)
            4 -> activity.getString(R.string.no_kings_descr)
            5 -> activity.getString(R.string.only_black_descr)
            6 -> activity.getString(R.string.only_red_descr)
            7 -> activity.getString(R.string.father_pucci_descr)
            else -> ""
        }
    }

    override fun getProgress(): String {
        return if (completed) "1 / 1" else "0 / 1"
    }

    override fun isCompleted(): Boolean = completed
}