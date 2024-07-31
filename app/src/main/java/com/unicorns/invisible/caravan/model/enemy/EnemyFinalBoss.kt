package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJoker
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class EnemyFinalBoss : Enemy() {
    private var update = 0
    private fun createCustomDeck(update: Int): CustomDeck {
        val deck = CustomDeck(CardBack.UNPLAYABLE, false)
        return when (update) {
            0 -> {
                deck.removeAll(deck.toList().filter { it.rank.value !in listOf(1, 2, 3, 4, 5, 6) })
                deck.addOnTop(Card(Rank.ACE, Suit.DIAMONDS, CardBack.UNPLAYABLE, true))
                deck.shuffle()
                deck.addOnTop(Card(Rank.ACE, Suit.HEARTS, CardBack.UNPLAYABLE, true))
                deck
            }
            1 -> {
                deck.removeAll(deck.toList().filter { it.rank.value in listOf(9, 10, 12, 14) })
                deck.addOnTop(Card(Rank.ACE, Suit.CLUBS, CardBack.UNPLAYABLE, true))
                deck.shuffle()
                deck.addOnTop(Card(Rank.ACE, Suit.HEARTS, CardBack.UNPLAYABLE, true))
                deck
            }
            2 -> {
                deck.removeAll(deck.toList().filter { it.rank.value in listOf(12, 14) })
                deck.addOnTop(Card(Rank.ACE, Suit.CLUBS, CardBack.UNPLAYABLE, true))
                deck.shuffle()
                deck.addOnTop(Card(Rank.ACE, Suit.HEARTS, CardBack.UNPLAYABLE, true))
                deck
            }
            3 -> {
                deck.removeAll(deck.toList().filter { it.rank == Rank.QUEEN })
                deck.addOnTop(Card(Rank.ACE, Suit.SPADES, CardBack.UNPLAYABLE, true))
                deck.addOnTop(Card(Rank.ACE, Suit.DIAMONDS, CardBack.UNPLAYABLE, true))
                deck.shuffle()
                deck.addOnTop(Card(Rank.ACE, Suit.HEARTS, CardBack.UNPLAYABLE, true))
                deck
            }
            in (4..6) -> {
                deck.removeAll(deck.toList().filter { it.rank == Rank.QUEEN })
                deck.addOnTop(Card(Rank.ACE, Suit.HEARTS, CardBack.UNPLAYABLE, true))
                deck.shuffle()
                deck.addOnTop(Card(Rank.ACE, Suit.CLUBS, CardBack.UNPLAYABLE, true))
                deck.addOnTop(Card(Rank.ACE, Suit.DIAMONDS, CardBack.UNPLAYABLE, true))
                deck.addOnTop(Card(Rank.ACE, Suit.SPADES, CardBack.UNPLAYABLE, true))
                deck
            }
            7 -> {
                deck.addOnTop(Card(Rank.ACE, Suit.HEARTS, CardBack.UNPLAYABLE, true))
                deck.addOnTop(Card(Rank.ACE, Suit.CLUBS, CardBack.UNPLAYABLE, true))
                deck.addOnTop(Card(Rank.ACE, Suit.DIAMONDS, CardBack.UNPLAYABLE, true))
                deck.addOnTop(Card(Rank.ACE, Suit.SPADES, CardBack.UNPLAYABLE, true))
                deck.shuffle()
                deck.addOnTop(Card(Rank.TWO, Suit.CLUBS, CardBack.UNPLAYABLE, true))
                deck.addOnTop(Card(Rank.TWO, Suit.DIAMONDS, CardBack.UNPLAYABLE, true))
                deck.addOnTop(Card(Rank.TWO, Suit.SPADES, CardBack.UNPLAYABLE, true))
                deck
            }
            8 -> {
                deck.addOnTop(Card(Rank.ACE, Suit.HEARTS, CardBack.UNPLAYABLE, true))
                deck.addOnTop(Card(Rank.ACE, Suit.CLUBS, CardBack.UNPLAYABLE, true))
                deck.addOnTop(Card(Rank.ACE, Suit.DIAMONDS, CardBack.UNPLAYABLE, true))
                deck.addOnTop(Card(Rank.ACE, Suit.SPADES, CardBack.UNPLAYABLE, true))
                deck.shuffle()
                deck.addOnTop(Card(Rank.TWO, Suit.CLUBS, CardBack.UNPLAYABLE, true))
                deck
            }
            else -> {
                CustomDeck()
            }
        }
    }

    override fun createDeck(): CResources = CResources(createCustomDeck(0)).apply {
        canBeShuffled = false
    }
    override fun getRewardBack() = null

    @Transient
    var playAlarm: () -> Unit = {}
    @Transient
    var sayThing: (Int) -> Unit = {}

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.shuffled().filter { !it.isSpecial() }.filter { !it.isFace() }.minBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        makeMoveInner(game)

        if (game.enemyCResources.deckSize == 0) {
            playAlarm()
            game.enemyCResources.addNewDeck(createCustomDeck(++update))
            sayThing(update)
        } else if (game.enemyCResources.getDeckBack()?.second == true) {
            playAlarm()
        }

        if (game.playerCResources.deckSize == 0 && update == 0) {
            sayThing(10)
            CardBack.classicDecks.forEach { back ->
                game.playerCResources.addNewDeck(CustomDeck(back, false))
            }
            game.playerCResources.shuffleDeck()
        }
    }

    private fun makeMoveInner(game: Game) {
        fun blowUpTheBomb(card: CardWithModifier, bombIndex: Int) {
            game.nukeBlownSound()
            card.addModifier(game.enemyCResources.removeFromHand(bombIndex))

            if (game.enemyCResources.hand.none { it.isSpecial() } && update < 9) {
                game.enemyCResources.addOnTop(Card(Rank.THREE, Suit.HEARTS, CardBack.UNPLAYABLE, true))
            }
        }

        fun isCaravanDangerous(caravanValue: Int, cardValue: Int): Boolean {
            return caravanValue in (21..26) || caravanValue + cardValue in (21..26) || caravanValue - cardValue in (21..26)
        }
        val hand = game.enemyCResources.hand
        val dangerousCaravans = game.enemyCaravans.filter { it.cards.any { card ->
            isCaravanDangerous(it.getValue(), card.getValue())
        } }
        val playerPerfectCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }
        val playerOverWeightCaravans = game.playerCaravans.filter { it.getValue() > 26 }

        if (playerPerfectCaravans.size + playerOverWeightCaravans.size >= 2 || hand.filter { it.isSpecial() }.size >= 2) {
            hand.withIndex()
                .filter { it.value.isSpecial() }
                .forEach { (bombIndex, bomb) ->
                    val cards = game.enemyCaravans.flatMap { it.cards }.filter { it.canAddModifier(bomb) }
                    if (cards.isNotEmpty()) {
                        blowUpTheBomb(cards.random(), bombIndex)
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

        if (update >= 7) {
            game.enemyCaravans.withIndex().forEach {
                val isWinningMovePossible = checkMoveOnPossibleVictory(game, it.index)
                val lowerBound = 21
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

            val isLosing = (0..2).count { game.playerCaravans[it].getValue() >= 11 } >= 1
            if (isLosing) {
                hand.withIndex()
                    .filter { it.value.isSpecial() }
                    .forEach { (bombIndex, bomb) ->
                        val cards = game.enemyCaravans.flatMap { it.cards }.filter { it.canAddModifier(bomb) }
                        if (cards.isNotEmpty()) {
                            blowUpTheBomb(cards.random(), bombIndex)
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
        }

        when (update) {
            7 -> {
                EnemySecuritron38.makeMove(game)
                return
            }
            8 -> {
                EnemyCaesar.makeMove(game)
                return
            }
        }


        (playerPerfectCaravans + playerOverWeightCaravans).forEach { caravan ->
            hand.withIndex()
                .filter { !it.value.isSpecial() }
                .filter { it.value.rank == Rank.KING }
                .forEach { (kingIndex, king) ->
                    caravan.cards.withIndex()
                        .filter { it.value.canAddModifier(king) }
                        .sortedByDescending { it.value.getValue() }
                        .forEach {
                            if (caravan.getValue() + it.value.getValue() > 26) {
                                it.value.addModifier(game.enemyCResources.removeFromHand(kingIndex))
                                return
                            }
                        }
                }

            if (StrategyJoker.move(game)) {
                game.jokerPlayedSound()
                return
            }

            hand.withIndex()
                .filter { !it.value.isSpecial() }
                .filter { it.value.rank == Rank.JACK }
                .forEach { (jackIndex, jack) ->
                    caravan.cards.withIndex()
                        .filter { it.value.canAddModifier(jack) }
                        .sortedByDescending { it.value.getValue() }
                        .forEach {
                            if (caravan.getValue() - it.value.getValue() < 21) {
                                it.value.addModifier(game.enemyCResources.removeFromHand(jackIndex))
                                return
                            }
                        }
                }
        }

        if (dangerousCaravans.isNotEmpty()) {
            val caravan = dangerousCaravans.random()
            caravan.dropCaravan()
            return
        }

        hand.withIndex()
            .filter { !it.value.isFace() }
            .forEach { (cardIndex, card) ->
                game.enemyCaravans.shuffled().forEach { caravan ->
                    if (caravan.canPutCardOnTop(card)) {
                        if (!isCaravanDangerous(caravan.getValue() + card.rank.value, card.rank.value)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }

        game.playerCaravans.forEach { playerCaravan ->
            hand.withIndex().filter { it.value.rank == Rank.QUEEN }.forEach { (cardIndex, card) ->
                if (playerCaravan.size >= 2) {
                    val last = playerCaravan.cards.last().card.rank.value
                    val preLast = playerCaravan.cards[playerCaravan.cards.lastIndex - 1].card.rank.value
                    if (playerCaravan.cards.last().canAddModifier(card)) {
                        val isRev = playerCaravan.cards.last().isQueenReversingSequence()
                        val isAscending = preLast < last && !isRev || preLast > last && isRev
                        if (isAscending && last <= 4) {
                            playerCaravan.cards.last().addModifier(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                        val isDescending = preLast > last && !isRev || preLast < last && isRev
                        if (isDescending && last >= 8) {
                            playerCaravan.cards.last().addModifier(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }
        }


        game.enemyCResources.dropCardFromHand(hand.withIndex().minByOrNull { (_, cardInHand) ->
            if (cardInHand.isSpecial())
                15
            else when (cardInHand.rank) {
                Rank.ACE -> 4
                Rank.TWO -> 3
                Rank.THREE -> 3
                Rank.FOUR -> 4
                Rank.FIVE -> 5
                Rank.SIX -> 5
                Rank.SEVEN -> 6
                Rank.EIGHT -> 6
                Rank.NINE -> 7
                Rank.TEN -> 8
                Rank.JACK -> 12
                Rank.QUEEN -> 6
                Rank.KING -> 13
                Rank.JOKER -> 14
            }
        }!!.index)
    }
}