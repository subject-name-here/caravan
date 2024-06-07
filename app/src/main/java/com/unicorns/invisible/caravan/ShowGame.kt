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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.utils.ShowCard
import com.unicorns.invisible.caravan.utils.ShowCardBack
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
import com.unicorns.invisible.caravan.utils.playCardFlipSound
import com.unicorns.invisible.caravan.utils.pxToDp
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.startAmbient
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.min


@Composable
fun ShowGame(activity: MainActivity, game: Game, goBack: () -> Unit) {
    var selectedCard by remember { mutableStateOf<Int?>(null) }
    var selectedCaravan by remember { mutableIntStateOf(-1) }

    var caravansKey by remember { mutableStateOf(true) }
    var enemyHandKey by remember { mutableIntStateOf(0) }

    game.enemyCResources.onDropCardFromHand = { enemyHandKey = -1 }

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
        enemyHandKey = when (enemyHandKey) {
            -2 -> 0
            -1 -> -2
            else -> (1 - enemyHandKey).coerceIn(0, 1)
        }
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
            resetSelected()
            game.afterPlayerMove { updateEnemyHand(); updateCaravans() }
            updateCaravans()
        }

        val cardIndex = selectedCard
        val card = cardIndex?.let { game.playerCResources.hand[cardIndex] }
        if (card != null && game.isPlayerTurn && !game.isOver() && (!game.isInitStage() || !card.isFace())) {
            when (card.rank.value) {
                in 1..10 -> {
                    if (position == caravan.cards.size && !isEnemy) {
                        if (caravan.canPutCardOnTop(card)) {
                            playCardFlipSound(activity)
                            caravan.putCardOnTop(game.playerCResources.removeFromHand(cardIndex))
                            onCaravanCardInserted()
                        }
                    }
                }
                Rank.JACK.value, Rank.QUEEN.value, Rank.KING.value, Rank.JOKER.value -> {
                    if (position in caravan.cards.indices && caravan.cards[position].canAddModifier(card)) {
                        playCardFlipSound(activity)
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
        enemyHandKey
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
    enemyHandKey: Int
) {
    LaunchedEffect(Unit) { startAmbient(activity) }

    val state1Enemy = rememberLazyListState()
    val state1Player = rememberLazyListState()
    val state2Enemy = rememberLazyListState()
    val state2Player = rememberLazyListState()
    val state3Enemy = rememberLazyListState()
    val state3Player = rememberLazyListState()
    fun stateToSizeOfItem(state: LazyListState): Int {
        return state.layoutInfo.visibleItemsInfo[0].size
    }

    fun addCardToEnemyCaravan(caravanNum: Int, position: Int) {
        addCardToCaravan(game.enemyCaravans[caravanNum], caravanNum, position, true)
        MainScope().launch {
            val caravan = game.enemyCaravans[caravanNum]
            when (caravanNum) {
                0 -> state1Enemy.scrollToItem(0, (stateToSizeOfItem(state1Enemy).toFloat() * position / caravan.size).toInt())
                1 -> state2Enemy.scrollToItem(0, (stateToSizeOfItem(state2Enemy).toFloat() * position / caravan.size).toInt())
                2 -> state3Enemy.scrollToItem(0, (stateToSizeOfItem(state3Enemy).toFloat() * position / caravan.size).toInt())
            }
        }
    }
    fun addCardToPlayerCaravan(caravanNum: Int, position: Int) {
        addCardToCaravan(game.playerCaravans[caravanNum], caravanNum, position, false)
        MainScope().launch {
            val caravan = game.playerCaravans[caravanNum]
            when (caravanNum) {
                0 -> state1Player.scrollToItem(0, (stateToSizeOfItem(state1Player).toFloat() * position / caravan.size).toInt())
                1 -> state2Player.scrollToItem(0, (stateToSizeOfItem(state2Player).toFloat() * position / caravan.size).toInt())
                2 -> state3Player.scrollToItem(0, (stateToSizeOfItem(state3Player).toFloat() * position / caravan.size).toInt())
            }
        }
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
                var wasCardDropped by remember { mutableStateOf(false) }
                if (maxWidth > maxHeight) {
                    Row(Modifier.fillMaxSize()) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        ) {
                            EnemySide(activity, isPvP, getEnemySymbol, game, fillHeight = 0.45f, enemyHandKey)
                            PlayerSide(activity, isPvP, game, {
                                val res = wasCardDropped
                                wasCardDropped = false
                                res
                            }, selectedCard, onCardClicked, getMySymbol, setMySymbol)
                        }
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
                            { wasCardDropped = true; dropCardFromHand() },
                            dropCaravan,
                            ::isInitStage,
                            { game.isPlayerTurn },
                            ::canDiscard,
                            { num -> game.playerCaravans[num] },
                            { num -> game.enemyCaravans[num] },
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        EnemySide(activity, isPvP, getEnemySymbol, game, fillHeight = 0.15f, enemyHandKey)
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
                            { wasCardDropped = true; dropCardFromHand() },
                            dropCaravan,
                            ::isInitStage,
                            { game.isPlayerTurn },
                            ::canDiscard,
                            { num -> game.playerCaravans[num] },
                            { num -> game.enemyCaravans[num] },
                        )
                        PlayerSide(activity, isPvP, game, {
                            val res = wasCardDropped
                            wasCardDropped = false
                            res
                        }, selectedCard, onCardClicked, getMySymbol, setMySymbol)
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
    enemyHandKey: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fillHeight)
            .padding(vertical = 4.dp),
    ) {
        RowOfEnemyCards(activity, game.enemyCResources.hand, enemyHandKey < 0)
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
    wasCardDropped: () -> Boolean,
    selectedCard: Int?,
    onCardClicked: (Int) -> Unit,
    getMySymbol: () -> String, setMySymbol: () -> Unit
) {
    val selectedCardColor = getAccentColor(activity)
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxSize()
    ) {
        PlayerCards(activity, game.playerCResources.hand, wasCardDropped(), selectedCard, selectedCardColor, onCardClicked)
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
fun PlayerCards(activity: MainActivity, cards: List<Card>, wasCardDropped: Boolean, selectedCard: Int?, selectedCardColor: Color, onClick: (Int) -> Unit) {
    Hand(activity, false, cards, wasCardDropped, selectedCard, selectedCardColor, onClick)
}

@Composable
fun RowOfEnemyCards(activity: MainActivity, cards: List<Card>, wasCardDropped: Boolean = false) {
    Hand(activity, true, cards, wasCardDropped, -1, Color.Transparent, {})
}


@Composable
fun Hand(
    activity: MainActivity,
    isEnemy: Boolean,
    cards: List<Card>,
    wasCardDropped: Boolean,
    selectedCard: Int?, selectedCardColor: Color, onClick: (Int) -> Unit
) {
    val enemyMult = if (isEnemy) -1f else 1f
    BoxWithConstraints(
        Modifier.fillMaxWidth(0.8f).fillMaxHeight(),
        Alignment.TopStart
    ) {
        val cardHeight = (maxHeight - 16.dp) / 2
        val cardWidth = (maxWidth - 40.dp) / 5
        val scaleH = cardHeight / 256.pxToDp()
        val scaleW = cardWidth / 183.pxToDp()
        val scale = min(scaleW, scaleH)

        val memCards = remember { mutableObjectListOf<Card>() }

        val itemVerticalOffsetMovingIn = remember { Animatable(2.5f * enemyMult) }
        val itemVerticalOffsetMovingOut = remember { Animatable(0f) }

        var recomposeKey by remember { mutableStateOf(false) }

        val key = cards.size - memCards.size
        LaunchedEffect(key) {
            if (key > 0) {
                playCardFlipSound(activity)
                itemVerticalOffsetMovingIn.snapTo(2.5f * enemyMult)
                itemVerticalOffsetMovingIn.animateTo(0f, TweenSpec(190))
            } else if (key < 0) {
                playCardFlipSound(activity)
                itemVerticalOffsetMovingOut.snapTo(0f)
                itemVerticalOffsetMovingOut.animateTo((if (wasCardDropped) 2.5f else -2.5f) * enemyMult, TweenSpec(190))
            }
            memCards.clear()
            memCards.addAll(cards)
            recomposeKey = !recomposeKey
        }

        LaunchedEffect(recomposeKey) { }

        val iteratedCollection = (memCards.asMutableList() + cards.toList()).distinct()
        iteratedCollection.forEach {
            val index = if (it in memCards) memCards.indexOf(it) else cards.indexOf(it)
            val isMovingOut = it !in cards
            val isMovingIn = it !in memCards

            if (isEnemy) {
                ShowCardBack(
                    activity,
                    it,
                    Modifier
                        .scale(scale)
                        .layout { measurable, constraints ->
                            val cardsSize = iteratedCollection.size
                            val rowWidth = if (cardsSize <= 5) cardsSize else (if (index < 5) 5 else (cardsSize - 5))
                            val placeable = measurable.measure(constraints)
                            val scaledWidth = placeable.width
                            val scaledHeight = placeable.height
                            val handVerticalAlignment = if (cardsSize > 5) 0 else scaledHeight / 2
                            val offsetWidth = (index % 5) * scaledWidth + maxWidth.toPx() / 2 - (rowWidth / 2f) * scaledWidth
                            val offsetHeight = scaledHeight * (index / 5) + handVerticalAlignment +
                                    scaledHeight * (
                                    (if (isMovingIn) itemVerticalOffsetMovingIn.value else 0f) +
                                            (if (isMovingOut) itemVerticalOffsetMovingOut.value else 0f)
                                    )
                            layout(constraints.maxWidth, 0) {
                                placeable.place(offsetWidth.toInt(), offsetHeight.toInt())
                            }
                        }
                )
            } else {
                ShowCard(
                    activity,
                    it,
                    Modifier
                        .scale(scale)
                        .layout { measurable, constraints ->
                            val cardsSize = iteratedCollection.size
                            val rowWidth = if (cardsSize <= 5) cardsSize else (if (index < 5) 5 else (cardsSize - 5))
                            val placeable = measurable.measure(constraints)
                            val scaledWidth = placeable.width
                            val scaledHeight = placeable.height
                            val handVerticalAlignment = if (cardsSize > 5) 0 else scaledHeight / 2
                            val offsetWidth = (index % 5) * scaledWidth + maxWidth.toPx() / 2 - (rowWidth / 2f) * scaledWidth
                            val offsetHeight = scaledHeight * (index / 5) + handVerticalAlignment +
                                    scaledHeight * (
                                    (if (isMovingIn) itemVerticalOffsetMovingIn.value else 0f) +
                                            (if (isMovingOut) itemVerticalOffsetMovingOut.value else 0f)
                                    )
                            layout(constraints.maxWidth, 0) {
                                placeable.place(offsetWidth.toInt(), offsetHeight.toInt())
                            }
                        }
                        .clickable {
                            if (itemVerticalOffsetMovingIn.value == 0f && itemVerticalOffsetMovingIn.value == 0f) {
                                onClick(index)
                            }
                        }
                        .border(
                            width = if (index == (selectedCard ?: -1)) 4.dp else (-1).dp,
                            color = selectedCardColor
                        )
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12f)),
                    toModify = false
                )
            }
        }
    }
}


@Composable
fun RowScope.CaravanOnField(
    activity: MainActivity,
    isPlayerTurn: Boolean,
    caravan: Caravan,
    isEnemy: Boolean,
    isInitStage: Boolean,
    state: LazyListState,
    canPutSelectedCardOnTop: () -> Int,
    selectCaravan: () -> Unit = {},
    addSelectedCardOnPosition: (Int) -> Unit,
) {
    val enemyMult = if (isEnemy) -1 else 1

    val memCaravan = remember { mutableObjectListOf<CardWithModifier>() }

    val itemVerticalOffsetMovingIn = remember { Animatable(2.5f * enemyMult) }
    val itemHorizontalOffsetMovingOut = remember { Animatable(0f) }

    var recomposeKey by remember { mutableStateOf(false) }

    val key = caravan.size - memCaravan.size
    val scope = rememberCoroutineScope()
    LaunchedEffect(key) {
        scope.launch {
            if (key > 0) {
                playCardFlipSound(activity)
                itemVerticalOffsetMovingIn.snapTo(2.5f * enemyMult)
                itemVerticalOffsetMovingIn.animateTo(0f, TweenSpec(190, 380))
            } else if (key < 0) {
                playCardFlipSound(activity)
                itemHorizontalOffsetMovingOut.snapTo(0f)
                itemHorizontalOffsetMovingOut.animateTo(2f, TweenSpec(190, 380))
            }
            memCaravan.clear()
            memCaravan.addAll(caravan.cards)
            recomposeKey = !recomposeKey
        }
    }

    Column(Modifier
        .fillMaxHeight()
        .weight(0.25f)
    ) {
        if (!isEnemy) {
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
        }
        LaunchedEffect(recomposeKey) {}

        LazyColumn(
            state = state,
            verticalArrangement = if (isEnemy) Arrangement.Bottom else Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .scrollbar(
                    state,
                    horizontal = false,
                    knobColor = getKnobColor(activity),
                    trackColor = getTrackColor(activity),
                )
        ) {
            item {
                Box(Modifier.wrapContentHeight(unbounded = true)) {
                    val iteratedCollection = (memCaravan.asMutableList() + caravan.cards).distinct()
                    iteratedCollection.forEach {
                        val index = if (it in memCaravan) memCaravan.indexOf(it) else caravan.cards.indexOf(it)
                        val isMovingOut = it !in caravan.cards
                        val isMovingIn = it !in memCaravan

                        val itemIndex = if (isEnemy) (caravan.cards.size - index) else index
                        val modifier = Modifier
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)
                                val offsetWidth = constraints.maxWidth / 2 - placeable.width / 2
                                val antiOffsetHeight = placeable.height / 3 * itemIndex
                                layout(constraints.maxWidth, placeable.height / 3 * (iteratedCollection.size + 3)) {
                                    placeable.place(
                                        offsetWidth +
                                                (if (isMovingOut) (itemHorizontalOffsetMovingOut.value * placeable.width).toInt() else 0),
                                        antiOffsetHeight +
                                                (if (isMovingIn) (itemVerticalOffsetMovingIn.value * placeable.height).toInt() else 0)
                                    )
                                }
                            }

                        Box(modifier = modifier) {
                            ShowCard(activity, it.card, Modifier.clickable {
                                if (itemVerticalOffsetMovingIn.value == 0f && itemHorizontalOffsetMovingOut.value == 0f) {
                                    addSelectedCardOnPosition(index)
                                }
                            })
                        }

                        val memModifiers = remember { mutableObjectListOf<Card>() }
                        val modifierVerticalOffsetMovingIn = remember { Animatable(3f * (if (isPlayerTurn) 1f else -1f)) }
                        val key2 = it.modifiersCopy().size - memModifiers.size
                        LaunchedEffect(key2) {
                            scope.launch {
                                if (key2 > 0) {
                                    playCardFlipSound(activity)
                                    modifierVerticalOffsetMovingIn.snapTo(3f * (if (isPlayerTurn) 1f else -1f))
                                    modifierVerticalOffsetMovingIn.animateTo(0f, TweenSpec(190, 380))
                                }
                                memModifiers.clear()
                                memModifiers.addAll(it.modifiersCopy())
                                recomposeKey = !recomposeKey
                            }
                        }

                        (memModifiers.asMutableList() + it.modifiersCopy()).distinct().forEachIndexed { modifierIndex, card ->
                            val isMovingInModifier = card !in memModifiers
                            Box(modifier = Modifier
                                .layout { measurable, constraints ->
                                    val placeable = measurable.measure(constraints)
                                    val offsetWidth = constraints.maxWidth / 2 - placeable.width / 2
                                    layout(constraints.maxWidth, 0) {
                                        placeable.place(
                                            ((if (isEnemy) (-10).dp else 10.dp) * (modifierIndex + 1)).toPx().toInt() + offsetWidth +
                                                    if (isMovingOut) (itemHorizontalOffsetMovingOut.value * placeable.width).toInt() else 0,
                                            placeable.height / 3 * itemIndex +
                                                    if (isMovingInModifier) (modifierVerticalOffsetMovingIn.value * placeable.height).toInt() else 0
                                        )
                                    }
                                })
                            {
                                ShowCard(activity, card, Modifier)
                            }
                        }
                    }
                    if (!isEnemy) {
                        if (!caravan.isFull() && (!isInitStage || caravan.cards.isEmpty())) {
                            when (canPutSelectedCardOnTop()) {
                                1 -> {
                                    Box(modifier = Modifier
                                        .fillParentMaxWidth()
                                        .height(20.dp)
                                        .align(Alignment.BottomCenter)
                                        .padding(horizontal = 4.dp)
                                        .background(colorResource(id = R.color.green))
                                        .border(4.dp, colorResource(id = R.color.dark_green))
                                        .clickable {
                                            addSelectedCardOnPosition(caravan.size)
                                        }
                                    ) {}
                                }
                                -1 -> {
                                    Box(modifier = Modifier
                                        .fillParentMaxWidth()
                                        .height(20.dp)
                                        .align(Alignment.BottomCenter)
                                        .padding(horizontal = 4.dp)
                                        .background(colorResource(id = R.color.red))
                                        .border(4.dp, colorResource(id = R.color.dark_red))
                                    ) {}
                                }
                                else -> {
                                    Box(modifier = Modifier
                                        .fillParentMaxWidth()
                                        .height(20.dp)
                                        .align(Alignment.BottomCenter)
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
    getEnemyCaravan: (Int) -> Caravan
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
                CaravanOnField(
                    activity,
                    isPlayersTurn(),
                    getEnemyCaravan(it),
                    isEnemy = true,
                    isInitStage = getIsInitStage(),
                    enemyStates[it],
                    { -1 },
                    {}
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
                CaravanOnField(
                    activity,
                    isPlayersTurn(),
                    getPlayerCaravan(it),
                    isEnemy = false,
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