package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class ChallengeDailyAll : Challenge {
    override fun processMove(move: Challenge.Move, game: Game) {}
    override fun processGameResult(game: Game) {}

    override fun getName(activity: MainActivity): String {
        return activity.getString(R.string.complete_all_dailys)
    }
    override fun getDescription(activity: MainActivity): String {
        return activity.getString(R.string.complete_all_dailys_descr)
    }

    override fun getProgress(): String {
        val dailys = save.challengesNew
        return "${5 - dailys.size} / 5"
    }

    override fun reward(activity: MainActivity): List<Pair<String, () -> Unit>> {
        fun isCardNew(card: CardWithPrice): Boolean {
            return !save.isCardAvailableAlready(card)
        }

        val rewardBack = CardBack.STANDARD_UNCOMMON
        val deck = CollectibleDeck(rewardBack)
        val card = deck.toList().filter(::isCardNew).randomOrNull(Random(save.dailyHash))

        return listOf(
            if (card != null) {
                activity.getString(R.string.claim_card) to { save.addCard(card) }
            } else {
                val prize = (rewardBack.getRarityMult() * CardBack.BASE_CARD_COST).toInt()
                activity.getString(R.string.claim_caps, prize.toString()) to { save.capsInHand += prize }
            }
        )
    }

    override fun isCompleted(): Boolean = save.challengesNew.size <= 1
}