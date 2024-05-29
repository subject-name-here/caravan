package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString


@Serializable
data object EnemySecuritron38 : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.LUCKY_38, true).apply {
        removeAll(toList().filter { it.rank.value < 5 && it.rank.value != Rank.QUEEN.value })
    })
    override fun getRewardBack() = CardBack.LUCKY_38
    override fun isAlt(): Boolean {
        return true
    }

    override suspend fun makeMove(game: Game) {
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val playersReadyCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        hand.withIndex().sortedBy { -it.value.rank.value }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.KING) {
                if (playersReadyCaravans.isNotEmpty()) {
                    val caravanToOverweight = playersReadyCaravans.maxBy { it.getValue() }
                    val cardToKing = caravanToOverweight.cards.maxBy { it.getValue() }
                    if (caravanToOverweight.getValue() + cardToKing.getValue() > 26 && cardToKing.canAddModifier(card)) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
                game.enemyCaravans.forEach { enemyCaravan ->
                    enemyCaravan.cards.sortedBy { -it.card.rank.value }.forEach { caravanCard ->
                        if (enemyCaravan.getValue() + caravanCard.getValue() in (21..26) && caravanCard.canAddModifier(card)) {
                            caravanCard.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }

            if (card.rank == Rank.JACK) {
                if (playersReadyCaravans.isNotEmpty()) {
                    val caravanToAttack = playersReadyCaravans.maxBy { it.getValue() }
                    val cardToJack = caravanToAttack.cards.maxBy { it.getValue() }
                    if (cardToJack.canAddModifier(card)) {
                        cardToJack.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }

                if (overWeightCaravans.isNotEmpty()) {
                    val enemyCaravan = overWeightCaravans.minBy { it.getValue() }
                    val cardToDelete = enemyCaravan.cards.maxBy { it.getValue() }
                    if (cardToDelete.canAddModifier(card)) {
                        cardToDelete.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedBy { -it.getValue() }.forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }

            if (card.rank == Rank.JOKER) {
                val cards = (game.playerCaravans + game.enemyCaravans).flatMap { it.cards }
                val gameCopyString = json.encodeToString(game)
                cards.forEach { potentialCardToJoker ->
                    val gameCopy = json.decodeFromString<Game>(gameCopyString)
                    val cardInCopy = (gameCopy.playerCaravans + gameCopy.enemyCaravans).flatMap { it.cards }.find {
                        potentialCardToJoker.card.rank == it.card.rank && potentialCardToJoker.card.suit == it.card.suit
                    }
                    if (cardInCopy?.canAddModifier(card) == true) {
                        val overWeightCaravansCopy = gameCopy.enemyCaravans.filter { it.getValue() > 26 }
                        val playersReadyCaravansCopy = gameCopy.playerCaravans.filter { it.getValue() in (21..26) }
                        if (overWeightCaravansCopy.size < overWeightCaravans.size || playersReadyCaravansCopy.size < playersReadyCaravans.size) {
                            if (potentialCardToJoker.canAddModifier(card)) {
                                potentialCardToJoker.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                                return
                            }
                        }
                    }
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.maxBy { it.getValue() }.dropCaravan()
            return
        }

        game.enemyCResources.removeFromHand(hand.indices.random())
    }
}