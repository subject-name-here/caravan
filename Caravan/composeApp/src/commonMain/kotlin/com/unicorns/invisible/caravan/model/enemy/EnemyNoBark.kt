package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.no_bark
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropAllButFace
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingMedium
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable


@Serializable
class EnemyNoBark : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.no_bark
    override val isEven
        get() = false
    override val level: Int
        get() = 4
    override val isAvailable: Boolean
        get() = true

    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.GOMORRAH_DARK).apply {
        Suit.entries.forEach { suit ->
            listOf(CardBack.GOMORRAH, CardBack.LUCKY_38, CardBack.ULTRA_LUXE, CardBack.TOPS, CardBack.VAULT_21_DAY).forEach { back ->
                add(CardFaceSuited(RankFace.JACK, suit, back))
            }
        }
        removeAll { it is CardFaceSuited && it.rank == RankFace.QUEEN }
        removeAll { it is CardJoker }
    })


    override val maxBets: Int
        get() = 4
    override var curBets: Int = maxBets
    override val bet: Int
        get() = 20

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.RANDOM).move(game, speed)
            return
        }

        val baseCards = game.enemyCResources.hand.filterIsInstance<CardBase>().shuffled()
        val modifiers = game.enemyCResources.hand.filterIsInstance<CardFace>().shuffled()

        modifiers.forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            if (modifier.rank == RankFace.JACK) {
                if (StrategyJackToPlayer(index).move(game, speed)) {
                    return
                }
            }
        }

        val caravans = game.enemyCaravans.withIndex().shuffled()
        baseCards.forEach { card ->
            caravans.forEach { (indexC, caravan) ->
                if (caravan.canPutCardOnTop(card) && caravan.getValue() + card.rank.value <= 26) {
                    val state = gameToState(game)
                    state.enemy[indexC] += card.rank.value
                    if (checkTheOutcome(state) != 1) {
                        val index = game.enemyCResources.hand.indexOf(card)
                        caravan.putCardOnTop(game.enemyCResources.removeFromHand(index, speed) as CardBase, speed)
                        return
                    }
                }
            }
        }

        modifiers.forEach { modifier ->
            val index = game.enemyCResources.hand.indexOf(modifier)
            if (modifier.rank == RankFace.KING) {
                if (StrategyKingMedium(index).move(game, speed)) {
                    return
                }
            }
        }

        game.enemyCaravans.shuffled().forEach { caravan ->
            if (caravan.getValue() > 26) {
                caravan.dropCaravan(speed)
                return
            }
        }

        StrategyDropAllButFace(RankFace.JACK).move(game, speed)
    }
}