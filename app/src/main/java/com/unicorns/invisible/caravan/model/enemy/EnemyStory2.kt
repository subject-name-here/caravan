package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardWildWasteland
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.WWType


class EnemyStory2 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.TOPS).apply {
        removeAll(toList().filter { it is CardModifier && !(it is CardFace && it.rank == RankFace.JACK) })
    })

    private var cazadorsAdded = 0
    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        val hand = game.enemyCResources.hand

        if (game.isInitStage()) {
            val card = hand.filterIsInstance<CardBase>().maxBy { it.rank.value }
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(hand.indexOf(card), speed) as CardBase, speed)
            return
        } else if (cazadorsAdded % 13 == 0) {
            game.enemyCResources.addOnTop(CardWildWasteland(WWType.CAZADOR))
        }
        cazadorsAdded++

        val specials = hand.withIndex().filter { it.value is CardWildWasteland }
        specials.forEach { (index, special) ->
            special as CardWildWasteland
            when (special.type) {
                WWType.CAZADOR -> {
                    val candidate = game.playerCaravans
                        .filter { it.getValue() in (11..26) }
                        .filter { !it.cards.flatMap { card -> card.modifiersCopy() }.any { mod -> mod is CardWildWasteland } }
                        .maxByOrNull { it.size }
                        ?.cards
                        ?.filter { it.canAddModifier(special) }
                        ?.maxByOrNull { it.getValue() }
                    if (candidate != null) {
                        candidate.addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                        game.wildWastelandSound()
                        return
                    }
                }
                else -> {}
            }
        }

        hand.withIndex().filter { it.value !is CardWildWasteland }.shuffled().forEach { (cardIndex, card) ->
            if (card is CardBase) {
                game.enemyCaravans.shuffled().forEach { caravan ->
                    if (caravan.getValue() + card.rank.value <= 26) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.enemyCResources.removeFromHand(cardIndex, speed) as CardBase, speed)
                            return
                        }
                    }
                }
            }
            if (card is CardFace && card.rank == RankFace.JACK) {
                val caravan =
                    game.playerCaravans.filter { it.getValue() in (21..26) }.randomOrNull()
                if (caravan != null) {
                    val cardToAdd = caravan.cards.maxBy { it.getValue() }
                    if (cardToAdd.canAddModifier(card)) {
                        cardToAdd.addModifier(game.enemyCResources.removeFromHand(cardIndex, speed) as CardFace, speed)
                        return
                    }
                }
            }
        }

        game.enemyCResources.dropCardFromHand(hand.withIndex().minByOrNull {
            when (val c = it.value) {
                is CardBase -> when (c.rank) {
                    RankNumber.ACE -> 3
                    RankNumber.TWO -> 2
                    RankNumber.THREE -> 2
                    RankNumber.FOUR -> 3
                    RankNumber.FIVE -> 3
                    RankNumber.SIX -> 4
                    RankNumber.SEVEN -> 5
                    RankNumber.EIGHT -> 5
                    RankNumber.NINE -> 5
                    RankNumber.TEN -> 5
                }
                is CardFace -> c.rank.value
                else -> 15
            }
        }!!.index, speed)
    }
}