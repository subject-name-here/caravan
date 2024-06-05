package com.unicorns.invisible.caravan

import androidx.collection.mutableObjectListOf
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.utils.ShowCard
import com.unicorns.invisible.caravan.utils.ShowCardBack
import com.unicorns.invisible.caravan.utils.caravanScrollbar
import com.unicorns.invisible.caravan.utils.getAccentColor
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getGameTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getGameTextColor
import com.unicorns.invisible.caravan.utils.getGrayTransparent
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.pxToDp
import kotlin.math.min


@Composable
fun ShowGame(activity: MainActivity, game: Game, goBack: () -> Unit) {
    var selectedCard by remember { mutableStateOf<Int?>(null) }
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
        game.afterPlayerMove { updateEnemyHand(); updateCaravans() }
    }
    fun dropCaravan() {
        val selectedCaravanNN = selectedCaravan
        if (selectedCaravanNN == -1) return
        game.playerCaravans[selectedCaravanNN].dropCaravan()
        updateCaravans()
        resetSelected()
        game.afterPlayerMove { updateEnemyHand(); updateCaravans() }
    }

    fun addCardToCaravan(caravan: Caravan, position: Int, isEnemy: Boolean) {
        fun onCaravanCardInserted() {
            game.afterPlayerMove { updateEnemyHand(); updateCaravans() }
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

    ShowGameRaw(
        activity,
        false,
        game,
        goBack,
        { "" },
        { "" },
        {},
        ::onCardClicked,
        selectedCard,
        getSelectedCaravan = { selectedCaravan },
        setSelectedCaravan = {
            selectedCaravan = it
            selectedCard = null
            caravansKey = !caravansKey
        },
        { a1, _, a3, a4 -> addCardToCaravan(a1, a3, a4) },
        ::dropCardFromHand,
        ::dropCaravan,
        enemyHandKey,
        caravansKey
    )
}

@Composable
fun ShowGameRaw(
    activity: MainActivity,
    isPvP: Boolean,
    game: Game,
    goBack: () -> Unit,
    getEnemySymbol: () -> String,
    getMySymbol: () -> String,
    setMySymbol: () -> Unit,
    onCardClicked: (Int) -> Unit,
    selectedCard: Int?,
    getSelectedCaravan: () -> Int,
    setSelectedCaravan: (Int) -> Unit,
    addCardToCaravan: (Caravan, Int, Int, Boolean) -> Unit,
    dropCardFromHand: () -> Unit,
    dropCaravan: () -> Unit,
    enemyHandKey: Boolean,
    caravansKey: Boolean
) {
    val state1Enemy = rememberLazyListState()
    val state1Player = rememberLazyListState()
    val state2Enemy = rememberLazyListState()
    val state2Player = rememberLazyListState()
    val state3Enemy = rememberLazyListState()
    val state3Player = rememberLazyListState()

    fun addCardToEnemyCaravan(caravanNum: Int, position: Int) {
        addCardToCaravan(game.enemyCaravans[caravanNum], caravanNum, position, true)
    }
    fun addCardToPlayerCaravan(caravanNum: Int, position: Int) {
        addCardToCaravan(game.playerCaravans[caravanNum], caravanNum, position, false)
    }
    fun isInitStage(): Boolean {
        return game.isInitStage()
    }
    fun canDiscard(): Boolean {
        return !(game.isOver() || !game.isPlayerTurn || game.isInitStage())
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        Scaffold(bottomBar = {
            Row(modifier = Modifier
                .align(Alignment.BottomCenter)
                .wrapContentHeight().fillMaxWidth()
                .background(getBackgroundColor(activity))
            ) {
                Text(
                    text = stringResource(R.string.back_to_menu),
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    modifier = Modifier
                        .clickable {
                            goBack()
                        }
                        .fillMaxWidth()
                        .padding(8.dp),
                    style = TextStyle(color = getTextColor(activity), fontSize = 16.sp, textAlign = TextAlign.Center),
                )
            }
        }) { innerPadding ->
            BoxWithConstraints(Modifier.padding(innerPadding).paint(
                painterResource(id = R.drawable.game_back3),
                contentScale = ContentScale.FillBounds
            )) {
                if (maxWidth > maxHeight) {
                    Row(Modifier.fillMaxSize()) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        ) {
                            EnemySide(activity, isPvP, getEnemySymbol, game, fillHeight = 0.45f, enemyHandKey)
                            PlayerSide(activity, isPvP, game, selectedCard, onCardClicked, getMySymbol, setMySymbol)
                        }
                        key(caravansKey) {
                            Caravans(
                                activity,
                                { selectedCard },
                                { selectedCard?.let { game.playerCResources.hand[it] } },
                                getSelectedCaravan,
                                setSelectedCaravan,
                                isMaxHeight = true,
                                state1Enemy,
                                state1Player,
                                state2Enemy,
                                state2Player,
                                state3Enemy,
                                state3Player,
                                ::addCardToPlayerCaravan,
                                ::addCardToEnemyCaravan,
                                dropCardFromHand,
                                dropCaravan,
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
                        EnemySide(activity, isPvP, getEnemySymbol, game, fillHeight = 0.15f, enemyHandKey)
                        key(caravansKey) {
                            Caravans(
                                activity,
                                { selectedCard },
                                { selectedCard?.let { game.playerCResources.hand[it] } },
                                getSelectedCaravan,
                                setSelectedCaravan,
                                isMaxHeight = false,
                                state1Enemy,
                                state1Player,
                                state2Enemy,
                                state2Player,
                                state3Enemy,
                                state3Player,
                                ::addCardToPlayerCaravan,
                                ::addCardToEnemyCaravan,
                                dropCardFromHand,
                                dropCaravan,
                                ::isInitStage,
                                { game.isPlayerTurn },
                                ::canDiscard,
                                { num -> game.playerCaravans[num] },
                                { num -> game.enemyCaravans[num] },
                            )
                        }
                        PlayerSide(activity, isPvP, game, selectedCard, onCardClicked, getMySymbol, setMySymbol)
                    }
                }
            }
        }
    }
}

@Composable
fun EnemySide(
    activity: MainActivity,
    isPvP: Boolean,
    getEnemySymbol: () -> String,
    game: Game,
    fillHeight: Float,
    enemyHandKey: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fillHeight)
            .padding(vertical = 4.dp),
    ) {
        RowOfEnemyCards(activity, game.enemyCResources.hand)
        key(enemyHandKey) {
            Box(Modifier.fillMaxSize()) {
                ShowDeck(game.enemyCResources, activity)
                if (isPvP) {
                    Text(text = getEnemySymbol(), style = TextStyle(
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.symbola)),
                        color = getGameTextColor(activity),
                        background = getTextBackgroundColor(activity)
                    ), modifier = Modifier.align(Alignment.BottomEnd))
                }
            }
        }
    }
}

@Composable
fun PlayerSide(
    activity: MainActivity,
    isPvP: Boolean,
    game: Game,
    selectedCard: Int?,
    onCardClicked: (Int) -> Unit,
    getMySymbol: () -> String, setMySymbol: () -> Unit
) {
    val selectedCardColor = getAccentColor(activity)
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxSize()
    ) {
        val handSize = game.playerCResources.hand.size
        Column(Modifier.fillMaxWidth(0.8f)) {
            RowOfCards(activity, cards = game.playerCResources.hand.subList(0, minOf(4, handSize)), 0, selectedCard, selectedCardColor, onCardClicked)
            val cards = if (handSize >= 5) {
                game.playerCResources.hand.subList(4, handSize)
            } else {
                emptyList()
            }
            RowOfCards(activity, cards = cards, 4, selectedCard, selectedCardColor, onCardClicked)
        }

        Box {
            ShowDeck(game.playerCResources, activity)
            if (isPvP) {
                Text(text = getMySymbol(), style = TextStyle(
                    fontSize = 24.sp,
                    fontFamily = FontFamily(Font(R.font.symbola)),
                    color = getGameTextColor(activity),
                    background = getTextBackgroundColor(activity)
                ), modifier = Modifier.clickable {
                    setMySymbol()
                })
            }
        }
    }
}

@Composable
fun ColumnScope.RowOfCards(activity: MainActivity, cards: List<Card>, offset: Int = 0, selectedCard: Int?, selectedCardColor: Color, onClick: (Int) -> Unit) {
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
                    .padding(4.dp)
                    .clickable {
                        onClick(offset + index)
                    }

            } else {
                Modifier
                    .padding(4.dp)
                    .clickable {
                        onClick(offset + index)
                    }
            }.clip(RoundedCornerShape(12f))

            ShowCard(activity, it, modifier, false)
        }
    }
}

@Composable
fun RowOfEnemyCards(activity: MainActivity, cards: List<Card>) {
    BoxWithConstraints(
        Modifier.fillMaxWidth(0.8f).fillMaxHeight(),
        Alignment.TopStart
    ) {
        val cardHeight = maxHeight / 2
        val cardWidth = maxWidth / 2
        val scaleH = cardHeight / 256.pxToDp()
        val scaleW = cardWidth / 183.pxToDp()
        val scale = min(scaleW, scaleH)

        val memCards = remember { mutableObjectListOf<Card>().apply { addAll(cards) } }

        var recomposeToggleState by remember { mutableStateOf(false) }
        LaunchedEffect(recomposeToggleState) { }
        memCards.forEachIndexed { index, it ->
            val maxVerticalOffset = if (index < 5) 2f else 1f
            val itemVerticalOffset = remember { Animatable(-maxVerticalOffset) }
            if (it !in cards) {
                LaunchedEffect(Unit) {
                    itemVerticalOffset.animateTo(maxVerticalOffset, TweenSpec(190))
                    itemVerticalOffset.animateTo(-10f, TweenSpec(0))
                    memCards.remove(it)
                    memCards.addAll((cards.toSet() - memCards.asMutableList().toSet()).toList())
                    recomposeToggleState = !recomposeToggleState
                }
            } else {
                LaunchedEffect(Unit) {
                    itemVerticalOffset.animateTo(0f, TweenSpec(190))
                }
            }
            ShowCardBack(
                activity,
                it,
                Modifier
                    .scale(scale)
                    .layout { measurable, constraints ->
                        val cardsSize = if (itemVerticalOffset.isRunning) memCards.size - 1 else memCards.size
                        val rowWidth = if (memCards.size <= 5) memCards.size else (if (index < 5) 5 else (memCards.size - 5))
                        val placeable = measurable.measure(constraints)
                        val scaledWidth = placeable.width
                        val scaledHeight = placeable.height
                        val handVerticalAlignment = if (cardsSize > 5) 0 else scaledHeight / 2
                        val offsetWidth = (index % 5) * scaledWidth + maxWidth.toPx() / 2 - (rowWidth / 2f) * scaledWidth
                        val offsetHeight = scaledHeight * (index / 5) + handVerticalAlignment + scaledHeight * itemVerticalOffset.value
                        layout(constraints.maxWidth, 0) {
                            placeable.place(offsetWidth.toInt(), offsetHeight.toInt())
                        }
                    }
            )
        }
    }
}

@Composable
fun RowScope.EnemyCaravanOnField(
    activity: MainActivity,
    caravan: Caravan,
    state: LazyListState,
    addSelectedCardOnPosition: (Int) -> Unit,
) {
    LazyColumn(
        state = state,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .weight(0.25f)
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
                ShowCard(activity, it.card, Modifier
                    .clickable {
                        addSelectedCardOnPosition(caravan.cards.lastIndex - index)
                    })
                it.modifiersCopy().forEachIndexed { index, card ->
                    ShowCard(activity, card, Modifier.offset(x = -(10.dp) * (index + 1)))
                }
            }
        }
    }
}


@Composable
fun RowScope.PlayerCaravanOnField(
    activity: MainActivity,
    caravan: Caravan,
    isInitStage: Boolean,
    state: LazyListState,
    canPutSelectedCardOnTop: () -> Int,
    selectCaravan: () -> Unit = {},
    addSelectedCardOnPosition: (Int) -> Unit,
) {
    Column(Modifier
        .fillMaxHeight()
        .weight(0.25f)
    ) {
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
                .padding(2.dp)
                .background(
                    if (isInitStage || caravan.getValue() == 0) Color.Transparent else getGameTextBackgroundColor(
                        activity
                    )
                )
                .padding(2.dp)
        )

        LazyColumn(
            state = state,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
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
                    ShowCard(activity, it.card, Modifier
                        .clickable {
                            addSelectedCardOnPosition(index)
                        })
                    it.modifiersCopy().forEachIndexed { index, card ->
                        ShowCard(
                            activity, card, Modifier
                                .offset(x = (10.dp) * (index + 1))
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
                                .padding(horizontal = 4.dp)
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
                                .padding(horizontal = 4.dp)
                                .background(colorResource(id = R.color.red))
                                .border(4.dp, colorResource(id = R.color.dark_red))
                            ) {}
                        }
                        else -> {
                            Box(modifier = Modifier
                                .fillParentMaxWidth()
                                .height(20.dp)
                                .padding(horizontal = 4.dp)
                                .background(getTextBackgroundColor(activity))
                                .border(4.dp, getGameTextColor(activity))
                            ) {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowDeck(cResources: CResources, activity: MainActivity, isKnown: Boolean = true) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(end = 4.dp),
        verticalArrangement = Arrangement.Center
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
        if (isKnown) {
            val (back, isAlt) = cResources.getDeckBack() ?: (null to false)
            if (back != null) {
                ShowCardBack(activity, Card(Rank.ACE, Suit.HEARTS, back, isAlt), Modifier.fillMaxWidth())
            }
        }
    }
}

private val cities = listOf("BONEYARD", "REDDING", "VAULT CITY", "DAYGLOW", "NEW RENO", "THE HUB")
@Composable
fun RowScope.Score(activity: MainActivity, num: Int, caravan: Caravan, opposingValue: Int) {
    val (textColor, text, isColored) = if (caravan.getValue() > 26)
        Triple(Color.Red, caravan.getValue().toString(), true)
    else if (caravan.getValue() in (21..26) && (opposingValue !in (21..26) || caravan.getValue() > opposingValue))
        Triple(Color.Green, caravan.getValue().toString(), true)
    else
        Triple(getAccentColor(activity), caravan.getValue().toString(), false)
    Box(Modifier.weight(0.25f).height(24.dp).padding(2.dp).background(getGrayTransparent(activity)).padding(2.dp)) {
        Text(
            text = cities[num],
            textAlign = TextAlign.Center,
            color = if (isColored) textColor else Color.Black,
            fontFamily = FontFamily(Font(R.font.help)),
            modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterStart),
            fontSize = 9.sp,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            softWrap = false,
        )
        Spacer(Modifier.width(2.dp))
        Text(
            text = text,
            modifier = Modifier.align(Alignment.CenterEnd),
            textAlign = TextAlign.Center,
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily(Font(R.font.monofont)),
            maxLines = 1,
            softWrap = false,
            fontSize = 14.sp,
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
    val enemyCaravanFillHeight = if (isMaxHeight) 0.36f else 0.425f
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(if (isMaxHeight) 1f else 0.725f),
    ) {
        val enemyStates = listOf(state1Enemy, state2Enemy, state3Enemy)
        val playerStates = listOf(state1Player, state2Player, state3Player)
        Row(Modifier.fillMaxWidth().fillMaxHeight(enemyCaravanFillHeight)) {
            repeat(3) {
                EnemyCaravanOnField(
                    activity,
                    getEnemyCaravan(it),
                    enemyStates[it]
                ) { card ->
                    addCardToEnemyCaravan(it, card)
                }
            }

            Box(Modifier.weight(0.25f))
        }

        Row(Modifier.fillMaxWidth().height(52.dp)) {
            Column(Modifier.fillMaxHeight().weight(0.75f)) {
                Row(Modifier.fillMaxWidth().height(24.dp)) {
                    repeat(3) {
                        val caravan = getEnemyCaravan(it)
                        val opposingValue = getPlayerCaravan(it).getValue()
                        Score(activity, it, caravan, opposingValue)
                    }
                }
                HorizontalDivider(thickness = 4.dp, color = getDividerColor(activity))
                Row(Modifier.fillMaxWidth().height(24.dp)) {
                    repeat(3) {
                        val caravan = getPlayerCaravan(it)
                        val opposingValue = getEnemyCaravan(it).getValue()
                        Score(activity, it + 3, caravan, opposingValue)
                    }
                }
            }
            Column(modifier = Modifier.fillMaxHeight().weight(0.25f)) {
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
                        .height(52.dp)
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
                        .height(52.dp)
                        .background(getGrayTransparent(activity))
                        .padding(6.dp)
                }
                Box(modifier) {
                    Text(
                        text = text,
                        textAlign = TextAlign.Center,
                        color = getAccentColor(activity),
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily(Font(R.font.monofont)),
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 16.sp,
                    )
                }
            }
        }

        Row(Modifier.fillMaxWidth().fillMaxHeight()) {
            repeat(3) {
                PlayerCaravanOnField(
                    activity,
                    getPlayerCaravan(it),
                    isInitStage = getIsInitStage(),
                    playerStates[it],
                    {
                        val selectedCard = getSelectedCard()
                        if (selectedCard == null) {
                            0
                        } else if (getPlayerCaravan(it).canPutCardOnTop(selectedCard)) {
                            1
                        } else {
                            -1
                        }
                    },
                    {
                        setSelectedCaravan(
                            if (getSelectedCaravan() == it || getPlayerCaravan(it).getValue() == 0) {
                                -1
                            } else {
                                it
                            }
                        )
                    }
                ) { card ->
                    addCardToPlayerCaravan(it, card)
                }
            }
            Box(Modifier.weight(0.25f))
        }
    }
}