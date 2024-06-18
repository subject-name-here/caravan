package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.EnemyPlayer
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.multiplayer.MoveResponse
import com.unicorns.invisible.caravan.multiplayer.decodeMove
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playVatsReady
import com.unicorns.invisible.caravan.utils.sendRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


fun afterPlayerMove(
    game: Game,
    hTick: Long,
    roomNumber: Int,
    isCreator: Boolean,
    move: MoveResponse,
    chosenSymbol: Int,
    updateView: () -> Unit,
    corrupt: (String) -> Unit,
    startPinging: () -> Unit,
) {

    CoroutineScope(Dispatchers.Default).launch {
        delay(hTick * 2)
        game.isPlayerTurn = false
        val isNewCardAdded = game.playerCResources.deckSize > 0 && game.playerCResources.hand.size < 5
        if (game.processField()) {
            delay(hTick * 2) // Remove cards; move cards within caravan
            updateView()
        }
        if (game.processHand(game.playerCResources)) {
            delay(hTick) // Take card into hand
            updateView()
        }

        if (isNewCardAdded) {
            val newCard = game.playerCResources.hand.last()
            move.newCardInHandBack = newCard.back.ordinal
            move.newCardInHandSuit = newCard.suit.ordinal
            move.newCardInHandRank = newCard.rank.ordinal
            move.isNewCardAlt = newCard.isAlt
        } else {
            move.newCardInHandBack = -1
        }
        game.checkOnGameOver()

        sendRequest(
            "$crvnUrl/crvn/move?room=$roomNumber" +
                    "&is_creators_move=${isCreator.toPythonBool()}" +
                    "&symbol=$chosenSymbol" +
                    "&move_code=${move.moveCode}" +
                    "&caravan_code=${move.caravanCode}" +
                    "&hand_card_number=${move.handCardNumber}" +
                    "&card_in_caravan_number=${move.cardInCaravanNumber}" +
                    "&new_card_back_in_hand_code=${move.newCardInHandBack}" +
                    "&new_card_rank_in_hand_code=${move.newCardInHandRank}" +
                    "&new_card_suit_in_hand_code=${move.newCardInHandSuit}" +
                    "&is_alt=${move.isNewCardAlt.toPythonBool()}"
        ) { result ->
            if (result.toString().contains("oom")) {
                corrupt(result.toString())
                return@sendRequest
            }
            if (game.isOver()) {
                return@sendRequest
            }
            startPinging()
        }
    }
}


fun pingForMove(
    game: Game, hTick: Long, room: Int, isCreator: Boolean,
    setEnemySymbol: (Int) -> Unit,
    corrupt: (String) -> Unit,
    updateView: () -> Unit,
    afterEnemyMove: (Boolean) -> Unit
) {
    sendRequest("$crvnUrl/crvn/get_move?room=$room&is_creators_move=${isCreator.toPythonBool()}") { result ->
        if (result.getString("body") == "-1") {
            CoroutineScope(Dispatchers.Unconfined).launch {
                delay(760L)
                pingForMove(game, hTick, room, isCreator, setEnemySymbol, corrupt, updateView, afterEnemyMove)
            }
            return@sendRequest
        }

        try {
            val enemyMove = decodeMove(result.getString("body"))
            (game.enemy as EnemyPlayer).latestMoveResponse = enemyMove
            setEnemySymbol(enemyMove.symbolNumber)
        } catch (e: Exception) {
            corrupt(result.toString())
            return@sendRequest
        }

        CoroutineScope(Dispatchers.Default).launch {
            delay(hTick) // Just break.
            game.enemy.makeMove(game)
            updateView()
            delay(hTick * 2) // Move card from hand; move card ontoField
            if (game.processField()) {
                delay(hTick * 2)
                updateView()
            }
            if (game.processHand(game.enemyCResources)) {
                delay(hTick)
                updateView()
            }
            game.isPlayerTurn = true
            game.checkOnGameOver()
            updateView()
            delay(hTick)
            afterEnemyMove(!game.isOver())
        }
    }
}


@Composable
fun ShowGamePvP(
    activity: MainActivity,
    game: Game,
    isCreator: Boolean,
    roomNumber: Int,
    showAlert: (String, String) -> Unit,
    enemyHandKey: Boolean,
    updateEnemyHand: () -> Unit,
    updateCaravans: () -> Unit,
    goBack: () -> Unit,
) {
    val hTick = activity.animationTickLength.value!! / 2
    var selectedCard by remember { mutableStateOf<Int?>(null) }

    var selectedCaravan by remember { mutableIntStateOf(-1) }

    var chosenSymbol by rememberSaveable { mutableIntStateOf(0) }
    var enemyChosenSymbol by rememberSaveable { mutableIntStateOf(0) }

    var timeOnTimer by rememberSaveable { mutableIntStateOf(0) }
    var timeOnTimerTrigger by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = timeOnTimerTrigger) {
        if (game.isPlayerTurn) {
            timeOnTimer = 38
            while (isActive && timeOnTimer > 0) {
                timeOnTimer--
                delay(1000L)
            }
        } else {
            timeOnTimer = -1
        }
    }

    fun onCardClicked(index: Int) {
        if (game.isOver()) {
            return
        }
        selectedCard = if (index == selectedCard) {
            null
        } else {
            index
        }
        if (selectedCard == null) {
            playCloseSound(activity)
        } else {
            playSelectSound(activity)
        }
        selectedCaravan = -1
    }

    fun corruptGame(message: String) {
        game.isCorrupted = true
        if (currentGameId == game.id) {
            showAlert("Corrupted!", message)
        }
    }

    fun resetSelected() {
        selectedCaravan = -1
        selectedCard = null
        timeOnTimer = 0
    }
    fun dropCardFromHand() {
        if (game.isExchangingCards) return
        val selectedCardNN = selectedCard ?: return
        playVatsReady(activity)
        game.playerCResources.removeFromHand(selectedCardNN)
        resetSelected()
        afterPlayerMove(game, hTick, roomNumber, isCreator = isCreator, MoveResponse(
            moveCode = 2,
            handCardNumber = selectedCardNN,
        ), chosenSymbol,
            { updateCaravans(); updateEnemyHand() },
            ::corruptGame,
            {
                pingForMove(
                    game, hTick, roomNumber, isCreator,
                    { enemyChosenSymbol = it },
                    ::corruptGame,
                    { updateCaravans(); updateEnemyHand() },
                    { if (it) { timeOnTimerTrigger = !timeOnTimerTrigger } }
                )
            }
        )
    }
    fun dropCaravan() {
        if (game.isExchangingCards) return
        val selectedCaravanNN = selectedCaravan
        if (selectedCaravanNN == -1) return
        playVatsReady(activity)
        game.playerCaravans[selectedCaravanNN].dropCaravan()
        updateCaravans()
        resetSelected()
        afterPlayerMove(game, hTick, roomNumber, isCreator = isCreator, MoveResponse(
            moveCode = 1,
            caravanCode = selectedCaravanNN,
        ), chosenSymbol,
            { updateCaravans(); updateEnemyHand() },
            ::corruptGame,
            {
                pingForMove(
                    game, hTick, roomNumber, isCreator,
                    { enemyChosenSymbol = it },
                    ::corruptGame,
                    { updateCaravans(); updateEnemyHand() },
                    { if (it) { timeOnTimerTrigger = !timeOnTimerTrigger } }
                )
            }
        )
    }

    fun addCardToCaravan(caravan: Caravan, caravanIndex: Int, position: Int, isEnemy: Boolean = false) {
        if (game.isExchangingCards) return
        fun onCaravanCardInserted(cardIndex: Int, caravanIndex: Int, cardInCaravan: Int? = null) {
            resetSelected()
            updateCaravans()
            if (cardInCaravan == null) {
                afterPlayerMove(game, hTick, roomNumber, isCreator = isCreator, MoveResponse(
                    moveCode = 3,
                    handCardNumber = cardIndex,
                    caravanCode = caravanIndex
                ), chosenSymbol,
                    { updateCaravans(); updateEnemyHand() },
                    ::corruptGame,
                    {
                        pingForMove(
                            game, hTick, roomNumber, isCreator,
                            { enemyChosenSymbol = it },
                            ::corruptGame,
                            { updateCaravans(); updateEnemyHand() },
                            { if (it) { timeOnTimerTrigger = !timeOnTimerTrigger } }
                        )
                    }
                )
            } else {
                afterPlayerMove(game, hTick, roomNumber, isCreator = isCreator, MoveResponse(
                    moveCode = 4,
                    handCardNumber = cardIndex,
                    cardInCaravanNumber = cardInCaravan,
                    caravanCode = if (isEnemy) (-3 + caravanIndex) else caravanIndex
                ), chosenSymbol,
                    { updateCaravans(); updateEnemyHand() },
                    ::corruptGame,
                    {
                        pingForMove(
                            game, hTick, roomNumber, isCreator,
                            { enemyChosenSymbol = it },
                            ::corruptGame,
                            { updateCaravans(); updateEnemyHand() },
                            { if (it) { timeOnTimerTrigger = !timeOnTimerTrigger } }
                        )
                    }
                )
            }
        }

        val cardIndex = selectedCard
        val card = cardIndex?.let { game.playerCResources.hand[cardIndex] }
        if (card != null && game.isPlayerTurn && !game.isOver() && (!game.isInitStage() || !card.isFace())) {
            when (card.rank.value) {
                in 1..10 -> {
                    if (position == caravan.cards.size && !isEnemy) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.playerCResources.removeFromHand(cardIndex))
                            onCaravanCardInserted(
                                cardIndex, caravanIndex, null,
                            )
                        }
                    }
                }
                Rank.JACK.value, Rank.QUEEN.value, Rank.KING.value, Rank.JOKER.value -> {
                    if (position in caravan.cards.indices && caravan.cards[position].canAddModifier(card)) {
                        if (card.rank == Rank.JOKER) {
                            playJokerSounds(activity)
                        }
                        caravan.cards[position].addModifier(game.playerCResources.removeFromHand(cardIndex))
                        onCaravanCardInserted(
                            cardIndex, caravanIndex, position
                        )
                    }
                }
            }
        }
    }

    val symbols = listOf(
        "\uD83D\uDC4B", // HI!
        "\uD83D\uDC4F", // Good move
        "\uD83D\uDE0A", // Happy face
        "\uD83D\uDE15", // Errmmm...
        "\uD83D\uDE28", // Devastated
        "\uD83D\uDE0E", // deal with it.
        "\uD83E\uDD2F", // mind-blowing move
        "\uD83D\uDC80", // death
        "\uD83D\uDE08", // Devil happy
        "\uD83D\uDC7F", // Devil angry
    )

    ShowGameRaw(
        activity,
        true,
        game,
        goBack,
        getEnemySymbol = { symbols[enemyChosenSymbol.coerceIn(0, symbols.size - 1)] },
        getMySymbol = { symbols[chosenSymbol.coerceIn(0, symbols.size - 1)] },
        setMySymbol = { chosenSymbol = (chosenSymbol + 1) % symbols.size },
        ::onCardClicked,
        selectedCard,
        getSelectedCaravan = { selectedCaravan },
        setSelectedCaravan = lambda@ {
            if (game.isOver()) {
                return@lambda
            }
            selectedCaravan = it
            if (selectedCaravan == -1) {
                playCloseSound(activity)
            } else {
                playSelectSound(activity)
            }
            selectedCard = null
            updateCaravans()
        },
        ::addCardToCaravan,
        ::dropCardFromHand,
        ::dropCaravan,
        if (enemyHandKey) 0 else 1,
    )

    key (timeOnTimer) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Transparent), contentAlignment = Alignment.BottomEnd) {
            TextFallout(
                timeOnTimer.toString(),
                getTextColor(activity),
                getTextStrokeColor(activity),
                14.sp,
                Alignment.BottomEnd,
                Modifier
                    .background(getTextBackgroundColor(activity))
                    .padding(8.dp),
                TextAlign.Center
            )
        }
    }
}
