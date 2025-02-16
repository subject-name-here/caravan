package com.unicorns.invisible.caravan.model

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyMadnessCardinal
import com.unicorns.invisible.caravan.model.enemy.EnemyTheManInTheMirror
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID


@Serializable
class Game(
    val playerCResources: CResources,
    val enemy: Enemy
) {
    val playerCaravans = listOf(Caravan(), Caravan(), Caravan())
    val enemyCaravans = listOf(Caravan(), Caravan(), Caravan())

    val enemyCResources = enemy.createDeck()

    var isPlayerTurn = true
    var canPlayerMove = false

    var id = UUID.randomUUID().toString()

    @Transient
    var onWin: () -> Unit = {}
    @Transient
    var onLose: () -> Unit = {}

    @Transient
    var jokerPlayedSound: () -> Unit = {}
    @Transient
    var nukeBlownSound: () -> Unit = {}
    @Transient
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
            while (enemyCResources.deckSize < playerCResources.deckSize + 27) {
                enemyCResources.addNewDeck(CustomDeck(CardBack.MADNESS, false))
            }
        }

        playerCResources.initResources()
        enemyCResources.initResources()

        if (enemy is EnemyTheManInTheMirror) {
            enemyCResources.copyFrom(playerCResources)
        }

        canPlayerMove = true
    }

    fun processField(): Boolean {
        val caravans = playerCaravans + enemyCaravans
        val cards = caravans.flatMap { it.cards }
        var flag = false

        cards.forEach {
            if (it.hasBomb) {
                processBomb(it)
                flag = true
            }
        }

        if (cards.any { it.hasJacks() || it.hasActiveJoker }) {
            processJacks()
            processJoker()
            flag = true
        }

        if (cards.any { it.hasActiveUfo || it.hasActivePete }) {
            processUfo()
            processPete()
            flag = true
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
                it.getCazadorPoison(queensBlock)
                flag = true
            }
        }

        return flag
    }

    fun afterPlayerMove(updateView: () -> Unit, speed: AnimationSpeed) {
        val delayLength = speed.delay.coerceAtLeast(95L)

        canPlayerMove = false
        CoroutineScope(Dispatchers.Default).launch {
            updateView()
            delay(delayLength)

            if (processField()) {
                updateView()
                delay(delayLength)
            }
            if (playerCResources.hand.size < 5 && playerCResources.deckSize > 0) {
                playerCResources.addToHand()
                updateView()
                delay(delayLength)
            }
            isPlayerTurn = false

            if (checkOnGameOver()) {
                updateView()
                return@launch
            }

            delay(delayLength * 2) // Just break.

            enemy.makeMove(this@Game)
            updateView()
            delay(delayLength)

            if (processField()) {
                updateView()
                delay(delayLength)
            }
            if (enemyCResources.hand.size < 5 && enemyCResources.deckSize > 0) {
                enemyCResources.addToHand()
                updateView()
                delay(delayLength)
            }

            isPlayerTurn = true

            checkOnGameOver()
            updateView()

            canPlayerMove = true
        }
    }

    @Transient
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

    private fun processJacks() {
        (playerCaravans + enemyCaravans).forEach { caravan ->
            caravan.removeAllJackedCards()
        }
    }

    private fun processBomb(bombOwner: CardWithModifier) {
        val isFBomb = bombOwner.modifiersCopy().lastOrNull()?.isAlt == true
        (playerCaravans + enemyCaravans).forEach {
            if (bombOwner !in it.cards) {
                if (isFBomb) {
                    // TODO: some bonus
                    it.dropCaravan()
                } else {
                    if (it.cards.all { card -> !card.isProtectedByMuggy }) {
                        it.dropCaravan()
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
    private fun processUfo() {
        (playerCaravans + enemyCaravans).forEach { caravan ->
            caravan.cards.filter {
                it.hasActiveUfo
            }.forEach { card ->
                val seed = card.card.rank.ordinal * 4 + card.card.suit.ordinal
                (playerCaravans + enemyCaravans).forEach { caravan ->
                    caravan.getUfo(seed)
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

    fun processJoker() {
        (playerCaravans + enemyCaravans).forEach { caravan ->
            caravan.cards.filter {
                it.hasActiveJoker
            }.forEach {
                putJokerOntoCard(it)
                it.deactivateJoker()
            }
        }
    }

    private fun putJokerOntoCard(cardWithModifier: CardWithModifier) {
        val card = cardWithModifier.card
        if (card.rank == Rank.ACE) {
            (playerCaravans + enemyCaravans).forEach { caravan ->
                caravan.jokerRemoveAllSuits(card)
            }
        } else {
            (playerCaravans + enemyCaravans).forEach { caravan ->
                caravan.jokerRemoveAllRanks(card)
            }
        }
    }
}