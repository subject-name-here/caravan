package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropLadiesFirst
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersMedium
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace


data object EnemyStory5 : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.ULTRA_LUXE_CRIME).apply {
        removeAll {
            it is CardNumber && it.rank.value <= 5 || it is CardFace && it.rank == RankFace.QUEEN
        }
    })

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.MAX_FIRST_TO_RANDOM).move(game, speed)
            return
        }
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }
        val hand = game.enemyCResources.hand

        val modifiers = hand.filterIsInstance<CardFace>().sortedByDescending { it.rank }
        val joker = modifiers.find { it is CardJoker }
        if (joker != null) {
            if (StrategyJokerSimple(hand.indexOf(joker)).move(game, speed)) {
                return
            }
        }

        val king = modifiers.find { it.rank == RankFace.KING }
        if (king != null) {
            if (StrategyKingMedium(hand.indexOf(king)).move(game, speed)) {
                return
            }
        }

        val jack = modifiers.find { it.rank == RankFace.JACK }
        if (jack != null) {
            if (StrategyJackMedium(hand.indexOf(jack)).move(game, speed)) {
                return
            }
        }

        if (StrategyPutNumbersMedium().move(game, speed)) {
            return
        }

        if (overWeightCaravans.isNotEmpty()) {
            overWeightCaravans.random().dropCaravan(speed)
            return
        }

        StrategyDropLadiesFirst().move(game, speed)
    }
}