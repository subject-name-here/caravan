package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.getCardName
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank


@Composable
fun ShowGame(activity: MainActivity, game: Game, goBack: () -> Unit) {
    var selectedCard by remember { mutableStateOf<Card?>(null) }
    val selectedCardColor = Color(activity.getColor(R.color.colorAccent))

    var selectedCaravan by remember { mutableIntStateOf(-1) }

    var caravansKey by remember { mutableStateOf(true) }

    fun onCardClicked(index: Int) {
        selectedCard = if (game.playerDeck.hand[index] == selectedCard) {
            null
        } else {
            game.playerDeck.hand[index]
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(activity.getColor(R.color.colorPrimaryDark)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            BoxWithConstraints(modifier = Modifier
                .fillMaxWidth(0.75f)
                .fillMaxHeight(),
            ) {
                if (maxWidth < 400.dp) {
                    val handSize = game.enemyDeck.hand.size
                    Column {
                        RowOfEnemyCards(minOf(4, handSize), game.enemyDeck.back)
                        if (handSize - 4 > 0) {
                            RowOfEnemyCards(handSize - 4, game.enemyDeck.back)
                        }
                    }
                } else {
                    Row {
                        RowOfEnemyCards(game.enemyDeck.hand.size, game.enemyDeck.back)
                    }
                }
            }
            Column(modifier = Modifier.fillMaxHeight()) {
                Text(
                    text = game.enemyDeck.deckSize.toString(),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorAccent)), fontSize = 16.sp, textAlign = TextAlign.Center)
                )
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/${game.enemyDeck.back.getCardBackName()}",
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        val state1Enemy = rememberLazyListState()
        val state1Player = rememberLazyListState()
        val state2Enemy = rememberLazyListState()
        val state2Player = rememberLazyListState()
        val state3Enemy = rememberLazyListState()
        val state3Player = rememberLazyListState()
        key(caravansKey) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f),
            ) {
                fun addCardToCaravan(caravan: Caravan, position: Int, isEnemy: Boolean = false) {
                    fun onCaravanCardInserted(card: Card) {
                        game.playerDeck.hand.remove(card)
                        game.afterPlayerMove { caravansKey = !caravansKey }
                        selectedCard = null
                        selectedCaravan = -1
                        caravansKey = !caravansKey
                    }

                    val card = selectedCard
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
                                    caravan.cards.removeAt(position)
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
                                // TODO
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

                Spacer(modifier = Modifier.weight(0.03f))
                Column(modifier = Modifier
                    .weight(0.25f)
                    .fillMaxHeight()) {
                    CaravanOnField(activity, game.enemyCaravans[0], true, state1Enemy) {
                        addCardToEnemyCaravan(0, it)
                    }
                    HorizontalDivider()
                    CaravanOnField(activity, game.playerCaravans[0], false, state1Player, {
                        selectedCaravan = if (selectedCaravan == 0 || game.playerCaravans[0].getValue() == 0) {
                            -1
                        } else {
                            0
                        }
                    }) {
                        addCardToPlayerCaravan(0, it)
                    }
                }
                Column(modifier = Modifier
                    .weight(0.25f)
                    .fillMaxHeight()) {
                    CaravanOnField(activity, game.enemyCaravans[1], true, state2Enemy) {
                        addCardToEnemyCaravan(1, it)
                    }
                    HorizontalDivider()
                    CaravanOnField(activity, game.playerCaravans[1], false, state2Player, {
                        selectedCaravan = if (selectedCaravan == 1 || game.playerCaravans[1].getValue() == 0) {
                            -1
                        } else {
                            1
                        }
                    }) {
                        addCardToPlayerCaravan(1, it)
                    }
                }
                Column(modifier = Modifier
                    .weight(0.25f)
                    .fillMaxHeight()) {
                    CaravanOnField(activity, game.enemyCaravans[2], true, state3Enemy) {
                        addCardToEnemyCaravan(2, it)
                    }
                    HorizontalDivider()
                    CaravanOnField(activity, game.playerCaravans[2], false, state3Player, {
                        selectedCaravan = if (selectedCaravan == 2 || game.playerCaravans[2].getValue() == 0) {
                            -1
                        } else {
                            2
                        }
                    }) {
                        addCardToPlayerCaravan(2, it)
                    }
                }
                Column(modifier = Modifier
                    .weight(0.22f)
                    .fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                    Text(
                        text = when {
                            selectedCard != null -> {
                                "DISCARD CARD"
                            }
                            selectedCaravan in (0..2) -> {
                                "DROP CARAVAN #${selectedCaravan}"
                            }
                            else -> ""
                        },
                        textAlign = TextAlign.Center,
                        color = Color(activity.getColor(R.color.colorAccent)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (!game.isPlayerTurn) return@clickable
                                if (selectedCard != null) {
                                    game.playerDeck.hand.remove(selectedCard)
                                    selectedCard = null
                                    selectedCaravan = -1
                                    game.afterPlayerMove { caravansKey = !caravansKey }
                                } else if (selectedCaravan in (0..2)) {
                                    game.playerCaravans[selectedCaravan].dropCaravan()
                                    caravansKey = !caravansKey
                                    selectedCard = null
                                    selectedCaravan = -1
                                    game.afterPlayerMove { caravansKey = !caravansKey }
                                }
                            }
                    )
                }
            }
        }
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
        ) {
            BoxWithConstraints(modifier = Modifier
                .fillMaxWidth(0.75f)
                .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                if (maxWidth < 400.dp) {
                    Column {
                        RowOfCards(cards = game.playerDeck.hand.subList(0, minOf(4, game.playerDeck.hand.size)), 0, selectedCard, selectedCardColor, ::onCardClicked)
                        if (game.playerDeck.hand.size >= 4) {
                            RowOfCards(cards = game.playerDeck.hand.subList(4, game.playerDeck.hand.size), 4, selectedCard, selectedCardColor, ::onCardClicked)
                        }
                    }
                } else {
                    Row {
                        RowOfCards(cards = game.playerDeck.hand, 0, selectedCard, selectedCardColor, ::onCardClicked)
                    }
                }
            }
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = game.playerDeck.deckSize.toString(),
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorAccent)), fontSize = 16.sp, textAlign = TextAlign.Center)
                )
                AsyncImage(
                    model = "file:///android_asset/caravan_cards_back/${game.playerDeck.back.getCardBackName()}",
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
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

@Composable
fun RowOfCards(cards: List<Card>, offset: Int = 0, selectedCard: Card?, selectedCardColor: Color, onClick: (Int) -> Unit) {
    Row {
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
fun RowOfEnemyCards(numOfCards: Int, back: CardBack) {
    if (numOfCards <= 0) {
        return
    }
    Row {
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
                .fillMaxHeight(0.5f)
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
                        layout(constraints.maxWidth, height) {
                            placeable.place(offsetWidth, 0)
                        }
                    }
                    .clipToBounds()
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
                            modifier = Modifier.offset(x = (10.dp) * (index + 1))
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
                    }
                    .clipToBounds()) {
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
                        .fillParentMaxHeight(0.25f)
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