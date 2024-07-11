package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class CResources(private val deck: CustomDeck) {
    constructor(back: CardBack, isAlt: Boolean) : this(CustomDeck(back, isAlt))

    private val handMutable: MutableList<Card> = mutableListOf()
    val hand
        get() = handMutable.toList()

    fun getTopHand(): List<Card> {
        val cards = deck.toList()
        return if (cards.any { it.rank == Rank.JOKER && it.suit == Suit.SPADES }) {
            val card = cards.find { it.rank == Rank.JOKER && it.suit == Suit.SPADES }!!
            (cards - card).take(7) + card
        } else {
            cards.take(8)
        }
    }
    fun initHand(toPutInHand: List<Card>) {
        deck.removeAll(toPutInHand)
        toPutInHand.forEach { it.handAnimationMark = Card.AnimationMark.MOVING_IN }
        handMutable.addAll(toPutInHand)
    }

    fun addToHand() {
        if (deck.size > 0) {
            val card = deck.removeFirst()
            card.handAnimationMark = Card.AnimationMark.MOVING_IN
            handMutable.add(card)
        }
    }

    fun addToHandR(): Card? {
        if (deck.size == 0) {
            return null
        }
        val card = deck.removeFirst()
        card.handAnimationMark = Card.AnimationMark.MOVING_IN
        handMutable.add(card)
        return card
    }

    fun addCardToHandPvP(card: Card) {
        if (deck.size > 0) {
            deck.removeFirst()
        }
        card.handAnimationMark = Card.AnimationMark.MOVING_IN
        handMutable.add(card)
    }

    @Transient
    var onRemoveFromHand: () -> Unit = {}
    fun removeFromHand(index: Int): Card {
        onRemoveFromHand()
        handMutable[index].handAnimationMark = Card.AnimationMark.MOVING_OUT
        return handMutable.removeAt(index)
    }

    @Transient
    var onDropCardFromHand: () -> Unit = {}
    fun dropCardFromHand(index: Int) {
        onRemoveFromHand()
        onDropCardFromHand()
        handMutable[index].handAnimationMark = Card.AnimationMark.MOVING_OUT
        handMutable.removeAt(index)
    }

    fun getDeckBack() = deck.firstOrNull()?.run { this.back to this.isAlt }
    fun shuffleDeck() = deck.shuffle()

    val deckSize: Int
        get() = deck.size

    val numOfNumbers: Int
        get() = deck.count { !it.isFace() }
}