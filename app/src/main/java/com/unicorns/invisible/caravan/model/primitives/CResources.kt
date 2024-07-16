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
        val cards = deck.toList().toMutableList()
        cards.removeAll { it.back == CardBack.WILD_WASTELAND && !it.isAlt && it.getWildWastelandCardType() != null }

        val bomb = cards.find { (it.back == CardBack.WILD_WASTELAND || it.back == CardBack.UNPLAYABLE) && it.isAlt }
        return if (bomb != null) {
            (cards - bomb).take(7) + bomb
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
            processHandAddedCard(card)
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
    var canBeShuffled = true
    fun shuffleDeck() {
        if (canBeShuffled) deck.shuffle()
    }

    val deckSize: Int
        get() = deck.size

    val numOfNumbers: Int
        get() = deck.count { !it.isFace() }

    fun mutateFev(card: Card) {
        val cards = handMutable.size
        handMutable.forEach { it.handAnimationMark = Card.AnimationMark.MOVING_OUT }
        handMutable.clear()
        repeat(cards) {
            handMutable.add(Card(card.rank, card.suit, CardBack.WILD_WASTELAND, false))
        }
    }

    fun addNewDeck(newDeck: CustomDeck) {
        newDeck.toList().forEach {
            deck.addOnTop(it)
        }
    }
    fun addOnTop(card: Card) {
        deck.addOnTop(card)
    }
}