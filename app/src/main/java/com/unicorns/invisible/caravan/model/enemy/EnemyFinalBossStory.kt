package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.utils.checkMoveOnImminentVictory
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


    private fun makeMoveInner(game: Game) {
        fun blowUpTheBomb(card: CardWithModifier, bombIndex: Int) {
            game.nukeBlownSound()
            card.addModifier(game.enemyCResources.removeFromHand(bombIndex))
        }

        val hand = game.enemyCResources.hand

        game.enemyCaravans.withIndex().forEach {
            val isWinningMovePossible = checkMoveOnImminentVictory(game, it.index)
            val rivalCaravanValue = game.playerCaravans[it.index].getValue()
            val lowerBound = (rivalCaravanValue + 1).coerceIn(21..26)
            if (isWinningMovePossible) {
                val jack = hand.withIndex().find { card -> card.value.rank == Rank.JACK }
                if (jack != null) {
                    it.value.cards
                        .filter { card -> card.canAddModifier(jack.value) }
                        .sortedBy { card -> card.getValue() }
                        .forEach { card ->
                            if (it.value.getValue() - card.getValue() in (lowerBound..26)) {
                                card.addModifier(game.enemyCResources.removeFromHand(jack.index))
                                return
                            }
                        }
                }
                val king = hand.withIndex().find { card -> card.value.rank == Rank.KING }
                if (king != null) {
                    it.value.cards
                        .filter { card -> card.canAddModifier(king.value) }
                        .sortedBy { card -> card.getValue() }
                        .forEach { card ->
                            if (it.value.getValue() + card.getValue() in (lowerBound..26)) {
                                card.addModifier(game.enemyCResources.removeFromHand(king.index))
                                return
                            }
                        }
                }
                hand.withIndex()
                    .filter { card -> !card.value.isFace() }
                    .forEach { (cardIndex, card) ->
                        if (it.value.getValue() + card.rank.value in (lowerBound..26) && it.value.canPutCardOnTop(card)) {
                            it.value.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
            }
        }

        val playerPerfectCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }.size
        val playerOverWeightCaravans = game.playerCaravans.filter { it.getValue() > 26 }.size
        val dangerousCaravans = game.playerCaravans.filter { it.getValue() in 11..20 }.size
        val caravansScore = playerPerfectCaravans + playerOverWeightCaravans + dangerousCaravans
        if (caravansScore >= 2 || hand.filter { it.isNuclear() }.size >= 2) {
            hand.withIndex()
                .filter { it.value.isNuclear() }
                .forEach { (bombIndex, bomb) ->
                    val cards = game.enemyCaravans
                        .sortedByDescending { if (it.getValue() > 26) 0 else it.getValue() }
                        .flatMap { it.cards }
                        .filter { it.canAddModifier(bomb) }
                    if (cards.isNotEmpty()) {
                        blowUpTheBomb(cards.first(), bombIndex)
                        return
                    } else {
                        val playerCards = game.playerCaravans.flatMap { it.cards }.filter { it.canAddModifier(bomb) }
                        if (playerCards.isNotEmpty()) {
                            blowUpTheBomb(playerCards.random(), bombIndex)
                            return
                        }
                    }
                }
        }

        // TODO: make move
    }
}