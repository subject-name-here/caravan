package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class EnemyStory6(@Transient val showMessage: (Int) -> Unit = {}) : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.STANDARD, false))
    override fun getRewardBack() = null

    // TODO: bug.

    private fun getHash(game: Game): Int {
        val enemyHash = game.enemyCaravans
            .flatMap { it.cards }
            .flatMap { listOf(it.card) + it.modifiersCopy() }
            .sumOf { it.hashCode() }
        val playerHash = game.playerCaravans
            .flatMap { it.cards }
            .flatMap { listOf(it.card) + it.modifiersCopy() }
            .sumOf { it.hashCode() }
        return enemyHash + playerHash
    }
    private fun updateHash(game: Game) {
        caravansHash = getHash(game)
    }

    var shownMessage = false
    private var caravansHash = 0
    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            when (hand.size) {
                8 -> {
                    showMessage(1)
                }
                6 -> {
                    showMessage(2)
                }
            }
            val cardIndex = hand.withIndex().filter { !it.value.isFace() }.random().index
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
            updateHash(game)
            return
        }
        if (!shownMessage) {
            if (getHash(game) != caravansHash) {
                shownMessage = true
                showMessage(3)
            }
        } else {
            if (game.enemyCResources.deckSize == 0) {
                game.enemyCResources.addNewDeck(CustomDeck(CardBack.STANDARD, false))
            }
        }

        EnemyCaesar.makeMove(game)

        updateHash(game)
    }
}