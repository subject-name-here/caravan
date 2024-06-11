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

    fun getTopHand() = deck.toList().take(8)
    fun initHand(toPutInHand: List<Card>) {
        deck.removeAll(toPutInHand)
        handMutable.addAll(toPutInHand)
    }

    fun addToHand() {
        if (deck.size > 0) {
            handMutable.add(deck.removeFirst())
        }
    }
    fun addToHandR(): Card? {
        if (deck.size == 0) {
            return null
        }
        val card = deck.removeFirst()
        handMutable.add(card)
        return card
    }
    fun addCardToHandPvP(card: Card) {
        if (deck.size > 0) {
            deck.removeFirst()
        }
        handMutable.add(card)
    }

    @Transient
    var onRemoveFromHand: () -> Unit = {}
    fun removeFromHand(index: Int): Card {
        onRemoveFromHand()
        return handMutable.removeAt(index)
    }

    @Transient
    var onDropCardFromHand: () -> Unit = {}
    fun dropCardFromHand(index: Int) {
        onRemoveFromHand()
        onDropCardFromHand()
        handMutable.removeAt(index)
    }

    fun getDeckBack() = deck.firstOrNull()?.run { this.back to this.isAlt }
    fun shuffleDeck() = deck.shuffle()

    val deckSize: Int
        get() = deck.size

    val numOfNumbers: Int
        get() = deck.count { !it.isFace() }

    fun copyFrom(resources: CResources) {
        handMutable.clear()
        handMutable.addAll(resources.handMutable)
        deck.clear()
        resources.deck.toList().forEach {
            deck.add(it.copy())
        }
    }

    fun getDeckCopy(): List<Card> = deck.toList()
}