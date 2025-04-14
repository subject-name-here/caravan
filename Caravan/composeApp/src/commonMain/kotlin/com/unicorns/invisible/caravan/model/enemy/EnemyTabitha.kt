package com.unicorns.invisible.caravan.model.enemy

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.tabitha
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDestructiveCleverNoJoker
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyDropAllButFace
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyInit
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJackToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyJokerSimple
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyKingToPlayer
import com.unicorns.invisible.caravan.model.enemy.strategy.StrategyPutNumbersSimpleSafe
import com.unicorns.invisible.caravan.model.enemy.strategy.checkIfEnemyVictoryIsClose
import com.unicorns.invisible.caravan.model.enemy.strategy.checkTheOutcome
import com.unicorns.invisible.caravan.model.enemy.strategy.gameToState
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFace
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import kotlinx.serialization.Serializable
import kotlin.math.min


@Serializable
class EnemyTabitha : EnemyPvEWithBank() {
    override val nameId
        get() = Res.string.tabitha
    override val isEven
        get() = false
    override val isAvailable: Boolean
        get() = true

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        val backs = listOf(
            CardBack.STANDARD_UNCOMMON,
            CardBack.ULTRA_LUXE,
            CardBack.LUCKY_38,
            CardBack.GOMORRAH,
            CardBack.TOPS,
            CardBack.VAULT_21_NIGHT
        )
        val suit = Suit.DIAMONDS
        backs.forEach { back ->
            RankNumber.entries.forEach { rank ->
                if (rank != RankNumber.ACE) {
                    add(CardNumber(rank, suit, back))
                }
            }
            add(CardFaceSuited(RankFace.JACK, suit, back))
            add(CardFaceSuited(RankFace.KING, suit, back))
            add(CardJoker(CardJoker.Number.ONE, back))
            add(CardJoker(CardJoker.Number.TWO, back))
        }
    })

    override var bank: Int = 0
    override val maxBank: Int
        get() = 40
    override val bet: Int
        get() = min(bank, 20)

    override var winsNoBet: Int = 0
    override var winsBet: Int = 0
    override var winsBlitzNoBet: Int = 0
    override var winsBlitzBet: Int = 0


    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {
        /**
         * - Tabitha (Has only Diamonds (no aces),
         * otherwise, to fix overweight caravans, doesn't use Kings;
         * Jokers are wild cards - not fully random, but not the simple strategy.)
         */
        if (game.isInitStage()) {
            StrategyInit(StrategyInit.Type.MAX_FIRST_TO_RANDOM).move(game, speed)
            return
        }

        game.enemyCaravans.withIndex().forEach { (index, _) ->
            if (checkIfEnemyVictoryIsClose(gameToState(game), index)) {
                if (StrategyDestructiveCleverNoJoker().move(game, speed)) {
                    return
                }
            }
        }

        val jack = game.enemyCResources.hand.find { it is CardFace && it.rank == RankFace.JACK }
        val aceOfDiamonds = game.playerCaravans.flatMap { it.cards }.find { it.card.rank == RankNumber.ACE && it.card.suit == Suit.DIAMONDS }
        if (jack != null && aceOfDiamonds != null) {
            val state = gameToState(game)
            val index = game.playerCaravans.indexOfFirst { aceOfDiamonds in it.cards }
            when (index) {
                0 -> state.player.v1 -= aceOfDiamonds.getValue()
                1 -> state.player.v2 -= aceOfDiamonds.getValue()
                2 -> state.player.v3 -= aceOfDiamonds.getValue()
            }

            if (checkTheOutcome(state) != 1) {
                val jackIndex = game.enemyCResources.hand.indexOf(jack)
                aceOfDiamonds.addModifier(game.enemyCResources.removeFromHand(jackIndex, speed) as CardFace, speed)
                return
            }
        }

        if (StrategyPutNumbersSimpleSafe().move(game, speed)) {
            return
        }

        StrategyDropAllButFace(RankFace.JACK).move(game, speed)
    }
}