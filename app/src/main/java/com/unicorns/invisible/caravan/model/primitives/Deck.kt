package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


@Serializable
class Deck(private val cards: CustomDeck) {
    constructor(back: CardBack) : this(CustomDeck(back))

    val hand: MutableList<Card> = mutableListOf()

    fun getInitHand() = cards.take(8)
    fun initHand(toPutInHand: List<Card>) {
        cards.removeAll(toPutInHand.toSet())
        hand.addAll(toPutInHand)
    }

    fun addToHand() {
        if (cards.size > 0) {
            val card = cards.first()
            hand.add(card)
            cards.remove(card)
        }
    }
    fun getDeckBack() = cards.first().back

    val deckSize: Int
        get() = cards.size

    val numOfNumbers: Int
        get() = cards.count { !it.isFace() }
}