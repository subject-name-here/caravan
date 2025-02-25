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
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
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
            if (isBlitz || game.enemy.isSpeedOverriding())
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
        if (!game.canPlayerMove) return

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
        if (!game.canPlayerMove) return

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
        if (!game.canPlayerMove) return

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
        if (game.isPlayerTurn && !game.isOver() && !(game.isInitStage() && card.isModifier())) {
            if (card.isModifier()) {
                if (caravan.cards.getOrNull(position)?.canAddModifier(card) == true) {
                    playCardFlipSound(activity)
                    if (card.isOrdinary() && card.rank == Rank.JOKER) {
                        playJokerSounds(activity)
                    } else if (card.getWildWastelandType() != null) {
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
                if (!isEnemy && !(game.isInitStage() && !caravan.isEmpty())) {
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
                    Modifier
                        .background(getTextBackgroundColor(activity))
                        .padding(8.dp),
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
                    Modifier
                        .fillMaxWidth()
                        .clickableCancel(activity) {
                            goBack()
                        }
                        .padding(8.dp),
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
                            EnemySide(activity, animationSpeed, isPvP, getEnemySymbol, game, fillHalfMaxHeight = true, enemyHandKey)
                            PlayerSide(activity, animationSpeed, isPvP, game, getSelectedCard(), setSelectedCard, getMySymbol, setMySymbol, playerHandKey)
                        }
//                        Caravans(
//                            activity,
//                            animationSpeed,
//                            { game.playerCResources.hand.getOrNull(getSelectedCard()) },
//                            getSelectedCaravan,
//                            setSelectedCaravan,
//                            isMaxHeight = true,
//                            state1Enemy,
//                            state1Player,
//                            state2Enemy,
//                            state2Player,
//                            state3Enemy,
//                            state3Player,
//                            ::addCardToPlayerCaravan,
//                            ::addCardToEnemyCaravan,
//                            { dropCardFromHand() },
//                            dropCaravan,
//                            ::isInitStage,
//                            { game.isPlayerTurn },
//                            { game.canPlayerMove },
//                            ::canDiscard,
//                            { game.isOver() },
//                            { num -> game.playerCaravans[num] },
//                            { num -> game.enemyCaravans[num] },
//                            caravansKey
//                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        EnemySide(activity, animationSpeed, isPvP, getEnemySymbol, game, fillHalfMaxHeight = false, enemyHandKey)
                        Caravans(
                            activity,
                            animationSpeed,
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
                            dropCardFromHand,
                            dropCaravan,
                            ::isInitStage,
                            { game.isPlayerTurn },
                            { game.canPlayerMove },
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
    fillHalfMaxHeight: Boolean,
    enemyHandKey: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (fillHalfMaxHeight) {
                    it.fillMaxHeight(0.5f)
                } else {
                    it.wrapContentHeight()
                }
                it
            }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        RowOfEnemyCards(activity, animationSpeed, game.enemyCResources.hand)
        key(enemyHandKey) {
            Box(Modifier.fillMaxWidth().wrapContentHeight()) {
                ShowDeck(game.enemyCResources, activity, isKnown = !isPvP)
                if (isPvP) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        TextSymbola(
                            getEnemySymbol(),
                            getTextColor(activity),
                            24.sp,
                            Modifier.background(getTextBackgroundColor(activity))
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
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp, start = 4.dp).wrapContentHeight(),
    ) {
        PlayerCards(
            activity,
            animationSpeed,
            game.playerCResources.hand,
            selectedCard,
            selectedCardColor,
            onCardClicked
        )
        key(playerHandKey) {
            Box(Modifier.fillMaxWidth().wrapContentHeight()) {
                ShowDeck(game.playerCResources, activity)
                if (isPvP) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        TextSymbola(
                            getMySymbol(),
                            getTextColor(activity),
                            24.sp,
                            Modifier
                                .background(getTextBackgroundColor(activity))
                                .clickableOk(activity) {
                                    setMySymbol()
                                }
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
            .wrapContentHeight(),
        Alignment.TopStart
    ) {
        var scale by remember { mutableFloatStateOf(1f) }
        val cardHeight = maxHeight.dpToPx().toFloat() - 8.dp.dpToPx()
        val cardWidth = maxWidth.dpToPx().toFloat() / 5f - 8.dp.dpToPx()
        val scaleH = cardHeight / 256f
        val scaleW = cardWidth / 183f
        scale = when {
            scaleH == 0f -> scaleW
            scaleW == 0f -> scaleH
            else -> min(scaleW, scaleH)
        }.coerceAtMost(1f)

        val memCards = remember { mutableObjectListOf<Card>() }
        memCards.addAll(cards - memCards.asMutableList().toSet())
        memCards.removeIf { it.handAnimationMark == Card.AnimationMark.MOVED_OUT }

        val itemVerticalOffsetMovingIn = remember { Animatable(2.5f * enemyMult) }
        val itemVerticalOffsetMovingOut = remember { Animatable(0f) }

        var recomposeKey by remember { mutableStateOf(false) }
        val isAnyMovingIn = memCards.any { it.handAnimationMark.isMovingIn() }
        val isAnyMovingOut = memCards.any { it.handAnimationMark.isMovingOut() }

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
                val isDropping = memCards.any { it.handAnimationMark.isAlt() }
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

        val state = rememberLazyListState()
        LazyRow(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .scrollbar(
                    state,
                    knobColor = getKnobColor(activity),
                    trackColor = getTrackColor(activity),
                    horizontal = true
                ),
            state = state,
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            item { key (recomposeKey) {
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
                    val index = memCards.asMutableList().indexOf(it)
                    if (index == -1) return@forEach

                    val modifier = Modifier
                        .scale(scale)
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            val scaledWidth = placeable.width * scale
                            val scaledHeight = placeable.height * scale
                            val offsetHeight = scaledHeight * (inValue + outValue)
                            layout(scaledWidth.toInt(), scaledHeight.toInt()) {
                                placeable.place(0, offsetHeight.toInt())
                            }
                        }

                    if (isEnemy) {
                        ShowCardBack(activity, it, modifier)
                    } else {
                        if (it.rank == Rank.JOKER && it.handAnimationMark == Card.AnimationMark.MOVING_IN) {
                            LaunchedEffect(Unit) {
                                playJokerReceivedSounds(activity)
                            }
                        }
                        ShowCard(
                            activity,
                            it,
                            modifier
                                .clickable {
                                    if (!itemVerticalOffsetMovingIn.isRunning && !itemVerticalOffsetMovingIn.isRunning) {
                                        onClick(index)
                                    }
                                }
                                .border(
                                    width = if (index == (selectedCard ?: -1)) 3.dp else (-1).dp,
                                    color = selectedCardColor
                                )
                                .padding(4.dp),
                        )
                    }
                }
            } }
        }
    }
}

private fun canPutCard(
    card: Card,
    caravan: Caravan,
    isPlayerCaravan: Boolean,
    index: Int
): Boolean {
    val cardOn = caravan.cards.getOrNull(index) ?: return false
    return if (card.isModifier()) {
        cardOn.canAddModifier(card)
    } else if (isPlayerCaravan && index == caravan.size - 1) {
        caravan.canPutCardOnTop(card)
    } else {
        false
    }
}

@Composable
fun RowScope.CaravanOnField(
    activity: MainActivity,
    animationSpeed: AnimationSpeed,
    caravan: Caravan,
    isPlayerTurn: Boolean,
    isEnemyCaravan: Boolean,
    isInitStage: Boolean,
    state: LazyListState,
    selectCaravan: () -> Unit = {},
    canPutSelectedCardOn: (Int) -> Boolean,
    addSelectedCardOnPosition: (Int) -> Unit,
    caravansKey: Int,
) {
    var width by remember { mutableIntStateOf(0) }
    LaunchedEffect(width) {
        if (width == 0) {
            width = state.layoutInfo.viewportSize.width
        }
    }
    val modifierOffset = 14.dp * (if (isEnemyCaravan) -1 else 1)
    Column(
        Modifier
            .wrapContentHeight()
            .weight(0.25f)
    ) {
        if (!isEnemyCaravan && !isInitStage && !caravan.isEmpty()) {
            TextFallout(
                stringResource(R.string.discard),
                getTextColor(activity),
                getTextStrokeColor(activity),
                14.sp,
                Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .padding(start = 2.dp, end = 2.dp, bottom = 2.dp, top = 0.dp)
                    .clickable {
                        selectCaravan()
                    }
                    .background(
                        getTextBackgroundColor(activity).let { color ->
                            Color(color.red, color.green, color.blue, 0.75f)
                        }
                    )
                    .padding(2.dp)
            )
        }
        if (!isEnemyCaravan && caravan.isEmpty()) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .padding(horizontal = 4.dp)
                .background(colorResource(id = R.color.green))
                .border(4.dp, colorResource(id = R.color.dark_green))
                .clickable {
                    addSelectedCardOnPosition(-1)
                }
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
                    val enemyTurnMult = if (isPlayerTurn) 1 else -1

                    val memCards = remember { mutableObjectListOf<CardWithModifier>() }
                    memCards.addAll(caravan.cards - memCards.asMutableList().toSet())
                    memCards.removeIf { it.card.caravanAnimationMark == Card.AnimationMark.MOVED_OUT }

                    val animationIn = remember { Animatable(3f * enemyTurnMult) }
                    val animationOut = remember { Animatable(0f) }
                    var recomposeKey by remember { mutableStateOf(false) }
                    LaunchedEffect(recomposeKey) {}

                    val isAnyMovingIn = memCards.any { it.card.caravanAnimationMark.isMovingIn() ||
                                it.modifiersCopy().any { mod -> mod.caravanAnimationMark.isMovingIn() }
                    }
                    val isAnyMovingOut = memCards.any { it.card.caravanAnimationMark.isMovingOut() }
                    LaunchedEffect(isAnyMovingIn) {
                        if (isAnyMovingIn) {
                            playCardFlipSound(activity)
                            animationIn.snapTo(3f * enemyTurnMult)
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
                            animationOut.snapTo(0f)
                            memCards.forEach {
                                if (it.card.caravanAnimationMark == Card.AnimationMark.MOVING_OUT) {
                                    it.card.caravanAnimationMark = Card.AnimationMark.MOVING_OUT_WIP
                                }
                            }
                            animationOut.animateTo(2f, TweenSpec(animationSpeed.delay.toInt(), animationSpeed.delay.toInt())) {
                                recomposeKey = !recomposeKey
                            }
                            memCards.forEach {
                                if (it.card.caravanAnimationMark == Card.AnimationMark.MOVING_OUT_WIP) {
                                    it.card.caravanAnimationMark = Card.AnimationMark.MOVED_OUT
                                }
                                it.modifiersCopy().forEach { mod ->
                                    if (mod.caravanAnimationMark == Card.AnimationMark.MOVING_OUT_WIP) {
                                        mod.caravanAnimationMark = Card.AnimationMark.MOVED_OUT
                                    }
                                }
                            }
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
                                3f * enemyTurnMult
                            }
                            Card.AnimationMark.MOVING_IN_WIP -> {
                                animationIn.value * enemyTurnMult
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
                                val placeableHeight = placeable.height.toInt()
                                val placeableWidth = placeable.width.toInt()
                                val modifierOffset = (modifierOffset * (modifierIndex + 1)).toPx()
                                layout(placeableWidth.coerceAtLeast(0), 0) {
                                    placeable.place(
                                        (modifierOffset + placeableWidth * outValue).toInt(),
                                        (placeableHeight * inValue).toInt()
                                    )
                                }
                            })
                        {
                            ShowCard(activity, modifier, Modifier)
                        }
                    }

                    @Composable
                    fun CardInCaravan(
                        it: CardWithModifier,
                        index: Int
                    ) {
                        val inValue = when (it.card.caravanAnimationMark) {
                            Card.AnimationMark.MOVING_IN -> {
                                3f * enemyTurnMult
                            }
                            Card.AnimationMark.MOVING_IN_WIP -> {
                                animationIn.value * enemyTurnMult
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

                        val coverColor = if (canPutSelectedCardOn(index)) {
                            Color(0f, 1f, 0f, 0.33f)
                        } else {
                            Color.Transparent
                        }
                        val modifier = Modifier
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)
                                val placeableHeight = placeable.height.toInt()
                                val placeableWidth = placeable.width.toInt()
                                val cardOffsetWidth = constraints.maxWidth / 2 - placeable.width / 2
                                val cardOffsetHeight = placeableHeight * 3 / 7 * index

                                val layoutSize = state.layoutInfo.viewportSize.height - 2
                                val caravanHeight = placeableHeight * 3 / 7 * (caravan.size - 1) + placeableHeight
                                val finalCaravanHeight = if (isEnemyCaravan) {
                                    max(caravanHeight, layoutSize)
                                } else {
                                    caravanHeight
                                }

                                val finalHeightOffset = if (isEnemyCaravan) {
                                    finalCaravanHeight - cardOffsetHeight - placeableHeight
                                } else {
                                    cardOffsetHeight
                                }

                                layout(constraints.maxWidth, finalCaravanHeight) {
                                    placeable.place(
                                        cardOffsetWidth + (placeableWidth * outValue).toInt(),
                                        finalHeightOffset + (placeableHeight * inValue).toInt()
                                    )
                                }
                            }
                            .drawWithContent {
                                drawContent()
                                drawRect(coverColor)
                            }


                        Box(modifier = modifier) {
                            ShowCard(activity, it.card,
                                Modifier.clickable {
                                    addSelectedCardOnPosition(caravan.cards.indexOf(it))
                                }
                            )

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
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ShowDeck(cResources: CResources, activity: MainActivity, isKnown: Boolean = true) {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFallout(
            cResources.deckSize.toString(),
            getTextColor(activity),
            getTextStrokeColor(activity),
            16.sp,
            Modifier
                .wrapContentSize()
                .background(getBackgroundColor(activity))
        )
        if (isKnown) {
            val (back, isAlt) = cResources.getDeckBack() ?: (null to false)
            if (back != null) {
                BoxWithConstraints(
                    Modifier.fillMaxWidth().wrapContentHeight().padding(bottom = 24.pxToDp()),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val cardHeight = maxHeight.dpToPx().toFloat()
                    val cardWidth = maxWidth.dpToPx().toFloat()
                    val scaleH = cardHeight / 256f
                    val scaleW = cardWidth / 183f
                    val scale = min(scaleW, scaleH).coerceAtMost(1f)
                    ShowCardBack(
                        activity,
                        Card(Rank.ACE, Suit.HEARTS, back, isAlt),
                        Modifier.scale(scale)
                    )
                }
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
            Modifier.fillMaxSize(),
            textAlignment = TextAlign.End,
            boxAlignment = Alignment.CenterEnd
        )
    }
}

@Composable
fun ColumnScope.Caravans(
    activity: MainActivity,
    animationSpeed: AnimationSpeed,
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
    canPlayerMove: () -> Boolean,
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
            .weight(1f),
    ) {
        val enemyStates = listOf(state1Enemy, state2Enemy, state3Enemy)
        val playerStates = listOf(state1Player, state2Player, state3Player)
        Row(
            Modifier
                .fillMaxWidth()
                .weight(0.5f)) {
            repeat(3) {
                val caravan = getEnemyCaravan(it)
                CaravanOnField(
                    activity,
                    animationSpeed,
                    caravan,
                    isPlayersTurn(),
                    isEnemyCaravan = true,
                    isInitStage = getIsInitStage(),
                    enemyStates[it],
                    { -1 },
                    { index ->
                        getSelectedCard()?.let { canPutCard(it, caravan, false, index) } == true
                    },
                    { card -> addCardToEnemyCaravan(it, card) },
                    caravansKey
                )
            }
        }

        key(caravansKey) {
            Column(Modifier.wrapContentHeight()) {
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
                val modifier = Modifier.fillMaxWidth(0.66f)
                        .let {
                            if (canDiscard() && (getSelectedCard() != null || getSelectedCaravan() in (0..2))) {
                                it
                                    .clickable {
                                        if (!canDiscard()) return@clickable
                                        if (getSelectedCard() != null) {
                                            dropCardFromHand()
                                        } else if (getSelectedCaravan() in (0..2)) {
                                            dropSelectedCaravan()
                                        }
                                    }
                                    .background(getTextBackgroundColor(activity).let {
                                        Color(it.red, it.green, it.blue, 0.75f)
                                    })
                            } else {
                                it.background(getGrayTransparent(activity))
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)

                Box(Modifier.fillMaxWidth().wrapContentHeight(), contentAlignment = Alignment.Center) {
                    TextFallout(
                        text,
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        modifier,
                    )
                }

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

        Row(
            Modifier
                .fillMaxWidth()
                .weight(0.5f)) {
            repeat(3) {
                val caravan = getPlayerCaravan(it)
                CaravanOnField(
                    activity,
                    animationSpeed,
                    caravan,
                    isPlayersTurn(),
                    isEnemyCaravan = false,
                    isInitStage = getIsInitStage(),
                    playerStates[it],
                    {
                        setSelectedCaravan(
                            if (getSelectedCaravan() == it || getPlayerCaravan(it).getValue() == 0) {
                                -1
                            } else {
                                it
                            }
                        )
                    },
                    canPutSelectedCardOn@ { index ->
                        val selectedCard = getSelectedCard() ?: return@canPutSelectedCardOn false
                        canPlayerMove() && !getIsInitStage() && canPutCard(selectedCard, caravan, true, index)
                    },
                    { card -> addCardToPlayerCaravan(it, card) },
                    caravansKey
                )
            }
        }
    }
}