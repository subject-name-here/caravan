package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.ace_in_the_hole
import caravan.composeapp.generated.resources.all_in
import caravan.composeapp.generated.resources.always_bet_on_jack
import caravan.composeapp.generated.resources.empty_string
import caravan.composeapp.generated.resources.four_flush
import caravan.composeapp.generated.resources.good_things_come_in_threes
import caravan.composeapp.generated.resources.high_five
import caravan.composeapp.generated.resources.in_dice_i_would_have_won
import caravan.composeapp.generated.resources.king_of_the_hill
import caravan.composeapp.generated.resources.lucky_seven
import caravan.composeapp.generated.resources.pieces_of_eight
import caravan.composeapp.generated.resources.play_cards_of_rank
import caravan.composeapp.generated.resources.queen_s_gambit
import caravan.composeapp.generated.resources.the_whole_nine_yards
import caravan.composeapp.generated.resources.two_is_enough
import caravan.composeapp.generated.resources.wild_card
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


@Serializable
class ChallengePlayCard(val rankValue: Int) : ChallengeDaily {
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

    override fun getName(): StringResource {
        return when (rankValue) {
            RankNumber.ACE.value -> Res.string.ace_in_the_hole
            RankNumber.TWO.value -> Res.string.two_is_enough
            RankNumber.THREE.value -> Res.string.good_things_come_in_threes
            RankNumber.FOUR.value -> Res.string.four_flush
            RankNumber.FIVE.value -> Res.string.high_five
            RankNumber.SIX.value -> Res.string.in_dice_i_would_have_won
            RankNumber.SEVEN.value -> Res.string.lucky_seven
            RankNumber.EIGHT.value -> Res.string.pieces_of_eight
            RankNumber.NINE.value -> Res.string.the_whole_nine_yards
            RankNumber.TEN.value -> Res.string.all_in
            RankFace.JACK.value -> Res.string.always_bet_on_jack
            RankFace.QUEEN.value -> Res.string.queen_s_gambit
            RankFace.KING.value -> Res.string.king_of_the_hill
            RankFace.JOKER.value -> Res.string.wild_card
            else -> Res.string.empty_string
        }
    }

    override suspend fun getDescription(): String {
        val rankNameId = (if (rankValue in (1..10)) {
            RankNumber.entries.find { it.value == rankValue }?.nameId
        } else if (rankValue in (11..14)) {
            RankFace.entries.find { it.value == rankValue }?.nameId
        } else {
            null
        }) ?: Res.string.empty_string
        return getString(
            Res.string.play_cards_of_rank,
            target.toString(),
            getString(rankNameId)
        )
    }

    override fun getProgress(): String {
        return "$counter / $target"
    }

    override fun isCompleted(): Boolean = counter >= target
}