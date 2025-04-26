package com.unicorns.invisible.caravan

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.back_to_menu
import caravan.composeapp.generated.resources.can_t_act
import caravan.composeapp.generated.resources.discard
import caravan.composeapp.generated.resources.discard_card
import caravan.composeapp.generated.resources.drop_caravan
import caravan.composeapp.generated.resources.help
import caravan.composeapp.generated.resources.init_stage
import caravan.composeapp.generated.resources.wait
import caravan.composeapp.generated.resources.your_turn
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.color.Colors
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.model.enemy.EnemyPlayer
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardBase
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardWithModifier
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
import com.unicorns.invisible.caravan.utils.playNoBeep
import com.unicorns.invisible.caravan.utils.playNoCardAlarm
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playVatsReady
import com.unicorns.invisible.caravan.utils.pxToDp
import io.github.oikvpqya.compose.fastscroller.ScrollbarStyle
import io.github.oikvpqya.compose.fastscroller.ThumbStyle
import io.github.oikvpqya.compose.fastscroller.TrackStyle
import io.github.oikvpqya.compose.fastscroller.VerticalScrollbar
import io.github.oikvpqya.compose.fastscroller.rememberScrollbarAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import kotlin.math.max
import kotlin.math.min


@Composable
fun ShowGame(
    game: Game,
    isBlitz: Boolean = false,
    isPvP: Boolean = false,
    onMove: (Int, Int, Int, Int) -> Unit = { _, _, _, _ -> },
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
                saveGlobal.animationSpeed
        )
    }

    fun onCardClicked(index: Int) {
        if (game.isOver()) {
            return
        }
        selectedCard = if (index == selectedCard || index !in game.playerCResources.hand.indices) {
            playCloseSound()
            -1
        } else {
            playSelectSound()
            index
        }
        selectedCaravan = -1
    }

    fun onCaravanClicked(index: Int) {
        if (game.isOver()) {
            return
        }
        selectedCaravan = if (index == selectedCaravan || index !in game.playerCaravans.indices) {
            playCloseSound()
            -1
        } else {
            playSelectSound()
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
            playCardFlipSound()
            game.playerCResources.dropCardFromHand(selectedCardNN, animationSpeed)
            playVatsReady()
            onMove(2, selectedCardNN, 0, 0)
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
            game.playerCaravans[selectedCaravanNN].dropCaravan(animationSpeed)
            playVatsReady()
            processChallengesMove(Challenge.Move(moveCode = 1), game)
            onMove(1, 0, selectedCaravanNN, 0)
            game.afterPlayerMove(animationSpeed)
            game.canPlayerMove = true
        }
    }

    fun addCardToCaravan(caravan: Caravan, caravanNum: Int, position: Int, isEnemy: Boolean) {
        if (!game.canPlayerMove) return

        val cardIndex = selectedCard
        val card = game.playerCResources.hand.getOrNull(cardIndex) ?: return
        if (game.isPlayerTurn && !game.isOver() && !(game.isInitStage() && card is CardModifier)) {
            if (card is CardModifier) {
                if (caravan.cards.getOrNull(position)?.canAddModifier(card) == true) {
                    game.canPlayerMove = false
                    resetSelected()
                    scope.launch {
                        playCardFlipSound()
                        val removedCard = game.playerCResources.removeFromHand(cardIndex, animationSpeed) as CardModifier
                        caravan.cards[position].addModifier(removedCard, animationSpeed)
                        caravan.recomposeResources++

                        processChallengesMove(Challenge.Move(
                            moveCode = 4,
                            handCard = card
                        ), game)
                        onMove(3, cardIndex, caravanNum, position)

                        game.afterPlayerMove(animationSpeed)
                        game.canPlayerMove = true
                    }
                }
            } else if (card is CardBase) {
                if (!isEnemy && !(game.isInitStage() && !caravan.isEmpty())) {
                    if (caravan.canPutCardOnTop(card)) {
                        game.canPlayerMove = false
                        resetSelected()
                        scope.launch {
                            playCardFlipSound()
                            val removedCard = game.playerCResources.removeFromHand(cardIndex, animationSpeed) as CardBase
                            caravan.putCardOnTop(removedCard, animationSpeed)

                            processChallengesMove(Challenge.Move(
                                moveCode = 3,
                                handCard = card
                            ), game)
                            onMove(3, cardIndex, caravanNum, position)

                            game.afterPlayerMove(animationSpeed)
                            game.canPlayerMove = true
                        }
                    }
                }
            }
        }
    }

    ShowGameRaw(
        isPvP,
        isBlitz,
        game,
        goBack,
        animationSpeed,
        { "" }, { "" }, {},
        { selectedCard }, ::onCardClicked,
        { selectedCaravan }, ::onCaravanClicked,
        ::addCardToCaravan,
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
                    playNoBeep()
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
                    getTextColor(),
                    getTextStrokeColor(),
                    16.sp,
                    Modifier
                        .background(getTextBackgroundColor())
                        .padding(8.dp),
                )
            }
        }
    }
}

@Composable
fun ShowGameRaw(
    isPvP: Boolean,
    isBlitz: Boolean,
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

    fun addCardToEnemyCaravan(caravanNum: Int, position: Int) {
        addCardToCaravan(game.enemyCaravans[caravanNum], caravanNum + 3, position, true)
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
                    .background(getBackgroundColor())
            ) {
                TextFallout(
                    stringResource(Res.string.back_to_menu),
                    getTextColor(),
                    getTextStrokeColor(),
                    16.sp,
                    Modifier
                        .fillMaxWidth()
                        .clickableCancel {
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
            BoxWithConstraints(Modifier.padding(innerPadding)) field@ {
                var handCardsScale by remember { mutableFloatStateOf(1f) }
                val cardWidth = maxWidth.dpToPx().toFloat() / 5f - 8.dp.dpToPx()
                handCardsScale = (cardWidth / 183f).coerceAtMost(1f)
                val handCardHeight = 256f * handCardsScale

                if (maxWidth > maxHeight) {
                    Row(Modifier.fillMaxSize()) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(0.5f).fillMaxHeight()
                        ) {
                            EnemySide(animationSpeed, isPvP, getEnemySymbol, game, fillHalfMaxHeight = true, handCardHeight)
                            PlayerSide(animationSpeed, isPvP, game, getSelectedCard(), setSelectedCard, getMySymbol, setMySymbol, fillMaxHeight = true, handCardHeight)
                        }
                        Caravans(
                            animationSpeed,
                            { game.playerCResources.hand.getOrNull(getSelectedCard()) },
                            getSelectedCaravan,
                            setSelectedCaravan,
                            height = this@field.maxHeight.dpToPx(),
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
                            { game.recomposeResources }
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        EnemySide(
                            animationSpeed,
                            isPvP,
                            getEnemySymbol,
                            game,
                            fillHalfMaxHeight = false,
                            handCardHeight
                        )
                        Caravans(
                            animationSpeed,
                            { game.playerCResources.hand.getOrNull(getSelectedCard()) },
                            getSelectedCaravan,
                            setSelectedCaravan,
                            this@field.maxHeight.dpToPx() - 2 * handCardHeight,
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
                            { game.recomposeResources }
                        )
                        PlayerSide(
                            animationSpeed,
                            isPvP,
                            game,
                            getSelectedCard(),
                            setSelectedCard,
                            getMySymbol,
                            setMySymbol,
                            fillMaxHeight = true,
                            handCardHeight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnemySide(
    animationSpeed: AnimationSpeed,
    isPvP: Boolean,
    getEnemySymbol: () -> String,
    game: Game,
    fillHalfMaxHeight: Boolean,
    height: Float,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (fillHalfMaxHeight) {
                    it.fillMaxHeight(0.5f)
                } else {
                    it.height(height.toInt().pxToDp())
                }
            },
        verticalAlignment = Alignment.Top
    ) {
        Hand(animationSpeed, true, game.enemyCResources, -1, Color.Transparent) {}
        Box(Modifier.fillMaxSize()) {
            key (game.enemyCResources.recomposeResources) {
                ShowDeck(game.enemyCResources, if (isPvP) (game.enemy as EnemyPlayer).deckSize ?: 0 else game.enemyCResources.deckSize, isKnown = !isPvP)
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
                        getTextColor(),
                        24.sp,
                        Modifier.background(getTextBackgroundColor())
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerSide(
    animationSpeed: AnimationSpeed,
    isPvP: Boolean,
    game: Game,
    selectedCard: Int,
    onCardClicked: (Int) -> Unit,
    getMySymbol: () -> String, setMySymbol: () -> Unit,
    fillMaxHeight: Boolean,
    height: Float,
) {
    if (game.playerCResources.deckSize == 0) { // TODO: make size mutableStateOf
        LaunchedEffect(Unit) { playNoCardAlarm() }
    }

    val selectedCardColor = getGameSelectionColor()
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .let {
                if (fillMaxHeight) {
                    it.fillMaxHeight()
                } else {
                    it.height(height.toInt().pxToDp())
                }
            },
    ) {
        Hand(
            animationSpeed,
            false,
            game.playerCResources,
            selectedCard,
            selectedCardColor,
            onCardClicked
        )
        Box(Modifier.fillMaxSize()) {
            key (game.playerCResources.recomposeResources) {
                ShowDeck(game.playerCResources, game.playerCResources.deckSize)
            }
            if (isPvP) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    contentAlignment = Alignment.TopCenter
                ) {
                    TextSymbola(
                        getMySymbol(),
                        getTextColor(),
                        24.sp,
                        Modifier
                            .background(getTextBackgroundColor())
                            .clickableOk {
                                setMySymbol()
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun Hand(
    animationSpeed: AnimationSpeed,
    isEnemy: Boolean,
    cResources: CResources,
    selectedCard: Int?, selectedCardColor: Color, onClick: (Int) -> Unit,
) {
    val enemyMult = if (isEnemy) -1f else 1f
    val cards = cResources.hand

    LaunchedEffect(cResources.recomposeResources) {
        repeat(cards.count { it.handAnimationMark == Card.AnimationMark.NEW }) {
            playCardFlipSound()
        }
    }

    BoxWithConstraints(
        Modifier.fillMaxWidth(0.83f).fillMaxHeight(),
        Alignment.CenterStart
    ) {
        val scale = (maxHeight - 4.dp * 2).dpToPx() / 256f
        cards.forEachIndexed { index, it ->
            LaunchedEffect(it.handAnimationMark) {
                if (it.handAnimationMark == Card.AnimationMark.NEW) {
                    if (it is CardJoker && !isEnemy) {
                        playJokerReceivedSounds()
                    }
                    it.handAnimationMark = Card.AnimationMark.STABLE
                }
            }

            var prevState by rememberScoped { mutableStateOf(it.handAnimationMark) }
            val needsSnap = prevState == it.handAnimationMark
            val offsetMult by animateFloatAsState(
                when (it.handAnimationMark) {
                    Card.AnimationMark.STABLE -> 0f
                    Card.AnimationMark.MOVING_OUT -> -1f * enemyMult
                    Card.AnimationMark.MOVING_OUT_ALT -> 1f * enemyMult
                    Card.AnimationMark.MOVED_OUT -> 0f
                    Card.AnimationMark.NEW -> 3f * enemyMult
                },
                if (it.handAnimationMark == Card.AnimationMark.MOVED_OUT || needsSnap) {
                    SnapSpec()
                } else {
                    tween(animationSpeed.delay.toInt())
                },
                label = "$index $it"
            ) { newOffset ->
                prevState = it.handAnimationMark
            }

            val widthOffset = index * (maxWidth.dpToPx() - 183 * scale) / max(4, cards.size - 1)
            val modifier = Modifier.offset { IntOffset(widthOffset.toInt(), (256f * scale * offsetMult).toInt()) }

            if (isEnemy) {
                ShowCardBack(it, modifier.padding(4.dp), scale)
            } else {
                Box(modifier
                        .border(
                            width = if (index == (selectedCard ?: -1)) 3.dp else (-1).dp,
                            color = selectedCardColor
                        )
                        .padding(4.dp)
                ) {
                    ShowCard(
                        it,
                        Modifier
                            .clickable { if (offsetMult == 0f) { onClick(index) } },
                        scale
                    )
                }
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
    return if (card is CardModifier) {
        cardOn.canAddModifier(card)
    } else if (card is CardBase && isPlayerCaravan && index == caravan.size - 1) {
        caravan.canPutCardOnTop(card)
    } else {
        false
    }
}

@Composable
fun RowScope.CaravanOnField(
    animationSpeed: AnimationSpeed,
    caravan: Caravan,
    isPlayerTurn: () -> Boolean,
    isEnemyCaravan: Boolean,
    isInitStage: Boolean,
    state: LazyListState,
    selectCaravan: () -> Unit = {},
    canPutSelectedCardOn: (Int) -> Boolean,
    addSelectedCardOnPosition: (Int) -> Unit,
    getGameUpdated: () -> Int,
) {
    var width by remember { mutableIntStateOf(0) }
    LaunchedEffect(width) {
        if (width == 0) {
            width = state.layoutInfo.viewportSize.width
        }
    }
    val modifierOffset = 16.dp * (if (isEnemyCaravan) -1 else 1)
    Column(Modifier.wrapContentHeight().weight(0.25f)) {
        if (!isEnemyCaravan && !isInitStage && !caravan.isEmpty()) {
            LaunchedEffect(caravan.recomposeResources) {}
            TextFallout(
                stringResource(Res.string.discard),
                getTextColor(),
                getTextStrokeColor(),
                14.sp,
                Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .padding(start = 2.dp, end = 2.dp, bottom = 2.dp, top = 0.dp)
                    .clickable {
                        selectCaravan()
                    }
                    .background(
                        getTextBackgroundColor().let { color ->
                            Color(color.red, color.green, color.blue, 0.75f)
                        }
                    )
                    .padding(2.dp)
            )
        }
        if (!isEnemyCaravan && caravan.isEmpty()) {
            LaunchedEffect(caravan.recomposeResources) {}
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .padding(horizontal = 4.dp)
                .background(Colors.Green)
                .border(4.dp, Colors.DarkGreen)
                .clickable {
                    addSelectedCardOnPosition(-1)
                }
            )
        }
        val lazyListState = rememberLazyListState()
        BoxWithConstraints(Modifier.fillMaxHeight().wrapContentWidth()) {
            LazyColumn(
                state = lazyListState,
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 2.dp)
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
            ) {
                item {
                    Box(Modifier.wrapContentHeight(unbounded = true)) {
                        LaunchedEffect(caravan.recomposeResources) {}
                        val enemyTurnMult = if (isPlayerTurn()) 1 else -1
                        @Composable
                        fun ModifierOnCardInCaravan(
                            modifier: Card,
                            modifierIndex: Int,
                            it: CardWithModifier
                        ) {
                            var prevStateHeight by rememberScoped { mutableStateOf(modifier.caravanAnimationMark) }
                            var prevStateWidth by rememberScoped { mutableStateOf(it.card.caravanAnimationMark) }
                            var needsSnap = prevStateHeight == modifier.caravanAnimationMark &&
                                    prevStateWidth == it.card.caravanAnimationMark
                            val offsetHeightMult by animateFloatAsState(when (modifier.caravanAnimationMark) {
                                Card.AnimationMark.STABLE -> 0f
                                Card.AnimationMark.NEW -> 2f * enemyTurnMult
                                Card.AnimationMark.MOVING_OUT -> 0f
                                Card.AnimationMark.MOVING_OUT_ALT -> 0f
                                Card.AnimationMark.MOVED_OUT -> 0f
                            }, if (modifier.caravanAnimationMark == Card.AnimationMark.MOVED_OUT || needsSnap) {
                                SnapSpec()
                            } else {
                                tween(animationSpeed.delay.toInt())
                            }
                            ) { _ -> prevStateHeight = modifier.caravanAnimationMark }

                            val offsetWidthMult by animateFloatAsState(when (it.card.caravanAnimationMark) {
                                Card.AnimationMark.STABLE -> 0f
                                Card.AnimationMark.NEW -> 0f
                                Card.AnimationMark.MOVING_OUT -> 2f
                                Card.AnimationMark.MOVING_OUT_ALT -> 2f
                                Card.AnimationMark.MOVED_OUT -> 2f
                            }, if (it.card.caravanAnimationMark == Card.AnimationMark.MOVED_OUT || needsSnap) {
                                SnapSpec()
                            } else {
                                tween(animationSpeed.delay.toInt())
                            }
                            ) { _ -> prevStateWidth = it.card.caravanAnimationMark }

                            LaunchedEffect(it.card.caravanAnimationMark, modifier.caravanAnimationMark) {
                                if (modifier.caravanAnimationMark == Card.AnimationMark.NEW) {
                                    playCardFlipSound()
                                    modifier.caravanAnimationMark = Card.AnimationMark.STABLE
                                }
                            }

                            Box(modifier = Modifier
                                .layout { measurable, constraints ->
                                    val placeable = measurable.measure(constraints)
                                    val placeableHeight = placeable.height.toInt()
                                    val placeableWidth = placeable.width.toInt()
                                    val modifierOffset = (modifierOffset * (modifierIndex + 1)).toPx()
                                    layout(placeableWidth.coerceAtLeast(0), placeableHeight) {
                                        placeable.place(
                                            (modifierOffset + placeableWidth * offsetWidthMult).toInt(),
                                            (placeableHeight * offsetHeightMult).toInt()
                                        )
                                    }
                                })
                            {
                                ShowCard(modifier, Modifier)
                            }
                        }

                        @Composable
                        fun CardInCaravan(
                            it: CardWithModifier,
                            index: Int
                        ) {
                            var prevState by rememberScoped { mutableStateOf(it.card.caravanAnimationMark) }
                            val needsSnap = prevState == it.card.caravanAnimationMark
                            val offsetHeightMult by animateFloatAsState(when (it.card.caravanAnimationMark) {
                                Card.AnimationMark.STABLE -> 0f
                                Card.AnimationMark.NEW -> 3f * enemyTurnMult
                                Card.AnimationMark.MOVING_OUT -> 0f
                                Card.AnimationMark.MOVING_OUT_ALT -> 0f
                                Card.AnimationMark.MOVED_OUT -> 0f
                            }, if (it.card.caravanAnimationMark == Card.AnimationMark.MOVED_OUT || needsSnap) {
                                SnapSpec()
                            } else {
                                tween(animationSpeed.delay.toInt())
                            }
                            ) { _ -> prevState = it.card.caravanAnimationMark }

                            val offsetWidthMult by animateFloatAsState(when (it.card.caravanAnimationMark) {
                                Card.AnimationMark.STABLE -> 0f
                                Card.AnimationMark.NEW -> 0f
                                Card.AnimationMark.MOVING_OUT -> 2f
                                Card.AnimationMark.MOVING_OUT_ALT -> 2f
                                Card.AnimationMark.MOVED_OUT -> 2f
                            }, if (it.card.caravanAnimationMark == Card.AnimationMark.MOVED_OUT || needsSnap) {
                                SnapSpec()
                            } else {
                                tween(animationSpeed.delay.toInt())
                            }
                            ) { _ -> prevState = it.card.caravanAnimationMark }

                            LaunchedEffect(it.card.caravanAnimationMark) {
                                if (it.card.caravanAnimationMark == Card.AnimationMark.NEW) {
                                    it.card.caravanAnimationMark = Card.AnimationMark.STABLE
                                    playCardFlipSound()
                                }
                            }

                            LaunchedEffect(getGameUpdated()) { }
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

                                    val layoutSize = lazyListState.layoutInfo.viewportSize.height - 2
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
                                LaunchedEffect(it.recomposeResources) { }
                                ShowCard(
                                    it.card,
                                    Modifier.clickable {
                                        addSelectedCardOnPosition(caravan.cards.indexOf(it))
                                    }
                                )

                                it.modifiersCopy().forEachIndexed { modifierIndex, card ->
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

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(vertical = 2.dp),
                adapter = rememberScrollbarAdapter(state),
                style = ScrollbarStyle(
                    minimalHeight = 0.dp,
                    thickness = 2.dp,
                    hoverDurationMillis = 0,
                    thumbStyle = ThumbStyle(
                        shape = RoundedCornerShape(100),
                        unhoverColor = getKnobColor(),
                        hoverColor = getKnobColor()
                    ),
                    trackStyle = TrackStyle(
                        shape = RoundedCornerShape(100),
                        unhoverColor = getTrackColor(),
                        hoverColor = getTrackColor()
                    )
                )
            )
        }
    }
}

@Composable
fun ShowDeck(cResources: CResources, deckSize: Int, isKnown: Boolean = true) {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFallout(
            deckSize.toString(),
            getTextColor(),
            getTextStrokeColor(),
            16.sp,
            Modifier
                .wrapContentSize()
                .background(getBackgroundColor())
        )
        if (isKnown) {
            val backCard = cResources.getDeckBack()
            if (backCard != null) {
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
                        backCard,
                        Modifier,
                        scale = scale
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.Score(num: Int, caravan: Caravan, opposingValue: Int) {
    val cities = getStyleCities(styleId)
    val (textColor, text, isColored) = if (caravan.getValue() > 26)
        Triple(Color.Red, caravan.getValue().toString(), true)
    else if (caravan.getValue() in (21..26) && (opposingValue !in (21..26) || caravan.getValue() > opposingValue))
        Triple(Color.Green, caravan.getValue().toString(), true)
    else
        Triple(getGameScoreColor(), caravan.getValue().toString(), false)
    Box(
        Modifier
            .weight(0.25f)
            .height(24.dp)
            .padding(2.dp)
            .background(getGrayTransparent())
            .padding(2.dp)
    ) {
        Text(
            text = cities[num],
            textAlign = TextAlign.Center,
            color = if (isColored) textColor else Color.Black,
            fontFamily = FontFamily(Font(Res.font.help)),
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
fun Caravans(
    animationSpeed: AnimationSpeed,
    getSelectedCard: () -> Card?,
    getSelectedCaravan: () -> Int,
    setSelectedCaravan: (Int) -> Unit,
    height: Float,
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
    getGameUpdated: () -> Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth().height(height.toInt().pxToDp()),
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
                    
                    animationSpeed,
                    caravan,
                    isPlayersTurn,
                    isEnemyCaravan = true,
                    isInitStage = getIsInitStage(),
                    enemyStates[it],
                    { -1 },
                    { index ->
                        getSelectedCard()?.let { canPutCard(it, caravan, false, index) } == true
                    },
                    { card -> addCardToEnemyCaravan(it, card) },
                    getGameUpdated
                )
            }
        }

        Column(Modifier.wrapContentHeight()) {
            LaunchedEffect(getGameUpdated()) {}
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(24.dp)) {
                repeat(3) {
                    val caravan = getEnemyCaravan(it)
                    val opposingValue = getPlayerCaravan(it).getValue()
                    key(caravan.recomposeResources, getGameUpdated()) {
                        Score(it, caravan, opposingValue)
                    }
                }
            }
            val text = when {
                isGameOver() -> stringResource(Res.string.can_t_act)
                !isPlayersTurn() || !canPlayerMove() -> stringResource(Res.string.wait)
                getIsInitStage() -> stringResource(Res.string.init_stage)
                getSelectedCard() != null -> {
                    stringResource(Res.string.discard_card)
                }
                getSelectedCaravan() in (0..2) -> {
                    stringResource(Res.string.drop_caravan, getSelectedCaravan() + 1)
                }
                else -> stringResource(Res.string.your_turn)
            }
            val modifier = Modifier
                .fillMaxWidth(0.66f)
                .wrapContentHeight()
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
                            .background(getTextBackgroundColor().let {
                                Color(it.red, it.green, it.blue, 0.81f)
                            })
                    } else {
                        it.background(getGrayTransparent())
                    }
                }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            Box(Modifier
                .fillMaxWidth()
                .wrapContentHeight(), contentAlignment = Alignment.Center) {
                TextFallout(
                    text,
                    getTextColor(),
                    getTextStrokeColor(),
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
                    key(caravan.recomposeResources, getGameUpdated()) {
                        Score(it + 3, caravan, opposingValue)
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
                    animationSpeed,
                    caravan,
                    isPlayersTurn,
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
                    getGameUpdated
                )
            }
        }
    }
}