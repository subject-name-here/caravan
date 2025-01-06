package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack
import kotlinx.serialization.Serializable


@Serializable
class CResources(private val deck: CustomDeck) {
    constructor(back: CardBack, isAlt: Boolean) : this(CustomDeck(back, isAlt))

    private val handMutable: MutableList<Card> = mutableListOf()
    val hand: List<Card>
        get() = handMutable.toList()

    /**
     * Two conditions on starting hand:
     * 1) no Wild Wasteland cards in the hand
     * 2) if there is a bomb in a deck, there must be exactly one in the hand
     */
    private fun getTopHand(): List<Card> {
        val cards = deck.toList().toMutableList()
        val nuclears = cards.filter { it.isNuclear() }
        cards.removeAll { !it.isOrdinary() }

        return if (nuclears.isNotEmpty()) {
            cards.take(7) + nuclears.first()
        } else {
            cards.take(8)
        }
    }
    fun initResources(maxNumOfFaces: Int, initHand: Boolean) {
        shuffleDeck()
        if (initHand) {
            var tmpHand = getTopHand()
            while (tmpHand.count { it.isFace() } > maxNumOfFaces) {
                shuffleDeck()
                tmpHand = getTopHand()
            }

            deck.removeAllOnce(tmpHand)
            tmpHand.forEach { it.handAnimationMark = Card.AnimationMark.MOVING_IN }
            handMutable.addAll(tmpHand)
        } else {
            var tmpHand = deck.toList().take(8)
            while (tmpHand.count { it.isFace() } > maxNumOfFaces || tmpHand.any { it.isWildWasteland() }) {
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
        if (card.getWildWastelandCardType() == Card.WildWastelandCardType.CAZADOR) {
            val notSpecial = handMutable.filter { it.isOrdinary() }
            notSpecial.forEach { it.handAnimationMark = Card.AnimationMark.MOVED_OUT }
            handMutable.removeAll(notSpecial)
            repeat(notSpecial.size) {
                handMutable.add(Card(Rank.QUEEN, Suit.HEARTS, CardBack.MADNESS, true))
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

    fun getDeckBack() = deck.firstOrNull()?.let { it.back to it.isAlt }

    fun shuffleDeck() = deck.shuffle()

    val deckSize: Int
        get() = deck.size

    val numOfNumbers: Int
        get() = deck.count { !it.rank.isFace() }

    fun mutateFev(card: Card) {
        val cards = handMutable.size
        handMutable.forEach { it.handAnimationMark = Card.AnimationMark.MOVED_OUT }
        handMutable.clear()
        repeat(cards) {
            handMutable.add(Card(card.rank, card.suit, CardBack.MADNESS, true))
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
    fun moveOnTop(rank: Rank, suit: Suit) = deck.moveOnTop(rank, suit)

    fun copyFrom(cResources: CResources) {
        addNewDeck(cResources.deck.copy())
        for (card in cResources.hand) {
            handMutable.add(Card(card.rank, card.suit, card.back, card.isAlt))
        }
    }

    fun isCustomDeckValid(): Boolean {
        val numOfDecks = deck.toList().distinctBy { it.back }.size
        return numOfDecks <= MAX_NUMBER_OF_DECKS &&
                deckSize >= MIN_DECK_SIZE &&
                numOfNumbers >= MIN_NUM_OF_NUMBERS
    }

    fun isDeckCourier6(): Boolean {
        return deck.toList().all { it.rank in listOf(Rank.SIX, Rank.TEN, Rank.KING) }
    }

    companion object {
        const val MAX_NUMBER_OF_DECKS = 6
        const val MIN_DECK_SIZE = 30
        const val MIN_NUM_OF_NUMBERS = 15
    }
}