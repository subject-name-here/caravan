package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import kotlinx.serialization.Serializable
import kotlin.random.Random


@Serializable
class EnemyOliver : EnemyPve {
    override fun getNameId() = R.string.pve_enemy_oliver_real
    override fun isEven() = true

    override fun createDeck() = CResources(CardBack.STANDARD, false)

    private var bank = 0
    override fun getBank(): Int { return bank }
    override fun refreshBank() { bank = 15 }
    override fun getBet(): Int { return bank }
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