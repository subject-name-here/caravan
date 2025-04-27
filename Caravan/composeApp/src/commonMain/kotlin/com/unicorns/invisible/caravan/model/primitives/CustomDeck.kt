package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.model.CardBack

class CustomDeck() {
    private val cards = ArrayList<Card>()

    constructor(back: CardBack): this() {
        addAll(CollectibleDeck(back))
    }

    val size: Int
        get() = cards.size

    operator fun get(index: Int) = cards[index]

    fun add(element: Card) = cards.add(element)
    fun addOnTop(element: Card) = cards.add(0, element)
    fun addAll(elements: CollectibleDeck) = cards.addAll(elements.toCardList())
    fun removeFirst() = cards.removeAt(0)

    private fun getEqPredicate(it: Card): (Card) -> Boolean = { c ->
        when (c) {
            is CardFaceSuited -> it is CardFaceSuited && it.rank == c.rank && it.suit == c.suit && it.cardBack == c.cardBack
            is CardJoker -> it is CardJoker && it.number == c.number && it.cardBack == c.cardBack
            is CardNumber -> it is CardNumber && it.rank == c.rank && it.suit == c.suit && it.cardBack == c.cardBack
            is CardNumberWW -> it is CardNumberWW && it.rank == c.rank && it.suit == c.suit
            is CardAtomic -> it is CardAtomic
            is CardFBomb -> it is CardFBomb
            is CardWildWasteland -> it is CardWildWasteland && it.wwType == c.wwType
        }
    }

    fun removeAll(elements: Collection<Card>) = elements.forEach { cardToRemove ->
        cards.removeAll(getEqPredicate(cardToRemove))
    }
    fun removeAll(predicate: (Card) -> Boolean) {
        cards.removeAll(predicate)
    }
    fun removeAllOnce(elements: Collection<Card>) = elements.forEach { cardToRemove ->
        val predicate = getEqPredicate(cardToRemove)
        val card = cards.filter { predicate(it) }.randomOrNull()
        cards.remove(card)
    }

    operator fun contains(card: Card): Boolean {
        return cards.any(getEqPredicate(card))
    }

    fun shuffle() = cards.shuffle()
    fun toList() = cards.toList()

    fun copy(): CustomDeck {
        val res = CustomDeck()
        for (card in cards) {
            res.add(when (card) {
                is CardFaceSuited -> CardFaceSuited(card.rank, card.suit, card.cardBack)
                is CardJoker -> CardJoker(card.number, card.cardBack)
                is CardNumber -> CardNumber(card.rank, card.suit, card.cardBack)
                is CardNumberWW -> CardNumberWW(card.rank, card.suit)
                is CardAtomic -> CardAtomic()
                is CardFBomb -> CardFBomb()
                is CardWildWasteland -> CardWildWasteland(card.wwType)
            })
        }
        return res
    }
}