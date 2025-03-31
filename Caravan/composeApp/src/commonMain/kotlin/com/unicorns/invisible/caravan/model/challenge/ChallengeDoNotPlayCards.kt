package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.empty_string
import caravan.composeapp.generated.resources.father_pucci
import caravan.composeapp.generated.resources.father_pucci_descr
import caravan.composeapp.generated.resources.no_jacks
import caravan.composeapp.generated.resources.no_jacks_descr
import caravan.composeapp.generated.resources.no_kings
import caravan.composeapp.generated.resources.no_kings_descr
import caravan.composeapp.generated.resources.only_black
import caravan.composeapp.generated.resources.only_black_descr
import caravan.composeapp.generated.resources.only_red
import caravan.composeapp.generated.resources.only_red_descr
import caravan.composeapp.generated.resources.play_only_even
import caravan.composeapp.generated.resources.play_only_even_descr
import caravan.composeapp.generated.resources.play_only_odd
import caravan.composeapp.generated.resources.play_only_odd_descr
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


@Serializable
class ChallengeDoNotPlayCards(private val code: Int) : ChallengeDaily {
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

    override fun getName(): StringResource {
        return when (code) {
            1 -> Res.string.play_only_odd
            2 -> Res.string.play_only_even
            3 -> Res.string.no_jacks
            4 -> Res.string.no_kings
            5 -> Res.string.only_black
            6 -> Res.string.only_red
            7 -> Res.string.father_pucci
            else -> Res.string.empty_string
        }
    }

    override suspend fun getDescription(): String {
        return when (code) {
            1 -> getString(Res.string.play_only_odd_descr)
            2 -> getString(Res.string.play_only_even_descr)
            3 -> getString(Res.string.no_jacks_descr)
            4 -> getString(Res.string.no_kings_descr)
            5 -> getString(Res.string.only_black_descr)
            6 -> getString(Res.string.only_red_descr)
            7 -> getString(Res.string.father_pucci_descr)
            else -> ""
        }
    }

    override fun getProgress(): String {
        return if (completed) "1 / 1" else "0 / 1"
    }

    override fun isCompleted(): Boolean = completed
}