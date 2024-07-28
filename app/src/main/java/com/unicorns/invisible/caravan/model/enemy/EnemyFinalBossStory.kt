package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class EnemyFinalBossStory(@Transient private var update: Int = 0) : Enemy() {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.UNPLAYABLE, false))
    override fun getRewardBack() = null

    @Transient
    var playAlarm: () -> Unit = {}
    @Transient
    var sayThing: (String) -> Unit = {}

    override fun makeMove(game: Game) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.shuffled().filter { !it.isSpecial() }.filter { !it.isFace() }.minBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card)))
            return
        }

        makeMoveInner(game)

        if (game.enemyCResources.deckSize == 0) {
            playAlarm()
            // TODO: rework update thing
            ++update
            if (update < 5) {
                game.enemyCResources.addNewDeck(CustomDeck(CardBack.UNPLAYABLE, false))
                game.enemyCResources.shuffleDeck()
            }
            when (update) {
                // TODO: translate this!
                1 -> sayThing("My resources are infinite. Yours - are not.")
                2 -> sayThing("The whole China is against you. Don't you see how futile it is?")
                3 -> sayThing("Resisting is illogical. You are fighting against the brighter future for humanity.")
                4 -> sayThing("You are getting weaker. I am not.")
                5 -> sayThing("Huh, that's weird. I cannot restock. Probably some mistake in supply lines.")
                6 -> sayThing("That's... that's not right. Where are my resources?")
                7 -> sayThing("You. It's all you, isn't it?\nYou doomed us. You doomed everyone.")
                8 -> sayThing("")
            }
        } else if (game.enemyCResources.getDeckBack()?.second == true) {
            playAlarm()
        }

        if (game.enemyCResources.hand.none { it.isSpecial() } && update < 6) {
            game.enemyCResources.addOnTop(Card(Rank.THREE, Suit.HEARTS, CardBack.UNPLAYABLE, true))
        }
    }

    private fun makeMoveInner(game: Game) {
        fun blowUpTheBomb(card: CardWithModifier, bombIndex: Int) {
            game.nukeBlownSound()
            card.addModifier(game.enemyCResources.removeFromHand(bombIndex))
        }

        val hand = game.enemyCResources.hand

        game.enemyCaravans.withIndex().forEach {
            val isWinningMovePossible = checkMoveOnImminentVictory(game, it.index)
            val rivalCaravanValue = game.playerCaravans[it.index].getValue()
            val lowerBound = (rivalCaravanValue + 1).coerceIn(21..26)
            if (isWinningMovePossible) {
                val jack = hand.withIndex().find { card -> card.value.rank == Rank.JACK }
                if (jack != null) {
                    it.value.cards
                        .filter { card -> card.canAddModifier(jack.value) }
                        .sortedBy { card -> card.getValue() }
                        .forEach { card ->
                            if (it.value.getValue() - card.getValue() in (lowerBound..26)) {
                                card.addModifier(game.enemyCResources.removeFromHand(jack.index))
                                return
                            }
                        }
                }
                val king = hand.withIndex().find { card -> card.value.rank == Rank.KING }
                if (king != null) {
                    it.value.cards
                        .filter { card -> card.canAddModifier(king.value) }
                        .sortedBy { card -> card.getValue() }
                        .forEach { card ->
                            if (it.value.getValue() + card.getValue() in (lowerBound..26)) {
                                card.addModifier(game.enemyCResources.removeFromHand(king.index))
                                return
                            }
                        }
                }
                hand.withIndex()
                    .filter { card -> !card.value.isFace() }
                    .forEach { (cardIndex, card) ->
                        if (it.value.getValue() + card.rank.value in (lowerBound..26) && it.value.canPutCardOnTop(card)) {
                            it.value.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex))
                            return
                        }
                    }
            }
        }

        val playerPerfectCaravans = game.playerCaravans.filter { it.getValue() in (21..26) }.size
        val playerOverWeightCaravans = game.playerCaravans.filter { it.getValue() > 26 }.size
        val dangerousCaravans = game.playerCaravans.filter { it.getValue() in 11..20 }.size
        val caravansScore = playerPerfectCaravans + playerOverWeightCaravans + dangerousCaravans
        if (caravansScore >= 2 || hand.filter { it.isSpecial() }.size >= 2) {
            hand.withIndex()
                .filter { it.value.isSpecial() }
                .forEach { (bombIndex, bomb) ->
                    val cards = game.enemyCaravans
                        .sortedByDescending { if (it.getValue() > 26) 0 else it.getValue() }
                        .flatMap { it.cards }
                        .filter { it.canAddModifier(bomb) }
                    if (cards.isNotEmpty()) {
                        blowUpTheBomb(cards.first(), bombIndex)
                        return
                    } else {
                        val playerCards = game.playerCaravans.flatMap { it.cards }.filter { it.canAddModifier(bomb) }
                        if (playerCards.isNotEmpty()) {
                            blowUpTheBomb(playerCards.random(), bombIndex)
                            return
                        }
                    }
                }
        }

        EnemyCaesar.makeMove(game)
    }
}