package com.unicorns.invisible.caravan.model.challenge

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.claim_caps
import caravan.composeapp.generated.resources.claim_card
import caravan.composeapp.generated.resources.complete_all_dailys
import caravan.composeapp.generated.resources.complete_all_dailys_descr
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import kotlin.random.Random


@Serializable
class ChallengeDailyAll : Challenge {
    override fun processMove(move: Challenge.Move, game: Game) {}
    override fun processGameResult(game: Game) {}

    override fun getXp(): Int = 50

    override fun getName(): StringResource {
        return Res.string.complete_all_dailys
    }
    override suspend fun getDescription(): String {
        return getString(Res.string.complete_all_dailys_descr)
    }

    override fun getProgress(): String {
        val dailys = save.challengesNew
        return "${5 - dailys.size} / 5"
    }

    override suspend fun reward(): List<Pair<String, () -> Unit>> {
        fun isCardNew(card: CardWithPrice): Boolean {
            return !save.isCardAvailableAlready(card)
        }

        val rewardBack = CardBack.STANDARD_UNCOMMON
        val deck = CollectibleDeck(rewardBack)
        val card = deck.toList().filter(::isCardNew).randomOrNull(Random(save.dailyHash))

        return listOf(
            if (card != null) {
                getString(Res.string.claim_card) to { save.addCard(card) }
            } else {
                val prize = (rewardBack.getRarityMult() * CardBack.BASE_CARD_COST).toInt()
                getString(Res.string.claim_caps, prize.toString()) to { save.capsInHand += prize }
            }
        )
    }

    override fun isCompleted(): Boolean = save.challengesNew.size <= 1
}