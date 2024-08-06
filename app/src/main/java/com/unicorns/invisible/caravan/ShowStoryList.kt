package com.unicorns.invisible.caravan

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyFinalBossStory
import com.unicorns.invisible.caravan.model.enemy.EnemyPriestess
import com.unicorns.invisible.caravan.model.enemy.EnemyStory1
import com.unicorns.invisible.caravan.model.enemy.EnemyStory2
import com.unicorns.invisible.caravan.model.enemy.EnemyStory3
import com.unicorns.invisible.caravan.model.enemy.EnemyStory4
import com.unicorns.invisible.caravan.model.enemy.EnemyStory6
import com.unicorns.invisible.caravan.model.enemy.EnemyStory7
import com.unicorns.invisible.caravan.model.enemy.EnemyStory8
import com.unicorns.invisible.caravan.model.enemy.EnemyStory9A
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextClassic
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDialogBackground
import com.unicorns.invisible.caravan.utils.getDialogTextColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getStrokeColorByStyle
import com.unicorns.invisible.caravan.utils.getTextBackByStyle
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.nextSong
import com.unicorns.invisible.caravan.utils.playHeartbeatSound
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playNoCardAlarm
import com.unicorns.invisible.caravan.utils.playNotificationSound
import com.unicorns.invisible.caravan.utils.playNukeBlownSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playSlideSound
import com.unicorns.invisible.caravan.utils.playTowerCompleted
import com.unicorns.invisible.caravan.utils.playTowerFailed
import com.unicorns.invisible.caravan.utils.playWWSound
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.startFinalBossTheme
import com.unicorns.invisible.caravan.utils.stopAmbient
import com.unicorns.invisible.caravan.utils.stopRadio
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ShowStoryList(activity: MainActivity, showAlertDialog: (String, String) -> Unit, goBack: () -> Unit) {
    var showChapter by rememberScoped { mutableStateOf<Int?>(null) }

    if (showChapter != null) {
        when (showChapter) {
            0 -> {
                ShowStoryChapter1(activity, showAlertDialog, {
                    activity.save?.let {
                        if (it.storyChaptersProgress == 0) {
                            it.availableDecks[CardBack.DECK_13] = true
                            it.availableCards.addAll(CustomDeck(CardBack.DECK_13, false).toList())
                            it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 1)
                        }
                        saveOnGD(activity)
                    }
                }) { showChapter = null }
            }
            1 -> {
                ShowStoryChapter2(activity, showAlertDialog, {
                    activity.save?.let {
                        it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 2)
                        saveOnGD(activity)
                    }
                }) { showChapter = null }
            }
            2 -> {
                ShowStoryChapter3(activity, showAlertDialog, {
                    activity.save?.let {
                        it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 3)
                        saveOnGD(activity)
                    }
                }) { showChapter = null }
            }
            3 -> {
                LaunchedEffect(Unit) {
                    stopRadio()
                    isSoundEffectsReduced = true
                }
                ShowStoryChapter4(activity, showAlertDialog, {
                    activity.save?.let {
                        it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 4)
                        saveOnGD(activity)
                    }
                }) { showChapter = null; isSoundEffectsReduced = false; nextSong(activity);  }
            }
            4 -> {
                ShowStoryChapter5(activity, showAlertDialog, {
                    activity.save?.let {
                        it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 5)
                        it.secretMode = true
                        it.availableDecksAlt[CardBack.DECK_13] = true
                        saveOnGD(activity)
                    }
                }) { showChapter = null }
            }
            5 -> {
                ShowStoryChapter6(activity, showAlertDialog, {
                    activity.save?.let {
                        it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 6)
                        saveOnGD(activity)
                    }
                }) { showChapter = null }
            }
            6 -> {
                ShowStoryChapter7(activity, showAlertDialog, {
                    activity.save?.let {
                        it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 7)
                        saveOnGD(activity)
                    }
                }) { showChapter = null }
            }
            7 -> {
                ShowStoryChapter8(activity, showAlertDialog, {
                    activity.save?.let {
                        it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 8)
                        saveOnGD(activity)
                    }
                }, {
                    activity.save?.let {
                        it.altStoryChaptersProgress = maxOf(it.altStoryChaptersProgress, 1)
                        saveOnGD(activity)
                    }
                }) { showChapter = null }
            }
            8 -> {
                LaunchedEffect(Unit) {
                    stopRadio()
                    isSoundEffectsReduced = true
                }
                ShowStoryChapter9(activity, showAlertDialog, {
                    activity.save?.let {
                        it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 9)
                        saveOnGD(activity)
                    }
                }) { showChapter = null; isSoundEffectsReduced = false; nextSong(activity) }
            }
            9 -> {
                LaunchedEffect(Unit) {
                    stopRadio()
                    isSoundEffectsReduced = true
                }
                ShowStoryChapter10(activity) { showChapter = null; isSoundEffectsReduced = false; nextSong(activity) }
            }
            else -> {
                ShowStoryChapter9A(activity, showAlertDialog) { showChapter = null }
            }
        }
        return
    }

    MenuItemOpen(activity, stringResource(R.string.menu_story), "<-", goBack) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity))
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
                Spacer(modifier = Modifier.height(16.dp))
                TextFallout(
                    stringResource(R.string.select_the_chapter),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    22.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                @Composable
                fun Chapter(number: Int, isAlt: Boolean = false, onClick: () -> Unit) {
                    val isAvailable = number <= (activity.save?.storyChaptersProgress ?: 0)
                    val text = if (!isAvailable && !isAlt) {
                        "???"
                    } else when (number) {
                        0 -> stringResource(R.string.chapter_1_name)
                        1 -> stringResource(R.string.chapter_2_name)
                        2 -> stringResource(R.string.chapter_3_name)
                        3 -> stringResource(R.string.chapter_4_name)
                        4 -> stringResource(R.string.chapter_5_name)
                        5 -> stringResource(R.string.chapter_6_name)
                        6 -> stringResource(R.string.chapter_7_name)
                        7 -> stringResource(R.string.chapter_8_name)
                        8 -> stringResource(R.string.chapter_9_name)
                        9 -> stringResource(R.string.chapter_the_end_slides)
                        10 -> stringResource(R.string.chapter_9a_name)
                        else -> "???"
                    }
                    TextFallout(
                        text,
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Alignment.Center,
                        if (isAvailable || isAlt) {
                            Modifier
                                .clickable { onClick() }
                                .background(getTextBackgroundColor(activity))
                        } else {
                            Modifier
                        }
                            .padding(4.dp),
                        TextAlign.Center
                    )
                }

                val chaptersRevealed = when (activity.save?.storyChaptersProgress ?: 0) {
                    in (0..2) -> 3
                    3 -> 4
                    in (4..7) -> 8
                    8 -> 9
                    else -> 10
                }
                repeat(chaptersRevealed) {
                    Chapter(it) { playSelectSound(activity); showChapter = it }
                    Spacer(modifier = Modifier.height(10.dp))
                }
                if ((activity.save?.altStoryChaptersProgress ?: 0) > 0) {
                    Chapter(10, isAlt = true) { playSelectSound(activity); showChapter = 10 }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun DialogLine(activity: MainActivity, line: String, isSelect: Boolean = true, onClick: () -> Unit) {
    val modifier = if (isSelect) {
        Modifier
            .clickableSelect(activity) {
                onClick()
            }
    } else {
        Modifier
            .clickable {
                onClick()
            }
    }
    TextClassic(
        line,
        getTextColorByStyle(activity, Style.PIP_BOY),
        getStrokeColorByStyle(activity, Style.PIP_BOY),
        18.sp,
        Alignment.CenterStart,
        modifier = modifier
            .background(getTextBackByStyle(activity, Style.PIP_BOY))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        TextAlign.Start
    )
    Spacer(Modifier.height(12.dp))
}

@Composable
fun ShowStoryChapter1(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(activity.getString(R.string.chapter_1_text_1)) }
    var lineNumber by rememberSaveable { mutableIntStateOf(0) }
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }
    if (isGame) {
        StartTowerGame(
            activity,
            EnemyStory1,
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            { gameResult = -1 },
            { isGame = false }
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .scrollbar(
                    state,
                    knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                    trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                    horizontal = false,
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) { item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = if (gameResult == -1) {
                                R.drawable.black_back
                            } else {
                                when (lineNumber) {
                                    0 -> R.drawable.ch1_1
                                    1 -> R.drawable.ch1_2
                                    2 -> R.drawable.ch1_3
                                    3 -> R.drawable.ch1_4
                                    -1 -> R.drawable.ch1_6
                                    else -> R.drawable.ch1_5
                                }
                            }
                        )
                    ))

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    text,
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    16.sp,
                    Alignment.CenterStart,
                    modifier = Modifier
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                when (gameResult) {
                    1 -> {
                        text = stringResource(R.string.chapter_1_on_win)
                        lineNumber = -1
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = stringResource(R.string.chapter_1_on_lose)
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine(activity, stringResource(R.string.chapter_1_q_1)) {
                        lineNumber = 1
                        text = activity.getString(R.string.chapter_1_text_2)
                    }
                    1 -> DialogLine(activity, stringResource(R.string.chapter_1_q_2)) {
                        lineNumber = 2
                        text = activity.getString(R.string.chapter_1_text_3)
                    }
                    2 -> DialogLine(activity, stringResource(R.string.chapter_1_q_3)) {
                        lineNumber = 3
                        text = activity.getString(R.string.chapter_1_text_4)
                    }
                    3 -> DialogLine(activity, stringResource(R.string.chapter_1_q_4)) {
                        lineNumber = 4
                        text = activity.getString(R.string.chapter_1_text_5)
                    }
                    -1 -> DialogLine(activity, activity.getString(R.string.finish)) { goBack() }
                    else -> {
                        DialogLine(activity, activity.getString(R.string.finish)) { isGame = true; gameResult = -1 }
                    }
                }
            }
        } }
    }
}

@Composable
fun ShowStoryChapter2(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(activity.getString(R.string.ch_2_t_1)) }
    var lineNumber by rememberSaveable { mutableIntStateOf(0) }
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }
    if (isGame) {
        StartTowerGame(
            activity,
            EnemyStory2,
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            { gameResult = -1 },
            { isGame = false }
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .scrollbar(
                    state,
                    knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                    trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                    horizontal = false,
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) { item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = if (gameResult == -1) {
                                R.drawable.black_back
                            } else {
                                when (lineNumber) {
                                    0 -> R.drawable.ch1_5
                                    1 -> R.drawable.ch1_6
                                    2 -> R.drawable.ch2_1
                                    3 -> R.drawable.ch2_2
                                    -1 -> R.drawable.ch2_win
                                    else -> R.drawable.ch2_3
                                }
                            }
                        )
                    ))

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    text,
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    16.sp,
                    Alignment.CenterStart,
                    modifier = Modifier
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                when (gameResult) {
                    1 -> {
                        text = stringResource(R.string.ch2_win)
                        lineNumber = -1
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = stringResource(R.string.ch2_lose)
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine(activity, stringResource(R.string.ch2_q1)) {
                        lineNumber = 1
                        text = activity.getString(R.string.ch2_t2)
                    }
                    1 -> DialogLine(activity, stringResource(R.string.ch2_q2)) {
                        lineNumber = 2
                        text = activity.getString(R.string.ch2_t3)
                    }
                    2 -> DialogLine(activity, stringResource(R.string.ch2_q3)) {
                        lineNumber = 3
                        text = activity.getString(R.string.ch2_t4)

                    }
                    3 -> DialogLine(activity, stringResource(R.string.ch2_q4)) {
                        lineNumber = 4
                        text = activity.getString(R.string.ch2_t5)
                    }
                    -1 -> DialogLine(activity, activity.getString(R.string.finish)) { goBack() }
                    else -> {
                        DialogLine(activity, activity.getString(R.string.finish)) { isGame = true; gameResult = -1 }
                    }
                }
            }
        } }
    }
}

@Composable
fun ShowStoryChapter3(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(activity.getString(R.string.c3_t1)) }
    var lineNumber by rememberSaveable { mutableIntStateOf(0) }
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }
    if (isGame) {
        StartStoryGame(
            activity,
            EnemyStory3(),
            CResources(activity.save?.getCustomDeckCopy() ?: CustomDeck(CardBack.STANDARD, false)),
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            { gameResult = -1 },
            { isGame = false }
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .scrollbar(
                    state,
                    knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                    trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                    horizontal = false,
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) { item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = if (gameResult == -1) {
                                R.drawable.black_back
                            } else {
                                when (lineNumber) {
                                    0 -> R.drawable.ch3_1
                                    1, 2 -> R.drawable.ch3_2
                                    3 -> R.drawable.ch3_3
                                    -1 -> R.drawable.ch3_win
                                    else -> R.drawable.ch3_4
                                }
                            }
                        )
                    ))

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    text,
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    16.sp,
                    Alignment.CenterStart,
                    modifier = Modifier
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                when (gameResult) {
                    1 -> {
                        text = stringResource(R.string.c3_win)
                        lineNumber = -1
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = stringResource(R.string.c3_lose)
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine(activity, stringResource(R.string.c3_q1)) {
                        lineNumber = 1
                        text = activity.getString(R.string.c3_t2)
                    }
                    1 -> DialogLine(activity, stringResource(R.string.c3_q2)) {
                        lineNumber = 2
                        text = activity.getString(R.string.c3_t3)
                    }
                    2 -> DialogLine(activity, stringResource(R.string.c3_q3)) {
                        lineNumber = 3
                        text = activity.getString(R.string.c3_t4)
                    }
                    3 -> DialogLine(activity, stringResource(R.string.c3_q4)) {
                        lineNumber = 4
                        text = activity.getString(R.string.c3_t5)
                    }
                    -1 -> DialogLine(activity, activity.getString(R.string.finish)) { goBack() }
                    else -> {
                        DialogLine(activity, activity.getString(R.string.finish)) { isGame = true; gameResult = -1 }
                    }
                }
            }
        } }
    }
}

@Composable
fun ShowStoryChapter4(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(activity.getString(R.string.c4_t1)) }
    var lineNumber by rememberSaveable { mutableIntStateOf(0) }
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }

    var messageNumber by rememberSaveable { mutableIntStateOf(-1) }
    if (messageNumber > 0) {
        LaunchedEffect(Unit) {
            playHeartbeatSound(activity)
            when (messageNumber) {
                2, 3 -> {
                    delay(500L)
                    playHeartbeatSound(activity)
                }
                4 -> {
                    repeat(3) {
                        delay(500L)
                        playHeartbeatSound(activity)
                    }
                }
            }
        }
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = Color(activity.getColor(R.color.colorText))),
            onDismissRequest = { messageNumber = -1 },
            confirmButton = {
                TextClassic(
                    when (messageNumber) {
                        1 -> stringResource(R.string.ch4_r1)
                        2 -> stringResource(R.string.ch4_r2)
                        3 -> stringResource(R.string.ch4_r3)
                        else -> stringResource(R.string.ch4_r4)
                    },
                    Color(activity.getColor(R.color.colorTextBack)),
                    Color(activity.getColor(R.color.colorTextBack)),
                    18.sp, Alignment.Center,
                    Modifier
                        .background(Color(activity.getColor(R.color.colorText)))
                        .clickableCancel(activity) { messageNumber = -1 }
                        .padding(4.dp),
                    TextAlign.Center
                )
            },
            title = {
                TextClassic(
                    stringResource(R.string.ch4_mh),
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    24.sp, Alignment.CenterStart, Modifier,
                    TextAlign.Start
                )
            },
            text = {
                TextClassic(
                    when (messageNumber) {
                        1 -> stringResource(R.string.ch4_m1)
                        2 -> stringResource(R.string.ch4_m2)
                        3 -> stringResource(R.string.ch4_m3)
                        else -> stringResource(R.string.ch4_m4)
                    },
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    16.sp, Alignment.CenterStart, Modifier,
                    TextAlign.Start
                )
            },
            containerColor = Color.Black,
            textContentColor = Color(activity.getColor(R.color.colorText)),
            shape = RectangleShape,
        )
    }

    if (isGame) {
        StartStoryGame(
            activity,
            EnemyStory4 { messageNumber = it },
            CResources(CustomDeck().apply {
                add(Card(Rank.ACE, Suit.SPADES, CardBack.TOPS, false))
                add(Card(Rank.ACE, Suit.SPADES, CardBack.ULTRA_LUXE, false))
                add(Card(Rank.ACE, Suit.SPADES, CardBack.GOMORRAH, false))
            }),
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            { gameResult = -1 },
            { isGame = false }
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .scrollbar(
                    state,
                    knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                    trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                    horizontal = false,
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) { item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = if (gameResult == -1) {
                                R.drawable.black_back
                            } else {
                                when (lineNumber) {
                                    -1 -> R.drawable.ch4_1
                                    -2 -> R.drawable.ch4_2
                                    -3 -> R.drawable.ch3_1
                                    else -> R.drawable.black_back
                                }
                            }
                        )
                    ))

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    text,
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    16.sp,
                    Alignment.CenterStart,
                    modifier = Modifier
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                when (gameResult) {
                    1 -> {
                        text = stringResource(R.string.ch4_w1)
                        lineNumber = -1
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = stringResource(R.string.ch4_l1)
                        lineNumber = -3
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine(activity, stringResource(R.string.ch4_q1)) {
                        lineNumber = 1
                        text = activity.getString(R.string.ch4_t2)
                    }
                    -1 -> DialogLine(activity, stringResource(R.string.ch4_q2)) {
                        lineNumber = -2
                        gameResult = 0
                        text = activity.getString(R.string.ch4_t3)
                    }
                    -2 -> DialogLine(activity, stringResource(R.string.ch4_q3)) {
                        lineNumber = -3
                        text = activity.getString(R.string.ch4_t4)
                    }
                    -3 -> DialogLine(activity, activity.getString(R.string.finish)) { goBack() }
                    else -> {
                        DialogLine(activity, activity.getString(R.string.finish)) { isGame = true; gameResult = -1 }
                    }
                }
            }
        } }
    }
}

@Composable
fun ShowStoryChapter5(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(activity.getString(R.string.c5_t1)) }
    var lineNumber by rememberSaveable { mutableIntStateOf(0) }
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }
    if (isGame) {
        StartStoryGame(
            activity,
            EnemyPriestess,
            CResources(activity.save?.getCustomDeckCopy() ?: CustomDeck(CardBack.STANDARD, false)),
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            { gameResult = -1; advanceChapter() },
            { isGame = false }
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .scrollbar(
                    state,
                    knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                    trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                    horizontal = false,
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) { item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = when (lineNumber) {
                                0 -> R.drawable.black_back
                                1 -> R.drawable.ch5_2
                                2, 3, 15, 5, -1 -> R.drawable.ch5_3
                                -2 -> R.drawable.ch5_win
                                else -> R.drawable.black_back
                            }
                        )
                    ))

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    text,
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    16.sp,
                    Alignment.CenterStart,
                    modifier = Modifier
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                when (gameResult) {
                    1, -1 -> {
                        text = stringResource(R.string.c5_wl)
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine(activity, stringResource(R.string.c5_q1)) {
                        lineNumber = 1
                        text = activity.getString(R.string.c5_t2)
                    }
                    1 -> DialogLine(activity, stringResource(R.string.c5_q2)) {
                        lineNumber = 15
                        text = activity.getString(R.string.c5_t3)
                    }
                    15, 2 -> {
                        if (lineNumber == 15) {
                            DialogLine(activity, stringResource(R.string.c5_q3)) {
                                lineNumber = 2
                                text = activity.getString(R.string.c5_t4)
                            }
                        }
                        DialogLine(activity, stringResource(R.string.c5_q4)) {
                            lineNumber = 3
                            text = activity.getString(R.string.c5_t5)
                        }
                    }
                    3 -> DialogLine(activity, stringResource(R.string.c5_q5)) {
                        lineNumber = 4
                        text = activity.getString(R.string.c5_t6)
                    }
                    4 -> DialogLine(activity, stringResource(R.string.c5_q6)) {
                        lineNumber = 5
                        text = activity.getString(R.string.c5_t7)
                    }
                    -1 -> DialogLine(activity, stringResource(R.string.c5_q7)) {
                        lineNumber = -2
                        gameResult = 0
                        text = activity.getString(R.string.c5_t8)
                    }
                    -2 -> DialogLine(activity, activity.getString(R.string.finish)) { goBack() }
                    else -> {
                        DialogLine(activity, activity.getString(R.string.finish)) { isGame = true; gameResult = -1 }
                    }
                }
            }
        } }
    }
}

@Composable
fun ShowStoryChapter6(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(activity.getString(R.string.c6_t1)) }
    var lineNumber by rememberSaveable { mutableIntStateOf(0) }
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }

    var messageNumber by rememberSaveable { mutableIntStateOf(-1) }
    if (messageNumber > 0) {
        LaunchedEffect(Unit) { playNotificationSound(activity) {} }
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = Color(activity.getColor(R.color.colorText))),
            onDismissRequest = { messageNumber = -1 },
            confirmButton = {
                TextClassic(
                    when (messageNumber) {
                        1 -> stringResource(R.string.ch6_r1)
                        2 -> stringResource(R.string.ch6_r2)
                        else -> stringResource(R.string.ch6_r3)
                    },
                    Color(activity.getColor(R.color.colorTextBack)),
                    Color(activity.getColor(R.color.colorTextBack)),
                    18.sp, Alignment.Center,
                    Modifier
                        .background(Color(activity.getColor(R.color.colorText)))
                        .clickableCancel(activity) { messageNumber = -1 }
                        .padding(4.dp),
                    TextAlign.Center
                )
            },
            title = {
                TextClassic(
                    stringResource(R.string.ch6_mh),
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    24.sp, Alignment.CenterStart, Modifier,
                    TextAlign.Start
                )
            },
            text = {
                TextClassic(
                    when (messageNumber) {
                        1 -> stringResource(R.string.ch6_m1)
                        2 -> stringResource(R.string.ch6_m2)
                        else -> stringResource(R.string.ch6_m3)
                    },
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    16.sp, Alignment.CenterStart, Modifier,
                    TextAlign.Start
                )
            },
            containerColor = Color.Black,
            textContentColor = Color(activity.getColor(R.color.colorText)),
            shape = RectangleShape,
        )
    }

    if (isGame) {
        val enemy = EnemyStory6 { messageNumber = it }
        StartTowerGame(
            activity,
            enemy,
            showAlertDialog,
            {},
            { gameResult = 2 },
            {
                if (enemy.shownMessage) {
                    gameResult = -1
                } else {
                    gameResult = 1
                    advanceChapter()
                }
            },
            { isGame = false }
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .scrollbar(
                    state,
                    knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                    trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                    horizontal = false,
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) { item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = when (gameResult) {
                                in listOf(-1, 2) -> {
                                    R.drawable.black_back
                                }
                                1 -> {
                                    R.drawable.ch6_5
                                }
                                else -> {
                                    when (lineNumber) {
                                        0, 3 -> R.drawable.ch6_1
                                        1, 2 -> R.drawable.ch6_2
                                        4 -> R.drawable.ch6_3
                                        5 -> R.drawable.ch6_4
                                        else -> R.drawable.black_back
                                    }
                                }
                            }
                        )
                    ))

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    text,
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    16.sp,
                    Alignment.CenterStart,
                    modifier = Modifier
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                when (gameResult) {
                    1 -> {
                        text = stringResource(R.string.ch6_give_up)
                        lineNumber = -1
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = stringResource(R.string.ch_6_lose)
                        lineNumber = -1
                    }
                    2 -> {
                        LaunchedEffect(Unit) {
                            playNukeBlownSound(activity)
                            delay(10000L)
                            playTowerFailed(activity)
                        }
                        text = stringResource(R.string.ch6_win)
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0, 1, 2 -> {
                        DialogLine(activity, stringResource(R.string.ch6_q1)) {
                            lineNumber = 3
                            text = activity.getString(R.string.ch6_t2)
                        }
                        when (lineNumber) {
                            0 -> DialogLine(activity, stringResource(R.string.ch6_q2)) {
                                lineNumber = 1
                                text = activity.getString(R.string.ch6_t3)
                            }
                            1 -> DialogLine(activity, stringResource(R.string.ch6_q3)) {
                                lineNumber = 2
                                text = activity.getString(R.string.ch6_t4)
                            }
                        }
                    }
                    3 -> DialogLine(activity, stringResource(R.string.ch6_q4)) {
                        lineNumber = 4
                        text = activity.getString(R.string.ch6_t5)
                    }
                    4 -> DialogLine(activity, stringResource(R.string.ch6_q5)) {
                        lineNumber = 5
                        text = activity.getString(R.string.ch6_t6)
                    }
                    5 -> DialogLine(activity, stringResource(R.string.ch6_q6)) {
                        lineNumber = 6
                        text = activity.getString(R.string.ch6_t7)
                    }
                    -1 -> DialogLine(activity, activity.getString(R.string.finish)) { goBack() }
                    else -> {
                        DialogLine(activity, activity.getString(R.string.finish)) { isGame = true; gameResult = -1 }
                    }
                }
            }
        } }
    }
}

@Composable
fun ShowStoryChapter7(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(activity.getString(R.string.ch7_t1)) }
    var lineNumber by rememberSaveable { mutableIntStateOf(0) }
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }
    if (isGame) {
        StartTowerGame(
            activity,
            EnemyStory7,
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            { gameResult = -1 },
            { isGame = false }
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .scrollbar(
                    state,
                    knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                    trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                    horizontal = false,
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) { item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = if (gameResult == -1) {
                                R.drawable.black_back
                            } else {
                                when (lineNumber) {
                                    0 -> R.drawable.ch7_0
                                    1 -> R.drawable.ch7_1
                                    2, -2 -> R.drawable.ch7_2
                                    -1 -> R.drawable.ch7_3
                                    else -> R.drawable.black_back
                                }
                            }
                        )
                    ))

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    text,
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    16.sp,
                    Alignment.CenterStart,
                    modifier = Modifier
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                when (gameResult) {
                    1 -> {
                        text = stringResource(R.string.ch7_win)
                        lineNumber = -2
                        gameResult = 0
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = stringResource(R.string.ch7_lose)
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine(activity, stringResource(R.string.ch7_q1)) {
                        lineNumber = 1
                        text = activity.getString(R.string.ch7_t2)
                    }
                    1 -> DialogLine(activity, stringResource(R.string.ch7_q2)) {
                        lineNumber = 2
                        text = activity.getString(R.string.ch7_t3)
                    }
                    -1 -> DialogLine(activity, activity.getString(R.string.finish)) { goBack() }
                    -2 -> DialogLine(activity, stringResource(R.string.ch7_q3)) {
                        lineNumber = -1
                        text = activity.getString(R.string.ch7_t4)
                    }
                    else -> {
                        DialogLine(activity, activity.getString(R.string.finish)) { isGame = true; gameResult = -1 }
                    }
                }
            }
        } }
    }
}

@Composable
fun ShowStoryChapter8(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    advanceAltChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(activity.getString(R.string.ch8_t1)) }
    var lineNumber by rememberSaveable { mutableIntStateOf(0) }
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }
    if (isGame) {
        val enemy by rememberScoped { mutableStateOf(EnemyStory8()) }
        StartTowerGame(
            activity,
            enemy,
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            {
                if (enemy.resisted) {
                    gameResult = -1
                } else {
                    gameResult = 2
                    advanceAltChapter()
                }
            },
            { isGame = false }
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .scrollbar(
                    state,
                    knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                    trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                    horizontal = false,
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) { item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = if (gameResult < 0) {
                                R.drawable.black_back
                            } else {
                                when (lineNumber) {
                                    0 -> R.drawable.ch8_0
                                    1 -> R.drawable.ch7_0
                                    2, 3 -> R.drawable.black_back
                                    4 -> R.drawable.ch7_2
                                    5, 6 -> R.drawable.ch8_1
                                    -1 -> R.drawable.ch8_2
                                    else -> R.drawable.black_back
                                }
                            }
                        )
                    ))

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    text,
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    16.sp,
                    Alignment.CenterStart,
                    modifier = Modifier
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                when (gameResult) {
                    1 -> {
                        text = stringResource(R.string.ch8_win)
                        lineNumber = -1
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = stringResource(R.string.ch8_lose)
                        lineNumber = -1
                    }
                    2 -> {
                        text = stringResource(R.string.ch8_give_up)
                        lineNumber = -1
                        gameResult = 0
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> {
                        DialogLine(activity, stringResource(R.string.ch8_q1)) {
                            lineNumber = 1
                            text = activity.getString(R.string.ch8_t2)
                        }
                    }
                    1 -> DialogLine(activity, stringResource(R.string.ch8_q2)) {
                        lineNumber = 2
                        text = activity.getString(R.string.ch8_t3)
                    }
                    2 -> {
                        DialogLine(activity, stringResource(R.string.ch8_q3)) {
                            lineNumber = 3
                            text = activity.getString(R.string.ch8_t4)
                        }
                        DialogLine(activity, stringResource(R.string.ch8_q4)) {
                            lineNumber = 3
                            text = activity.getString(R.string.ch8_t5)
                        }
                    }
                    3 -> DialogLine(activity, stringResource(R.string.ch8_q5)) {
                        lineNumber = 4
                        text = activity.getString(R.string.ch8_t6)
                    }
                    4 -> DialogLine(activity, stringResource(R.string.ch8_q6)) {
                        lineNumber = 5
                        text = activity.getString(R.string.ch8_t7)
                    }
                    5 -> DialogLine(activity, stringResource(R.string.ch8_q7)) {
                        lineNumber = 6
                        text = activity.getString(R.string.ch8_t8)
                    }
                    -1 -> DialogLine(activity, activity.getString(R.string.finish)) { goBack() }
                    else -> {
                        DialogLine(activity, activity.getString(R.string.finish)) { isGame = true; gameResult = -1 }
                    }
                }
            }
        } }
    }
}

@Composable
fun ShowStoryChapter9(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(activity.getString(R.string.ch9_t1)) }
    var lineNumber by rememberSaveable { mutableIntStateOf(0) }
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }

    var dialogText by rememberSaveable { mutableIntStateOf(-1) }
    var isDistracted by rememberScoped { mutableIntStateOf(0) }

    if (dialogText != -1) {
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = getTextColor(activity)),
            onDismissRequest = {},
            confirmButton = {
                TextFallout(
                    "OK",
                    getDialogTextColor(activity),
                    getDialogTextColor(activity),
                    14.sp,
                    Alignment.CenterEnd,
                    Modifier.clickableCancel(activity) { dialogText = -1 },
                    TextAlign.End
                )
            },
            title = {
                TextFallout(
                    stringResource(R.string.ch9_mh),
                    getDialogTextColor(activity),
                    getDialogTextColor(activity),
                    24.sp,
                    Alignment.CenterEnd,
                    Modifier,
                    TextAlign.End
                )
            },
            text = {
                TextFallout(
                    when (dialogText) {
                        0 -> stringResource(R.string.ch9_m0)
                        1 -> stringResource(R.string.ch9_m1)
                        2 -> stringResource(R.string.ch9_m2)
                        3 -> stringResource(R.string.ch9_m3)
                        4 -> stringResource(R.string.ch9_m4)
                        5 -> stringResource(R.string.ch9_m5)
                        6 -> stringResource(R.string.ch9_m6)
                        7 -> stringResource(R.string.ch9_m7)
                        8 -> stringResource(R.string.ch9_m8)
                        else -> ""
                    },
                    getDialogTextColor(activity),
                    getDialogTextColor(activity),
                    18.sp,
                    Alignment.CenterStart,
                    Modifier,
                    TextAlign.Start
                )
            },
            containerColor = getDialogBackground(activity),
            textContentColor = getDialogTextColor(activity),
            shape = RectangleShape,
        )
    }

    if (isGame) {
        val enemy = rememberScoped {
            EnemyFinalBossStory(isDistracted).apply {
                playAlarm = {
                    repeat(3) {
                        playNoCardAlarm(activity)
                    }
                }
                sayThing = { dialogText = it }
            }
        }
        LaunchedEffect(Unit) {
            dialogText = 0
        }
        StartStoryGame(
            activity,
            enemy,
            CResources(activity.save?.getCustomDeckCopy() ?: CustomDeck(CardBack.STANDARD, false)),
            showAlertDialog,
            { startFinalBossTheme(activity) },
            { gameResult = 1; advanceChapter(); stopRadio() },
            { gameResult = -1; stopRadio() },
            { isGame = false }
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .scrollbar(
                    state,
                    knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                    trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                    horizontal = false,
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) { item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = if (gameResult < 0) {
                                R.drawable.black_back
                            } else {
                                when (lineNumber) {
                                    0, 1, 2, 3 -> R.drawable.ch9_1
                                    4, 5, 6 -> R.drawable.ch4_1
                                    7, 8, 9 -> R.drawable.ch9_2
                                    10, 11, 12 -> R.drawable.ch9_3
                                    13, 14, 15 -> R.drawable.ch9_4
                                    -1 -> R.drawable.ch9_win
                                    -2 -> R.drawable.ch9_end
                                    -3, 16, 17 -> R.drawable.black_back
                                    else -> R.drawable.black_back
                                }
                            }
                        )
                    ))

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    text,
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    16.sp,
                    Alignment.CenterStart,
                    modifier = Modifier
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                when (gameResult) {
                    1 -> {
                        text = stringResource(R.string.ch9_win)
                        lineNumber = -1
                        gameResult = 0
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = stringResource(R.string.ch9_lose)
                        lineNumber = -3
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine(activity, stringResource(R.string.ch9_q1)) {
                        lineNumber = 1
                        text = activity.getString(R.string.ch9_t2)
                    }
                    1 -> DialogLine(activity, stringResource(R.string.ch9_q2)) {
                        lineNumber = 2
                        text = activity.getString(R.string.ch9_t3)
                    }
                    2 -> DialogLine(activity, stringResource(R.string.ch9_q3)) {
                        lineNumber = 3
                        text = activity.getString(R.string.ch9_t4)
                    }
                    3 -> DialogLine(activity, stringResource(R.string.ch9_q4)) {
                        lineNumber = 4
                        text = activity.getString(R.string.ch9_t5)
                    }
                    4 -> DialogLine(activity, stringResource(R.string.ch9_q5)) {
                        lineNumber = 5
                        text = activity.getString(R.string.ch9_t6)
                    }
                    5 -> DialogLine(activity, stringResource(R.string.ch9_q6)) {
                        lineNumber = 6
                        text = activity.getString(R.string.ch9_t7)
                    }
                    6 -> DialogLine(activity, stringResource(R.string.ch9_q7)) {
                        lineNumber = 7
                        text = activity.getString(R.string.ch9_t8)
                    }
                    in (7..15) -> {
                        DialogLine(activity, stringResource(R.string.ch9_q8)) {
                            lineNumber = 16
                            text = activity.getString(R.string.ch9_t9)
                        }
                        when (lineNumber) {
                            7 -> {
                                DialogLine(activity, stringResource(R.string.ch9_q9)) {
                                    lineNumber = 8
                                    text = activity.getString(R.string.ch9_t10)
                                }
                            }
                            8 -> {
                                DialogLine(activity, stringResource(R.string.ch9_q10)) {
                                    lineNumber = 9
                                    text = activity.getString(R.string.ch9_t11)
                                }
                            }
                            9 -> {
                                DialogLine(activity, stringResource(R.string.ch9_q11)) {
                                    isDistracted++
                                    lineNumber = 10
                                    text = activity.getString(R.string.ch9_t12)
                                }
                            }
                            10 -> {
                                DialogLine(activity, stringResource(R.string.ch9_q12)) {
                                    lineNumber = 11
                                    text = activity.getString(R.string.ch9_t13)
                                }
                            }
                            11 -> {
                                DialogLine(activity, stringResource(R.string.ch9_q13)) {
                                    lineNumber = 12
                                    text = activity.getString(R.string.ch9_t14)
                                }
                            }
                            12 -> {
                                DialogLine(activity, stringResource(R.string.ch9_q14)) {
                                    lineNumber = 13
                                    text = activity.getString(R.string.ch9_t15)
                                }
                            }
                            13 -> {
                                DialogLine(activity, stringResource(R.string.ch9_q15)) {
                                    isDistracted++
                                    lineNumber = 14
                                    text = activity.getString(R.string.ch9_t16)
                                }
                            }
                            14 -> {
                                DialogLine(activity, stringResource(R.string.ch9_q16)) {
                                    lineNumber = 15
                                    text = activity.getString(R.string.ch9_t17)
                                }
                            }
                        }

                        DialogLine(activity, stringResource(R.string.ch9_q17)) {
                            lineNumber = 17
                            text = activity.getString(R.string.ch9_t18)
                        }
                    }
                    16 -> DialogLine(activity, stringResource(R.string.ch9_q18)) { playTowerFailed(activity); goBack() }
                    -1 -> DialogLine(activity, stringResource(R.string.ch9_q19)) {
                        lineNumber = -2
                        text = activity.getString(R.string.ch9_t20)
                    }
                    -2 -> DialogLine(activity, stringResource(R.string.ch9_q20)) { playNukeBlownSound(activity); goBack() }
                    -3 -> DialogLine(activity, activity.getString(R.string.finish)) { goBack() }
                    else -> {
                        DialogLine(activity, activity.getString(R.string.finish)) { isGame = true; gameResult = -1 }
                    }
                }
            }
        } }
    }
}

@Composable
fun ShowStoryChapter10(
    activity: MainActivity,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(activity.getString(R.string.ch10_t1)) }
    var lineNumber by rememberSaveable { mutableIntStateOf(0) }

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    fun slide() {
        playSlideSound(activity)
        scope.launch {
            offsetX.snapTo(800f)
            offsetX.animateTo(0f, animationSpec = tween(durationMillis = 1000))
        }
    }
    LaunchedEffect(Unit) { slide() }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .scrollbar(
                    state,
                    knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                    trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                    horizontal = false,
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) { item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .offset { IntOffset(offsetX.value.toInt(), 0) }
                    .alpha((800f - offsetX.value) / 800f)
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            when (lineNumber) {
                                0 -> R.drawable.ch7_0
                                1, 2 -> R.drawable.ch9_win
                                3 -> R.drawable.ch7_2
                                4, 5 -> R.drawable.black_back
                                6 -> R.drawable.black_back
                                7 -> R.drawable.ch7_3
                                8 -> R.drawable.ch1_1
                                -1 -> R.drawable.black_back
                                else -> R.drawable.black_back
                            }
                        )
                    )
            )

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    text,
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    16.sp,
                    Alignment.CenterStart,
                    modifier = Modifier
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    when (lineNumber) {
                        0 -> DialogLine(activity, stringResource(R.string.ch10_q), isSelect = false) {
                            slide()
                            lineNumber = 1
                            text = activity.getString(R.string.ch10_t2)
                        }
                        1 -> DialogLine(activity, stringResource(R.string.ch10_q), isSelect = false) {
                            lineNumber = 2
                            text = activity.getString(R.string.ch10_t3)
                        }
                        2 -> DialogLine(activity, stringResource(R.string.ch10_q), isSelect = false) {
                            slide()
                            lineNumber = 3
                            text = activity.getString(R.string.ch10_t4)
                        }
                        3 -> DialogLine(activity, stringResource(R.string.ch10_q), isSelect = false) {
                            slide()
                            lineNumber = 4
                            text = activity.getString(R.string.ch10_t5)
                        }
                        4 -> DialogLine(activity, stringResource(R.string.ch10_q), isSelect = false) {
                            lineNumber = 5
                            text = activity.getString(R.string.ch10_t6)
                        }
                        5 -> DialogLine(activity, stringResource(R.string.ch10_q), isSelect = false) {
                            slide()
                            lineNumber = 6
                            text = activity.getString(R.string.ch10_t7)
                        }
                        6 -> DialogLine(activity, stringResource(R.string.ch10_q), isSelect = false) {
                            slide()
                            lineNumber = 7
                            text = activity.getString(R.string.ch10_t8)
                        }
                        7 -> DialogLine(activity, stringResource(R.string.ch10_q), isSelect = false) {
                            slide()
                            playTowerCompleted(activity)
                            lineNumber = 8
                            text = activity.getString(R.string.ch10_t9)
                        }
                        8 -> DialogLine(activity, stringResource(R.string.ch10_q_last)) {
                            lineNumber = -1
                            text = activity.getString(R.string.ch10_finale)
                        }
                        -1 -> DialogLine(activity, stringResource(R.string.ch_end)) { goBack() }
                    }
                }
            }
        } }
    }
}


@Composable
fun ShowStoryChapter9A(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(activity.getString(R.string.ch9a_t1)) }
    var lineNumber by rememberSaveable { mutableIntStateOf(0) }
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }
    if (isGame) {
        StartStoryGame(
            activity,
            EnemyStory9A,
            CResources(CustomDeck(CardBack.UNPLAYABLE, false).apply {
                Rank.entries.forEach { rank ->
                    Suit.entries.forEach { suit ->
                        add(Card(rank, suit, CardBack.UNPLAYABLE, true))
                    }
                }
            }),
            showAlertDialog,
            {},
            { gameResult = 1 },
            { gameResult = -1 },
            { isGame = false }
        )
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .scrollbar(
                    state,
                    knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                    trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                    horizontal = false,
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) { item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = if (gameResult == -1) {
                                R.drawable.black_back
                            } else {
                                when (lineNumber) {
                                    0, 1 -> R.drawable.ch9_1
                                    2 -> R.drawable.ch4_1
                                    3 -> R.drawable.ch9_4
                                    4 -> R.drawable.ch9_2
                                    5 -> R.drawable.ch9_3
                                    6 -> R.drawable.ch7_3
                                    -2 -> R.drawable.ch9_1
                                    -3 -> R.drawable.ch4_1
                                    else -> R.drawable.black_back
                                }
                            }
                        )
                    ))

            Column(Modifier.fillMaxWidth()) {
                TextClassic(
                    text,
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    16.sp,
                    Alignment.CenterStart,
                    modifier = Modifier
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Start
                )

                Spacer(Modifier.height(16.dp))

                when (gameResult) {
                    1 -> {
                        text = stringResource(R.string.ch9a_win)
                        gameResult = 0
                        lineNumber = -2
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = stringResource(R.string.ch9a_lose)
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine(activity, stringResource(R.string.ch9a_q1)) {
                        lineNumber = 1
                        text = activity.getString(R.string.ch9a_t2)
                    }
                    1 -> DialogLine(activity, stringResource(R.string.ch9a_q2)) {
                        lineNumber = 2
                        text = activity.getString(R.string.ch9a_t3)
                    }
                    2 -> DialogLine(activity, stringResource(R.string.ch9a_q3)) {
                        stopRadio()
                        isSoundEffectsReduced = true
                        lineNumber = 3
                        text = activity.getString(R.string.ch9a_t4)
                    }
                    3 -> DialogLine(activity, stringResource(R.string.ch9a_q4)) {
                        lineNumber = 4
                        text = activity.getString(R.string.ch9a_t5)
                    }
                    4 -> DialogLine(activity, stringResource(R.string.ch9a_q5)) {
                        lineNumber = 5
                        text = activity.getString(R.string.ch9a_t6)
                    }
                    5 -> DialogLine(activity, stringResource(R.string.ch9a_q6)) {
                        lineNumber = 6
                        text = activity.getString(R.string.ch9a_t7)
                    }
                    -1 -> DialogLine(activity, activity.getString(R.string.finish)) { goBack(); isSoundEffectsReduced = false; nextSong(activity) }
                    -2 -> DialogLine(activity, stringResource(R.string.ch9a_q7)) {
                        lineNumber = -3
                        text = activity.getString(R.string.ch9a_t8)
                    }
                    -3 -> DialogLine(activity, stringResource(R.string.ch_end)) { goBack(); isSoundEffectsReduced = false; nextSong(activity) }
                    else -> {
                        DialogLine(activity, activity.getString(R.string.finish)) { isGame = true; gameResult = -1 }
                    }
                }
            }
        } }
    }
}


@Composable
fun StartStoryGame(
    activity: MainActivity,
    enemy: Enemy,
    playerCResources: CResources,
    showAlertDialog: (String, String) -> Unit,
    onStart: () -> Unit,
    onWin: () -> Unit,
    onLose: () -> Unit,
    goBack: () -> Unit,
) {
    val game = rememberScoped {
        Game(
            playerCResources,
            enemy
        ).also {
            onStart()
            it.startGame()
        }
    }

    game.also {
        it.onWin = {
            activity.processChallengesGameOver(it)
            playWinSound(activity)
            onWin()
            showAlertDialog(
                activity.getString(R.string.result),
                activity.getString(R.string.you_win)
            )
        }
        it.onLose = {
            playLoseSound(activity)
            onLose()
            showAlertDialog(
                activity.getString(R.string.result),
                activity.getString(R.string.you_lose)
            )
        }
        it.jokerPlayedSound = { playJokerSounds(activity) }
        it.wildWastelandSound = { playWWSound(activity) }
        it.nukeBlownSound = { playNukeBlownSound(activity) }
    }

    activity.goBack = { stopAmbient(); goBack(); activity.goBack = null }

    ShowGame(activity, game) {
        if (game.isOver()) {
            activity.goBack?.invoke()
            return@ShowGame
        }
        showAlertDialog(activity.getString(R.string.check_back_to_menu),
            activity.getString(R.string.tower_progress_will_be_lost))
    }
}