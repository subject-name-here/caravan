package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.multiplayer.MoveResponse


class EnemyPlayer(
    private val startDeck: CustomDeck,
) : Enemy {
    override fun createDeck(): CResources = CResources(startDeck)

    var latestMoveResponse: MoveResponse? = null

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        val move = latestMoveResponse ?: throw Exception()
        when (move.moveCode) {
            1 -> {
                if (move.caravanCode !in game.enemyCaravans.indices || game.enemyCaravans[move.caravanCode].isEmpty()) {
                    game.isCorrupted = true
                    return
                }
                game.enemyCaravans[move.caravanCode].dropCaravan(speed)
            }

            2 -> {
                if (move.handCardNumber !in game.enemyCResources.hand.indices) {
                    game.isCorrupted = true
                    return
                }
                game.enemyCResources.dropCardFromHand(move.handCardNumber, speed)
            }

            3 -> {
                if (move.handCardNumber !in game.enemyCResources.hand.indices || move.caravanCode !in game.enemyCaravans.indices) {
                    game.isCorrupted = true
                    return
                }
                val card = game.enemyCResources.removeFromHand(move.handCardNumber, speed)
                if (!game.enemyCaravans[move.caravanCode].canPutCardOnTop(card)) {
                    game.isCorrupted = true
                    return
                }
                game.enemyCaravans[move.caravanCode].putCardOnTop(card, speed)
            }

            4 -> {
                if (move.handCardNumber !in game.enemyCResources.hand.indices) {
                    game.isCorrupted = true
                    return
                }
                val card = game.enemyCResources.removeFromHand(move.handCardNumber, speed)

                val cardInCaravan = if (move.caravanCode < 0) {
                    val playersCaravan = 3 + move.caravanCode
                    if (
                        playersCaravan !in game.playerCaravans.indices ||
                        move.cardInCaravanNumber !in game.playerCaravans[playersCaravan].cards.indices
                    ) {
                        game.isCorrupted = true
                        return
                    }
                    game.playerCaravans[playersCaravan].cards[move.cardInCaravanNumber]
                } else {
                    if (
                        move.caravanCode !in game.enemyCaravans.indices ||
                        move.cardInCaravanNumber !in game.enemyCaravans[move.caravanCode].cards.indices
                    ) {
                        game.isCorrupted = true
                        return
                    }
                    game.enemyCaravans[move.caravanCode].cards[move.cardInCaravanNumber]
                }
                if (!cardInCaravan.canAddModifier(card)) {
                    game.isCorrupted = true
                    return
                }
                if (card.rank == Rank.JOKER) {
                    game.jokerPlayedSound()
                } else if (card.isNuclear()) {
                    game.nukeBlownSound()
                } else if (card.getWildWastelandType() != null) {
                    game.wildWastelandSound()
                }
                cardInCaravan.addModifier(card, speed)
            }
        }

        if (move.newCardInHandBack != -1) {
            game.enemyCResources.addCardToHandDirect(
                Card(
                    Rank.entries[move.newCardInHandRank],
                    Suit.entries[move.newCardInHandSuit],
                    CardBack.entries[move.newCardInHandBack],
                    isAlt = move.isNewCardAlt
                )
            )
        }
    }
}