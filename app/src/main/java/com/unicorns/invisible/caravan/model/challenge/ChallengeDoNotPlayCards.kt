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
                    else -> {
                        { true }
                    }
                }
                if (!playedCard.isSpecial() && playedCard.rank != Rank.JOKER && predicate()) {
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
            1 -> activity.getString(R.string.say_no_to_even)
            2 -> activity.getString(R.string.say_no_to_odd)
            3 -> activity.getString(R.string.not_every_man_jack)
            4 -> activity.getString(R.string.french_revolution)
            5 -> activity.getString(R.string.put_it_all_on_black)
            6 -> activity.getString(R.string.put_it_all_on_red)
            else -> ""
        }
    }

    override fun getDescription(activity: MainActivity): String {
        return when (code) {
            1 -> {
                activity.getString(R.string.do_not_play_even_number_cards_2_4_6_8_10)
            }
            2 -> {
                activity.getString(R.string.do_not_play_odd_number_cards_3_5_7_9)
            }
            3 -> {
                activity.getString(R.string.do_not_play_jacks)
            }
            4 -> {
                activity.getString(R.string.do_not_play_kings)
            }
            5 -> {
                activity.getString(R.string.do_not_play_hearts_or_diamonds)
            }
            6 -> {
                activity.getString(R.string.do_not_play_clubs_or_spades)
            }
            else -> ""
        }
    }

    override fun getProgress(): String {
        return if (completed) "1 / 1" else "0 / 1"
    }

    override fun isCompleted(): Boolean = completed
}