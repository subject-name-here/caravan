package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class EnemySignificantOther(
    val deck: CustomDeck,
    @Transient val speaker: (Int) -> Unit = {}
) : Enemy() {
    override fun createDeck(): CResources = CResources(deck)

    @Transient
    private val linesSaid = ArrayList<Int>()
    override fun makeMove(game: Game) {
        val playerValues = game.playerCaravans.map { it.getValue() }
        val enemyValues = game.enemyCaravans.map { it.getValue() }
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val hand = game.enemyCResources.hand

        if (checkIfPlayerNeedsHelp(game)) {
            helpPlayer(game)
            checkIfPlayerNeedsHelp(game)
            return
        }


        // Try creating a draw!
        hand.withIndex()
            .filter { !it.value.isSpecial() }
            .filter { !it.value.isFace()  }
            .sortedByDescending { it.value.rank.value }
            .forEach { (cardIndex, card) ->
                game.enemyCaravans
                    .withIndex()
                    .forEach { (otherCaravanIndex, otherCaravan) ->
                        if (
                            otherCaravan.getValue() + card.rank.value == game.playerCaravans[otherCaravanIndex].getValue() &&
                            otherCaravan.canPutCardOnTop(card)
                        ) {
                            otherCaravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
            }

        // TODO: use face cards


        // 4) only then try to put card on our caravan.
        game.enemyCaravans
            .withIndex()
            .filter { it.value.getValue() < 26 }
            .shuffled()
            .forEach { (caravanIndex, caravan) ->
                hand.withIndex().filter { !it.value.isSpecial() }.filter { !it.value.isFace() }
                    .sortedBy { it.value.rank.value }
                    .forEach { (cardIndex, card) ->
                        val futureValue = caravan.getValue() + card.rank.value
                        if (futureValue <= game.playerCaravans[caravanIndex].getValue() &&
                            futureValue <= 26 &&
                            caravan.canPutCardOnTop(card)
                        ) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
            }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.maxBy { it.getValue() }.dropCaravan()
            return
        }

        game.enemyCResources.dropCardFromHand(hand.indices.random())
    }

    private fun helpPlayer(game: Game) {
        val hand = game.enemyCResources.hand
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }

        hand.withIndex().shuffled().forEach { (cardIndex, card) ->
            if (!card.rank.isFace()) {
                game.enemyCaravans.shuffled().forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 20) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }

            if (card.rank == Rank.KING) {
                game.playerCaravans.shuffled().forEach { playerCaravan ->
                    val cardToKing = playerCaravan.cards
                        .filter {
                            it.canAddModifier(card) && playerCaravan.getValue() + it.getValue() <= 26
                        }
                        .maxByOrNull { it.getValue() }
                    if (cardToKing != null) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.maxBy { it.getValue() }.dropCaravan()
            return
        }

        game.enemyCResources.dropCardFromHand(hand.indices.random())
    }
    private fun checkIfPlayerNeedsHelp(game: Game): Boolean {
        if (1 in linesSaid) {
            return false
        }
        val playerValues = game.playerCaravans.map { it.getValue() }
        if (playerValues.any { it in (21..26) }) {
            speaker(1)
            linesSaid.add(1)
            return false
        }
        return true
    }
}