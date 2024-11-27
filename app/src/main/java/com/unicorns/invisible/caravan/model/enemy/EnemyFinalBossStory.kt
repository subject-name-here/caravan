package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class EnemyFinalBossStory(@Transient private var update: Int = 0) : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.CHINESE, false))

    @Transient
    var playAlarm: () -> Unit = {}
    @Transient
    var sayThing: (Int) -> Unit = {}

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.shuffled().filter { it.isOrdinary() }.filter { !it.isFace() }.minBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        // TODO: makeMoveInner(game)

        if (game.enemyCResources.deckSize == 0) {
            playAlarm()
            ++update
            if (update < 5) {
                game.enemyCResources.addNewDeck(CustomDeck(CardBack.CHINESE, false))
                game.enemyCResources.shuffleDeck()
            }
            if (update <= 8) {
                sayThing(update)
            }
        } else if (game.enemyCResources.getDeckBack()?.second == true) {
            playAlarm()
        }

        if (game.enemyCResources.hand.none { it.isNuclear() } && update < 6) {
            game.enemyCResources.addOnTop(Card(Rank.THREE, Suit.HEARTS, CardBack.CHINESE, true))
        }
    }
}