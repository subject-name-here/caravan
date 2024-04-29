package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


@Serializable
class CResources(private val deck: CustomDeck) {
    constructor(back: CardBack) : this(CustomDeck(back))

    private val handMutable: MutableList<Card> = mutableListOf()
    val hand
        get() = handMutable.toList()

    fun getInitHand() = deck.takeRandom(8)
    fun initHand(toPutInHand: List<Card>) {
        deck.removeAll(toPutInHand)
        handMutable.addAll(toPutInHand)
    }

    fun addToHand() {
        if (deck.size > 0) {
            handMutable.add(deck.removeFirst())
        }
    }

    fun removeFromHand(index: Int) = handMutable.removeAt(index)

    fun getDeckBack() = deck.firstOrNull()?.back
    fun shuffleDeck() = deck.shuffle()

    val deckSize: Int
        get() = deck.size

    val numOfNumbers: Int
        get() = deck.count { !it.isFace() }
}