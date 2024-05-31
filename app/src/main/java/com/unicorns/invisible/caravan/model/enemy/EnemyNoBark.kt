package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString


@Serializable
data object EnemyNoBark : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.STANDARD, false).apply {
        listOf(CardBack.TOPS, CardBack.ULTRA_LUXE, CardBack.GOMORRAH, CardBack.LUCKY_38).forEach { back ->
            Suit.entries.forEach { suit ->
                add(Card(Rank.JACK, suit, back, true))
            }
        }
    })
    override fun getRewardBack() = CardBack.GOMORRAH
    override fun isAlt(): Boolean {
        return true
    }

    override suspend fun makeMove(game: Game) {
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val playersReadyCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filter { !it.isFace() }.maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        hand.withIndex().shuffled().sortedByDescending {
            when (it.value.rank) {
                Rank.JACK -> 30
                Rank.KING, Rank.TEN, Rank.NINE, Rank.SEVEN, Rank.SIX -> 20
                Rank.QUEEN -> 0
                else -> it.value.rank.value
            }
        }.forEach { (cardIndex, card) ->
            if (card.rank == Rank.JACK) {
                val caravan = game.playerCaravans.filter { it.getValue() in (1..26) }.maxByOrNull { it.getValue() }
                val cardToJack = caravan?.cards?.maxBy { it.getValue() }
                if (cardToJack != null && cardToJack.canAddModifier(card)) {
                    cardToJack.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }
            if (card.rank == Rank.KING) {
                val caravan = game.playerCaravans.filter { it.getValue() in (21..26) }.maxByOrNull { it.getValue() }
                if (caravan != null) {
                    val cardToKing = caravan.cards.maxByOrNull { it.getValue() }
                    if (cardToKing != null && cardToKing.canAddModifier(card)) {
                        cardToKing.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                        return
                    }
                }

                game.enemyCaravans
                    .flatMap { c -> c.cards.map { it to c } }
                    .sortedByDescending { it.first.getValue() }
                    .forEach {
                        if (it.second.getValue() + it.first.getValue() in (12..26)) {
                            if (it.first.canAddModifier(card)) {
                                it.first.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                                return
                            }
                        }
                    }
            }

            if (!card.rank.isFace()) {
                game.enemyCaravans.sortedBy { -it.getValue() }.forEachIndexed { caravanIndex, caravan ->
                    if (caravan.getValue() + card.rank.value <= 26 && caravan.canPutCardOnTop(card)) {
                        val otherCaravansIndices = game.enemyCaravans.indices.filter { it != caravanIndex }
                        var score = 0
                        fun check(p0: Int, e0: Int) {
                            if (p0 in (21..26) && (p0 > e0 || e0 > 26)) {
                                score++
                            }
                        }
                        otherCaravansIndices.forEach {
                            check(game.playerCaravans[it].getValue(), game.enemyCaravans[it].getValue())
                        }
                        if (!(score == 2 && caravan.getValue() + card.rank.value in (21..26))) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
                }
            }

            if (card.rank == Rank.QUEEN) {
                val possibleQueenCaravans = game.enemyCaravans
                    .filter { c ->
                        c.size >= 2 && c.getValue() < 21 && hand.all { !c.canPutCardOnTop(it) } && c.cards.last().canAddModifier(card)
                    }
                if (possibleQueenCaravans.isNotEmpty()) {
                    possibleQueenCaravans
                        .random()
                        .cards
                        .last()
                        .addModifier(game.enemyCResources.removeFromHand(cardIndex))
                    return
                }
            }

            if (card.rank == Rank.JOKER) {
                val cards = (game.playerCaravans + game.enemyCaravans).flatMap { it.cards }
                val gameCopyString = json.encodeToString(game)
                cards.forEach { potentialCardToJoker ->
                    val gameCopy = json.decodeFromString<Game>(gameCopyString)
                    val cardInCopy = (gameCopy.playerCaravans + gameCopy.enemyCaravans).flatMap { it.cards }.find {
                        potentialCardToJoker.card.rank == it.card.rank && potentialCardToJoker.card.suit == it.card.suit
                    }
                    if (cardInCopy?.canAddModifier(card) == true) {
                        val overWeightCaravansCopy = gameCopy.enemyCaravans.filter { it.getValue() > 26 }
                        val playersReadyCaravansCopy = gameCopy.playerCaravans.filter { it.getValue() in (21..26) }
                        if (overWeightCaravansCopy.size < overWeightCaravans.size || playersReadyCaravansCopy.size < playersReadyCaravans.size) {
                            if (potentialCardToJoker.canAddModifier(card)) {
                                potentialCardToJoker.addModifier(game.enemyCResources.removeFromHand(cardIndex))
                                return
                            }
                        }
                    }
                }
            }
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan()
            return
        }

        game.enemyCResources.removeFromHand(hand.withIndex().minByOrNull {
            when (it.value.rank) {
                Rank.QUEEN -> 0
                else -> it.value.rank.value
            }
        }!!.index)
    }
}