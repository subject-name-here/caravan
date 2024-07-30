package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit


class EnemySignificantOther(
    val playerCResources: CResources,
    val speaker: (String, String) -> Unit
) : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        while (size < playerCResources.deckSize) {
            add(Card(Rank.entries.random(), Suit.entries.random(), CardBack.playableBacks.random(), false))
        }
    })

    override fun makeMove(game: Game) {
        // TODO
    }
}