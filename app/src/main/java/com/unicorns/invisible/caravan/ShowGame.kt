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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.getCardName
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.utils.caravanScrollbar
import com.unicorns.invisible.caravan.utils.getAccentColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getGameBackgroundColor
import com.unicorns.invisible.caravan.utils.getGameTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getGameTextColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTrackColor


@Composable
fun ShowGame(activity: MainActivity, game: Game, goBack: () -> Unit) {
    var selectedCard by remember { mutableStateOf<Int?>(null) }
    val selectedCardColor = getAccentColor(activity)

    var selectedCaravan by remember { mutableIntStateOf(-1) }

    var caravansKey by remember { mutableStateOf(true) }
    var enemyHandKey by remember { mutableStateOf(true) }

    fun onCardClicked(index: Int) {
        if (game.isOver()) {
            return
        }
        selectedCard = if (index == selectedCard) null else index
        selectedCaravan = -1
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
        val selectedCardNN = selectedCard ?: return
        game.playerCResources.removeFromHand(selectedCardNN)
        resetSelected()
        game.afterPlayerMove { updateCaravans(); updateEnemyHand() }
    }
    fun dropCaravan() {
        val selectedCaravanNN = selectedCaravan
        if (selectedCaravanNN == -1) return
        game.playerCaravans[selectedCaravanNN].dropCaravan()
        updateCaravans()
        resetSelected()
        game.afterPlayerMove { updateCaravans(); updateEnemyHand() }
    }

    fun addCardToCaravan(caravan: Caravan, position: Int, isEnemy: Boolean = false) {
        fun onCaravanCardInserted() {
            game.afterPlayerMove { updateCaravans(); updateEnemyHand() }
            resetSelected()
            updateCaravans()
        }

        val cardIndex = selectedCard
        val card = cardIndex?.let { game.playerCResources.hand[cardIndex] }
        if (card != null && game.isPlayerTurn && !game.isOver() && (!game.isInitStage() || !card.isFace())) {
            when (card.rank.value) {
                in 1..10 -> {
                    if (position == caravan.cards.size && !isEnemy) {
                        if (caravan.canPutCardOnTop(card)) {
                            caravan.putCardOnTop(game.playerCResources.removeFromHand(cardIndex))
                            onCaravanCardInserted()
                        }
                    }
                }
                Rank.JACK.value, Rank.QUEEN.value, Rank.KING.value, Rank.JOKER.value -> {
                    if (position in caravan.cards.indices && caravan.cards[position].canAddModifier(card)) {
                        caravan.cards[position].addModifier(game.playerCResources.removeFromHand(cardIndex))
                        onCaravanCardInserted()
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
    fun isInitStage(): Boolean {
        return game.isInitStage()
    }
    fun canDiscard(): Boolean {
        return !(game.isOver() || !game.isPlayerTurn || game.isInitStage())
    }


    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(getGameBackgroundColor(activity))
//            .paint(
//                painterResource(id = R.drawable.game_back3),
//                contentScale = ContentScale.Crop
//            )
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
                            ShowDeck(game.enemyCResources, activity)
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
                            fontFamily = FontFamily(Font(R.font.monofont)),
                            modifier = Modifier
                                .clickable {
                                    goBack()
                                }
                                .fillMaxWidth()
                                .background(getGameTextBackgroundColor(activity))
                                .padding(8.dp),
                            style = TextStyle(color = getGameTextColor(activity), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center),
                        )
                    }
                }
                key(caravansKey) {
                    Caravans(
                        activity,
                        { selectedCard },
                        { selectedCard?.let { game.playerCResources.hand[it] } },
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
                        ShowDeck(game.enemyCResources, activity)
                    }
                }
                key(caravansKey) {
                    Caravans(
                        activity,
                        { selectedCard },
                        { selectedCard?.let { game.playerCResources.hand[it] } },
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
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily(Font(R.font.monofont)),
                        modifier = Modifier
                            .clickable {
                                goBack()
                            }
                            .fillMaxWidth()
                            .background(getGameTextBackgroundColor(activity))
                            .padding(8.dp),
                        style = TextStyle(color = getGameTextColor(activity), fontSize = 16.sp, textAlign = TextAlign.Center),
                    )
                }
            }
        }
    }
}

@Composable
fun ColumnScope.RowOfCards(cards: List<Card>, offset: Int = 0, selectedCard: Int?, selectedCardColor: Color, onClick: (Int) -> Unit) {
    Row(
        Modifier
            .weight(1f)
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        cards.forEachIndexed { index, it ->
            val modifier = if (index == (selectedCard ?: -1) - offset) {
                Modifier
                    .border(
                        width = 4.dp,
                        color = selectedCardColor
                    )
                    .background(selectedCardColor)
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
                    .clip(RoundedCornerShape(6f))
            )
        }
    }
}

@Composable
fun ColumnScope.RowOfEnemyCards(cards: List<Card>) {
    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.Center) {
        cards.forEach {
            AsyncImage(
                model = "file:///android_asset/caravan_cards_back/${it.back.getCardBackAsset()}",
                contentDescription = "",
                modifier = Modifier.clip(RoundedCornerShape(6f))
            )
        }
    }
}

@Composable
fun CaravanOnField(
    activity: MainActivity,
    caravan: Caravan,
    getOpposingCaravanValue: () -> Int,
    isInitStage: Boolean,
    isEnemy: Boolean,
    state: LazyListState,
    canPutSelectedCardOnTop: () -> Int,
    selectCaravan: () -> Unit = {},
    addSelectedCardOnPosition: (Int) -> Unit,
) {
    val (textColor, text) = if (caravan.getValue() > 26)
        Color.Red to caravan.getValue().toString() + " ☓"
    else if (caravan.getValue() in (21..26) && (getOpposingCaravanValue() !in (21..26) || caravan.getValue() > getOpposingCaravanValue()))
        Color.Green to caravan.getValue().toString() + " ✔"
    else
        getAccentColor(activity) to caravan.getValue().toString()
    if (isEnemy) {
        LazyColumn(
            state = state,
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight(0.4f)
                .fillMaxWidth()
                .caravanScrollbar(
                    state,
                    knobColor = getKnobColor(activity),
                    trackColor = getTrackColor(activity)
                )
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
                        modifier = Modifier
                            .clickable {
                                addSelectedCardOnPosition(caravan.cards.lastIndex - index)
                            }
                            .clip(RoundedCornerShape(6f))
                    )
                    it.modifiersCopy().forEachIndexed { index, card ->
                        AsyncImage(
                            model = "file:///android_asset/caravan_cards/${getCardName(card)}",
                            contentDescription = "",
                            modifier = Modifier
                                .offset(x = -(10.dp) * (index + 1))
                                .clip(RoundedCornerShape(6f))
                        )
                    }
                }
            }
        }
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily(Font(R.font.monofont)),
            modifier = Modifier.fillMaxWidth(),
            fontSize = 16.sp,
        )
    } else {
        Column {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                color = textColor,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily(Font(R.font.monofont)),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 16.sp,
            )
            Text(text = if (isInitStage || caravan.getValue() == 0) "" else stringResource(R.string.discard),
                textAlign = TextAlign.Center,
                color = getGameTextColor(activity),
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily(Font(R.font.monofont)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectCaravan()
                    }
                    .background(if (isInitStage || caravan.getValue() == 0) Color.Transparent else getGameTextBackgroundColor(activity))
                    .padding(4.dp)
            )
        }
        LazyColumn(
            state = state,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .caravanScrollbar(
                    state,
                    knobColor = getKnobColor(activity),
                    trackColor = getTrackColor(activity),
                    hasNewCardPlaceholder = true
                )
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
                        modifier = Modifier
                            .clickable {
                                addSelectedCardOnPosition(index)
                            }
                            .clip(RoundedCornerShape(6f))
                    )
                    it.modifiersCopy().forEachIndexed { index, card ->
                        AsyncImage(
                            model = "file:///android_asset/caravan_cards/${getCardName(card)}",
                            contentDescription = "",
                            modifier = Modifier
                                .offset(x = (10.dp) * (index + 1))
                                .clip(RoundedCornerShape(6f))
                        )
                    }
                }
            }
            if (!caravan.isFull() && (!isInitStage || caravan.cards.isEmpty())) {
                item {
                    when (canPutSelectedCardOnTop()) {
                        1 -> {
                            Box(modifier = Modifier
                                .fillParentMaxWidth()
                                .height(20.dp)
                                .background(colorResource(id = R.color.green))
                                .border(4.dp, colorResource(id = R.color.dark_green))
                                .clickable {
                                    addSelectedCardOnPosition(caravan.cards.size)
                                }
                            ) {}
                        }
                        -1 -> {
                            Box(modifier = Modifier
                                .fillParentMaxWidth()
                                .height(20.dp)
                                .background(colorResource(id = R.color.red))
                                .border(4.dp, colorResource(id = R.color.dark_red))
                                .clickable {
                                    addSelectedCardOnPosition(caravan.cards.size)
                                }
                            ) {}
                        }
                        else -> {
                            Box(modifier = Modifier
                                .fillParentMaxWidth()
                                .height(20.dp)
                                .background(getTextBackgroundColor(activity))
                                .border(4.dp, getGameTextColor(activity))
                                .clickable {
                                    addSelectedCardOnPosition(caravan.cards.size)
                                }
                            ) {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowDeck(cResources: CResources, activity: MainActivity, isToBottom: Boolean = false, isKnown: Boolean = true) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(end = 4.dp),
        verticalArrangement = if (isToBottom) Arrangement.Bottom else Arrangement.Top
    ) {
        Text(
            text = cResources.deckSize.toString(),
            textAlign = TextAlign.Center,
            color = getAccentColor(activity),
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily(Font(R.font.monofont)),
            modifier = Modifier.fillMaxWidth(),
            fontSize = 16.sp,
        )
        val link = "file:///android_asset/caravan_cards_back/${if (isKnown) cResources.getDeckBack()?.getCardBackAsset() else null}"
        AsyncImage(
            model = link,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(if (!isToBottom) 12f else 6f)),
        )
    }
}

@Composable
fun Caravans(
    activity: MainActivity,
    getSelectedCardInt: () -> Int?,
    getSelectedCard: () -> Card?,
    getSelectedCaravan: () -> Int,
    setSelectedCaravan: (Int) -> Unit,
    isMaxHeight: Boolean = false,
    state1Enemy: LazyListState,
    state1Player: LazyListState,
    state2Enemy: LazyListState,
    state2Player: LazyListState,
    state3Enemy: LazyListState,
    state3Player: LazyListState,
    addCardToPlayerCaravan: (Int, Int) -> Unit,
    addCardToEnemyCaravan: (Int, Int) -> Unit,
    dropCardFromHand: () -> Unit,
    dropSelectedCaravan: () -> Unit,
    getIsInitStage: () -> Boolean,
    isPlayersTurn: () -> Boolean,
    canDiscard: () -> Boolean,
    getPlayerCaravan: (Int) -> Caravan,
    getEnemyCaravan: (Int) -> Caravan,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(if (isMaxHeight) 1f else 0.65f),
    ) {
        @Composable
        fun CaravansColumn(num: Int, enemyLazyListState: LazyListState, playerLazyListState: LazyListState) {
            Column(modifier = Modifier
                .weight(0.25f)
                .fillMaxHeight()
                .padding(6.dp)) {
                CaravanOnField(activity,
                    getEnemyCaravan(num),
                    { getPlayerCaravan(num).getValue() },
                    isInitStage = getIsInitStage(),
                    isEnemy = true, enemyLazyListState,
                    { -1 }
                ) {
                    addCardToEnemyCaravan(num, it)
                }
                HorizontalDivider(color = getDividerColor(activity))
                CaravanOnField(
                    activity,
                    getPlayerCaravan(num),
                    { getEnemyCaravan(num).getValue() },
                    isInitStage = getIsInitStage(),
                    false,
                    playerLazyListState,
                    {
                        val selectedCard = getSelectedCard()
                        if (selectedCard == null) {
                            0
                        } else if (getPlayerCaravan(num).canPutCardOnTop(selectedCard)) {
                            1
                        } else {
                            -1
                        }
                    },
                    {
                        setSelectedCaravan(
                            if (getSelectedCaravan() == num || getPlayerCaravan(num).getValue() == 0) {
                                -1
                            } else {
                                num
                            }
                        )
                    }
                ) {
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
            val text = when {
                !isPlayersTurn() -> stringResource(R.string.wait)
                getIsInitStage() -> stringResource(R.string.init_stage)
                !canDiscard() -> stringResource(R.string.can_t_act)
                getSelectedCardInt() != null -> {
                    stringResource(R.string.discard_card)
                }
                getSelectedCaravan() in (0..2) -> {
                    stringResource(R.string.drop_caravan, getSelectedCaravan() + 1)
                }
                else -> stringResource(R.string.your_turn)
            }
            val modifier = if (canDiscard() && (getSelectedCardInt() != null || getSelectedCaravan() in (0..2))) {
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (!canDiscard()) return@clickable
                        val selectedCard = getSelectedCardInt()
                        if (selectedCard != null) {
                            dropCardFromHand()
                        } else if (getSelectedCaravan() in (0..2)) {
                            dropSelectedCaravan()
                        }
                    }
                    .background(getGameTextBackgroundColor(activity))
                    .padding(6.dp)
            } else {
                Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .padding(bottom = 24.dp)
            }
            Text(
                text = text,
                textAlign = TextAlign.Center,
                color = getAccentColor(activity),
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily(Font(R.font.monofont)),
                modifier = modifier,
                fontSize = 16.sp,
            )
        }
    }
}