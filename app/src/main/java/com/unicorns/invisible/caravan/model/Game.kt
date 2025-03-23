package com.unicorns.invisible.caravan.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyMadnessCardinal
import com.unicorns.invisible.caravan.model.enemy.EnemyTheManInTheMirror
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.CardFBomb
import com.unicorns.invisible.caravan.model.primitives.CardNuclear
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID


class Game(
    val playerCResources: CResources,
    val enemy: Enemy
) {
    var recomposeResources by mutableIntStateOf(0)

    val playerCaravans = listOf(Caravan(), Caravan(), Caravan())
    val enemyCaravans = listOf(Caravan(), Caravan(), Caravan())

    val enemyCResources = enemy.createDeck()

    var isPlayerTurn = true
        set(value) {
            field = value
            recomposeResources++
        }
    var canPlayerMove = false
        set(value) {
            field = value
            recomposeResources++
        }

    var id = UUID.randomUUID().toString()

    var onWin: () -> Unit = {}
    var onLose: () -> Unit = {}

    var jokerPlayedSound: () -> Unit = {}
    var nukeBlownSound: () -> Unit = {}
    var wildWastelandSound: () -> Unit = {}

    var isGameOver = 0
        private set(value) {
            if (field == 0) {
                when (value) {
                    -1 -> { field = -1; onLose() }
                    1 -> { field = 1; onWin() }
                }
            }
        }

    fun isOver() = isGameOver != 0

    var isCorrupted = false

    fun isInitStage(): Boolean {
        return playerCResources.hand.size > 5 || enemyCResources.hand.size > 5
    }

    fun startGame() {
        if (enemy is EnemyMadnessCardinal) {
            while (enemyCResources.deckSize < playerCResources.deckSize + 17) {
                enemyCResources.addNewDeck(CustomDeck(CardBack.MADNESS, 0))
            }
        }

        playerCResources.initResources()
        enemyCResources.initResources()

        if (enemy is EnemyTheManInTheMirror) {
            enemyCResources.copyFrom(playerCResources)
        }

        canPlayerMove = true
    }

    suspend fun processField(speed: AnimationSpeed) {
        val caravans = playerCaravans + enemyCaravans
        val cards = caravans.flatMap { it.cards }

        cards.forEach {
            if (it.hasBomb) {
                processBomb(it, speed)
            }
        }

        if (cards.any { it.hasJacks() || it.hasActiveJoker }) {
            processJacks(speed)
            processJoker(speed)
        }

        if (cards.any { it.hasActiveUfo || it.hasActivePete }) {
            processUfo(speed)
            processPete()
        }

        if (cards.any { it.hasActiveFev }) {
            processFev()
        }

        caravans.forEach {
            val caravanCards = it.cards
            val isMuggyHere = caravanCards.any { it.hasActiveMuggy }
            it.cards.forEach { card ->
                card.isProtectedByMuggy = isMuggyHere
            }

            val cazadorOwners = caravanCards.filter { it.hasActiveCazador }
            val queensBlock = cazadorOwners.all { owner -> owner.isQueenReversingSequence() }

            if (cazadorOwners.isNotEmpty()) {
                it.getCazadorPoison(queensBlock, speed)
            }
        }
    }

    suspend fun afterPlayerMove(speed: AnimationSpeed) {
        delay(speed.delay.coerceAtLeast(95L))
        processField(speed)
        if (playerCResources.hand.size < 5 && playerCResources.deckSize > 0) {
            playerCResources.addToHand()
            delay(speed.delay)
        }
        isPlayerTurn = false

        if (checkOnGameOver()) {
            return
        }

        delay(speed.delay.coerceAtLeast(95L))

        enemy.makeMove(this@Game, speed)
        delay(speed.delay.coerceAtLeast(95L))

        processField(speed)
        if (enemyCResources.hand.size < 5 && enemyCResources.deckSize > 0) {
            enemyCResources.addToHand()
        }
        isPlayerTurn = true

        checkOnGameOver()
    }

    var specialGameOverCondition: () -> Int = { 0 }
    fun checkOnGameOver(): Boolean {
        if (!isPlayerTurn && enemyCResources.hand.isEmpty()) {
            isGameOver = 1
            return true
        }
        if (isPlayerTurn && playerCResources.hand.isEmpty()) {
            isGameOver = -1
            return true
        }

        var scorePlayer = 0
        var scoreEnemy = 0

        fun checkLine(p0: Int, e0: Int) {
            if (p0 in (21..26)) {
                if (e0 in (21..26)) {
                    when {
                        p0 > e0 -> scorePlayer++
                        p0 < e0 -> scoreEnemy++
                    }
                } else {
                    scorePlayer++
                }
            } else if (e0 in (21..26)) {
                scoreEnemy++
            }
        }

        checkLine(playerCaravans[0].getValue(), enemyCaravans[0].getValue())
        checkLine(playerCaravans[1].getValue(), enemyCaravans[1].getValue())
        checkLine(playerCaravans[2].getValue(), enemyCaravans[2].getValue())

        if (scorePlayer + scoreEnemy == 3) {
            isGameOver = if (scorePlayer > scoreEnemy) 1 else -1
            return true
        }

        val special = specialGameOverCondition()
        if (special != 0) {
            isGameOver = special
            return true
        }

        return false
    }

    private suspend fun processJacks(speed: AnimationSpeed) {
        (playerCaravans + enemyCaravans).forEach { caravan ->
            caravan.removeAllJackedCards(speed)
        }
    }

    private suspend fun processBomb(bombOwner: CardWithModifier, speed: AnimationSpeed) {
        val isFBomb = bombOwner.modifiersCopy().findLast { it is CardNuclear } is CardFBomb
        (playerCaravans + enemyCaravans).forEach {
            if (bombOwner !in it.cards) {
                CoroutineScope(Dispatchers.Unconfined).launch() {
                    if (isFBomb) {
                        // TODO: some bonus
                        it.dropCaravan(speed)
                    } else {
                        if (it.cards.all { card -> !card.isProtectedByMuggy }) {
                            it.dropCaravan(speed)
                        }
                    }
                }
            }
        }
        bombOwner.deactivateBomb()
    }

    private fun processFev() {
        playerCaravans.forEach { caravan ->
            caravan.cards.filter {
                it.hasActiveFev
            }.forEach {
                playerCResources.mutateFev(it.card)
                it.deactivateFev()
            }
        }
        enemyCaravans.forEach { caravan ->
            caravan.cards.filter {
                it.hasActiveFev
            }.forEach {
                enemyCResources.mutateFev(it.card)
                it.deactivateFev()
            }
        }
    }
    private suspend fun processUfo(speed: AnimationSpeed) {
        (playerCaravans + enemyCaravans).forEach { caravan ->
            caravan.cards.filter {
                it.hasActiveUfo
            }.forEach { card ->
                val seed = card.card.rank.ordinal * 4 + card.card.suit.ordinal
                (playerCaravans + enemyCaravans).forEach { caravan ->
                    caravan.getUfo(seed, speed)
                }
                card.deactivateUfo()
            }
        }
    }
    private fun processPete() {
        (playerCaravans + enemyCaravans).forEach { caravan ->
            caravan.cards
                .filter { it.hasActivePete }
                .forEach { card ->
                    (playerCaravans + enemyCaravans).forEach {
                        it.getPetePower()
                    }
                    card.deactivatePete()
                }
        }
    }

    suspend fun processJoker(speed: AnimationSpeed) {
        (playerCaravans + enemyCaravans).forEach { caravan ->
            caravan.cards.filter {
                it.hasActiveJoker
            }.forEach {
                putJokerOntoCard(it, speed)
                it.deactivateJoker()
            }
        }
    }

    private suspend fun putJokerOntoCard(cardWithModifier: CardWithModifier, speed: AnimationSpeed) {
        val card = cardWithModifier.card
        if (card.rank == RankNumber.ACE) {
            (playerCaravans + enemyCaravans).forEach { caravan ->
                caravan.jokerRemoveAllSuits(card, speed)
            }
        } else {
            (playerCaravans + enemyCaravans).forEach { caravan ->
                caravan.jokerRemoveAllRanks(card, speed)
            }
        }
    }
}