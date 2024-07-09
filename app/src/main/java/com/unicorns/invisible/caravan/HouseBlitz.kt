package com.unicorns.invisible.caravan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyBenny
import com.unicorns.invisible.caravan.model.enemy.EnemyBetter
import com.unicorns.invisible.caravan.model.enemy.EnemyHouse
import com.unicorns.invisible.caravan.model.enemy.EnemySecuritron38
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.SliderValueRangeCustom
import com.unicorns.invisible.caravan.utils.SwitchCustomUsualBackground
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playCardFlipSound
import com.unicorns.invisible.caravan.utils.playClickSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playVatsReady
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.playYesBeep
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.stopAmbient
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt


@Composable
fun BlitzScreen(
    activity: MainActivity,
    selectedDeck: () -> Pair<CardBack, Boolean>,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit,
) {
    var showGameBetter by rememberSaveable { mutableStateOf(false) }
    var showGameBenny by rememberSaveable { mutableStateOf(false) }
    var showGame38 by rememberSaveable { mutableStateOf(false) }
    var showGameHouse by rememberSaveable { mutableStateOf(false) }

    var selectedEnemy by rememberSaveable { mutableIntStateOf(-1) }
    fun selectEnemy(index: Int) {
        selectedEnemy = if (index == selectedEnemy) {
            playCloseSound(activity)
            -1
        } else {
            playSelectSound(activity)
            index
        }
    }

    var time by rememberSaveable { mutableStateOf(BlitzTime.FAST) }
    var bet by rememberSaveable { mutableIntStateOf(30) }

    var checkedCustomDeck by rememberSaveable {
        mutableStateOf(
            activity.save?.useCustomDeck ?: false
        )
    }
    fun getPlayerDeck(): CResources {
        return if (checkedCustomDeck)
            CResources(activity.save?.getCustomDeckCopy() ?: CustomDeck(CardBack.STANDARD, false))
        else
            CResources(selectedDeck().first, selectedDeck().second)
    }

    if (showGameBetter) {
        StartBlitz(
            activity = activity,
            playerCResources = getPlayerDeck(),
            enemy = EnemyBetter,
            showAlertDialog = showAlertDialog,
            time, bet,
        ) {
            showGameBetter = false
        }
        return
    } else if (showGameBenny) {
        StartBlitz(
            activity = activity,
            playerCResources = getPlayerDeck(),
            enemy = EnemyBenny,
            showAlertDialog = showAlertDialog,
            time, bet,
        ) {
            showGameBenny = false
        }
        return
    } else if (showGame38) {
        StartBlitz(
            activity = activity,
            playerCResources = getPlayerDeck(),
            enemy = EnemySecuritron38,
            showAlertDialog = showAlertDialog,
            time, bet,
        ) {
            showGame38 = false
        }
        return
    } else if (showGameHouse) {
        StartBlitz(
            activity = activity,
            playerCResources = getPlayerDeck(),
            enemy = EnemyHouse,
            showAlertDialog = showAlertDialog,
            time, bet,
        ) {
            showGameHouse = false
        }
        return
    }


    Column(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .scrollbar(
                    state,
                    knobColor = getKnobColor(activity), trackColor = getTrackColor(activity),
                    horizontal = false,
                )
                .padding(end = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) {
            item {
                Box(Modifier.fillMaxWidth()) {
                    Row(Modifier.align(Alignment.Center), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        TextFallout(
                            stringResource(R.string.blitz),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            32.sp,
                            Alignment.Center,
                            Modifier,
                            TextAlign.Center
                        )
                        TextFallout(
                            "(?)",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            24.sp,
                            Alignment.Center,
                            Modifier
                                .background(getTextBackgroundColor(activity))
                                .clickableOk(activity) {
                                    showAlertDialog(
                                        activity.getString(R.string.blitz_rules),
                                        activity.getString(R.string.blitz_rules_body)
                                    )
                                }
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextFallout(
                    stringResource(R.string.pve_select_enemy),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    22.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                @Composable
                fun OpponentItem(name: String, index: Int) {
                    TextFallout(
                        name,
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Alignment.Center,
                        Modifier
                            .border(
                                BorderStroke(
                                    if (index == selectedEnemy) 3.dp else (-1).dp,
                                    getSelectionColor(activity)
                                )
                            )
                            .padding(4.dp)
                            .clickable { selectEnemy(index) }
                            .background(getTextBackgroundColor(activity))
                            .padding(4.dp),
                        TextAlign.Center
                    )
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        OpponentItem(stringResource(R.string.pve_enemy_better), 0)
                        Spacer(Modifier.height(10.dp))
                        OpponentItem(stringResource(R.string.pve_enemy_38), 1)
                    }

                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        OpponentItem(stringResource(R.string.benny), 2)
                        Spacer(Modifier.height(10.dp))
                        OpponentItem(stringResource(R.string.mr_house), 3)
                    }
                }
                Spacer(Modifier.height(8.dp))
                TextFallout(
                    stringResource(R.string.start),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Alignment.Center,
                    modifier = Modifier
                        .clickableOk(activity) {
                            if (!activity.checkIfCustomDeckCanBeUsedInGame(getPlayerDeck())) {
                                showAlertDialog(
                                    activity.getString(R.string.custom_deck_is_too_small),
                                    activity.getString(R.string.custom_deck_is_too_small_message)
                                )
                                return@clickableOk
                            }
                            if (bet > (activity.save?.caps ?: 0)) {
                                showAlertDialog(
                                    activity.getString(R.string.not_enough_caps_warning),
                                    activity.getString(R.string.please_lower_your_bet_to_play)
                                )
                                return@clickableOk
                            }

                            when (selectedEnemy) {
                                -1 -> {
                                    showAlertDialog(
                                        activity.getString(R.string.select_your_opponent),
                                        ""
                                    )
                                    return@clickableOk
                                }

                                0 -> {
                                    showGameBetter = true
                                }

                                1 -> {
                                    showGame38 = true
                                }

                                2 -> {
                                    if (checkedCustomDeck) {
                                        showAlertDialog(
                                            activity.getString(R.string.benny_custom_deck_header),
                                            activity.getString(R.string.benny_custom_deck_body)
                                        )
                                    } else {
                                        showGameBenny = true
                                    }
                                }

                                3 -> {
                                    showGameHouse = true
                                }
                            }
                        }
                        .background(getTextBackgroundColor(activity))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Center
                )
            }
        }
        HorizontalDivider(color = getDividerColor(activity))

        Row(
            modifier = Modifier
                .height(56.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextFallout(
                stringResource(R.string.pve_use_custom_deck),
                getTextColor(activity),
                getTextStrokeColor(activity),
                14.sp,
                Alignment.CenterStart,
                Modifier.fillMaxWidth(0.7f),
                TextAlign.Start
            )
            CheckboxCustom(
                activity,
                { checkedCustomDeck },
                {
                    checkedCustomDeck = !checkedCustomDeck
                    if (checkedCustomDeck) {
                        playClickSound(activity)
                    } else {
                        playCloseSound(activity)
                    }
                    activity.save?.let {
                        it.useCustomDeck = checkedCustomDeck
                        saveOnGD(activity)
                    }
                },
                { true }
            )
        }

        HorizontalDivider(color = getDividerColor(activity))
        val state2 = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxHeight(0.75f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .scrollbar(
                    state2,
                    knobColor = getKnobColor(activity), trackColor = getTrackColor(activity),
                    horizontal = false,
                ),
            state = state2,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                TextFallout(
                    stringResource(R.string.place_your_bets, activity.save?.caps ?: 0),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextFallout(
                        stringResource(R.string.time_s, BlitzTime.FAST.time),
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Alignment.CenterEnd,
                        modifier = Modifier.weight(0.5f),
                        TextAlign.End,
                    )
                    Spacer(Modifier.width(8.dp))
                    SwitchCustomUsualBackground(
                        activity,
                        { time == BlitzTime.NORMAL },
                        {
                            time = time.switch()
                            playClickSound(activity)
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    TextFallout(
                        stringResource(R.string.seconds, BlitzTime.NORMAL.time),
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Alignment.CenterStart,
                        modifier = Modifier.weight(0.5f),
                        TextAlign.Start,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextFallout(
                        stringResource(R.string.bet_caps, bet),
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Alignment.CenterStart,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        TextAlign.Start,
                    )

                    SliderValueRangeCustom(
                        activity,
                        { bet / 50f },
                        { bet = (it * 500).roundToInt() / 10 },
                        4,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    val timeMult = getTimeMult(time)
                    val enemyMult = when (selectedEnemy) {
                        0, 1 -> 1.5
                        3 -> 2.0
                        2 -> 1.25
                        else -> 0.0
                    }
                    val reward = (bet * timeMult * enemyMult).toInt()
                    TextFallout(
                        stringResource(R.string.your_current_reward_caps, reward),
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        20.sp,
                        Alignment.Center,
                        modifier = Modifier.fillMaxWidth(),
                        TextAlign.Center,
                    )
                }
            }
        }
        TextFallout(
            stringResource(R.string.menu_back),
            getTextColor(activity),
            getTextStrokeColor(activity),
            24.sp,
            Alignment.Center,
            modifier = Modifier
                .clickableCancel(activity) { goBack() }
                .background(getTextBackgroundColor(activity))
                .padding(8.dp),
            TextAlign.Center
        )
    }
}

@Composable
fun StartBlitz(
    activity: MainActivity,
    playerCResources: CResources,
    enemy: Enemy,
    showAlertDialog: (String, String) -> Unit,
    time: BlitzTime,
    onBet: Int,
    game: Game = rememberScoped {
        Game(
            playerCResources,
            enemy
        ).also {
            activity.save?.let { save ->
                save.caps -= onBet
                saveOnGD(activity)
            }
            it.startGame()
        }
    },
    goBack: () -> Unit,
) {
    var timeOnTimer by rememberSaveable { mutableIntStateOf(time.time) }
    fun onMove() {
        timeOnTimer += 1
    }

    game.also {
        it.onWin = {
            activity.processChallengesGameOver(it)
            playWinSound(activity)
            var message = activity.getString(R.string.you_win)
            activity.save?.let { save ->
                val reward = (onBet * getTimeMult(time) * getEnemyMult(enemy)).toInt()
                message += activity.getString(R.string.you_have_earned_caps, reward)
                save.caps += reward + onBet
                saveOnGD(activity)
            }
            showAlertDialog(activity.getString(R.string.result), message)
        }
        it.onLose = {
            playLoseSound(activity)
            showAlertDialog(
                activity.getString(R.string.result),
                activity.getString(R.string.you_lose)
            )
        }
        it.jokerPlayedSound = { playJokerSounds(activity) }
        it.specialGameOverCondition = { if (timeOnTimer <= 0f) -1 else 0 }
    }

    LaunchedEffect(Unit) {
        if (game.isPlayerTurn) {
            while (isActive && timeOnTimer > 0 && !game.isOver()) {
                timeOnTimer--
                if (timeOnTimer < 10) {
                    playYesBeep(activity)
                }
                delay(1000L)
            }
            if (timeOnTimer <= 0f) {
                game.checkOnGameOver()
            }
        }
    }

    activity.goBack = { stopAmbient(); goBack() }


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
        onMove()
        game.afterPlayerMove(
            { updateEnemyHand(); updateCaravans() },
            AnimationSpeed.NONE
        )
    }

    fun dropCaravan() {
        val selectedCaravanNN = selectedCaravan
        if (selectedCaravanNN == -1) return
        playVatsReady(activity)
        activity.processChallengesMove(Challenge.Move(moveCode = 1), game)
        game.playerCaravans[selectedCaravanNN].dropCaravan()
        resetSelected()
        onMove()
        game.afterPlayerMove(
            { updateEnemyHand(); updateCaravans() },
            AnimationSpeed.NONE
        )
    }

    fun addCardToCaravan(caravan: Caravan, position: Int, isEnemy: Boolean) {
        fun onCaravanCardInserted() {
            resetSelected()
            onMove()
            game.afterPlayerMove(
                { updateEnemyHand(); updateCaravans() },
                AnimationSpeed.NONE
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
                    activity.processChallengesMove(Challenge.Move(
                        moveCode = 4,
                        handCard = card
                    ), game)
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
        {
            if (game.isOver()) {
                activity.goBack?.invoke()
                activity.goBack = null
                return@ShowGameRaw
            }

            if (game.enemy is EnemyHouse) {
                showAlertDialog(activity.getString(R.string.what_tired_of_losing), "")
            } else {
                showAlertDialog(activity.getString(R.string.check_back_to_menu), "")
            }
        },
        AnimationSpeed.NONE,
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
                14.sp,
                Alignment.BottomEnd,
                Modifier
                    .background(getTextBackgroundColor(activity))
                    .padding(8.dp),
                TextAlign.Center
            )
        }
    }
}

enum class BlitzTime(val time: Int) {
    FAST(20),
    NORMAL(30);

    fun switch(): BlitzTime {
        return when (this) {
            FAST -> NORMAL
            NORMAL -> FAST
        }
    }
}

fun getTimeMult(time: BlitzTime): Double {
    return when (time) {
        BlitzTime.FAST -> 1.5
        BlitzTime.NORMAL -> 1.0
    }
}

fun getEnemyMult(enemy: Enemy): Double {
    return when (enemy) {
        is EnemyBetter, is EnemySecuritron38 -> 1.5
        is EnemyHouse -> 2.0
        is EnemyBenny -> 1.25
        else -> 1.0
    }
}