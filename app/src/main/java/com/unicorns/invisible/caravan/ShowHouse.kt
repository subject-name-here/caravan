package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
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
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.GameSaver
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyBetter
import com.unicorns.invisible.caravan.model.enemy.EnemyHouse
import com.unicorns.invisible.caravan.model.enemy.EnemyBenny
import com.unicorns.invisible.caravan.model.enemy.EnemySecuritron38
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.SliderValueRangeCustom
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
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
import com.unicorns.invisible.caravan.utils.playVatsEnter
import com.unicorns.invisible.caravan.utils.playVatsReady
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.playYesBeep
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.stopAmbient
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt


@Composable
fun ShowHouse(
    activity: MainActivity,
    selectedDeck: () -> Pair<CardBack, Boolean>,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit,
) {
    BlitzScreen(activity, selectedDeck, showAlertDialog, goBack)
}

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

    var time by rememberSaveable { mutableIntStateOf(30) }
    var bet by rememberSaveable { mutableIntStateOf(0) }

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
        if (!activity.checkIfCustomDeckCanBeUsedInGame(getPlayerDeck())) {
            showAlertDialog(
                stringResource(R.string.custom_deck_is_too_small),
                stringResource(R.string.custom_deck_is_too_small_message)
            )
            return
        }
        if (bet > (activity.save?.caps ?: 0)) {
            showAlertDialog(
                "Not enough caps!",
                "Please, lower your bet to play."
            )
            return
        }
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
        if (!activity.checkIfCustomDeckCanBeUsedInGame(getPlayerDeck())) {
            showAlertDialog(
                stringResource(R.string.custom_deck_is_too_small),
                stringResource(R.string.custom_deck_is_too_small_message)
            )
            return
        }
        if (bet > (activity.save?.caps ?: 0)) {
            showAlertDialog(
                "Not enough caps!",
                "Please, lower your bet to play."
            )
            return
        }
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
        if (!activity.checkIfCustomDeckCanBeUsedInGame(getPlayerDeck())) {
            showAlertDialog(
                stringResource(R.string.custom_deck_is_too_small),
                stringResource(R.string.custom_deck_is_too_small_message)
            )
            return
        }
        if (bet > (activity.save?.caps ?: 0)) {
            showAlertDialog(
                "Not enough caps!",
                "Please, lower your bet to play."
            )
            return
        }

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
        if (!activity.checkIfCustomDeckCanBeUsedInGame(getPlayerDeck())) {
            showAlertDialog(
                stringResource(R.string.custom_deck_is_too_small),
                stringResource(R.string.custom_deck_is_too_small_message)
            )
            return
        }
        if (bet > (activity.save?.caps ?: 0)) {
            showAlertDialog(
                "Not enough caps!",
                "Please, lower your bet to play."
            )
            return
        }

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
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) {
            item {
                TextFallout(
                    "BLITZ!",
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    32.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
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
                fun OpponentItem(name: String, onClick: () -> Unit) {
                    TextFallout(
                        name,
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Alignment.Center,
                        Modifier
                            .clickable { onClick() }
                            .background(getTextBackgroundColor(activity))
                            .padding(4.dp),
                        TextAlign.Center
                    )
                }

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        OpponentItem(stringResource(R.string.pve_enemy_38)) { playVatsEnter(activity); showGame38 = true }
                        Spacer(Modifier.height(10.dp))
                        OpponentItem(stringResource(R.string.pve_enemy_better)) { playVatsEnter(activity); showGameBetter = true }
                    }

                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        OpponentItem("Benny") {
                            if (checkedCustomDeck) {
                                showAlertDialog(
                                    "Babe, I ain't good enough already.",
                                    "Now how about you leave that custom deck in your deep pocket?"
                                )
                            } else {
                                playVatsEnter(activity)
                                showGameBenny = true
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        OpponentItem("Mr. House") { playVatsEnter(activity); showGameHouse = true }
                    }
                }
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
                    "Place your bets!\n(You have ${activity.save?.caps ?: 0} caps.)",
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
                        "Time: $time s",
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Alignment.CenterStart,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        TextAlign.Start,
                    )

                    SliderValueRangeCustom(
                        activity,
                        { (time - 10).toFloat() / 20f },
                        { time = (it * 20).toInt() + 10 },
                        3,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextFallout(
                        "Bet: $bet caps",
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Alignment.CenterStart,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        TextAlign.Start,
                    )

                    SliderValueRangeCustom(
                        activity,
                        { bet / 100f },
                        { bet = (it * 1000).roundToInt() / 10 },
                        9,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    val timeMult = getTimeMult(time)
                    val reward = (bet * timeMult).toInt()
                    val reward1 = (reward * 1.207).toInt()
                    val reward2 = (reward * 1.414).toInt()
                    TextFallout(
                        "Your current reward:\n$reward2 for Mr. House;\n$reward1 caps for other enemies.",
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
    time: Int,
    onBet: Int,
    goBack: () -> Unit,
) {
    var timeOnTimer by rememberSaveable { mutableIntStateOf(0) }
    fun onMove() {
        timeOnTimer += 1
    }

    val game by rememberSaveable(stateSaver = GameSaver) {
        mutableStateOf(
            Game(
                playerCResources,
                enemy
            ).also {
                it.specialGameOverCondition = { if (timeOnTimer <= 0f) -1 else 0 }
                it.startGame()
            }
        )
    }
    game.also {
        it.onWin = {
            playWinSound(activity)
            var message = activity.getString(R.string.you_win)
            activity.save?.let { save ->
                val reward = (onBet * getTimeMult(time) * getEnemyMult(enemy)).toInt()
                message += "\nYou have earned $reward caps!"
                save.caps += reward
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
    }

    LaunchedEffect(Unit) {
        if (game.isPlayerTurn) {
            timeOnTimer = time
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

fun getTimeMult(time: Int): Double {
    return when (time) {
        in (0..14) -> 1.414
        in (15..19) -> 1.32
        in (20..24) -> 1.22
        in (25..29) -> 1.11
        else -> 1.05
    }
}

fun getEnemyMult(enemy: Enemy): Double {
    return when (enemy) {
        is EnemyBetter, is EnemySecuritron38, is EnemyBenny -> 1.207
        is EnemyHouse -> 1.414
        else -> 1.0
    }
}