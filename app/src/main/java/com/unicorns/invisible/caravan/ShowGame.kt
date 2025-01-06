package com.unicorns.invisible.caravan

import android.annotation.SuppressLint
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
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.model.enemy.EnemyFinalBossStory
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
import com.unicorns.invisible.caravan.utils.playNoBeep
import com.unicorns.invisible.caravan.utils.playNoCardAlarm
import com.unicorns.invisible.caravan.utils.playNukeBlownSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playVatsReady
import com.unicorns.invisible.caravan.utils.playWWSound
import com.unicorns.invisible.caravan.utils.pxToDp
import com.unicorns.invisible.caravan.utils.scrollbar
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min


@Composable
fun ShowGame(
    activity: MainActivity,
    game: Game,
    isBlitz: Boolean = false,
    onMove: (Card?) -> Unit = {},
    goBack: () -> Unit
) {
    var selectedCard by remember { mutableIntStateOf(-1) }
    var selectedCaravan by remember { mutableIntStateOf(-1) }

    var caravansKey by rememberSaveable { mutableIntStateOf(0) }
    var enemyHandKey by rememberSaveable { mutableIntStateOf(0) }
    var playerHandKey by rememberSaveable { mutableIntStateOf(0) }
    fun updateCaravans() { caravansKey++ }
    fun updateEnemyHand() { enemyHandKey++ }
    fun updatePlayerHand() { playerHandKey++ }

    val animationSpeed by rememberSaveable {
        mutableStateOf(
            if (isBlitz || game.enemy is EnemyFinalBossStory)
                AnimationSpeed.NONE
            else
                save.animationSpeed
        )
    }

    fun onCardClicked(index: Int) {
        if (game.isOver()) {
            return
        }
        selectedCard = if (index == selectedCard || index !in game.playerCResources.hand.indices) {
            playCloseSound(activity)
            -1
        } else {
            playSelectSound(activity)
            index
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
        selectedCard = -1
        updateCaravans()
    }

    fun resetSelected() {
        selectedCaravan = -1
        selectedCard = -1
    }

    fun dropCardFromHand() {
        val selectedCardNN = selectedCard
        if (selectedCardNN !in game.playerCResources.hand.indices) return
        playVatsReady(activity)
        game.playerCResources.dropCardFromHand(selectedCardNN)
        resetSelected()
        updatePlayerHand()
        onMove(null)
        game.afterPlayerMove(
            { updateEnemyHand(); updateCaravans(); updatePlayerHand() },
            animationSpeed
        )
    }

    fun dropCaravan() {
        val selectedCaravanNN = selectedCaravan
        if (selectedCaravanNN !in game.playerCaravans.indices) return
        playVatsReady(activity)
        activity.processChallengesMove(Challenge.Move(moveCode = 1), game)
        game.playerCaravans[selectedCaravanNN].dropCaravan()
        resetSelected()
        updateCaravans()
        onMove(null)
        game.afterPlayerMove(
            { updateEnemyHand(); updateCaravans(); updatePlayerHand() },
            animationSpeed
        )
    }

    fun addCardToCaravan(caravan: Caravan, position: Int, isEnemy: Boolean) {
        val cardIndex = selectedCard
        val card = game.playerCResources.hand.getOrNull(cardIndex) ?: return
        fun onCaravanCardInserted() {
            resetSelected()
            updatePlayerHand()
            onMove(card)
            game.afterPlayerMove(
                { updateEnemyHand(); updateCaravans(); updatePlayerHand() },
                animationSpeed
            )
        }
        if (game.isPlayerTurn && !game.isOver() && !(game.isInitStage() && card.isFace())) {
            if (card.isFace()) {
                if (caravan.cards.getOrNull(position)?.canAddModifier(card) == true) {
                    playCardFlipSound(activity)
                    if (card.isOrdinary() && card.rank == Rank.JOKER) {
                        playJokerSounds(activity)
                    } else if (card.getWildWastelandCardType() != null) {
                        playWWSound(activity)
                    } else if (card.isNuclear()) {
                        playNukeBlownSound(activity)
                    }

                    activity.processChallengesMove(Challenge.Move(
                        moveCode = 4,
                        handCard = card
                    ), game)
                    caravan.cards[position].addModifier(
                        game.playerCResources.removeFromHand(cardIndex)
                    )
                    onCaravanCardInserted()
                }
            } else {
                if (!isEnemy) {
                    if (caravan.canPutCardOnTop(card)) {
                        playCardFlipSound(activity)
                        activity.processChallengesMove(Challenge.Move(
                            moveCode = 3,
                            handCard = card
                        ), game)
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
        animationSpeed,
        { "" }, { "" }, {},
        { selectedCard }, ::onCardClicked,
        { selectedCaravan }, ::onCaravanClicked,
        { a1, _, a3, a4 -> addCardToCaravan(a1, a3, a4) },
        ::dropCardFromHand,
        ::dropCaravan,
        enemyHandKey,
        caravansKey,
        playerHandKey
    )

    if (isBlitz) {
        var timeOnTimer by rememberScoped { mutableIntStateOf(
            if (
                game.enemyCResources.deckSize < game.playerCResources.deckSize &&
                game.enemyCResources.deckSize > 20
            ) {
                game.enemyCResources.deckSize * 4 / 3
            } else {
                game.playerCResources.deckSize * 4 / 3
            }
        ) }
        game.specialGameOverCondition = { if (timeOnTimer <= 0f) -1 else 0 }

        LaunchedEffect(Unit) {
            while (isActive && timeOnTimer > 0 && !game.isOver()) {
                timeOnTimer--
                if (timeOnTimer < 10) {
                    playNoBeep(activity)
                }
                delay(1000L)
            }
            if (timeOnTimer <= 0f) {
                game.checkOnGameOver()
            }
        }

        key(timeOnTimer) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                contentAlignment = Alignment.BottomEnd
            ) {
                TextFallout(
                    timeOnTimer.toString(),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    16.sp,
                    Alignment.BottomEnd,
                    Modifier
                        .background(getTextBackgroundColor(activity))
                        .padding(8.dp),
                    TextAlign.Center
                )
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
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
    getSelectedCard: () -> Int,
    setSelectedCard: (Int) -> Unit,
    getSelectedCaravan: () -> Int,
    setSelectedCaravan: (Int) -> Unit,
    addCardToCaravan: (Caravan, Int, Int, Boolean) -> Unit,
    dropCardFromHand: () -> Unit,
    dropCaravan: () -> Unit,
    enemyHandKey: Int,
    caravansKey: Int,
    playerHandKey: Int,
) {
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
                0 -> state1Enemy.scrollToItem(0,
                    (stateToSizeOfItem(state1Enemy).toFloat() * (caravan.size - position - 1) / caravan.size).toInt()
                )
                1 -> state2Enemy.scrollToItem(0,
                    (stateToSizeOfItem(state2Enemy).toFloat() * (caravan.size - position - 1) / caravan.size).toInt()
                )
                2 -> state3Enemy.scrollToItem(0,
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
                0 -> state1Player.scrollToItem(0,
                    (stateToSizeOfItem(state1Player).toFloat() * position / caravan.size).toInt()
                )
                1 -> state2Player.scrollToItem(0,
                    (stateToSizeOfItem(state2Player).toFloat() * position / caravan.size).toInt()
                )
                2 -> state3Player.scrollToItem(0,
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
                    .rotate(180f)
            ) {
                Box(Modifier.fillMaxSize().getTableBackground()) {}
            }
            BoxWithConstraints(Modifier.padding(innerPadding)) {
                if (maxWidth > maxHeight) {
                    Row(Modifier.fillMaxSize()) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        ) {
                            EnemySide(activity, animationSpeed, isPvP, getEnemySymbol, game, fillHeight = 0.45f, enemyHandKey)
                            PlayerSide(activity, animationSpeed, isPvP, game, getSelectedCard(), setSelectedCard, getMySymbol, setMySymbol, playerHandKey)
                        }
                        Caravans(
                            activity,
                            animationSpeed,
                            getSelectedCard,
                            { game.playerCResources.hand.getOrNull(getSelectedCard()) },
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
                            { dropCardFromHand() },
                            dropCaravan,
                            ::isInitStage,
                            { game.isPlayerTurn },
                            ::canDiscard,
                            { game.isOver() },
                            { num -> game.playerCaravans[num] },
                            { num -> game.enemyCaravans[num] },
                            caravansKey
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        EnemySide(activity, animationSpeed, isPvP, getEnemySymbol, game, fillHeight = 0.15f, enemyHandKey)
                        Caravans(
                            activity,
                            animationSpeed,
                            getSelectedCard,
                            { game.playerCResources.hand.getOrNull(getSelectedCard()) },
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
                            { dropCardFromHand() },
                            dropCaravan,
                            ::isInitStage,
                            { game.isPlayerTurn },
                            ::canDiscard,
                            { game.isOver() },
                            { num -> game.playerCaravans[num] },
                            { num -> game.enemyCaravans[num] },
                            caravansKey
                        )
                        PlayerSide(activity, animationSpeed, isPvP, game, getSelectedCard(), setSelectedCard, getMySymbol, setMySymbol, playerHandKey)
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
        RowOfEnemyCards(activity, animationSpeed, game.enemyCResources.hand)
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
    selectedCard: Int,
    onCardClicked: (Int) -> Unit,
    getMySymbol: () -> String, setMySymbol: () -> Unit,
    playerHandKey: Int,
) {
    val selectedCardColor = getGameSelectionColor(activity)
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxSize()) {
        PlayerCards(
            activity,
            animationSpeed,
            game.playerCResources.hand,
            selectedCard,
            selectedCardColor,
            onCardClicked
        )
        key(playerHandKey) {
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
}

@Composable
fun PlayerCards(
    activity: MainActivity,
    animationSpeed: AnimationSpeed,
    cards: List<Card>,
    selectedCard: Int,
    selectedCardColor: Color,
    onClick: (Int) -> Unit
) {
    Hand(activity, animationSpeed, false, cards, selectedCard, selectedCardColor, onClick)
}

@Composable
fun RowOfEnemyCards(activity: MainActivity, animationSpeed: AnimationSpeed, cards: List<Card>) {
    Hand(activity, animationSpeed, true, cards, -1, Color.Transparent) {}
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun Hand(
    activity: MainActivity,
    animationSpeed: AnimationSpeed,
    isEnemy: Boolean,
    cards: List<Card>,
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
        memCards.removeIf { it.handAnimationMark == Card.AnimationMark.MOVED_OUT }

        val itemVerticalOffsetMovingIn = remember { Animatable(2.5f * enemyMult) }
        val itemVerticalOffsetMovingOut = remember { Animatable(0f) }

        var recomposeKey by remember { mutableStateOf(false) }
        LaunchedEffect(recomposeKey) {}
        val isAnyMovingIn = memCards.any { it.handAnimationMark.isMovingIn() }
        val isAnyMovingOut = memCards.any { it.handAnimationMark.isMovingOut() }

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
                        if (it.handAnimationMark.isMovingIn()) {
                            it.handAnimationMark = Card.AnimationMark.STABLE
                        }
                    }
                    recomposeKey = !recomposeKey
                }
            }

            LaunchedEffect(isAnyMovingOut) {
                if (isAnyMovingOut) {
                    playCardFlipSound(activity)
                    val isDropping = memCards.any { it.handAnimationMark in listOf(Card.AnimationMark.MOVING_OUT_ALT, Card.AnimationMark.MOVING_OUT_ALT_WIP) }
                    val isDisappearing = memCards.any { it.handAnimationMark == Card.AnimationMark.MOVED_OUT }
                    val target = (if (isDropping) 2.5f else -2.5f) * enemyMult
                    itemVerticalOffsetMovingOut.snapTo(0f)
                    memCards.forEach {
                        if (it.handAnimationMark == Card.AnimationMark.MOVING_OUT) {
                            it.handAnimationMark = Card.AnimationMark.MOVING_OUT_WIP
                        }
                        if (it.handAnimationMark == Card.AnimationMark.MOVING_OUT_ALT) {
                            it.handAnimationMark = Card.AnimationMark.MOVING_OUT_ALT_WIP
                        }
                    }
                    if (!isDisappearing) {
                        itemVerticalOffsetMovingOut.animateTo(target, TweenSpec(animationSpeed.delay.toInt())) {
                            recomposeKey = !recomposeKey
                        }
                    }

                    memCards.removeIf { it.handAnimationMark.isMovingOut() }
                    recomposeKey = !recomposeKey
                }
            }
        } else {
            if (isAnyMovingIn || isAnyMovingOut) {
                recomposeKey = !recomposeKey
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
                Card.AnimationMark.MOVING_OUT_WIP, Card.AnimationMark.MOVING_OUT_ALT_WIP -> itemVerticalOffsetMovingOut.value
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
    caravansKey: Int,
) {
    val enemyMult = if (isEnemy) -1 else 1
    var width by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (width == 0) {
            width = state.layoutInfo.viewportSize.width
            delay(95L)
        }
    }
    val trueWidth = (width - 3f * 13.dp.dpToPx())
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
                                        ((if (isEnemy) (-13).dp else 13.dp) * (modifierIndex + 1)).toPx()
                                            .toInt()
                                    layout((placeable.width * scale).toInt().coerceAtLeast(0), 0) {
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
                                            placeableHeight * 3 / 7 * caravan.size + placeableHeight * 4 / 7,
                                            state.layoutInfo.viewportSize.height - 1
                                        )
                                    } else {
                                        placeableHeight * 3 / 7 * (caravan.size + 1) + placeableHeight * 4 / 7
                                    }
                                    val offsetWidth = constraints.maxWidth / 2 - placeable.width / 2
                                    val antiOffsetHeight = if (isEnemy)
                                        (layoutFullHeight - placeableHeight * 3 / 7 * index - placeableHeight)
                                    else
                                        (placeableHeight * 3 / 7 * index)
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

                        key(caravansKey) {
                            caravan.cards.forEachIndexed { index, it ->
                                CardInCaravan(it, index)
                            }
                        }
                    } else {
                        val memCards = remember { mutableObjectListOf<CardWithModifier>() }
                        memCards.addAll(caravan.cards - memCards.asMutableList().toSet())
                        memCards.removeIf { it.card.caravanAnimationMark == Card.AnimationMark.MOVED_OUT }

                        val animationIn = remember { Animatable(3f) }
                        val animationOut = remember { Animatable(0f) }
                        var recomposeKey by remember { mutableStateOf(false) }
                        LaunchedEffect(recomposeKey) {}

                        val isAnyMovingIn = memCards.any {
                            it.card.caravanAnimationMark.isMovingIn() ||
                                    it.modifiersCopy().any { mod -> mod.caravanAnimationMark.isMovingIn() }
                        }
                        val isAnyMovingOut = memCards.any { it.card.caravanAnimationMark.isMovingOut() }
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
                                recomposeKey = !recomposeKey
                            }
                        }

                        LaunchedEffect(isAnyMovingOut) {
                            if (isAnyMovingOut) {
                                playCardFlipSound(activity)
                                val isDisappearing = memCards.any { it.card.caravanAnimationMark == Card.AnimationMark.MOVED_OUT }
                                animationOut.snapTo(0f)
                                memCards.forEach {
                                    if (it.card.caravanAnimationMark == Card.AnimationMark.MOVING_OUT) {
                                        it.card.caravanAnimationMark = Card.AnimationMark.MOVING_OUT_WIP
                                    }
                                }
                                delay(animationSpeed.delay * 2)
                                if (!isDisappearing) {
                                    animationOut.animateTo(2f, TweenSpec(animationSpeed.delay.toInt(), animationSpeed.delay.toInt() * 2)) {
                                        recomposeKey = !recomposeKey
                                    }
                                }
                                memCards.removeIf { it.card.caravanAnimationMark.isMovingOut() }
                                recomposeKey = !recomposeKey
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
                                        ((if (isEnemy) (-13).dp else 13.dp) * (modifierIndex + 1)).toPx()
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
                                            placeableHeight * 3 / 7 * memCards.size + placeableHeight * 4 / 7,
                                            state.layoutInfo.viewportSize.height - 1
                                        )
                                    } else {
                                        placeableHeight * 3 / 7 * (memCards.size + 1) + placeableHeight * 4 / 7
                                    }
                                    val offsetWidth = constraints.maxWidth / 2 - placeable.width / 2
                                    val antiOffsetHeight = if (isEnemy)
                                        (layoutFullHeight - placeableHeight * 3 / 7 * index - placeableHeight)
                                    else
                                        (placeableHeight * 3 / 7 * index)
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

                        key(caravansKey, recomposeKey) {
                            memCards.forEachIndexed { index, it ->
                                CardInCaravan(it, index)
                            }
                        }
                    }

                    if (!isEnemy) {
                        if (!caravan.isFull() && (!isInitStage || caravan.cards.isEmpty())) {
                            @Composable
                            fun getBoxModifier() = Modifier
                                .fillParentMaxWidth()
                                .height(20.dp)
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 4.dp)

                            when (canPutSelectedCardOnTop()) {
                                1 -> {
                                    Box(modifier = getBoxModifier()
                                        .background(colorResource(id = R.color.green))
                                        .border(4.dp, colorResource(id = R.color.dark_green))
                                        .clickable {
                                            addSelectedCardOnPosition(caravan.size)
                                        }
                                    ) {}
                                }
                                -1 -> {
                                    Box(
                                        modifier = getBoxModifier()
                                            .background(colorResource(id = R.color.red))
                                            .border(4.dp, colorResource(id = R.color.dark_red))
                                    ) {}
                                }
                                else -> {
                                    Box(
                                        modifier = getBoxModifier()
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
    getSelectedCardInt: () -> Int,
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
    getEnemyCaravan: (Int) -> Caravan,
    caravansKey: Int
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
                    {},
                    { card ->
                        addCardToEnemyCaravan(it, card)
                    },
                    caravansKey
                )
            }

            Box(Modifier.weight(0.25f))
        }

        Row(
            Modifier
                .fillMaxWidth()
                .height(52.dp)) {
            key(caravansKey) {
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
            }
            Column(modifier = Modifier
                .fillMaxHeight()
                .weight(0.25f)) {
                val text = when {
                    isGameOver() -> stringResource(R.string.can_t_act)
                    !isPlayersTurn() -> stringResource(R.string.wait)
                    getIsInitStage() -> stringResource(R.string.init_stage)
                    getSelectedCard() != null -> {
                        stringResource(R.string.discard_card)
                    }
                    getSelectedCaravan() in (0..2) -> {
                        stringResource(R.string.drop_caravan, getSelectedCaravan() + 1)
                    }
                    else -> stringResource(R.string.your_turn)
                }
                val modifier =
                    if (canDiscard() && (getSelectedCard() != null || getSelectedCaravan() in (0..2))) {
                        Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clickable {
                                if (!canDiscard()) return@clickable
                                if (getSelectedCard() != null) {
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
                    },
                    { card -> if (isPlayersTurn()) { addCardToPlayerCaravan(it, card) } },
                    caravansKey
                )
            }
            Box(Modifier.weight(0.25f))
        }
    }
}