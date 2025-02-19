package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable


@Serializable
data object EnemyTheManInTheMirror : EnemyPve {
    override fun getNameId() = R.string.man_in_the_mirror
    override fun isEven() = false

    override fun createDeck() = CResources(CustomDeck())


    override fun getBank(): Int {
        return 0
    }

    override fun refreshBank() {

    }

    override fun getBet(): Int? {
        return 0
    }

    override fun retractBet() {

    }

    override fun addReward(reward: Int) {

    }

    override fun makeMove(game: Game) {
        if (game.isInitStage()) {
            val caravan = game.enemyCaravans.withIndex().find {
                it.value.isEmpty() && !game.playerCaravans[it.index].isEmpty()
            }!!
            val card = game.playerCaravans[caravan.index].cards.first().card
            val cardInHand = game.enemyCResources.hand.find { it.rank == card.rank && it.suit == card.suit }
            val cardInHandIndex = game.enemyCResources.hand.indexOf(cardInHand)
            caravan.value.putCardOnTop(game.enemyCResources.removeFromHand(cardInHandIndex))
            return
        }
        EnemyFrank.makeMove(game)
    }
}