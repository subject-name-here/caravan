package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class ChallengePlayCard(val rankValue: Int) : Challenge {
    private var counter = 0

    @Transient
    private val target = when (rankValue) {
        RankNumber.ACE.value -> 10
        RankNumber.TWO.value -> 10
        RankNumber.THREE.value -> 10
        RankNumber.FOUR.value -> 10
        RankNumber.FIVE.value -> 10
        RankNumber.SIX.value -> 12
        RankNumber.SEVEN.value -> 14
        RankNumber.EIGHT.value -> 16
        RankNumber.NINE.value -> 18
        RankNumber.TEN.value -> 20
        RankFace.JACK.value -> 22
        RankFace.QUEEN.value -> 15
        RankFace.KING.value -> 24
        RankFace.JOKER.value -> 13
        else -> 1
    }
    override fun processMove(move: Challenge.Move, game: Game) {
        if (move.moveCode == 3 || move.moveCode == 4) {
            val c = move.handCard ?: return
            if (c is CardBase && c.rank.value == rankValue || c is CardFace && c.rank.value == rankValue) {
                counter++
            }
        }
    }

    override fun processGameResult(game: Game) {}

    override fun getName(activity: MainActivity): String {
        return when (rankValue) {
            RankNumber.ACE.value -> activity.getString(R.string.ace_in_the_hole)
            RankNumber.TWO.value -> activity.getString(R.string.always_bet_on_jack)
            RankNumber.THREE.value -> activity.getString(R.string.queen_s_gambit)
            RankNumber.FOUR.value -> activity.getString(R.string.king_of_the_hill)
            RankNumber.FIVE.value -> activity.getString(R.string.wild_card)
            RankNumber.SIX.value -> activity.getString(R.string.two_is_enough)
            RankNumber.SEVEN.value -> activity.getString(R.string.good_things_come_in_threes)
            RankNumber.EIGHT.value -> activity.getString(R.string.four_flush)
            RankNumber.NINE.value -> activity.getString(R.string.high_five)
            RankNumber.TEN.value -> activity.getString(R.string.in_dice_i_would_have_won)
            RankFace.JACK.value -> activity.getString(R.string.lucky_seven)
            RankFace.QUEEN.value -> activity.getString(R.string.pieces_of_eight)
            RankFace.KING.value -> activity.getString(R.string.the_whole_nine_yards)
            RankFace.JOKER.value -> activity.getString(R.string.all_in)
            else -> activity.getString(R.string.empty_string)
        }
    }

    override fun getDescription(activity: MainActivity): String {
        val rankNameId = (if (rankValue in (1..10)) {
            RankNumber.entries.find { it.value == rankValue }?.nameId
        } else if (rankValue in (11..14)) {
            RankFace.entries.find { it.value == rankValue }?.nameId
        } else {
            null
        }) ?: R.string.empty_string
        return activity.getString(
            R.string.play_cards_of_rank,
            target.toString(),
            activity.getString(rankNameId)
        )
    }

    override fun getProgress(): String {
        return "$counter / $target"
    }

    override fun isCompleted(): Boolean = counter >= target
}