package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class EnemyGlitch : Enemy {
    @Transient var showBrother: (Int) -> Unit = {}

    override fun createDeck() = CResources(CustomDeck().apply {
        Rank.entries.forEach { rank ->
            if (rank != Rank.QUEEN) {
                Suit.entries.forEach { suit ->
                    add(Card(rank, suit, CardBack.STANDARD, false))
                }
            }
        }
    })

    override fun makeMove(game: Game) {
        if (!game.isInitStage()) {
            if ((0..5).random() == 0) {
                showBrother(1)
                if (game.enemyCResources.hand.all { it.isOrdinary() }) {
                    game.enemyCResources.addOnTop(Card(Rank.KING, Suit.SPADES, CardBack.MADNESS, true))
                }
            } else if ((0..3).random() == 0) {
                showBrother(0)
            }
        }
        EnemyEasyPete.makeMove(game)
    }

    override fun onVictory() {
        save.glitchDefeated = true
    }
}