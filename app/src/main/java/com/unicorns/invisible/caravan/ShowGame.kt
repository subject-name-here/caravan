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
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.TextSymbola
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.dpToPx
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getGameDividerColor
import com.unicorns.invisible.caravan.utils.getGameScoreColor
import com.unicorns.invisible.caravan.utils.getGameSelectionColor
import com.unicorns.invisible.caravan.utils.getGrayTransparent
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playCardFlipSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playJokerReceivedSounds
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playNoCardAlarm
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playVatsReady
import com.unicorns.invisible.caravan.utils.pxToDp
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.startAmbient
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min


@Composable
fun ShowGame(activity: MainActivity, game: Game, goBack: () -> Unit) {
    var selectedCard by remember { mutableStateOf<Int?>(null) }
    var selectedCaravan by remember { mutableIntStateOf(-1) }

    var caravansKey by rememberSaveable { mutableStateOf(true) }
    var enemyHandKey by rememberSaveable { mutableIntStateOf(0) }

    game.enemyCResources.onDropCardFromHand = { enemyHandKey = -1 }

    fun onCardClicked(index: Int) {
        if (game.isOver()) {
            return
        }
        selectedCard = if (index == selectedCard || index !in game.playerCResources.hand.indices) {
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

    fun onCaravanClicked(index: Int) {
        if (game.isOver()) {
            return
        }
        selectedCaravan = index
        if (selectedCaravan == -1) {
            playCloseSound(activity)
        } else {
            playSelectSound(activity)
        }
        selectedCard = null
        caravansKey = !caravansKey
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
        playVatsReady(activity)
        game.playerCResources.removeFromHand(selectedCardNN)
        resetSelected()
        game.afterPlayerMove(
            { updateEnemyHand(); updateCaravans() },
            activity.animationSpeed.value ?: AnimationSpeed.NORMAL
        )
    }

    fun dropCaravan() {
        val selectedCaravanNN = selectedCaravan
        if (selectedCaravanNN == -1) return
        playVatsReady(activity)
        game.playerCaravans[selectedCaravanNN].dropCaravan()
        resetSelected()
        game.afterPlayerMove(
            { updateEnemyHand(); updateCaravans() },
            activity.animationSpeed.value ?: AnimationSpeed.NORMAL
        )
    }

    fun addCardToCaravan(caravan: Caravan, position: Int, isEnemy: Boolean) {
        fun onCaravanCardInserted() {
            resetSelected()
            game.afterPlayerMove(
                { updateEnemyHand(); updateCaravans() },
                activity.animationSpeed.value ?: AnimationSpeed.NORMAL
            )
        }

        val cardIndex = selectedCard
        val card = cardIndex?.let { game.playerCResources.hand[cardIndex] }
        if (card != null && game.isPlayerTurn && !game.isOver() && !(game.isInitStage() && card.isFace())) {
            if (card.isFace()) {
                if (position in caravan.cards.indices && caravan.cards[position].canAddModifier(card)) {
                    playCardFlipSound(activity)
                    if (card.rank == Rank.JOKER) {
                        playJokerSounds(activity)
                    }
                    caravan.cards[position].addModifier(
                        game.playerCResources.removeFromHand(
                            cardIndex
                        )
                    )
                    onCaravanCardInserted()
                }
            } else {
                if (position == caravan.cards.size && !isEnemy) {
                    if (caravan.canPutCardOnTop(card)) {
                        playCardFlipSound(activity)
                        caravan.putCardOnTop(game.playerCResources.removeFromHand(cardIndex))
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
        activity.animationSpeed.value ?: AnimationSpeed.NORMAL,
        { "" },
        { "" },
        {},
        ::onCardClicked,
        selectedCard,
        getSelectedCaravan = { selectedCaravan },
        setSelectedCaravan = ::onCaravanClicked,
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
    animationSpeed: AnimationSpeed,
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
    var ambientStartedFlag by rememberSaveable { mutableStateOf(false) }
    if (!ambientStartedFlag) {
        ambientStartedFlag = true
        LaunchedEffect(Unit) { startAmbient(activity) }
    }

    if (game.playerCResources.deckSize == 0) {
        LaunchedEffect(Unit) { playNoCardAlarm(activity) }
    }

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
                // TODO: check autoscrolls
                0 -> state1Enemy.scrollToItem(
                    0,
                    (stateToSizeOfItem(state1Enemy).toFloat() * (caravan.size - position - 1) / caravan.size).toInt()
                )

                1 -> state2Enemy.scrollToItem(
                    0,
                    (stateToSizeOfItem(state2Enemy).toFloat() * (caravan.size - position - 1) / caravan.size).toInt()
                )

                2 -> state3Enemy.scrollToItem(
                    0,
                    (stateToSizeOfItem(state3Enemy).toFloat() * (caravan.size - position - 1) / caravan.size).toInt()
                )
            }
        }
    }

    fun addCardToPlayerCaravan(caravanNum: Int, position: Int) {
        addCardToCaravan(game.playerCaravans[caravanNum], caravanNum, position, false)
        MainScope().launch {
            val caravan = game.playerCaravans[caravanNum]
            when (caravanNum) {
                0 -> state1Player.scrollToItem(
                    0,
                    (stateToSizeOfItem(state1Player).toFloat() * position / caravan.size).toInt()
                )

                1 -> state2Player.scrollToItem(
                    0,
                    (stateToSizeOfItem(state2Player).toFloat() * position / caravan.size).toInt()
                )

                2 -> state3Player.scrollToItem(
                    0,
                    (stateToSizeOfItem(state3Player).toFloat() * position / caravan.size).toInt()
                )
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
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .background(getBackgroundColor(activity))
            ) {
                TextFallout(
                    stringResource(R.string.back_to_menu),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    16.sp,
                    Alignment.Center,
                    Modifier
                        .fillMaxWidth()
                        .clickableCancel(activity) {
                            goBack()
                        }
                        .padding(8.dp),
                    TextAlign.Center
                )
            }
        }) { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .rotate(180f)) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .getTableBackground(activity.styleId)) {}
            }
            BoxWithConstraints(Modifier.padding(innerPadding)) {
                var wasCardDropped by remember { mutableStateOf(false) }
                if (maxWidth > maxHeight) {
                    Row(Modifier.fillMaxSize()) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        ) {
                            EnemySide(
                                activity,
                                animationSpeed,
                                isPvP,
                                getEnemySymbol,
                                game,
                                fillHeight = 0.45f,
                                enemyHandKey
                            )
                            PlayerSide(activity, animationSpeed, isPvP, game, {
                                val res = wasCardDropped
                                wasCardDropped = false
                                res
                            }, selectedCard, onCardClicked, getMySymbol, setMySymbol)
                        }
                        Caravans(
                            activity,
                            animationSpeed,
                            { selectedCard },
                            { selectedCard?.let { game.playerCResources.hand.getOrNull(it) } },
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
                            { game.isOver() },
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
                        EnemySide(
                            activity,
                            animationSpeed,
                            isPvP,
                            getEnemySymbol,
                            game,
                            fillHeight = 0.15f,
                            enemyHandKey
                        )
                        Caravans(
                            activity,
                            animationSpeed,
                            { selectedCard },
                            { selectedCard?.let { game.playerCResources.hand.getOrNull(it) } },
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
                            { game.isOver() },
                            { num -> game.playerCaravans[num] },
                            { num -> game.enemyCaravans[num] },
                        )
                        PlayerSide(activity, animationSpeed, isPvP, game, {
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
    animationSpeed: AnimationSpeed,
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
        RowOfEnemyCards(activity, animationSpeed, game.enemyCResources.hand, enemyHandKey < 0)
        key(enemyHandKey) {
            Box(Modifier.fillMaxSize()) {
                ShowDeck(game.enemyCResources, activity, isKnown = !isPvP)
                if (isPvP) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        TextSymbola(
                            getEnemySymbol(),
                            getTextColor(activity),
                            24.sp,
                            Alignment.BottomCenter,
                            Modifier.background(getTextBackgroundColor(activity)),
                            TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerSide(
    activity: MainActivity,
    animationSpeed: AnimationSpeed,
    isPvP: Boolean,
    game: Game,
    wasCardDropped: () -> Boolean,
    selectedCard: Int?,
    onCardClicked: (Int) -> Unit,
    getMySymbol: () -> String, setMySymbol: () -> Unit
) {
    val selectedCardColor = getGameSelectionColor(activity)
    Row(
        verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxSize()
    ) {
        PlayerCards(
            activity,
            animationSpeed,
            game.playerCResources.hand,
            wasCardDropped(),
            selectedCard,
            selectedCardColor,
            onCardClicked
        )
        Box {
            ShowDeck(game.playerCResources, activity)
            if (isPvP) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    TextSymbola(
                        getMySymbol(),
                        getTextColor(activity),
                        24.sp,
                        Alignment.BottomCenter,
                        Modifier
                            .background(getTextBackgroundColor(activity))
                            .clickableOk(activity) {
                                setMySymbol()
                            },
                        TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerCards(
    activity: MainActivity,
    animationSpeed: AnimationSpeed,
    cards: List<Card>,
    wasCardDropped: Boolean,
    selectedCard: Int?,
    selectedCardColor: Color,
    onClick: (Int) -> Unit
) {
    Hand(activity, animationSpeed, false, cards, wasCardDropped, selectedCard, selectedCardColor, onClick)
}

@Composable
fun RowOfEnemyCards(activity: MainActivity, animationSpeed: AnimationSpeed, cards: List<Card>, wasCardDropped: Boolean = false) {
    Hand(activity, animationSpeed, true, cards, wasCardDropped, -1, Color.Transparent) {}
}


@Composable
fun Hand(
    activity: MainActivity,
    animationSpeed: AnimationSpeed,
    isEnemy: Boolean,
    cards: List<Card>,
    wasCardDropped: Boolean,
    selectedCard: Int?, selectedCardColor: Color, onClick: (Int) -> Unit
) {
    val enemyMult = if (isEnemy) -1f else 1f
    BoxWithConstraints(
        Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(),
        Alignment.TopStart
    ) {
        val cardHeight = (maxHeight - 16.dp) / 2
        val cardWidth = (maxWidth - 40.dp) / 5
        val scaleH = cardHeight / 256.pxToDp()
        val scaleW = cardWidth / 183.pxToDp()
        val scale = min(scaleW, scaleH)

        val memCards = remember { mutableObjectListOf<Card>() }
        memCards.addAll(cards - memCards.asMutableList().toSet())

        val itemVerticalOffsetMovingIn = remember { Animatable(2.5f * enemyMult) }
        val itemVerticalOffsetMovingOut = remember { Animatable(0f) }

        var recomposeKey by remember { mutableStateOf(false) }
        LaunchedEffect(recomposeKey) { }

        val isAnyMovingIn = memCards.any { it.handAnimationMark == Card.AnimationMark.MOVING_IN || it.handAnimationMark == Card.AnimationMark.MOVING_IN_WIP }
        val isAnyMovingOut = memCards.any { it.handAnimationMark == Card.AnimationMark.MOVING_OUT || it.handAnimationMark == Card.AnimationMark.MOVING_OUT_WIP }

        if (animationSpeed.delay != 0L) {
            LaunchedEffect(isAnyMovingIn) {
                if (isAnyMovingIn) {
                    playCardFlipSound(activity)
                    itemVerticalOffsetMovingIn.snapTo(2.5f * enemyMult)
                    memCards.forEach {
                        if (it.handAnimationMark == Card.AnimationMark.MOVING_IN) {
                            it.handAnimationMark = Card.AnimationMark.MOVING_IN_WIP
                        }
                    }
                    itemVerticalOffsetMovingIn.animateTo(0f, TweenSpec(animationSpeed.delay.toInt())) {
                        recomposeKey = !recomposeKey
                    }
                    memCards.forEach {
                        if (it.handAnimationMark == Card.AnimationMark.MOVING_IN || it.handAnimationMark == Card.AnimationMark.MOVING_IN_WIP) {
                            it.handAnimationMark = Card.AnimationMark.STABLE
                        }
                    }
                    recomposeKey = !recomposeKey
                }
            }

            LaunchedEffect(isAnyMovingOut) {
                if (isAnyMovingOut) {
                    playCardFlipSound(activity)
                    val target = (if (wasCardDropped) 2.5f else -2.5f) * enemyMult
                    itemVerticalOffsetMovingOut.snapTo(0f)
                    memCards.forEach {
                        if (it.handAnimationMark == Card.AnimationMark.MOVING_OUT) {
                            it.handAnimationMark = Card.AnimationMark.MOVING_OUT_WIP
                        }
                    }
                    itemVerticalOffsetMovingOut.animateTo(target, TweenSpec(animationSpeed.delay.toInt())) {
                        recomposeKey = !recomposeKey
                    }
                    memCards.removeIf { it.handAnimationMark == Card.AnimationMark.MOVING_OUT || it.handAnimationMark == Card.AnimationMark.MOVING_OUT_WIP }
                    recomposeKey = !recomposeKey
                }
            }
        }

        val iteratedCollection = if (animationSpeed.delay == 0L) cards else memCards.asMutableList()
        memCards.forEach {
            val inValue = if (animationSpeed.delay == 0L) 0f else when (it.handAnimationMark) {
                Card.AnimationMark.MOVING_IN -> 2.5f * enemyMult
                Card.AnimationMark.MOVING_IN_WIP -> itemVerticalOffsetMovingIn.value
                else -> 0f
            }
            val outValue = if (animationSpeed.delay == 0L) 0f else when (it.handAnimationMark) {
                Card.AnimationMark.MOVING_OUT_WIP -> itemVerticalOffsetMovingOut.value
                else -> 0f
            }
            val index = iteratedCollection.indexOf(it)
            if (index == -1) return@forEach
            val cardsSize = iteratedCollection.size
            if (isEnemy) {
                ShowCardBack(
                    activity,
                    it,
                    Modifier
                        .scale(scale)
                        .layout { measurable, constraints ->
                            val rowWidth =
                                if (cardsSize <= 5) cardsSize else (if (index < 5) 5 else (cardsSize - 5))
                            val placeable = measurable.measure(constraints)
                            val scaledWidth = placeable.width
                            val scaledHeight = placeable.height
                            val handVerticalAlignment = if (cardsSize > 5) 0 else scaledHeight / 2
                            val offsetWidth =
                                (index % 5) * scaledWidth + maxWidth.toPx() / 2 - (rowWidth / 2f) * scaledWidth
                            val offsetHeight = scaledHeight * (index / 5) + handVerticalAlignment +
                                    scaledHeight *
                                    (inValue + outValue)
                            layout(constraints.maxWidth, 0) {
                                placeable.place(offsetWidth.toInt(), offsetHeight.toInt())
                            }
                        }
                )
            } else {
                if (it.rank == Rank.JOKER && it.handAnimationMark == Card.AnimationMark.MOVING_IN) {
                    if (animationSpeed.delay == 0L) {
                        it.handAnimationMark = Card.AnimationMark.STABLE
                    }
                    LaunchedEffect(Unit) {
                        playJokerReceivedSounds(activity)
                    }
                }
                ShowCard(
                    activity,
                    it,
                    Modifier
                        .scale(scale)
                        .layout { measurable, constraints ->
                            val rowWidth =
                                if (cardsSize <= 5) cardsSize else (if (index < 5) 5 else (cardsSize - 5))
                            val placeable = measurable.measure(constraints)
                            val scaledWidth = placeable.width
                            val scaledHeight = placeable.height
                            val handVerticalAlignment = if (cardsSize > 5) 0 else scaledHeight / 2
                            val offsetWidth =
                                (index % 5) * scaledWidth + maxWidth.toPx() / 2 - (rowWidth / 2f) * scaledWidth
                            val offsetHeight = scaledHeight * (index / 5) + handVerticalAlignment +
                                    scaledHeight *
                                    (inValue + outValue)
                            layout(constraints.maxWidth, 0) {
                                placeable.place(offsetWidth.toInt(), offsetHeight.toInt())
                            }
                        }
                        .clickable {
                            if (!itemVerticalOffsetMovingIn.isRunning && !itemVerticalOffsetMovingIn.isRunning) {
                                onClick(index)
                            }
                        }
                        .border(
                            width = if (index == (selectedCard ?: -1)) 3.dp else (-1).dp,
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
    animationSpeed: AnimationSpeed,
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
    var width by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (width == 0) {
            width = state.layoutInfo.viewportSize.width
            delay(95L)
        }
    }
    val trueWidth = (width - 3.5f * 10.dp.dpToPx())
    val scale = (trueWidth / 183.toFloat()).coerceAtMost(1.2f)
    Column(
        Modifier
            .fillMaxHeight()
            .weight(0.25f)
    ) {
        if (!isEnemy) {
            TextFallout(
                if (isInitStage || caravan.getValue() == 0) "" else stringResource(R.string.discard),
                getTextColor(activity),
                getTextStrokeColor(activity),
                14.sp,
                Alignment.Center,
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectCaravan()
                    }
                    .padding(start = 2.dp, end = 2.dp, bottom = 2.dp, top = 0.dp)
                    .background(
                        if (isInitStage || caravan.getValue() == 0) Color.Transparent else run {
                            val color = getTextBackgroundColor(activity)
                            Color(color.red, color.green, color.blue, 0.75f)
                        }
                    )
                    .padding(2.dp),
                TextAlign.Center
            )
        }
        LazyColumn(
            state = state,
            verticalArrangement = Arrangement.Top,
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
                    if (animationSpeed.delay == 0L) {
                        @Composable
                        fun ModifierOnCardInCaravan(
                            modifier: Card,
                            modifierIndex: Int,
                        ) {
                            Box(modifier = Modifier
                                .layout { measurable, constraints ->
                                    val placeable = measurable.measure(constraints)
                                    val modifierOffset =
                                        ((if (isEnemy) (-10).dp else 10.dp) * (modifierIndex + 1)).toPx()
                                            .toInt()
                                    layout((placeable.width * scale).toInt(), 0) {
                                        placeable.place(modifierOffset, 0)
                                    }
                                })
                            {
                                ShowCard(activity, modifier, Modifier.scale(scale))
                            }
                        }

                        @Composable
                        fun CardInCaravan(
                            it: CardWithModifier,
                            index: Int
                        ) {
                            val modifier = Modifier
                                .layout { measurable, constraints ->
                                    val placeable = measurable.measure(constraints)
                                    val placeableHeight = (placeable.height * scale).toInt()
                                    val scaleHeightOffset = placeable.height / 2 - placeableHeight / 2
                                    val layoutFullHeight = if (isEnemy) {
                                        max(
                                            placeableHeight / 3 * (caravan.cards.size + 2),
                                            state.layoutInfo.viewportSize.height - 1
                                        )
                                    } else {
                                        placeableHeight / 3 * (caravan.cards.size + 3)
                                    }
                                    val offsetWidth = constraints.maxWidth / 2 - placeable.width / 2
                                    val antiOffsetHeight = if (isEnemy)
                                        (layoutFullHeight - placeableHeight / 3 * index - placeableHeight)
                                    else
                                        (placeableHeight / 3 * index)
                                    layout(constraints.maxWidth, layoutFullHeight.coerceAtLeast(0)) {
                                        placeable.place(
                                            offsetWidth,
                                            antiOffsetHeight - scaleHeightOffset
                                        )
                                    }
                                }

                            Box(modifier = modifier) {
                                ShowCard(activity, it.card,
                                    Modifier
                                        .scale(scale)
                                        .clickable {
                                            addSelectedCardOnPosition(caravan.cards.indexOf(it))
                                        })

                                it.modifiersCopy().withIndex().forEach { (modifierIndex, card) ->
                                    ModifierOnCardInCaravan(card, modifierIndex)
                                }
                            }
                        }

                        caravan.cards.forEachIndexed { index, it ->
                            CardInCaravan(it, index)
                        }
                    } else {
                        val memCards = remember { mutableObjectListOf<CardWithModifier>() }
                        memCards.addAll(caravan.cards - memCards.asMutableList().toSet())

                        val animationIn = remember { Animatable(3f) }
                        val animationOut = remember { Animatable(0f) }
                        var recomposeKey by remember { mutableStateOf(false) }
                        LaunchedEffect(recomposeKey) { }

                        val isAnyMovingIn = memCards.any {
                            it.card.caravanAnimationMark == Card.AnimationMark.MOVING_IN
                                    || it.card.caravanAnimationMark == Card.AnimationMark.MOVING_IN_WIP
                                    || it.modifiersCopy().any { mod ->
                                        mod.caravanAnimationMark == Card.AnimationMark.MOVING_IN
                                                || mod.caravanAnimationMark == Card.AnimationMark.MOVING_IN_WIP
                                    }
                        }
                        val isAnyMovingOut = memCards.any { it.card.caravanAnimationMark == Card.AnimationMark.MOVING_OUT
                                || it.card.caravanAnimationMark == Card.AnimationMark.MOVING_OUT_WIP }
                        LaunchedEffect(isAnyMovingIn) {
                            if (isAnyMovingIn) {
                                playCardFlipSound(activity)
                                animationIn.snapTo(3f)
                                memCards.forEach {
                                    if (it.card.caravanAnimationMark == Card.AnimationMark.MOVING_IN) {
                                        it.card.caravanAnimationMark = Card.AnimationMark.MOVING_IN_WIP
                                    }
                                    it.modifiersCopy().forEach { mod ->
                                        if (mod.caravanAnimationMark == Card.AnimationMark.MOVING_IN) {
                                            mod.caravanAnimationMark = Card.AnimationMark.MOVING_IN_WIP
                                        }
                                    }
                                }
                                animationIn.animateTo(0f, TweenSpec(animationSpeed.delay.toInt(), animationSpeed.delay.toInt())) {
                                    recomposeKey = !recomposeKey
                                }
                                memCards.forEach {
                                    if (it.card.caravanAnimationMark == Card.AnimationMark.MOVING_IN_WIP) {
                                        it.card.caravanAnimationMark = Card.AnimationMark.STABLE
                                    }
                                    it.modifiersCopy().forEach { mod ->
                                        if (mod.caravanAnimationMark == Card.AnimationMark.MOVING_IN_WIP) {
                                            mod.caravanAnimationMark = Card.AnimationMark.STABLE
                                        }
                                    }
                                }
                            }
                        }

                        LaunchedEffect(isAnyMovingOut) {
                            if (isAnyMovingOut) {
                                playCardFlipSound(activity)
                                animationOut.snapTo(0f)
                                memCards.forEach {
                                    if (it.card.caravanAnimationMark == Card.AnimationMark.MOVING_OUT) {
                                        it.card.caravanAnimationMark = Card.AnimationMark.MOVING_OUT_WIP
                                    }
                                }
                                delay(animationSpeed.delay * 2)
                                animationOut.animateTo(2f, TweenSpec(animationSpeed.delay.toInt(), animationSpeed.delay.toInt() * 2)) {
                                    recomposeKey = !recomposeKey
                                }
                                memCards.removeIf { it.card.caravanAnimationMark == Card.AnimationMark.MOVING_OUT_WIP }
                            }
                        }

                        @Composable
                        fun ModifierOnCardInCaravan(
                            modifier: Card,
                            modifierIndex: Int,
                            it: CardWithModifier
                        ) {
                            val inValue = when (modifier.caravanAnimationMark) {
                                Card.AnimationMark.MOVING_IN -> {
                                    3f * (if (isPlayerTurn) 1 else -1)
                                }
                                Card.AnimationMark.MOVING_IN_WIP -> {
                                    animationIn.value * (if (isPlayerTurn) 1 else -1)
                                }
                                else -> {
                                    0f
                                }
                            }
                            val outValue = when (it.card.caravanAnimationMark) {
                                Card.AnimationMark.MOVING_OUT_WIP -> {
                                    animationOut.value
                                }
                                else -> {
                                    0f
                                }
                            }
                            Box(modifier = Modifier
                                .layout { measurable, constraints ->
                                    val placeable = measurable.measure(constraints)
                                    val placeableHeight = (placeable.height * scale).toInt()
                                    val placeableWidth = (placeable.width * scale).toInt()
                                    val modifierOffset =
                                        ((if (isEnemy) (-10).dp else 10.dp) * (modifierIndex + 1)).toPx()
                                            .toInt()
                                    layout(placeableWidth.coerceAtLeast(0), 0) {
                                        placeable.place(
                                            modifierOffset + (placeableWidth * outValue).toInt(),
                                            (placeableHeight * inValue * (if (isPlayerTurn) 1 else -1)).toInt()
                                        )
                                    }
                                })
                            {
                                ShowCard(activity, modifier, Modifier.scale(scale))
                            }
                        }

                        @Composable
                        fun CardInCaravan(
                            it: CardWithModifier,
                            index: Int
                        ) {
                            val inValue = when (it.card.caravanAnimationMark) {
                                Card.AnimationMark.MOVING_IN -> {
                                    3f * enemyMult
                                }
                                Card.AnimationMark.MOVING_IN_WIP -> {
                                    animationIn.value * enemyMult
                                }
                                else -> {
                                    0f
                                }
                            }
                            val outValue = when (it.card.caravanAnimationMark) {
                                Card.AnimationMark.MOVING_OUT_WIP -> {
                                    animationOut.value
                                }
                                else -> {
                                    0f
                                }
                            }
                            val modifier = Modifier
                                .layout { measurable, constraints ->
                                    val placeable = measurable.measure(constraints)
                                    val placeableHeight = (placeable.height * scale).toInt()
                                    val placeableWidth = (placeable.width * scale).toInt()
                                    val layoutFullHeight = if (isEnemy) {
                                        max(
                                            placeableHeight / 3 * (memCards.size + 2),
                                            state.layoutInfo.viewportSize.height - 1
                                        )
                                    } else {
                                        placeableHeight / 3 * (memCards.size + 3)
                                    }
                                    val offsetWidth = constraints.maxWidth / 2 - placeable.width / 2
                                    val antiOffsetHeight = if (isEnemy)
                                        (layoutFullHeight - placeableHeight / 3 * index - placeableHeight)
                                    else
                                        (placeableHeight / 3 * index)
                                    val scaleHeightOffset = placeable.height / 2 - placeableHeight / 2
                                    layout(constraints.maxWidth, layoutFullHeight.coerceAtLeast(0)) {
                                        placeable.place(
                                            offsetWidth + (placeableWidth * outValue).toInt(),
                                            antiOffsetHeight - scaleHeightOffset + (placeableHeight * inValue).toInt()
                                        )
                                    }
                                }

                            Box(modifier = modifier) {
                                ShowCard(activity, it.card,
                                    Modifier
                                        .scale(scale)
                                        .clickable {
                                            addSelectedCardOnPosition(caravan.cards.indexOf(it))
                                        })

                                it.modifiersCopy().withIndex().forEach { (modifierIndex, card) ->
                                    ModifierOnCardInCaravan(card, modifierIndex, it)
                                }
                            }
                        }

                        memCards.forEachIndexed { index, it ->
                            CardInCaravan(it, index)
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
                                    Box(
                                        modifier = Modifier
                                            .fillParentMaxWidth()
                                            .height(20.dp)
                                            .align(Alignment.BottomCenter)
                                            .padding(horizontal = 4.dp)
                                            .background(colorResource(id = R.color.red))
                                            .border(4.dp, colorResource(id = R.color.dark_red))
                                    ) {}
                                }

                                else -> {
                                    Box(
                                        modifier = Modifier
                                            .fillParentMaxWidth()
                                            .height(20.dp)
                                            .align(Alignment.BottomCenter)
                                            .padding(horizontal = 4.dp)
                                            .background(getTextBackgroundColor(activity))
                                            .border(4.dp, getTextColor(activity))
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
        TextFallout(
            cResources.deckSize.toString(),
            getTextColor(activity),
            getTextStrokeColor(activity),
            16.sp,
            Alignment.Center,
            Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .background(getBackgroundColor(activity)),
            TextAlign.Center
        )
        if (isKnown) {
            val (back, isAlt) = cResources.getDeckBack() ?: (null to false)
            if (back != null) {
                ShowCardBack(
                    activity,
                    Card(Rank.ACE, Suit.HEARTS, back, isAlt),
                    Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun RowScope.Score(activity: MainActivity, num: Int, caravan: Caravan, opposingValue: Int) {
    val cities = getStyleCities(activity.styleId)
    val (textColor, text, isColored) = if (caravan.getValue() > 26)
        Triple(Color.Red, caravan.getValue().toString(), true)
    else if (caravan.getValue() in (21..26) && (opposingValue !in (21..26) || caravan.getValue() > opposingValue))
        Triple(Color.Green, caravan.getValue().toString(), true)
    else
        Triple(getGameScoreColor(activity), caravan.getValue().toString(), false)
    Box(
        Modifier
            .weight(0.25f)
            .height(24.dp)
            .padding(2.dp)
            .background(getGrayTransparent(activity))
            .padding(2.dp)
    ) {
        Text(
            text = cities[num],
            textAlign = TextAlign.Center,
            color = if (isColored) textColor else Color.Black,
            fontFamily = FontFamily(Font(R.font.help)),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .align(Alignment.CenterStart),
            fontSize = 9.sp,
            maxLines = 1,
            overflow = TextOverflow.Visible,
            softWrap = false,
        )
        Spacer(Modifier.width(2.dp))
        TextFallout(
            text,
            textColor,
            textColor,
            14.sp,
            Alignment.CenterEnd,
            Modifier.fillMaxSize(),
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun Caravans(
    activity: MainActivity,
    animationSpeed: AnimationSpeed,
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
    isGameOver: () -> Boolean,
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
        Row(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(enemyCaravanFillHeight)) {
            repeat(3) {
                CaravanOnField(
                    activity,
                    animationSpeed,
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

        Row(
            Modifier
                .fillMaxWidth()
                .height(52.dp)) {
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(0.75f)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(24.dp)) {
                    repeat(3) {
                        val caravan = getEnemyCaravan(it)
                        val opposingValue = getPlayerCaravan(it).getValue()
                        Score(activity, it, caravan, opposingValue)
                    }
                }
                HorizontalDivider(thickness = 4.dp, color = getGameDividerColor(activity))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(24.dp)) {
                    repeat(3) {
                        val caravan = getPlayerCaravan(it)
                        val opposingValue = getEnemyCaravan(it).getValue()
                        Score(activity, it + 3, caravan, opposingValue)
                    }
                }
            }
            Column(modifier = Modifier
                .fillMaxHeight()
                .weight(0.25f)) {
                val text = when {
                    isGameOver() -> stringResource(R.string.can_t_act)
                    !isPlayersTurn() -> stringResource(R.string.wait)
                    getIsInitStage() -> stringResource(R.string.init_stage)
                    getSelectedCardInt() != null -> {
                        stringResource(R.string.discard_card)
                    }

                    getSelectedCaravan() in (0..2) -> {
                        stringResource(R.string.drop_caravan, getSelectedCaravan() + 1)
                    }

                    else -> stringResource(R.string.your_turn)
                }
                val modifier =
                    if (canDiscard() && (getSelectedCardInt() != null || getSelectedCaravan() in (0..2))) {
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
                            .background(run {
                                val color = getTextBackgroundColor(activity)
                                Color(color.red, color.green, color.blue, 0.75f)
                            })
                            .padding(6.dp)
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(getGrayTransparent(activity))
                            .padding(6.dp)
                    }
                TextFallout(
                    text,
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    16.sp,
                    Alignment.Center,
                    modifier,
                    TextAlign.Center
                )
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()) {
            repeat(3) {
                CaravanOnField(
                    activity,
                    animationSpeed,
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