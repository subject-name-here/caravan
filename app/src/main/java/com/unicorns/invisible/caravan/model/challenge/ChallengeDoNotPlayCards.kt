package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class ChallengeDoNotPlayCards(private val code: Int) : Challenge {
    @Transient
    private var notPlayed = true
    override fun processMove(move: Challenge.Move, game: Game) {
        if (game.isInitStage()) {
            notPlayed = true
            return
        }
        if (move.moveCode in listOf(3, 4)) {
            val playedCard = move.handCard ?: return
            val predicate: () -> Boolean = when (code) {
                1 -> {
                    { playedCard is CardBase && playedCard.rank.value % 2 == 0 }
                }

                2 -> {
                    { playedCard is CardBase && playedCard.rank != RankNumber.ACE && playedCard.rank.value % 2 == 1 }
                }

                3 -> {
                    { playedCard is CardFace && playedCard.rank == RankFace.JACK }
                }

                4 -> {
                    { playedCard is CardFace && playedCard.rank == RankFace.KING }
                }
                5 -> {
                    {
                        playedCard is CardBase && playedCard.suit in listOf(Suit.HEARTS, Suit.DIAMONDS) ||
                        playedCard is CardFaceSuited && playedCard.suit in listOf(Suit.HEARTS, Suit.DIAMONDS)
                    }
                }

                6 -> {
                    {
                        playedCard is CardBase && playedCard.suit in listOf(Suit.SPADES, Suit.CLUBS) ||
                        playedCard is CardFaceSuited && playedCard.suit in listOf(Suit.SPADES, Suit.CLUBS)
                    }
                }

                7 -> {
                    { playedCard is CardBase && playedCard.rank in listOf(RankNumber.FOUR, RankNumber.SIX, RankNumber.EIGHT, RankNumber.NINE, RankNumber.TEN) }
                }

                else -> {
                    { true }
                }
            }
            if (predicate()) {
                notPlayed = false
            }
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