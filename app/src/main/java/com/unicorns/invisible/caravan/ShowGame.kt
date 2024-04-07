package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.getCardName
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Deck
import com.unicorns.invisible.caravan.model.primitives.Rank


@Composable
fun ShowGame(activity: MainActivity, game: Game, goBack: () -> Unit) {
    var selectedCard by remember { mutableStateOf<Card?>(null) }
    val selectedCardColor = Color(activity.getColor(R.color.colorAccent))

    var selectedCaravan by remember { mutableIntStateOf(-1) }

    var caravansKey by remember { mutableStateOf(true) }

    fun onCardClicked(index: Int) {
        if (game.isOver()) return
        selectedCard = if (game.playerDeck.hand[index] == selectedCard) {
            null
        } else {
            game.playerDeck.hand[index]
        }
    }

    val state1Enemy = rememberLazyListState()
    val state1Player = rememberLazyListState()
    val state2Enemy = rememberLazyListState()
    val state2Player = rememberLazyListState()
    val state3Enemy = rememberLazyListState()
    val state3Player = rememberLazyListState()

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
                        val handSize = game.enemyDeck.hand.size
                        Column(Modifier.fillMaxWidth(0.8f), horizontalAlignment = Alignment.CenterHorizontally) {
                            RowOfEnemyCards(minOf(4, handSize), game.enemyDeck.back)
                            if (handSize - 4 > 0) {
                                RowOfEnemyCards(handSize - 4, game.enemyDeck.back)
                            }
                        }
                        Deck(game.enemyDeck, activity)
                    }
                    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                    ) {
                        Column(Modifier.fillMaxWidth(0.8f)) {
                            RowOfCards(cards = game.playerDeck.hand.subList(0, minOf(4, game.playerDeck.hand.size)), 0, selectedCard, selectedCardColor, ::onCardClicked)
                            if (game.playerDeck.hand.size >= 5) {
                                RowOfCards(cards = game.playerDeck.hand.subList(4, game.playerDeck.hand.size), 4, selectedCard, selectedCardColor, ::onCardClicked)
                            }
                        }
                        Deck(game.playerDeck, activity, isToBottom = true)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Back to Menu",
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
                        game,
                        activity,
                        { selectedCard },
                        { selectedCaravan },
                        { selectedCaravan = it },
                        { caravansKey = !caravansKey },
                        {
                            selectedCaravan = -1
                            selectedCard = null
                        },
                        isMaxHeight = true,
                        state1Enemy,
                        state1Player,
                        state2Enemy,
                        state2Player,
                        state3Enemy,
                        state3Player,
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
                    val handSize = game.enemyDeck.hand.size
                    Column(Modifier.fillMaxWidth(0.8f), horizontalAlignment = Alignment.CenterHorizontally) {
                        RowOfEnemyCards(minOf(4, handSize), game.enemyDeck.back)
                        if (handSize - 4 > 0) {
                            RowOfEnemyCards(handSize - 4, game.enemyDeck.back)
                        }
                    }
                    Deck(game.enemyDeck, activity)
                }
                key(caravansKey) {
                    Caravans(
                        game,
                        activity,
                        { selectedCard },
                        { selectedCaravan },
                        { selectedCaravan = it },
                        { caravansKey = !caravansKey },
                        {
                            selectedCaravan = -1
                            selectedCard = null
                        },
                        isMaxHeight = false,
                        state1Enemy,
                        state1Player,
                        state2Enemy,
                        state2Player,
                        state3Enemy,
                        state3Player,
                    )
                }
                Row(verticalAlignment = Alignment.Bottom, modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                ) {
                    Column(Modifier.fillMaxWidth(0.8f)) {
                        RowOfCards(cards = game.playerDeck.hand.subList(0, minOf(4, game.playerDeck.hand.size)), 0, selectedCard, selectedCardColor, ::onCardClicked)
                        if (game.playerDeck.hand.size >= 5) {
                            RowOfCards(cards = game.playerDeck.hand.subList(4, game.playerDeck.hand.size), 4, selectedCard, selectedCardColor, ::onCardClicked)
                        }
                    }
                    Deck(game.playerDeck, activity, isToBottom = true)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Back to Menu",
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

@Composable
fun ColumnScope.RowOfCards(cards: List<Card>, offset: Int = 0, selectedCard: Card?, selectedCardColor: Color, onClick: (Int) -> Unit) {
    Row(Modifier.weight(1f).fillMaxHeight().fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        cards.forEachIndexed { index, it ->
            val modifier = if (it == selectedCard) {
                Modifier.border(
                    width = 4.dp,
                    color = selectedCardColor
                )
            } else {
                Modifier
            }
                .clickable {
                    onClick(offset + index)
                }
                .padding(4.dp)
                .weight(1f, fill = false)
            AsyncImage(
                model = "file:///android_asset/caravan_cards/${getCardName(it)}",
                contentDescription = "",
                modifier
            )
        }
    }
}

@Composable
fun ColumnScope.RowOfEnemyCards(numOfCards: Int, back: CardBack) {
    if (numOfCards <= 0) {
        return
    }
    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
        repeat(numOfCards) {
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/${back.getCardBackName()}",
                contentDescription = ""
            )
        }
    }
}

@Composable
fun CaravanOnField(
    activity: MainActivity,
    caravan: Caravan,
    isEnemy: Boolean,
    state: LazyListState,
    selectCaravan: () -> Unit = {},
    addSelectedCardOnPosition: (Int) -> Unit,
) {
    if (isEnemy) {
        LazyColumn(
            state = state,
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight(0.4f)
                .fillMaxWidth()
        ) {
            itemsIndexed(caravan.cards.reversed()) { index, it ->
                Box(modifier = Modifier
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val height = if (index != 0) {
                            placeable.height / 3
                        } else {
                            placeable.height
                        }
                        val offsetWidth = constraints.maxWidth / 2 - placeable.width / 2
                        val offsetHeight = if (index != 0) {
                            -2 * placeable.height / 3
                        } else {
                            0
                        }
                        layout(constraints.maxWidth, height) {
                            placeable.place(offsetWidth, offsetHeight)
                        }
                    }
                    .zIndex((caravan.cards.size - index).toFloat())
                ) {
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards/${getCardName(it.card)}",
                        contentDescription = "",
                        modifier = Modifier.clickable {
                            addSelectedCardOnPosition(caravan.cards.lastIndex - index)
                        }
                    )
                    it.modifiers.forEachIndexed { index, card ->
                        AsyncImage(
                            model = "file:///android_asset/caravan_cards/${getCardName(card)}",
                            contentDescription = "",
                            modifier = Modifier.offset(x = -(10.dp) * (index + 1))
                        )
                    }
                }
            }
        }
        Text(text = caravan.getValue().toString(),
            textAlign = TextAlign.Center,
            color = if (caravan.getValue() > 26) Color.Red else Color(activity.getColor(R.color.colorAccent)),
            modifier = Modifier
                .fillMaxWidth()
        )
    } else {
        Text(text = caravan.getValue().toString(),
            textAlign = TextAlign.Center,
            color = if (caravan.getValue() > 26) Color.Red else Color(activity.getColor(R.color.colorAccent)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    selectCaravan()
                }
        )
        LazyColumn(
            state = state,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            itemsIndexed(caravan.cards) { index, it ->
                Box(modifier = Modifier
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        val height = if (index != caravan.cards.lastIndex) {
                            placeable.height / 3
                        } else {
                            placeable.height
                        }
                        val offsetWidth = constraints.maxWidth / 2 - placeable.width / 2
                        layout(constraints.maxWidth, height) {
                            placeable.place(offsetWidth, 0)
                        }
                    }) {
                    AsyncImage(
                        model = "file:///android_asset/caravan_cards/${getCardName(it.card)}",
                        contentDescription = "",
                        modifier = Modifier.clickable {
                            addSelectedCardOnPosition(index)
                        }
                    )
                    it.modifiers.forEachIndexed { index, card ->
                        AsyncImage(
                            model = "file:///android_asset/caravan_cards/${getCardName(card)}",
                            contentDescription = "",
                            modifier = Modifier.offset(x = (10.dp) * (index + 1))
                        )
                    }
                }
            }
            if (!caravan.isFull()) {
                item {
                    Box(modifier = Modifier
                        .fillParentMaxWidth()
                        .height(20.dp)
                        .background(Color(activity.getColor(R.color.colorPrimary)))
                        .border(4.dp, Color(activity.getColor(R.color.colorAccent)))
                        .clickable {
                            addSelectedCardOnPosition(caravan.cards.size)
                        }
                    ) {}
                }
            }
        }
    }
}

@Composable
fun Deck(deck: Deck, activity: MainActivity, isToBottom: Boolean = false) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = if (isToBottom) Arrangement.Bottom else Arrangement.Top
    ) {
        Text(
            text = deck.deckSize.toString(),
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(color = Color(activity.getColor(R.color.colorAccent)), fontSize = 16.sp, textAlign = TextAlign.Center)
        )
        AsyncImage(
            model = "file:///android_asset/caravan_cards_back/${deck.back.getCardBackName()}",
            contentDescription = "",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun Caravans(
    game: Game,
    activity: MainActivity,
    getSelectedCard: () -> Card?,
    getSelectedCaravan: () -> Int,
    setSelectedCaravan: (Int) -> Unit,
    updateCaravans: () -> Unit,
    resetSelected: () -> Unit,
    isMaxHeight: Boolean = false,
    state1Enemy: LazyListState,
    state1Player: LazyListState,
    state2Enemy: LazyListState,
    state2Player: LazyListState,
    state3Enemy: LazyListState,
    state3Player: LazyListState,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(if (isMaxHeight) 1f else 0.65f),
    ) {
        fun addCardToCaravan(caravan: Caravan, position: Int, isEnemy: Boolean = false) {
            fun onCaravanCardInserted(card: Card) {
                game.playerDeck.hand.remove(card)
                game.afterPlayerMove { updateCaravans() }
                resetSelected()
                updateCaravans()
            }

            val card = getSelectedCard()
            if (card != null && game.isPlayerTurn) {
                when (card.rank.value) {
                    in 1..10 -> {
                        if (position == caravan.cards.size && !isEnemy) {
                            if (caravan.putCardOnTop(card)) {
                                onCaravanCardInserted(card)
                            }
                        }
                    }
                    Rank.JACK.value -> {
                        if (position in caravan.cards.indices) {
                            caravan.cards[position].modifiers.add(card)
                            onCaravanCardInserted(card)
                        }
                    }
                    Rank.QUEEN.value, Rank.KING.value -> {
                        if (position in caravan.cards.indices && caravan.cards[position].modifiers.size < 3) {
                            caravan.cards[position].modifiers.add(card)
                            onCaravanCardInserted(card)
                        }
                    }
                    Rank.JOKER.value -> {
                        if (position in caravan.cards.indices && caravan.cards[position].modifiers.size < 3) {
                            caravan.cards[position].modifiers.add(card)
                            game.putJokerOntoCard(caravan.cards[position].card)
                            onCaravanCardInserted(card)
                        }
                    }
                }
            }
        }
        fun addCardToEnemyCaravan(caravanNum: Int, position: Int) {
            addCardToCaravan(game.enemyCaravans[caravanNum], position, isEnemy = true)
        }
        fun addCardToPlayerCaravan(caravanNum: Int, position: Int) {
            addCardToCaravan(game.playerCaravans[caravanNum], position, isEnemy = false)
        }

        @Composable
        fun CaravansColumn(num: Int, enemyLazyListState: LazyListState, playerLazyListState: LazyListState) {
            Column(modifier = Modifier
                .weight(0.25f)
                .fillMaxHeight()) {
                CaravanOnField(activity, game.enemyCaravans[num], true, enemyLazyListState) {
                    addCardToEnemyCaravan(num, it)
                }
                HorizontalDivider()
                CaravanOnField(activity, game.playerCaravans[num], false, playerLazyListState, {
                    setSelectedCaravan(if (getSelectedCaravan() == num || game.playerCaravans[num].getValue() == 0) {
                        -1
                    } else {
                        num
                    })
                }) {
                    addCardToPlayerCaravan(num, it)
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.03f))
        CaravansColumn(
            num = 0,
            enemyLazyListState = state1Enemy,
            playerLazyListState = state1Player
        )
        CaravansColumn(
            num = 1,
            enemyLazyListState = state2Enemy,
            playerLazyListState = state2Player
        )
        CaravansColumn(
            num = 2,
            enemyLazyListState = state3Enemy,
            playerLazyListState = state3Player
        )
        Column(modifier = Modifier
            .weight(0.22f)
            .fillMaxHeight(), verticalArrangement = Arrangement.Center) {
            Text(
                text = when {
                    game.isOver() -> ""
                    getSelectedCard() != null -> {
                        "DISCARD CARD"
                    }
                    getSelectedCaravan() in (0..2) -> {
                        "DROP CARAVAN #${getSelectedCaravan() + 1}"
                    }
                    else -> ""
                },
                textAlign = TextAlign.Center,
                color = Color(activity.getColor(R.color.colorAccent)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (!game.isPlayerTurn) return@clickable
                        if (getSelectedCard() != null) {
                            game.playerDeck.hand.remove(getSelectedCard())
                            resetSelected()
                            game.afterPlayerMove { updateCaravans() }
                        } else if (getSelectedCaravan() in (0..2)) {
                            game.playerCaravans[getSelectedCaravan()].dropCaravan()
                            updateCaravans()
                            resetSelected()
                            game.afterPlayerMove { updateCaravans() }
                        }
                    }
            )
        }
    }
}