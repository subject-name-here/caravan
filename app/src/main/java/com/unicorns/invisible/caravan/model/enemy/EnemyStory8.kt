package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable


@Serializable
class EnemyStory8 : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        Rank.entries.forEach { rank ->
            if (rank == Rank.JOKER) {
                add(Card(rank, Suit.HEARTS, CardBack.DECK_13, false))
                add(Card(rank, Suit.CLUBS,  CardBack.STANDARD, true))
                add(Card(rank, Suit.HEARTS, CardBack.VAULT_21, true))
                add(Card(rank, Suit.CLUBS,  CardBack.LUCKY_38, true))
            } else if (rank.value >= 5 && rank != Rank.QUEEN) {
                Suit.entries.forEach { suit ->
                    add(Card(rank, suit, CardBack.GOMORRAH, true))
                    add(Card(rank, suit, CardBack.ULTRA_LUXE, true))
                    add(Card(rank, suit, CardBack.TOPS, true))
                }
            }
        }
    })
    override fun getRewardBack() = null

    private fun getHash(game: Game): Int {
        val enemyHash = game.enemyCaravans
            .flatMap { it.cards }
            .flatMap { listOf(it.card) + it.modifiersCopy() }
            .sumOf { it.hashCode() }
        val playerHash = game.playerCaravans
            .flatMap { it.cards }
            .flatMap { listOf(it.card) + it.modifiersCopy() }
            .sumOf { it.hashCode() }
        return enemyHash + playerHash
    }
    private fun updateHash(game: Game) {
        caravansHash = getHash(game)
    }

    var resisted = false
    private var caravansHash = 0
    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val cardIndex = hand.withIndex().filter { !it.value.isFace() }.random().index
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
            updateHash(game)
            return
        }
        if (!resisted) {
            if (getHash(game) != caravansHash) {
                resisted = true
            }
        }

        EnemyOliver.makeMove(game)

        updateHash(game)
    }
}