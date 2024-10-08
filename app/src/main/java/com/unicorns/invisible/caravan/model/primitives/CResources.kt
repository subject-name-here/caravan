package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


@Serializable
class CResources(private val deck: CustomDeck) {
    constructor(back: CardBack, isAlt: Boolean) : this(CustomDeck(back, isAlt))

    private val handMutable: MutableList<Card> = mutableListOf()
    val hand
        get() = handMutable.toList()

    /**
     * Two conditions on starting hand:
     * 1) no Wild Wasteland cards in the hand
     * 2) if there is a bomb in a deck, there should be one in the hand
     */
    private fun getTopHand(): List<Card> {
        val cards = deck.toList().toMutableList()
        cards.removeAll { it.back == CardBack.WILD_WASTELAND && !it.isAlt && it.getWildWastelandCardType() != null }

        val bomb = cards.find { (it.back == CardBack.WILD_WASTELAND || it.back == CardBack.UNPLAYABLE) && it.isAlt }
        return if (bomb != null) {
            (cards - bomb).take(7) + bomb
        } else {
            cards.take(8)
        }
    }
    fun initResources(maxNumOfFaces: Int, initHand: Boolean = true) {
        shuffleDeck()
        if (initHand) {
            var tmpHand = getTopHand()
            while (tmpHand.count { it.isFace() } > maxNumOfFaces) {
                shuffleDeck()
                tmpHand = getTopHand()
            }

            deck.removeAll(tmpHand)
            tmpHand.forEach { it.handAnimationMark = Card.AnimationMark.MOVING_IN }
            handMutable.addAll(tmpHand)
        } else {
            var tmpHand = deck.toList().take(8)
            while (
                tmpHand.count { it.isFace() } > maxNumOfFaces ||
                tmpHand.any { it.isSpecial() && !it.isAlt }
            ) {
                shuffleDeck()
                tmpHand = deck.toList().take(8)
            }
        }
    }

    private fun addCardToHand(card: Card) {
        card.handAnimationMark = Card.AnimationMark.MOVING_IN
        handMutable.add(card)
    }

    fun addToHand() {
        if (deck.size == 0) {
            return
        }
        val card = deck.removeFirst()
        addCardToHand(card)
        processHandAddedCard(card)
    }

    fun addCardToHandPvPInit(): Card? {
        if (deck.size == 0) {
            return null
        }
        val card = deck.removeFirst()
        addCardToHand(card)
        return card
    }

    fun addCardToHandDirect(card: Card) {
        if (deck.size > 0) {
            deck.removeFirst()
        }
        addCardToHand(card)
        processHandAddedCard(card)
    }

    private fun processHandAddedCard(card: Card) {
        if (card.back == CardBack.WILD_WASTELAND && !card.isAlt) {
            if (card.getWildWastelandCardType() == Card.WildWastelandCardType.CAZADOR) {
                val notSpecial = handMutable.filter { !it.isSpecial() }
                notSpecial.forEach { it.handAnimationMark = Card.AnimationMark.MOVING_OUT }
                handMutable.removeAll(notSpecial)
                repeat(notSpecial.size) {
                    handMutable.add(Card(Rank.QUEEN, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
                }
            }
        }
    }

    fun removeFromHand(index: Int): Card {
        handMutable[index].handAnimationMark = Card.AnimationMark.MOVING_OUT
        return handMutable.removeAt(index)
    }

    fun dropCardFromHand(index: Int) {
        handMutable[index].handAnimationMark = Card.AnimationMark.MOVING_OUT_ALT
        handMutable.removeAt(index)
    }

    fun getDeckBack() = deck.firstOrNull()?.run { this.back to this.isAlt }

    var canBeShuffled = true
    fun shuffleDeck() {
        if (canBeShuffled) deck.shuffle()
    }

    val deckSize: Int
        get() = deck.size

    val numOfNumbers: Int
        get() = deck.count { !it.rank.isFace() }

    fun mutateFev(card: Card) {
        val cards = handMutable.size
        handMutable.forEach { it.handAnimationMark = Card.AnimationMark.MOVED_OUT }
        handMutable.clear()
        repeat(cards) {
            handMutable.add(Card(card.rank, card.suit, CardBack.WILD_WASTELAND, false))
        }
    }

    fun addNewDeck(newDeck: CustomDeck) {
        newDeck.toList().reversed().forEach {
            deck.addOnTop(it)
        }
    }
    fun addOnTop(card: Card) {
        deck.addOnTop(card)
    }

    fun copyFrom(cResources: CResources) {
        addNewDeck(cResources.deck.copy())
        for (card in cResources.hand) {
            handMutable.add(Card(card.rank, card.suit, card.back, card.isAlt))
        }
    }
}