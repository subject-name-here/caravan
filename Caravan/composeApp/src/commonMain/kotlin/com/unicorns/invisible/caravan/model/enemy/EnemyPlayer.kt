package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject


class EnemyPlayer(var deckSize: Int?, val session: DefaultClientWebSocketSession) : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck())

    var isCorrupted = false

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        val moveRaw = session.incoming.receive()
        if (moveRaw !is Frame.Text) {
            isCorrupted = true
            return
        }
        val move = try {
            Json.decodeFromString<Move>(moveRaw.readText())
        } catch (e: Exception) {
            isCorrupted = true
            return
        }

        try {
            when (move.moveType) {
                0 -> {}
                1 -> {
                    val caravan = game.enemyCaravans[move.caravanNumber]
                    if (!caravan.isEmpty()) {
                        caravan.dropCaravan(speed)
                    }
                }
                else -> {
                    val cardT = game.enemyCResources.hand[move.cardNumberInHand]
                    if (move.moveType == 2) {
                        game.enemyCResources.dropCardFromHand(move.cardNumberInHand, speed)
                    } else {
                        val caravan = if (move.caravanNumber < 3) {
                            game.enemyCaravans[move.caravanNumber]
                        } else {
                            game.playerCaravans[move.caravanNumber - 3]
                        }
                        when (cardT) {
                            is CardBase -> {
                                if (caravan.canPutCardOnTop(cardT)) {
                                    caravan.putCardOnTop(game.enemyCResources.removeFromHand(move.cardNumberInHand, speed) as CardBase, speed)
                                } else {
                                    isCorrupted = true
                                    return
                                }
                            }
                            is CardModifier -> {
                                val cardInCaravan = caravan.cards[move.cardInCaravanNumber]
                                if (cardInCaravan.canAddModifier(cardT)) {
                                    cardInCaravan.addModifier(game.enemyCResources.removeFromHand(move.cardNumberInHand, speed) as CardModifier, speed)
                                } else {
                                    isCorrupted = true
                                    return
                                }
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {
            isCorrupted = true
        }

        try {
            val ds = deckSize!!
            if (move.moveType in listOf(2, 3) && ds > 0) {
                val newCard = Json.decodeFromString<Card>(move.newCardInHand)
                game.enemyCResources.addCardToHandDirect(newCard)
                deckSize!!.dec()
                game.enemyCResources.recomposeResources++
            }
        } catch (e: Exception) {
            isCorrupted = true
        }
    }
}


@Serializable
data class Move(
    // 1 - drop caravan, 2 - drop card, 3 - put card in caravan; 0 - take card from deck (used only before init stage)
    val moveType: Int,
    val cardNumberInHand: Int,
    val caravanNumber: Int,
    val cardInCaravanNumber: Int,
    val newCardInHand: String
)