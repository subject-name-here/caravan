package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class ChallengePlayCard(val rank: Rank) : Challenge {
    private var counter = 0

    @Transient
    private val target = when (rank) {
        Rank.ACE -> 11
        Rank.TWO -> 12
        Rank.THREE -> 13
        Rank.FOUR -> 14
        Rank.FIVE -> 15
        Rank.SIX -> 16
        Rank.SEVEN -> 17
        Rank.EIGHT -> 18
        Rank.NINE -> 19
        Rank.TEN -> 20
        Rank.JACK -> 22
        Rank.QUEEN -> 16
        Rank.KING -> 24
        Rank.JOKER -> 13
    }
    override fun processMove(move: Challenge.Move, game: Game) {
        if (move.moveCode == 3 || move.moveCode == 4) {
            val cardFromHand = move.handCard ?: return
            if (cardFromHand.rank == rank && cardFromHand.isOrdinary()) {
                counter++
            }
        }
    }

    override fun processGameResult(game: Game) {}

    override fun getName(activity: MainActivity): String {
        return when (rank) {
            Rank.ACE -> activity.getString(R.string.ace_in_the_hole)
            Rank.JACK -> activity.getString(R.string.always_bet_on_jack)
            Rank.QUEEN -> activity.getString(R.string.queen_s_gambit)
            Rank.KING -> activity.getString(R.string.king_of_the_hill)
            Rank.JOKER -> activity.getString(R.string.wild_card)
            Rank.TWO -> activity.getString(R.string.two_is_enough)
            Rank.THREE -> activity.getString(R.string.good_things_come_in_threes)
            Rank.FOUR -> activity.getString(R.string.four_flush)
            Rank.FIVE -> activity.getString(R.string.high_five)
            Rank.SIX -> activity.getString(R.string.in_dice_i_would_have_won)
            Rank.SEVEN -> activity.getString(R.string.lucky_seven)
            Rank.EIGHT -> activity.getString(R.string.pieces_of_eight)
            Rank.NINE -> activity.getString(R.string.the_whole_nine_yards)
            Rank.TEN -> activity.getString(R.string.all_in)
        }
    }

    override fun getDescription(activity: MainActivity): String {
        return activity.getString(
            R.string.play_cards_of_rank,
            target.toString(),
            activity.getString(rank.nameId)
        )
    }

    override fun getProgress(): String {
        return "$counter / $target"
    }

    override fun isCompleted(): Boolean = counter >= target
}