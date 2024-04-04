package com.unicorns.invisible.caravan.model

import com.unicorns.invisible.caravan.model.primitives.Deck

class Game(val playerDeck: Deck, val enemyDeck: Deck) {

    fun startGame() {
        playerDeck.shuffle()
        enemyDeck.shuffle()
        playerDeck.initHand()
        enemyDeck.initHand()
    }
}