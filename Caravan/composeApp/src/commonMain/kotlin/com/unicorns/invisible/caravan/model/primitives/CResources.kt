package com.unicorns.invisible.caravan.model.primitives

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playWWSound
import kotlinx.coroutines.delay
import kotlin.random.Random


class CResources(private val deck: CustomDeck) {
    constructor(back: CardBack) : this(CustomDeck(back))

    var recomposeResources by mutableIntStateOf(0)

    private val handMutable: MutableList<Card> = mutableListOf()
    val hand: List<Card>
        get() = handMutable.toList()

    /**
     * Two conditions on starting hand:
     * 1) no Wild Wasteland cards in the hand
     * 2) if there is a bomb in a deck, there must be exactly one in the hand
     */
    private fun getTopHand(facesLimitExcluded: Int = 6): List<Card> {
        val cards = deck.toList().toMutableList()
        cards.removeAll { it is CardWildWasteland }
        val nuclears = cards.filter { it is CardNuclear }
        val faces = cards.filter { it is CardFace }
        val numbers = cards.filter { it is CardBase }

        val startingHand = mutableListOf<Card>()
        if (nuclears.isNotEmpty()) {
            startingHand.add(nuclears.random())
            startingHand.addAll(faces.take(Random.nextInt(0, facesLimitExcluded - 1)))
        } else {
            startingHand.addAll(faces.take(Random.nextInt(0, facesLimitExcluded)))
        }

        val remaining = 8 - startingHand.size
        startingHand.addAll(numbers.take(remaining))

        return startingHand
    }
    fun initResources() {
        shuffleDeck()
        val tmpHand = getTopHand()
        deck.removeAllOnce(tmpHand)
        tmpHand.shuffled().forEach { addCardToHand(it) }
    }
    fun initResourcesPvP() {
        shuffleDeck()
        val tmpHand = getTopHand()
        deck.removeAllOnce(tmpHand)
        tmpHand.forEach {
            deck.addOnTop(it)
        }
    }

    private fun addCardToHand(card: Card) {
        handMutable.add(card)
        recomposeResources++
    }

    fun addToHand() {
        if (deck.size == 0) {
            return
        }
        val card = deck.removeFirst()
        addCardToHand(card)
        processHandAddedCard(card)
    }

    fun addToHandPvP(): Card? {
        if (deck.size == 0 || hand.size >= 5) {
            return null
        }
        val card = deck.removeFirst()
        addCardToHand(card)
        processHandAddedCard(card)
        return card
    }

    fun addCardToHandDirect(card: Card) {
        addCardToHand(card)
        processHandAddedCard(card)
    }

    private fun processHandAddedCard(card: Card) {
        if (card is CardWildWasteland && card.wwType == WWType.CAZADOR) {
            val notSpecial = handMutable.filter { it is CardBase || it is CardFace }
            notSpecial.forEach { it.handAnimationMark = Card.AnimationMark.MOVED_OUT }
            handMutable.removeAll(notSpecial)
            repeat(notSpecial.size) {
                handMutable.add(CardWildWasteland(WWType.CAZADOR))
            }
            recomposeResources++
        }
    }

    suspend fun removeFromHand(index: Int, speed: AnimationSpeed): Card {
        if (handMutable[index] is CardJoker) {
            playJokerSounds()
        } else if (handMutable[index] is CardWildWasteland) {
            playWWSound()
        }
        handMutable[index].handAnimationMark = Card.AnimationMark.MOVING_OUT
        delay(speed.delay)
        val removedCard = handMutable.removeAt(index)
        recomposeResources++
        return removedCard
    }

    suspend fun dropCardFromHand(index: Int, speed: AnimationSpeed) {
        handMutable[index].handAnimationMark = Card.AnimationMark.MOVING_OUT_ALT
        delay(speed.delay)
        handMutable.removeAt(index)
        recomposeResources++
    }

    fun shuffleDeck() = deck.shuffle()

    val deckSize: Int
        get() = deck.size
    fun getDeckBack() = deck.toList().firstOrNull()

    fun mutateFev(card: CardBase) {
        val handSize = handMutable.size
        handMutable.forEach { it.handAnimationMark = Card.AnimationMark.MOVED_OUT }
        handMutable.clear()
        repeat(handSize) {
            handMutable.add(CardNumberWW(card.rank, card.suit))
        }
        recomposeResources++
    }

    fun addNewDeck(newDeck: CustomDeck) {
        newDeck.toList().reversed().forEach { deck.addOnTop(it) }
    }
    fun addOnTop(card: Card) = deck.addOnTop(card)

    fun copyFrom(cResources: CResources) {
        addNewDeck(cResources.deck.copy())
        for (card in cResources.hand) {
            handMutable.add(when (card) {
                is CardNumber -> CardNumber(card.rank, card.suit, card.cardBack)
                is CardNumberWW -> CardNumberWW(card.rank, card.suit)
                is CardFaceSuited -> CardFaceSuited(card.rank, card.suit, card.cardBack)
                is CardJoker -> CardJoker(card.number, card.cardBack)
                is CardAtomic -> CardAtomic()
                is CardFBomb -> CardFBomb()
                is CardWildWasteland -> CardWildWasteland(card.wwType)
            })
        }
    }

    companion object {
        const val MAX_NUMBER_OF_DECKS = 6
        const val MIN_DECK_SIZE = 30
        const val MIN_NUM_OF_NUMBERS = 15
    }
}