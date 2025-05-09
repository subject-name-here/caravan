package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.johnson_nash
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructiveCleverNoJoker
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropNashOrder
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.checkIfEnemyVictoryIsClose
import com.unicorns.invisible.caravan.model.enemy.strategy.checkIfPlayerVictoryIsClose
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable


@Serializable
class EnemyNash : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.johnson_nash
    override val isEven
        get() = false
    override val level: Int
        get() = 3
    override val isAvailable: Boolean
        get() = true

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(
            CardBack.TOPS,
            CardBack.GOMORRAH,
            CardBack.ULTRA_LUXE,
            CardBack.LUCKY_38,
            CardBack.STANDARD
        ).forEach { back ->
            Suit.entries.forEach { suit ->
                add(CardNumber(RankNumber.SIX, suit, back))
                add(CardFaceSuited(RankFace.JACK, suit, back))
                add(CardFaceSuited(RankFace.QUEEN, suit, back))
                add(CardFaceSuited(RankFace.KING, suit, back))
            }
        }
    })


    override val maxBets: Int
        get() = 3
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 10

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM_TO_LTR).move(game, speed)
            return
        }

        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }.toSet() +
                game.enemyCaravans
                    .filter {
                        !it.isEmpty() && it.cards[0].modifiersCopy().count { modifier ->
                            modifier is CardFaceSuited && modifier.rank != RankFace.KING
                        } >= 2
                    }.toSet()


        if (checkIfPlayerVictoryIsClose(gameToState(game))) {
            if (StrategyDestructiveCleverNoJoker().move(game, speed)) {
                return
            }
        }

        val six = game.enemyCResources.hand.find { it is CardBase && it.rank == RankNumber.SIX }
        val jack = game.enemyCResources.hand.find { it is CardFace && it.rank == RankFace.JACK }
        val queen = game.enemyCResources.hand.find { it is CardFace && it.rank == RankFace.QUEEN }
        val king = game.enemyCResources.hand.find { it is CardFace && it.rank == RankFace.KING }

        game.enemyCaravans.withIndex().forEach { (index, caravan) ->
            if (checkIfEnemyVictoryIsClose(gameToState(game), index)) {
                if (king != null && !caravan.isEmpty() && caravan.getValue() < 21 && caravan !in overWeightCaravans) {
                    val card = caravan.cards[0]
                    if (card.canAddModifier(king as CardModifier)) {
                        val kingIndex = game.enemyCResources.hand.indexOf(king)
                        card.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardModifier, speed)
                        return
                    }
                }
            }
        }

        suspend fun putJack(index: Int, cardToJack: CardWithModifier): Boolean {
            val state = gameToState(game)
            when (index) {
                0 -> state.player.v1 -= cardToJack.getValue()
                1 -> state.player.v2 -= cardToJack.getValue()
                2 -> state.player.v3 -= cardToJack.getValue()
            }

            if (checkTheOutcome(state) != 1 && jack != null) {
                val jackIndex = game.enemyCResources.hand.indexOf(jack)
                cardToJack.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardFace, speed)
                return true
            }
            return false
        }

        if (jack != null) {
            game.playerCaravans.withIndex()
                .shuffled()
                .forEach { (index, caravan) ->
                    val cardToJack = caravan.cards.find { it.card.rank == RankNumber.SIX && it.canAddModifier(jack as CardFace) }
                    if (cardToJack != null && putJack(index, cardToJack)) {
                        return
                    }
                }
            game.playerCaravans.withIndex()
                .filter { it.value.getValue() in (24..26) }
                .sortedByDescending { it.value.getValue() }
                .forEach { (index, caravan) ->
                    val cardToJack = caravan.cards.filter { it.canAddModifier(jack as CardFace) }
                        .maxByOrNull { it.getValue() }
                    if (cardToJack != null && putJack(index, cardToJack)) {
                        return
                    }
                }
        }


        game.enemyCaravans
            .withIndex()
            .shuffled()
            .forEach { (index, caravan) ->
                if (six != null && caravan.isEmpty()) {
                    if (caravan.canPutCardOnTop(six as CardNumber)) {
                        val sixIndex = game.enemyCResources.hand.indexOf(six)
                        caravan.putCardOnTop(game.enemyCResources.removeFromHand(sixIndex, speed) as CardNumber, speed)
                        return
                    }
                }

                if (king != null && !caravan.isEmpty() && caravan.getValue() < 21 && caravan !in overWeightCaravans) {
                    val card = caravan.cards[0]
                    if (card.canAddModifier(king as CardFace)) {
                        val state = gameToState(game)
                        when (index) {
                            0 -> state.player.v1 += card.getValue()
                            1 -> state.player.v2 += card.getValue()
                            2 -> state.player.v3 += card.getValue()
                        }
                        if (checkTheOutcome(state) != 1) {
                            val kingIndex = game.enemyCResources.hand.indexOf(king)
                            card.addModifier(game.enemyCResources.removeFromHand(kingIndex, speed) as CardFace, speed)
                        }
                        return
                    }
                }

                if (queen != null && !caravan.isEmpty() && caravan !in overWeightCaravans) {
                    val card = caravan.cards[0]
                    if (card.modifiersCopy().count { it is CardFace && it.rank != RankFace.KING } == 0 && card.canAddModifier(queen as CardFace)) {
                        val queenIndex = game.enemyCResources.hand.indexOf(queen)
                        card.addModifier(game.enemyCResources.removeFromHand(queenIndex, speed) as CardFace, speed)
                        return
                    }
                }
            }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan(speed)
            return
        }

        if (jack != null) {
            game.playerCaravans.withIndex()
                .filter { it.value.getValue() in (1..26) }
                .sortedByDescending { it.value.getValue() }
                .forEach { (index, caravan) ->
                    val cardToJack = caravan.cards.filter { it.canAddModifier(jack as CardFace) }
                        .maxByOrNull { it.getValue() }
                    if (cardToJack != null && putJack(index, cardToJack)) {
                        return
                    }
                }
        }

        StrategyDropNashOrder().move(game, speed)
    }
}