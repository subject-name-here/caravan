package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.EnemyPlayer
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.multiplayer.MoveResponse
import com.unicorns.invisible.caravan.multiplayer.decodeMove
import com.unicorns.invisible.caravan.utils.sendRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


fun afterPlayerMove(
    game: Game,
    roomNumber: Int,
    isCreator: Boolean,
    isUtil: Boolean = false,
    move: MoveResponse,
    updateView: () -> Unit,
    corrupt: (String) -> Unit,
) {
    game.isPlayerTurn = false
    CoroutineScope(Dispatchers.Default).launch {
        delay(350L)
        val isNewCardAdded = game.playerCResources.deckSize > 0 && game.playerCResources.hand.size < 5
        game.processFieldAndHand(game.playerCResources, updateView)
        if (isNewCardAdded) {
            val newCard = game.playerCResources.hand.last()
            move.newCardInHandBack = newCard.back.ordinal
            move.newCardInHandSuit = newCard.suit.ordinal
            move.newCardInHandRank = newCard.rank.ordinal
        } else {
            move.newCardInHandBack = -1
        }
        game.checkOnGameOver()

        sendRequest(
            "http://crvnserver.onrender.com/crvn/move?room=$roomNumber" +
                    "&is_creators_move=${isCreator.toPythonBool()}" +
                    "&is_util=${isUtil.toPythonBool()}" +
                    "&move_code=${move.moveCode}" +
                    "&caravan_code=${move.caravanCode}" +
                    "&hand_card_number=${move.handCardNumber}" +
                    "&card_in_caravan_number=${move.cardInCaravanNumber}" +
                    "&new_card_back_in_hand_code=${move.newCardInHandBack}" +
                    "&new_card_rank_in_hand_code=${move.newCardInHandRank}" +
                    "&new_card_suit_in_hand_code=${move.newCardInHandSuit}"
        ) { result ->
            if (game.isOver()) {
                return@sendRequest
            }
            try {
                (game.enemy as EnemyPlayer).latestMoveResponse = decodeMove(result.getString("body"))
            } catch (e: Exception) {
                corrupt(result.toString())
                return@sendRequest
            }

            CoroutineScope(Dispatchers.Default).launch {
                game.enemy.makeMove(game)
                delay(350L)
                updateView()
                game.processFieldAndHand(game.enemyCResources, updateView)

                game.isPlayerTurn = true
                game.checkOnGameOver()
                updateView()
            }
        }
    }
}


@Composable
fun ShowGamePvP(activity: MainActivity, game: Game, isCreator: Boolean, roomNumber: Int, showAlert: (String, String) -> Unit, goBack: () -> Unit) {
    var selectedCard by remember { mutableStateOf<Int?>(null) }
    val selectedCardColor = Color(activity.getColor(R.color.colorAccent))

    var selectedCaravan by remember { mutableIntStateOf(-1) }

    var caravansKey by remember { mutableStateOf(true) }
    var enemyHandKey by remember { mutableStateOf(true) }

    game.enemyCResources.onRemoveFromHand = { enemyHandKey = !enemyHandKey }

    fun onCardClicked(index: Int) {
        if (game.isOver()) {
            return
        }
        selectedCard = if (index == selectedCard) null else index
        selectedCaravan = -1
    }

    fun corruptGame(message: String) {
        game.isCorrupted = true
        showAlert("Corrupted!", message)
        goBack()
    }

    val state1Enemy = rememberLazyListState()
    val state1Player = rememberLazyListState()
    val state2Enemy = rememberLazyListState()
    val state2Player = rememberLazyListState()
    val state3Enemy = rememberLazyListState()
    val state3Player = rememberLazyListState()

    fun updateCaravans() {
        caravansKey = !caravansKey
    }
    fun updateEnemyHand() {
        enemyHandKey = !enemyHandKey
    }
    fun resetSelected() {
        selectedCaravan = -1
        selectedCard = null
    }
    fun dropCardFromHand() {
        if (game.isExchangingCards) return
        val selectedCardNN = selectedCard ?: return
        game.playerCResources.removeFromHand(selectedCardNN)
        resetSelected()
        afterPlayerMove(game, roomNumber, isCreator = isCreator, isUtil = false, MoveResponse(
            moveCode = 2,
            handCardNumber = selectedCardNN,
        ), { updateCaravans(); updateEnemyHand() }, ::corruptGame)
    }
    fun dropCaravan() {
        if (game.isExchangingCards) return
        val selectedCaravanNN = selectedCaravan
        if (selectedCaravanNN == -1) return
        game.playerCaravans[selectedCaravanNN].dropCaravan()
        updateCaravans()
        resetSelected()
        afterPlayerMove(game, roomNumber, isCreator = isCreator, isUtil = false, MoveResponse(
            moveCode = 1,
            caravanCode = selectedCaravanNN,
        ), { updateCaravans(); updateEnemyHand() }, ::corruptGame)
    }

    fun addCardToCaravan(caravan: Caravan, caravanIndex: Int, position: Int, isEnemy: Boolean = false) {
        if (game.isExchangingCards) return
        fun onCaravanCardInserted(cardIndex: Int, caravanIndex: Int, cardInCaravan: Int? = null) {
            resetSelected()
            updateCaravans()
            if (cardInCaravan == null) {
                afterPlayerMove(game, roomNumber, isCreator = isCreator, isUtil = false, MoveResponse(
                    moveCode = 3,
                    handCardNumber = cardIndex,
                    caravanCode = caravanIndex
                ), { updateCaravans(); updateEnemyHand() }, ::corruptGame)
            } else {
                afterPlayerMove(game, roomNumber, isCreator = isCreator, isUtil = false, MoveResponse(
                    moveCode = 4,
                    handCardNumber = cardIndex,
                    cardInCaravanNumber = cardInCaravan,
                    caravanCode = if (isEnemy) (-3 + caravanIndex) else caravanIndex
                ), { updateCaravans(); updateEnemyHand() }, ::corruptGame)
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
                        caravan.cards[position].addModifier(game.playerCResources.removeFromHand(cardIndex))
                        onCaravanCardInserted(
                            cardIndex, caravanIndex, position
                        )
                    }
                }
            }
        }
    }
    fun addCardToEnemyCaravan(caravanNum: Int, position: Int) {
        addCardToCaravan(game.enemyCaravans[caravanNum], caravanNum, position, isEnemy = true)
    }
    fun addCardToPlayerCaravan(caravanNum: Int, position: Int) {
        addCardToCaravan(game.playerCaravans[caravanNum], caravanNum, position, isEnemy = false)
    }
    fun isInitStage(): Boolean {
        return game.isInitStage()
    }
    fun canDiscard(): Boolean {
        return !(game.isOver() || !game.isPlayerTurn || game.isInitStage())
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(activity.getColor(R.color.colorPrimaryDark)))
    ) {
        if (maxWidth > maxHeight) {
            Row(Modifier.fillMaxSize()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.4f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        key(enemyHandKey) {
                            val handSize = game.enemyCResources.hand.size
                            Column(Modifier.fillMaxWidth(0.8f), horizontalAlignment = Alignment.CenterHorizontally) {
                                RowOfEnemyCards(game.enemyCResources.hand.take(4))
                                RowOfEnemyCards(game.enemyCResources.hand.takeLast((handSize - 4).coerceAtLeast(0)))
                            }
                            ShowDeck(game.enemyCResources, activity, isKnown = false)
                        }
                    }
                    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                    ) {
                        val handSize = game.playerCResources.hand.size
                        Column(Modifier.fillMaxWidth(0.8f)) {
                            RowOfCards(cards = game.playerCResources.hand.subList(0, minOf(4, handSize)), 0, selectedCard, selectedCardColor, ::onCardClicked)
                            val cards = if (handSize >= 5) {
                                game.playerCResources.hand.subList(4, handSize)
                            } else {
                                emptyList()
                            }
                            RowOfCards(cards = cards, 4, selectedCard, selectedCardColor, ::onCardClicked)
                        }
                        ShowDeck(game.playerCResources, activity, isToBottom = true)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = stringResource(R.string.back_to_menu),
                            modifier = Modifier
                                .clickable {
                                    goBack()
                                }
                                .fillMaxWidth(),
                            style = TextStyle(color = Color(activity.getColor(R.color.colorAccent)), fontSize = 16.sp, textAlign = TextAlign.Center),
                        )
                    }
                }
                key(caravansKey) {
                    Caravans(
                        activity,
                        { selectedCard },
                        { selectedCaravan },
                        {
                            selectedCaravan = it
                            selectedCard = null
                            caravansKey = !caravansKey
                        },
                        isMaxHeight = true,
                        state1Enemy,
                        state1Player,
                        state2Enemy,
                        state2Player,
                        state3Enemy,
                        state3Player,
                        ::addCardToPlayerCaravan,
                        ::addCardToEnemyCaravan,
                        ::dropCardFromHand,
                        ::dropCaravan,
                        ::isInitStage,
                        { game.isPlayerTurn },
                        ::canDiscard,
                        { num -> game.playerCaravans[num] },
                        { num -> game.enemyCaravans[num] },
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.15f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val handSize = game.enemyCResources.hand.size
                    key(enemyHandKey) {
                        Column(Modifier.fillMaxWidth(0.8f), horizontalAlignment = Alignment.CenterHorizontally) {
                            RowOfEnemyCards(game.enemyCResources.hand.take(4))
                            RowOfEnemyCards(game.enemyCResources.hand.takeLast((handSize - 4).coerceAtLeast(0)))
                        }
                        ShowDeck(game.enemyCResources, activity, isKnown = false)
                    }
                }
                key(caravansKey) {
                    Caravans(
                        activity,
                        { selectedCard },
                        { selectedCaravan },
                        {
                            selectedCaravan = it
                            selectedCard = null
                            caravansKey = !caravansKey
                        },
                        isMaxHeight = false,
                        state1Enemy,
                        state1Player,
                        state2Enemy,
                        state2Player,
                        state3Enemy,
                        state3Player,
                        ::addCardToPlayerCaravan,
                        ::addCardToEnemyCaravan,
                        ::dropCardFromHand,
                        ::dropCaravan,
                        ::isInitStage,
                        { game.isPlayerTurn },
                        ::canDiscard,
                        { num -> game.playerCaravans[num] },
                        { num -> game.enemyCaravans[num] },
                    )
                }
                Row(verticalAlignment = Alignment.Bottom, modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                ) {
                    Column(Modifier.fillMaxWidth(0.8f)) {
                        RowOfCards(cards = game.playerCResources.hand.subList(0, minOf(4, game.playerCResources.hand.size)), 0, selectedCard, selectedCardColor, ::onCardClicked)
                        val cards = if (game.playerCResources.hand.size >= 5) {
                            game.playerCResources.hand.subList(4, game.playerCResources.hand.size)
                        } else {
                            emptyList()
                        }
                        RowOfCards(cards = cards, 4, selectedCard, selectedCardColor, ::onCardClicked)
                    }
                    ShowDeck(game.playerCResources, activity, isToBottom = true)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(R.string.back_to_menu),
                        modifier = Modifier
                            .clickable {
                                goBack()
                            }
                            .fillMaxWidth(),
                        style = TextStyle(color = Color(activity.getColor(R.color.colorAccent)), fontSize = 16.sp, textAlign = TextAlign.Center),
                    )
                }
            }
        }
    }
}
