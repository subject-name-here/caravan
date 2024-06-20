package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.multiplayer.MoveResponse
import kotlinx.serialization.Serializable


@Serializable
class EnemyPlayer(
    private val startDeck: CustomDeck,
) : Enemy() {
    override fun createDeck(): CResources = CResources(startDeck)
    override fun getRewardBack() = CardBack.STANDARD
    override fun isAlt() = true

    var latestMoveResponse: MoveResponse? = null

    override fun makeMove(game: Game) {
        val move = latestMoveResponse ?: throw Exception()
        when (move.moveCode) {
            1 -> {
                if (move.caravanCode !in game.enemyCaravans.indices || game.enemyCaravans[move.caravanCode].isEmpty()) {
                    game.isCorrupted = true
                    return
                }
                game.enemyCaravans[move.caravanCode].dropCaravan()
            }

            2 -> {
                if (move.handCardNumber !in game.enemyCResources.hand.indices) {
                    game.isCorrupted = true
                    return
                }
                game.enemyCResources.dropCardFromHand(move.handCardNumber)
            }

            3 -> {
                if (move.handCardNumber !in game.enemyCResources.hand.indices || move.caravanCode !in game.enemyCaravans.indices) {
                    game.isCorrupted = true
                    return
                }
                val card = game.enemyCResources.removeFromHand(move.handCardNumber)
                if (!game.enemyCaravans[move.caravanCode].canPutCardOnTop(card)) {
                    game.isCorrupted = true
                    return
                }
                game.enemyCaravans[move.caravanCode].putCardOnTop(card)
            }

            4 -> {
                if (move.handCardNumber !in game.enemyCResources.hand.indices) {
                    game.isCorrupted = true
                    return
                }
                val card = game.enemyCResources.removeFromHand(move.handCardNumber)

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
                }
                cardInCaravan.addModifier(card)
            }
        }

        if (move.newCardInHandBack != -1) {
            game.enemyCResources.addCardToHandPvP(
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