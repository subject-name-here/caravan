package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit


data object EnemyFrank : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.ENCLAVE, false).apply {
        add(Card(Rank.ACE, Suit.HEARTS, CardBack.ENCLAVE, true))
        add(Card(Rank.ACE, Suit.CLUBS, CardBack.ENCLAVE, true))
        add(Card(Rank.ACE, Suit.DIAMONDS, CardBack.ENCLAVE, true))
    })

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}
