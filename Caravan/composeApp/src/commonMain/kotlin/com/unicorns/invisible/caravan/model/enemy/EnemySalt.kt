package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.salt
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropAllButFace
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersSimple
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable


@Serializable
class EnemySalt : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.salt
    override val isEven
        get() = false
    override val level: Int
        get() = 2
    override val isAvailable: Boolean
        get() = true

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(
            CardBack.STANDARD, CardBack.STANDARD_UNCOMMON,
            CardBack.STANDARD_RARE, CardBack.STANDARD_MYTHIC,
            CardBack.GOMORRAH_DARK, CardBack.LEGION
        ).forEach {
            add(CardNumber(RankNumber.FIVE, Suit.CLUBS, it))
            add(CardNumber(RankNumber.SIX, Suit.CLUBS, it))
            add(CardNumber(RankNumber.SEVEN, Suit.CLUBS, it))
            add(CardNumber(RankNumber.EIGHT, Suit.CLUBS, it))
            add(CardFaceSuited(RankFace.JACK, Suit.HEARTS, it))
            add(CardJoker(CardJoker.Number.ONE, it))
            add(CardJoker(CardJoker.Number.TWO, it))
        }
    })

    override val maxBets: Int
        get() = 4
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 10

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM).move(game, speed)
            return
        }
        val hand = game.enemyCResources.hand

        val jack = hand.find { it is CardFace && it.rank == RankFace.JACK }
        val joker = hand.find { it is CardJoker }
        val fiveToEight = game.playerCaravans.flatMap { it.cards }.find { it.card.rank.value in (5..8) }
        if (fiveToEight != null && jack != null) {
            val jackIndex = hand.indexOf(jack)
            fiveToEight.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardFace, speed)
            return
        }

        if (joker != null) {
            val cards = game.playerCaravans.flatMap { it.cards }
            if (cards.size > 2) {
                cards
                    .groupBy { it.card.rank.value }.toList()
                    .sortedByDescending { it.second.size }
                    .forEach { group ->
                        if (group.second.size > 2) {
                            val cardsInGroup = group.second
                            cardsInGroup.forEach {
                                if (it.canAddModifier(joker as CardJoker)) {
                                    val jokerIndex = hand.indexOf(joker)
                                    it.addModifier(game.enemyCResources.removeFromHand(jokerIndex, speed) as CardFace, speed)
                                    return
                                }
                            }
                        }
                    }
            }
        }

        if (StrategyPutNumbersSimple().move(game, speed)) {
            return
        }

        game.enemyCaravans.forEach { caravan ->
            if (caravan.getValue() > 26) {
                caravan.dropCaravan(speed)
                return
            }
        }

        if (jack != null) {
            val jackIndex = hand.indexOf(jack)
            game.playerCaravans
                .flatMap { it.cards }
                .filter { it.canAddModifier(jack as CardFace) }
                .sortedByDescending { it.card.rank.value }
                .forEach {
                    it.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardFace, speed)
                    return
                }
        }

        StrategyDropAllButFace(RankFace.JACK).move(game, speed)
    }
}