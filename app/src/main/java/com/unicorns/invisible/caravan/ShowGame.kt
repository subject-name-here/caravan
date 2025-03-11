package com.unicorns.invisible.caravan

import android.annotation.SuppressLint
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
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

    val scope = rememberCoroutineScope()

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
        selectedCaravan = if (index == selectedCaravan || index !in game.playerCaravans.indices) {
            playCloseSound(activity)
            -1
        } else {
            playSelectSound(activity)
            index
        }
        selectedCard = -1
    }

    fun resetSelected() {
        selectedCaravan = -1
        selectedCard = -1
    }

    fun dropCardFromHand() {
        if (!game.canPlayerMove) return

        val selectedCardNN = selectedCard
        if (selectedCardNN !in game.playerCResources.hand.indices) return
        game.canPlayerMove = false
        resetSelected()
        scope.launch {
            playCardFlipSound(activity)
            game.playerCResources.dropCardFromHand(selectedCardNN, animationSpeed.delay)
            playVatsReady(activity)
            onMove(null)
            game.afterPlayerMove(animationSpeed)
            game.canPlayerMove = true
        }
    }

    fun dropCaravan() {
        if (!game.canPlayerMove) return

        val selectedCaravanNN = selectedCaravan
        if (selectedCaravanNN !in game.playerCaravans.indices) return
        game.canPlayerMove = false
        resetSelected()
        scope.launch {
            game.playerCaravans[selectedCaravanNN].dropCaravan()
            playVatsReady(activity)
            activity.processChallengesMove(Challenge.Move(moveCode = 1), game)
            onMove(null)
            game.afterPlayerMove(animationSpeed)
            game.canPlayerMove = true
        }
    }

    fun addCardToCaravan(caravan: Caravan, position: Int, isEnemy: Boolean) {
        if (!game.canPlayerMove) return

        val cardIndex = selectedCard
        val card = game.playerCResources.hand.getOrNull(cardIndex) ?: return
        if (game.isPlayerTurn && !game.isOver() && !(game.isInitStage() && card.isModifier())) {
            if (card.isModifier()) {
                if (caravan.cards.getOrNull(position)?.canAddModifier(card) == true) {
                    game.canPlayerMove = false
                    resetSelected()
                    scope.launch {
                        playCardFlipSound(activity)
                        val removedCard = game.playerCResources.removeFromHand(cardIndex, animationSpeed.delay)

                        if (card.getWildWastelandType() != null) {
                            playWWSound(activity)
                        } else if (card.isNuclear()) {
                            playNukeBlownSound(activity)
                        }
                        caravan.cards[position].addModifier(removedCard)

                        activity.processChallengesMove(Challenge.Move(
                            moveCode = 4,
                            handCard = card
                        ), game)
                        onMove(card)

                        game.afterPlayerMove(animationSpeed)
                        game.canPlayerMove = true
                    }
                }
            } else {
                if (!isEnemy && !(game.isInitStage() && !caravan.isEmpty())) {
                    if (caravan.canPutCardOnTop(card)) {
                        game.canPlayerMove = false
                        resetSelected()
                        scope.launch {
                            playCardFlipSound(activity)
                            val removedCard = game.playerCResources.removeFromHand(cardIndex, animationSpeed.delay)
                            caravan.putCardOnTop(removedCard)

                            activity.processChallengesMove(Challenge.Move(
                                moveCode = 3,
                                handCard = card
                            ), game)
                            onMove(card)

                            game.afterPlayerMove(animationSpeed)
                            game.canPlayerMove = true
                        }
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
) {
    val state1Enemy = rememberLazyListState()
    val state1Player = rememberLazyListState()
    val state2Enemy = rememberLazyListState()
    val state2Player = rememberLazyListState()
    val state3Enemy = rememberLazyListState()
    val state3Player = rememberLazyListState()

    // TODO: scroll to the place of change
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
        return !(game.isOver() || !game.canPlayerMove || game.isInitStage())
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
                Box(Modifier
                    .fillMaxSize()
                    .getTableBackground()) {}
            }
            BoxWithConstraints(Modifier.padding(innerPadding)) {
                if (maxWidth > maxHeight) {
                    Row(Modifier.fillMaxSize()) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        ) {
                            EnemySide(activity, animationSpeed, isPvP, getEnemySymbol, game, fillHalfMaxHeight = true)
                            PlayerSide(activity, animationSpeed, isPvP, game, getSelectedCard(), setSelectedCard, getMySymbol, setMySymbol)
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
                        EnemySide(
                            activity,
                            animationSpeed,
                            isPvP,
                            getEnemySymbol,
                            game,
                            fillHalfMaxHeight = false
                        )
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
                            { num -> game.enemyCaravans[num] }
                        )
                        PlayerSide(
                            activity,
                            animationSpeed,
                            isPvP,
                            game,
                            getSelectedCard(),
                            setSelectedCard,
                            getMySymbol,
                            setMySymbol
                        )
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
        Hand(activity, animationSpeed, true, game.enemyCResources, -1, Color.Transparent, {})
        Box(Modifier
            .fillMaxWidth()
            .wrapContentHeight()) {
            key (game.enemyCResources.recomposeResources) {
                ShowDeck(game.enemyCResources, activity, isKnown = !isPvP)
            }
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

@Composable
fun PlayerSide(
    activity: MainActivity,
    animationSpeed: AnimationSpeed,
    isPvP: Boolean,
    game: Game,
    selectedCard: Int,
    onCardClicked: (Int) -> Unit,
    getMySymbol: () -> String, setMySymbol: () -> Unit,
) {
    if (game.playerCResources.deckSize == 0) { // TODO: make size mutableStateOf
        LaunchedEffect(Unit) { playNoCardAlarm(activity) }
    }

    val selectedCardColor = getGameSelectionColor(activity)
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp, start = 4.dp)
            .wrapContentHeight(),
    ) {
        Hand(
            activity,
            animationSpeed,
            false,
            game.playerCResources,
            selectedCard,
            selectedCardColor,
            onCardClicked
        )
        Box(Modifier.fillMaxWidth().wrapContentHeight()) {
            key (game.playerCResources.recomposeResources) {
                ShowDeck(game.playerCResources, activity)
            }
            if (isPvP) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    contentAlignment = Alignment.TopCenter
                ) {
                    // TODO: test this.
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

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun Hand(
    activity: MainActivity,
    animationSpeed: AnimationSpeed,
    isEnemy: Boolean,
    cResources: CResources,
    selectedCard: Int?, selectedCardColor: Color, onClick: (Int) -> Unit,
) {
    val enemyMult = if (isEnemy) -1f else 1f
    val cards = cResources.hand

    BoxWithConstraints(
        Modifier
            .fillMaxWidth(0.8f)
            .wrapContentHeight(),
        Alignment.TopStart
    ) {
        var scale by remember { mutableFloatStateOf(1f) }
        val cardHeight = maxHeight.dpToPx().toFloat()
        val cardWidth = maxWidth.dpToPx().toFloat() / 5f - 8.dp.dpToPx()
        val scaleH = cardHeight / 256f
        val scaleW = cardWidth / 183f
        scale = when {
            scaleH == 0f -> scaleW
            scaleW == 0f -> scaleH
            else -> min(scaleW, scaleH)
        }.coerceAtMost(1f)

        cards.forEachIndexed { index, it ->
            val targetValue = when (it.handAnimationMark) {
                Card.AnimationMark.STABLE -> 0f
                Card.AnimationMark.MOVING_OUT -> -1.5f * enemyMult
                Card.AnimationMark.MOVING_OUT_ALT -> 1.5f * enemyMult
                Card.AnimationMark.MOVED_OUT -> 1.5f * enemyMult
                Card.AnimationMark.NEW -> 3f * enemyMult
            }
            val offsetMult by animateFloatAsState(
                targetValue,
                tween(animationSpeed.delay.toInt())
            )

            LaunchedEffect(it.handAnimationMark) {
                if (it.handAnimationMark == Card.AnimationMark.NEW) {
                    if (it.rank == Rank.JOKER) {
                        playJokerReceivedSounds(activity)
                    }
                    it.handAnimationMark = Card.AnimationMark.STABLE
                    playCardFlipSound(activity)
                } else if (it.handAnimationMark == Card.AnimationMark.MOVING_OUT) {
                    if (it.rank == Rank.JOKER) {
                        playJokerSounds(activity)
                    }
                }
            }

            val widthOffset = index * maxWidth.dpToPx() / cards.size
            val modifier = Modifier.offset { IntOffset(widthOffset.toInt(), (256f * scale * offsetMult).toInt()) }

            if (isEnemy) {
                ShowCardBack(activity, it, modifier.padding(4.dp), scale)
            } else {
                ShowCard(
                    activity,
                    it,
                    modifier
                        .border(
                            width = if (index == (selectedCard ?: -1)) 3.dp else (-1).dp,
                            color = selectedCardColor
                        )
                        .padding(4.dp)
                        .clickable { if (offsetMult == 0f) { onClick(index) } },
                    scale
                )
            }
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
    addSelectedCardOnPosition: (Int) -> Unit
) {
    var width by remember { mutableIntStateOf(0) }
    LaunchedEffect(width) {
        if (width == 0) {
            width = state.layoutInfo.viewportSize.width
        }
    }
    val modifierOffset = 14.dp * (if (isEnemyCaravan) -1 else 1)
    Column(Modifier.wrapContentHeight().weight(0.25f)) { key (caravan.recomposeResources) {
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
                .wrapContentWidth(align = Alignment.CenterHorizontally)
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

                    @Composable
                    fun ModifierOnCardInCaravan(
                        modifier: Card,
                        modifierIndex: Int,
                        it: CardWithModifier
                    ) {
                        val offsetHeightMult by animateFloatAsState(when (modifier.caravanAnimationMark) {
                            Card.AnimationMark.STABLE -> 0f
                            Card.AnimationMark.NEW -> 3f * enemyTurnMult
                            Card.AnimationMark.MOVING_OUT -> 0f
                            Card.AnimationMark.MOVING_OUT_ALT -> 0f
                            Card.AnimationMark.MOVED_OUT -> 0f
                        }, animationSpec = tween(animationSpeed.delay.toInt()))

                        val offsetWidthMult by animateFloatAsState(when (it.card.caravanAnimationMark) {
                            Card.AnimationMark.STABLE -> 0f
                            Card.AnimationMark.NEW -> 0f
                            Card.AnimationMark.MOVING_OUT -> 2f
                            Card.AnimationMark.MOVING_OUT_ALT -> 2f
                            Card.AnimationMark.MOVED_OUT -> 2f
                        }, animationSpec = tween(animationSpeed.delay.toInt()))

                        LaunchedEffect(it.card.caravanAnimationMark) {
                            if (it.card.caravanAnimationMark == Card.AnimationMark.NEW) {
                                it.card.caravanAnimationMark = Card.AnimationMark.STABLE
                                playCardFlipSound(activity)
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
                                        (modifierOffset + placeableWidth * offsetWidthMult).toInt(),
                                        (placeableHeight * offsetHeightMult).toInt()
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
                        val offsetHeightMult by animateFloatAsState(when (it.card.caravanAnimationMark) {
                            Card.AnimationMark.STABLE -> 0f
                            Card.AnimationMark.NEW -> 3f * enemyTurnMult
                            Card.AnimationMark.MOVING_OUT -> 0f
                            Card.AnimationMark.MOVING_OUT_ALT -> 0f
                            Card.AnimationMark.MOVED_OUT -> 0f
                        }, animationSpec = tween(animationSpeed.delay.toInt()))

                        val offsetWidthMult by animateFloatAsState(when (it.card.caravanAnimationMark) {
                            Card.AnimationMark.STABLE -> 0f
                            Card.AnimationMark.NEW -> 0f
                            Card.AnimationMark.MOVING_OUT -> 2f
                            Card.AnimationMark.MOVING_OUT_ALT -> 2f
                            Card.AnimationMark.MOVED_OUT -> 2f
                        }, animationSpec = tween(animationSpeed.delay.toInt()))

                        LaunchedEffect(it.card.caravanAnimationMark) {
                            if (it.card.caravanAnimationMark == Card.AnimationMark.NEW) {
                                it.card.caravanAnimationMark = Card.AnimationMark.STABLE
                                playCardFlipSound(activity)
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
                                val caravanHeight =
                                    placeableHeight * 3 / 7 * (caravan.size - 1) + placeableHeight
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
                                        cardOffsetWidth + (placeableWidth * offsetWidthMult).toInt(),
                                        finalHeightOffset + (placeableHeight * offsetHeightMult).toInt()
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

                    caravan.cards.forEachIndexed { index, it ->
                        CardInCaravan(it, index)
                    }
                }
            }
        }
    } }
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
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = 24.pxToDp()),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val cardHeight = maxHeight.dpToPx().toFloat()
                    val cardWidth = maxWidth.dpToPx().toFloat()
                    val scaleH = cardHeight / 256f
                    val scaleW = cardWidth / 183f
                    val scale = min(scaleW, scaleH).coerceAtMost(0.75f)
                    ShowCardBack(
                        activity,
                        Card(Rank.ACE, Suit.HEARTS, back, isAlt),
                        Modifier,
                        scale = scale
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
                )
            }
        }

        Column(Modifier.wrapContentHeight()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(24.dp)) {
                repeat(3) {
                    val caravan = getEnemyCaravan(it)
                    val opposingValue = getPlayerCaravan(it).getValue()
                    key (caravan.recomposeResources) {
                        Score(activity, it, caravan, opposingValue)
                    }
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
            val modifier = Modifier
                .fillMaxWidth(0.66f)
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
                                Color(it.red, it.green, it.blue, 0.81f)
                            })
                    } else {
                        it.background(getGrayTransparent(activity))
                    }
                }
                .padding(horizontal = 8.dp, vertical = 4.dp)

            Box(Modifier
                .fillMaxWidth()
                .wrapContentHeight(), contentAlignment = Alignment.Center) {
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
                    key (caravan.recomposeResources) {
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
                )
            }
        }
    }
}