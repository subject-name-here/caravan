package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class EnemySnuffles : EnemyPve {
    override fun getNameId() = R.string.snuffles
    override fun isEven() = false

    override fun createDeck(): CResources = CResources(CustomDeck().apply {
        listOf(CardBack.LUCKY_38, CardBack.GOMORRAH, CardBack.ULTRA_LUXE, CardBack.TOPS).forEach { back ->
            Rank.entries.forEach { rank ->
                if (rank == Rank.JOKER) {
                    add(Card(Rank.JOKER, Suit.HEARTS, back, false))
                    add(Card(Rank.JOKER, Suit.CLUBS, back, false))
                } else {
                    listOf(Suit.CLUBS, Suit.SPADES).forEach { suit ->
                        add(Card(rank, suit, back, false))
                    }
                }
            }
        }
        Suit.entries.forEach { suit ->
            add(Card(Rank.TEN, suit, CardBack.NUCLEAR, false))
        }
        add(Card(Rank.KING, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.KING, Suit.CLUBS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.KING, Suit.DIAMONDS, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.KING, Suit.SPADES, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.JACK, Suit.SPADES, CardBack.WILD_WASTELAND, false))
        add(Card(Rank.QUEEN, Suit.HEARTS, CardBack.WILD_WASTELAND, false))
    })

    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() {
        bank += save.table
        save.table = 0
    }
    override fun getBet(): Int {
        return if (bank <= 1) {
            bank
        } else {
            bank / 2
        }
    }
    override fun retractBet() { bank -= getBet() }
    override fun addReward(reward: Int) { bank += reward }

    override fun makeMove(game: Game) {
        if (game.isInitStage()) {
            val card = game.enemyCResources.hand.withIndex().filter { !it.value.isModifier() }.random()
            val caravan = game.enemyCaravans.filter { it.isEmpty() }.random()
            caravan.putCardOnTop(game.enemyCResources.removeFromHand(card.index))
            return
        }

        val cards = game.enemyCResources.hand.withIndex()
            .filter { !it.value.isModifier() }
            .shuffled()
        val caravans = game.enemyCaravans.shuffled()
        cards.forEach { card ->
            caravans.forEach { caravan ->
                if (caravan.canPutCardOnTop(card.value) && Random.nextBoolean()) {
                    caravan.putCardOnTop(game.enemyCResources.removeFromHand(card.index))
                    return
                }
            }
        }

        if (Random.nextBoolean()) {
            val modifiers = game.enemyCResources.hand.withIndex()
                .filter { it.value.isModifier() }
                .shuffled()
            val cards = (game.playerCaravans + game.enemyCaravans).flatMap { it.cards }.shuffled()
            cards.forEach { card ->
                modifiers.forEach { modifier ->
                    if (card.canAddModifier(modifier.value) && Random.nextBoolean()) {
                        card.addModifier(game.enemyCResources.removeFromHand(modifier.index))
                        return
                    }
                }
            }
        }

        if (Random.nextBoolean()) {
            game.enemyCaravans.shuffled().forEach { caravan ->
                if (!caravan.isEmpty() && Random.nextBoolean()) {
                    caravan.dropCaravan()
                    return
                }
            }
        }

        game.enemyCResources.dropCardFromHand(game.enemyCResources.hand.indices.random())
    }
}