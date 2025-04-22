package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.pve_enemy_victor
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropLadiesFirst
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.RankFace
import kotlinx.serialization.Serializable


@Serializable
class EnemyVictor : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.pve_enemy_victor
    override val isEven
        get() = true
    override val level: Int
        get() = 3
    override val isAvailable: Boolean
        get() = true

    override fun createDeck() = CResources(CardBack.LUCKY_38)

    override val maxBets: Int
        get() = 2
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 25

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM).move(game, speed)
            return
        }

        if (StrategyPutNumbersMedium().move(game, speed)) {
            return
        }

        val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().shuffled()

        modifiers.forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            when (modifier.rank) {
                RankFace.JACK -> {
                    if (StrategyJackMedium(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.QUEEN -> {
                    game.enemyCResources.dropCardFromHand(index, speed)
                    return
                }
                RankFace.KING -> {
                    if (StrategyKingMedium(index).move(game, speed)) {
                        return
                    }
                }
                RankFace.JOKER -> {
                    val cards = game.enemyCaravans.flatMap { it.cards }.shuffled()
                    if (cards.isNotEmpty()) {
                        cards.random().addModifier(game.enemyCResources.removeFromHand(index, speed) as CardModifier, speed)
                        return
                    }
                }
            }
        }

        game.enemyCaravans.forEachIndexed { indexC, caravan ->
            if (caravan.getValue() > 26) {
                val state = gameToState(game)
                when (indexC) {
                    0 -> state.enemy.v1 = 0
                    1 -> state.enemy.v2 = 0
                    2 -> state.enemy.v3 = 0
                }
                if (checkTheOutcome(state) != 1) {
                    caravan.dropCaravan(speed)
                    return
                }
            }
        }

        StrategyDropLadiesFirst().move(game, speed)
    }
}