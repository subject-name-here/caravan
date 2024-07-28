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
                            id = R.drawable.frank_head
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
                            id = R.drawable.frank_head
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

// TODO: translate everything here!!!!
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
                            id = R.drawable.frank_head
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
    var text by rememberSaveable { mutableStateOf("Aarrghhhhhhhh… Uuuuuu… Mmmmmphhhhhhhh…") }
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
                        1 -> "yeah......"
                        2 -> "Yeah..."
                        3 -> "Yeah."
                        else -> "Yeah!"
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
                    "You feel the heartbeat...",
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    24.sp, Alignment.CenterStart, Modifier,
                    TextAlign.Start
                )
            },
            text = {
                TextClassic(
                    when (messageNumber) {
                        1 -> "Can't be the end, can it?"
                        2 -> "You are too strong to perish like this."
                        3 -> "Now get up and fight. Fight!"
                        else -> "You are not dying here today!"
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
                            id = R.drawable.frank_head
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
                        text = "The Prospector’s body had not succumbed to the poison. As his soul " +
                                "wrestled the angel of death and reigned victorious, he earnt himself yet " +
                                "another day to live."
                        lineNumber = -1
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = "The Prospector’s body had succumbed to the poison. He is just another dead body in the cursed vault."
                        lineNumber = -3
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine(activity, "Someone's coming, Prospector.") {
                        lineNumber = 1
                        text = "(grim reaper approaches)"
                    }
                    -1 -> DialogLine(activity, "...") {
                        lineNumber = -2
                        gameResult = 0
                        text = "Upon awakening, the Prospector remembered what happened, and broke " +
                                "the door that leads to the deeper sections of the vault, making sure the " +
                                "swarm would take no new lives. The Prospector made use of the remaining " +
                                "available rooms, using them for recovery."
                    }
                    -2 -> DialogLine(activity, "You made me worry.") {
                        lineNumber = -3
                        text = "After a few days, the rain ended, and the Prospector could continue his way..."
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
    var text by rememberSaveable { mutableStateOf("Weeks, maybe months passed… The Prospector reached the northern territories of the former Canada, where it borders with Alaska.") }
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
                            id = R.drawable.frank_head
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
                        text = "The Madness Priestess was joyous. She liked the match a lot, and awarded the Prospector with a deck that once belonged to one of her flock."
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine(activity, "Well, he's not far from Oasis.") {
                        lineNumber = 1
                        text =
                            "To his surprise, a bizarre scene unfolded before him: a gigantic wall of trash, tens of meters high. He attempted to traverse around them, but to no avail – the wall had no end."
                    }
                    1 -> DialogLine(activity, "Wall of trash? Who put it here?") {
                        lineNumber = 15
                        text = "The Prospector was discovered by a woman who was fixing the wall with the trash she brought from afar."
                    }
                    15, 2 -> {
                        if (lineNumber == 15) {
                            DialogLine(activity, "A woman? Who is she?") {
                                lineNumber = 2
                                text = "She is the priestess of Church of Madness, whatever that means. " +
                                        "The Prospector didn't feel the need to get more information. " +
                                        "He has seen enough freaks to understand that sometimes it's better not to know."
                            }
                        }
                        DialogLine(activity, "Why would she reinforce the wall?") {
                            lineNumber = 3
                            text = "She explained that the wall is meant to keep the people safe from what lays beyond, elaborating on which she refused."
                        }
                    }
                    3 -> DialogLine(activity, "So, how did Prospector proceed?") {
                        lineNumber = 4
                        text = "He didn't. Turned back and returned home."
                    }
                    4 -> DialogLine(activity, "Very funny.") {
                        lineNumber = 5
                        text = "Okay, she agreed to assist the Prospector and lead him through the secret passage, be he to entertain her in the friendly game of Caravan…"
                    }
                    -1 -> DialogLine(activity, "Yeah, but the Prospector arrived not to play Caravan...") {
                        lineNumber = -2
                        gameResult = 0
                        text = "The Prospector was lead beyond the wall. Yet what he saw there astonished him, and not in a good way…"
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
    var text by rememberSaveable { mutableStateOf("Ships. Lots and lots of ships were docked to the shore base " +
            "which was the Prospector’s destination. The steam ships were unloading lots and lots of boxes.") }
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
                        1 -> "..."
                        2 -> "..."
                        else -> "Hell 2 U!"
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
                    "Chinese soldier says out loud:",
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    24.sp, Alignment.CenterStart, Modifier,
                    TextAlign.Start
                )
            },
            text = {
                TextClassic(
                    when (messageNumber) {
                        1 -> "Put your hands in the air! Drop your weapons! Do not move!"
                        2 -> "Last warning! Drop your weapons and do not move any more!"
                        else -> "That's it! You're going down!"
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
                            id = R.drawable.frank_head
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
                        text = "Since the Prospector didn't resist, he was arrested and put in one of the ships. " +
                                "Little did his captors know it was the plan all along..."
                        lineNumber = -1
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = "Since the Prospector resisted, he was killed in place."
                        lineNumber = -1
                    }
                    2 -> {
                        LaunchedEffect(Unit) {
                            playNukeBlownSound(activity)
                            delay(10000L)
                            playTowerFailed(activity)
                        }
                        text = "Prospector did it. He defeated everyone. Then he heard a whistle. Nuclear bomb was coming his way. " +
                                "Then - hot breath of the air, then... nothing."
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0, 1, 2 -> {
                        DialogLine(activity, "Wait, Chinese?") {
                            lineNumber = 3
                            text =
                                "Yes. The Chinese. And lots of them. Most ghoulified, but plenty of them pure. " +
                                        "They have turned the Oasis into their base of operations on the North American continent."
                        }
                        when (lineNumber) {
                            0 -> DialogLine(activity, "What was in the boxes?") {
                                lineNumber = 1
                                text =
                                    "Most of them had the Caravan card decks, some of them artificially aged. " +
                                            "The Prospector understood everything: of course, how " +
                                            "could the food and such unnecessary and quick to decay things as cards " +
                                            "could be found in the wastes, if they were not dropped out by the Chinese?"
                            }
                            1 -> DialogLine(activity, "[INT 6/8] But why flooding America with food and stuff?") {
                                lineNumber = 2
                                text = "Pfft, that's obvious. I am not even gonna explain it to you, that's how obvious it is."
                            }
                        }
                    }
                    3 -> DialogLine(activity, "What did the Prospector do? Has he killed everyone and saved the world?") {
                        lineNumber = 4
                        text = "Not quite. The Prospector had thought it was too late to go back, and so he decided " +
                                "to wait for the dark and sneak onto one of the returning cargo ships."
                    }
                    4 -> DialogLine(activity, "[Sneak 69/70] Yay, ninja time!") {
                        lineNumber = 5
                        text = "Unfortunately, the Prospector was noticed by one of the guards, who has alerted EVERYONE around."
                    }
                    5 -> DialogLine(activity, "And then he defeated everyone?") {
                        lineNumber = 6
                        text = "See for yourself."
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
    var text by rememberSaveable { mutableStateOf("Turns out China was barely damaged during the Great War. " +
            "Or maybe it was, but the cities were rebuilt. Either way, the pre-war civilization did not only remain here, but thrived.") }
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
                            id = R.drawable.frank_head
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
                        text = "As they talked during the game, the Prospector learnt more about his cellmate."
                        lineNumber = -2
                        gameResult = 0
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = "Cellmate, disappointed in the Prospector, decided not to open up. " +
                                "Eventually, they both perished in the prison."
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine(activity, "What about the Prospector?") {
                        lineNumber = 1
                        text =
                            "The guards lead the Prospector to one of the city prisons, where he was interrogated. " +
                                    "Initially, they believed that he was one of the spies from " +
                                    "the many \"savage tribes\" that inhabited the fallen America, but once they were " +
                                    "proven wrong, their interest in the Prospector died."
                    }
                    1 -> DialogLine(activity, "So, did they let him go?") {
                        lineNumber = 2
                        text = "No, for the time being he was left in a cell with another convict, " +
                                "where they would wait for whatever comes their way. " +
                                "The inmate offered a friendly game of caravan to kill time…"
                    }
                    -1 -> DialogLine(activity, activity.getString(R.string.finish)) { goBack() }
                    -2 -> DialogLine(activity, "So, who is he?") {
                        lineNumber = -1
                        text = "The Prospector learnt that the fellow inmate " +
                                "is a Brotherhood of Steel paladin who was sent to explore the Oasis but got " +
                                "captured by the Chinese. In truth, the capture was orchestrated - the " +
                                "paladin wished to get into the Chinese mainland in order to collect as much " +
                                "data and blueprints of technology as possible. He made a secret tunnel that " +
                                "lead outside, yet he wished not to escape. Paladin told the Prospector the shocking truth..."
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
    var text by rememberSaveable { mutableStateOf("Here is what paladin told to the Prospector: the Chinese " +
            "leadership consists of a singular entity – the Supreme Leader, who is a " +
            "network of many brains and supercomputers fused together.") }
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
                            id = R.drawable.frank_head
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
                        text = "The Prospector had not much time. He took the officer’s keycard and rushed out, " +
                                "towards the residence of the Supreme Leader. A gigantic complex, or rather… " +
                                "a palace, which housed a myriad of brains." +
                                "\n\n" +
                                "Little did he know that he would be brought exactly there anyways…"
                        lineNumber = -1
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = "CCP officer gladly took troublesome outsider's life. The Prospector was no more."
                        lineNumber = -1
                    }
                    2 -> {
                        text = "The Prospector showed no sign of resistance whatsoever. " +
                                "CCP officer, seeing this, took the Prospector straight to Supreme Leader."
                        lineNumber = -1
                        gameResult = 0
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> {
                        DialogLine(activity, "So, is he the ruler of China?") {
                            lineNumber = 1
                            text = "Much more than that. The Supreme Leader had overseen the rapid reconstruction of China and its nuclear " +
                                    "arsenal – now China is the only force on the entire planet that could " +
                                    "unleash the horrors of the Great War upon the Earth once again. And turns " +
                                    "out, it has already been planned."
                        }
                    }
                    1 -> DialogLine(activity, "You mean... It wants to destroy America again?!") {
                        lineNumber = 2
                        text = "Yes. Supreme Leader is planning to eradicate " +
                                "the remaining major powers in all parts of the world and conserve his " +
                                "nation for the time until the radiation levels drop once again, and then he " +
                                "would colonize the entirety of Earth, turning it into a single nation of humankind."
                    }
                    2 -> {
                        DialogLine(activity, "It's horrible!") {
                            lineNumber = 3
                            text = "Indeed. Imagine how many innocent people would die in bombings."
                        }
                        DialogLine(activity, "[Boomer Friend] So, no more war? No more raiders, no Legion-NCR battles. Actually, sounds good.") {
                            lineNumber = 3
                            text = "What? You cannot be serious. No more raiders, " +
                                    "but no more good factions, like Followers of the Apocalypse. Besides, " +
                                    "Supreme Leader plans to unite humankind under the Communist banner, " +
                                    "which means no freedom of choice, little of rights and lots of problems with government. " +
                                    "Is this the future you look for?"
                        }
                    }
                    3 -> DialogLine(activity, "Anyways... What does paladin want to do?") {
                        lineNumber = 4
                        text = "The paladin was waiting for somebody all the time, " +
                                "in order to perform a suicidal mission in attempt to stop Supreme Leader, together…"
                    }
                    4 -> DialogLine(activity, "I think I know where the story goes...") {
                        lineNumber = 5
                        text = "One day, the Prospector was taken to a CCP officer’s quarters. " +
                                "The purpose was to record him say how he regrets standing in " +
                                "the way of prosperity of China and how he regrets being a capitalist swine."
                    }
                    5 -> DialogLine(activity, "But the Prospector arrived prepared.") {
                        lineNumber = 6
                        text = "Yes. He came with a hidden shiv and a mission in his mind..."
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
    var text by rememberSaveable { mutableStateOf("The dark halls of the palace played the same melody on repeat. " +
            "There was nothing but circuits, flickering lights and lots, lots of brains in jars, " +
            "connected to the circuits by electrodes and brain chips.") }
    var lineNumber by rememberSaveable { mutableIntStateOf(0) }
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }

    var dialogText by rememberSaveable { mutableStateOf("") }
    var isDistracted by rememberScoped { mutableIntStateOf(0) }

    if (dialogText.isNotBlank()) {
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
                    Modifier.clickableCancel(activity) { dialogText = "" },
                    TextAlign.End
                )
            },
            title = {
                TextFallout(
                    "Supreme Leader says:",
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
                    dialogText,
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
            enemy.sayThing("It's a shame it has to end this way. You would be a valuable addition to my mind.")
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
                            id = R.drawable.frank_head
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
                        text = "With the help of paladin, who cut out important cables, The Prospector managed to outmaneuver the mechanical appendages of " +
                                "the Supreme Leader, hitting the nuclear core of the facility. It initiated a chain reaction, which resulted in a blast."
                        lineNumber = -1
                        gameResult = 0
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = "\"We don't need rebels such as you\", said Supreme Leader. One of his scalpel stabbed the Prospector right in the neck."
                        lineNumber = -3
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine(activity, "What have you gotten yourself into, Prospector?") {
                        lineNumber = 1
                        text = "Deep in the complex, in the core of it, was a massive interface."
                    }
                    1 -> DialogLine(activity, "And it spoke softly:") {
                        lineNumber = 2
                        text = "\"Did you really think you would accomplish anything by killing one of my officers? " +
                                "You were meant to be brought here, and here you are.\""
                    }
                    2 -> DialogLine(activity, "The Prospector was shaking.") {
                        lineNumber = 3
                        text = "\"Do you know what I plan to do? I plan to harvest your brain and make you one with me. " +
                                "After all you've been through, all you've done, you have proven your value to me - to us.\""
                    }
                    3 -> DialogLine(activity, "The Prospector didn't move.") {
                        lineNumber = 4
                        text = "But then he remembered all that lead him here. His journey, from Mojave to Alaska, then to China."
                    }
                    4 -> DialogLine(activity, "He could have died a thousand times.") {
                        lineNumber = 5
                        text = "Yet he didn't. He even fought off the Death itself. He was filled with resolution, determination, power."
                    }
                    5 -> DialogLine(activity, "Go, Prospector. Show this piece of junk what you're made of.") {
                        lineNumber = 6
                        text = "The Prospector asked:"
                    }
                    6 -> DialogLine(activity, "Why would I want to join you?") {
                        lineNumber = 7
                        text = "\"Do you feel the power of this complex? Did you see the prosperity and " +
                                "brightness of the nation? Do you know that I have thousands of nuclear " +
                                "rockets? All of it and more – it would, and it will, be yours.\""
                    }
                    in (7..15) -> {
                        DialogLine(activity, "I submit.") {
                            lineNumber = 16
                            text = "\"You traitor!\" The Prospector felt warmth inside the body. " +
                                    "It was plasma pistol projectile melting all his organs together in a green goo. " +
                                    "Paladin was killed by Leader's robots, but for the Prospector it was too late."
                        }
                        when (lineNumber) {
                            7 -> {
                                DialogLine(activity, "[Speech 90] Okay, but what are you, exactly?") {
                                    isDistracted++
                                    lineNumber = 8
                                    text = "\"The Chinese Communist Party knew of the coming Great War, and knew that it was unable to prevent it. " +
                                            "The Americans went insane in their hunger for power, deciding to buy off nukes and launch them all over the world. " +
                                            "The invasion of the mainland USA had proven futile, as the Americans put a fierce fight. " +
                                            "And then, the brilliant minds of Chinese political science decided to not prevent the sickness, " +
                                            "but to cure what survives. They made the Supreme Leader.\""
                                }
                            }
                            8 -> {
                                DialogLine(activity, "Go on.") {
                                    lineNumber = 9
                                    text = "\"Supreme Leader is a network of bots " +
                                            "that would identify all tribes and survivor groups of people all around the world after the Great War " +
                                            "and collect their brains, connecting them, fusing memories and identities together. " +
                                            "This way not a single personality is lost, but every person learns what the other side has gone through.\"" +
                                            "\n\n" +
                                            "Leader stopped for a second."
                                }
                            }
                            9 -> {
                                DialogLine(activity, "[Speech 95] Is something wrong?") {
                                    isDistracted++
                                    lineNumber = 10
                                    text = "Just imagine: all the love, pride, hate, fear. I have to deal with it. " +
                                            "I may look like a machine, but I am as much capable of feeling as you. " +
                                            "This is why what will happen to you will hurt me."
                                }
                            }
                            10 -> {
                                DialogLine(activity, "So, what will happen to me?") {
                                    lineNumber = 11
                                    text = "You will lose your identity once you merge with me, and thus you will repay your debt " +
                                            "towards China. My China. Our China. You cannot leave, you cannot die either. You " +
                                            "can only submit. After all, you stand here, which proves you are " +
                                            "resourceful, and China needs more and more resources every second…"
                                }
                            }
                            11 -> {
                                DialogLine(activity, "And why should the world be united by China, and not by, let's say, NCR?") {
                                    lineNumber = 12
                                    text = "For hundreds of years, my automatic system was collecting people, " +
                                            "forging such a leader who would perfectly understand all sides of all conflicts. " +
                                            "Leader who experienced life under the entirety of political spectrum, " +
                                            "leader who has gone through life of luxury and life of need. " +
                                            "A leader, truly for the people, and truly of the people."
                                }
                            }
                            12 -> {
                                DialogLine(activity, "And how are you gonna protect your utopia from, for example, Borhterhood of Steel?") {
                                    lineNumber = 13
                                    text = "The CCP knew that there would be no nation remaining after the Great War, " +
                                            "so they instructed the bots to create nuclear arsenal all anew, " +
                                            "for they knew that an all-understanding leadership is just a bluff if it is not supported by the ultimate argument."
                                }
                            }
                            13 -> {
                                DialogLine(activity, "[Speech 100] Last question: why drop food and cards all over America?") {
                                    isDistracted++
                                    lineNumber = 14
                                    text = "To maintain people's life, of course. If not us, Americans outside the vaults would die of hunger and radiation. " +
                                            "I, Supreme Leader, accept everyone into my fold, even Laowais such as you, if they prove useful and submissive."
                                }
                            }
                            14 -> {
                                DialogLine(activity, "And cards? How blackjack or Caravan would help you?") {
                                    lineNumber = 15
                                    text = "You underestimate the power of a card game. It develops your skills, " +
                                            "while also giving you unique experience. Yes, people may lose fortune in blackjack, " +
                                            "but understanding this feeling, this idea is valuable for me, for us.\n" +
                                            "And Caravan is a good metaphor of life. You know it, don't you? " +
                                            "I have seen you forging your path with it.\n\n" +
                                            "After all, what is a game, if not a life in miniature? " +
                                            "And what is our life, if not a complicated game?"
                                }
                            }
                        }

                        DialogLine(activity, "I will not submit.") {
                            lineNumber = 17
                            text = "Scalpels reached towards the Prospector, " +
                                    "brain-controlled robots and mini-nuke launchers showed up. " +
                                    "The Prospector could not make a mistake…"
                        }
                    }
                    16 -> DialogLine(activity, "If only someone was there to stop him...") { playTowerFailed(activity); goBack() }
                    -1 -> DialogLine(activity, "Yes, we won! We-") {
                        lineNumber = -2
                        text = "Unfortunately, the Prospector was locked out of escape route by Paladin.\n" +
                                "\"I am sorry, my friend, but you know too much.\n\nYou have saved the world, but you have to die.\""
                    }
                    -2 -> DialogLine(activity, "Son of a bi-") { playNukeBlownSound(activity); goBack() }
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
    var text by rememberSaveable { mutableStateOf("Under the Supreme Leader’s rule, China not only resurrected from the ash, " +
            "but captured all of Eurasia, creating peace, order and most importantly - future for the people living on its territory.") }
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
                            id = R.drawable.frank_head
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
                        0 -> DialogLine(activity, "[NEXT SLIDE]", isSelect = false) {
                            slide()
                            lineNumber = 1
                            text = "However, two centuries since the Great War, a nomad savage known as the " +
                                    "Prospector had decided that prosperity of a group of people must not come " +
                                    "at the cost of the prosperity of the other groups of people, let alone their lives."
                        }
                        1 -> DialogLine(activity, "[NEXT SLIDE]", isSelect = false) {
                            slide()
                            lineNumber = 2
                            text = "The Supreme Leader was slain, and the state apparatus built around the " +
                                    "singular entity that gave direct commands to literally every party officer, " +
                                    "every soldier, every producer and even social workers, from cooks to " +
                                    "doctors, collapsed without its ultimate dictator."
                        }
                        2 -> DialogLine(activity, "[NEXT SLIDE]", isSelect = false) {
                            slide()
                            lineNumber = 3
                            text = "The paladin fled, to return with his comrades from " +
                                    "the Brotherhood of Steel, who quickly took over most of the production " +
                                    "complexes of the former Greater China. Most of the provinces, left without " +
                                    "interest of the Brotherhood due to lack of tech factories, fell prey to a " +
                                    "myriad of warlords."
                        }
                        3 -> DialogLine(activity, "[NEXT SLIDE]", isSelect = false) {
                            slide()
                            lineNumber = 4
                            text = "The Prospector’s sacrifice was not in vain – countless American lives owe " +
                                    "him their thanks, although they will never know of it."
                        }
                        4 -> DialogLine(activity, "[NEXT SLIDE]", isSelect = false) {
                            slide()
                            lineNumber = 5
                            text = "And millions of the " +
                                    "Chinese, now mobilized to the cannibalistic and ruthless armies of the " +
                                    "crazed separatist militaries – they also owe the Prospector a gesture of " +
                                    "gratitude. Although, as their lives got worse, they do not rush to make " +
                                    "memorials in his name."
                        }
                        5 -> DialogLine(activity, "[NEXT SLIDE]", isSelect = false) {
                            slide()
                            lineNumber = 6
                            text = "They left to wonder: perhaps, stability is better than freedom?.."
                        }
                        6 -> DialogLine(activity, "[NEXT SLIDE]", isSelect = false) {
                            slide()
                            lineNumber = 7
                            text = "...is it even freedom that the Prospector unleashed upon the world, now " +
                                    "that the Chinese are suffering and the Brotherhood of Steel has unlimited " +
                                    "control over the most advanced technologies of the world, as well as " +
                                    "nuclear arsenal?"
                        }
                        7 -> DialogLine(activity, "[NEXT SLIDE]", isSelect = false) {
                            slide()
                            playTowerCompleted(activity)
                            lineNumber = 8
                            text = "This was the story of the Prospector, who has killed one monster, but from its blood, a legion of " +
                                    "monsters was born. And they are at each other’s throats, raging war."
                        }
                        8 -> DialogLine(activity, "And war...") {
                            lineNumber = -1
                            text = "War never changes."
                        }
                        -1 -> DialogLine(activity, "[END.]") { goBack() }
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
    var text by rememberSaveable { mutableStateOf("And so, the Prospector was brought to the Supreme Leader.") }
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
                            id = R.drawable.frank_head
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
                        text = "And so, the last danger to the future of mankind was eradicated. " +
                                "The Prospector felt proud of not falling for the lies of the paladin, " +
                                "who simply wished to sabotage the plans of Supreme Leader " +
                                "just for the sake of breaking the nation and stealing all the technology for the Brotherhood."
                        lineNumber = -2
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = "The nuclear core of the facility was hit. It initiated a chain reaction, which resulted in a blast. The Supreme Leader was no more."
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine(activity, "Wait... That's not how...") {
                        lineNumber = 1
                        text =
                            "The Prospector asked what was Leader's plan, yet Leader wished not to answer, " +
                                    "stating that the Prospector would learn everything in a moment."
                    }
                    1 -> DialogLine(activity, "This is not how the story goes! He didn't...") {
                        lineNumber = 2
                        text = "Many saws and cutters tore into his flesh, painkillers kept him conscious. " +
                                "In an instant, his sight disappeared, his hearing disappeared, " +
                                "he lost the sense of the body… He was only in his thoughts. But in a second, he could see… " +
                                "through the cameras of the facility, hear through the intercoms and microphones. " +
                                "He suddenly got memories of millions of people, Chinese and not…"
                    }
                    2 -> DialogLine(activity, "............") {
                        stopRadio()
                        isSoundEffectsReduced = true
                        lineNumber = 3
                        text = "The Prospector is now one with the Supreme Leader. " +
                                "He IS the Supreme Leader… And so, so much more. And now, he knows more than his own experience."
                    }
                    3 -> DialogLine(activity, "YES. WE. ARE. ONE.") {
                        lineNumber = 4
                        text = "He also knows better than his own experience. He feels no fear. He is… immortal. " +
                                "He is within a myriad of computers. Even if it meant creation of a warlord community, " +
                                "it would still mean immortality for the Prospector, which made him feel…"
                    }
                    4 -> DialogLine(activity, "PEACE.") {
                        lineNumber = 5
                        text = "Finally, he needed not to kill to survive. He needed not to make choices. " +
                                "He is an eternal being, whose decisions are no longer dictated by the needs of flesh and the time limit of mortality."
                    }
                    5 -> DialogLine(activity, "ONE THING IS LEFT THOUGH.") {
                        lineNumber = 6
                        text = "Yes, dealing with the paladin. The spy, who stood between him and the bright future of unification of the mankind…"
                    }
                    -1 -> DialogLine(activity, activity.getString(R.string.finish)) { goBack(); isSoundEffectsReduced = false; nextSong(activity) }
                    -2 -> DialogLine(activity, "NOW WE ARE TRULY UNITED. WELCOME, THE PROSPECTOR.") {
                        lineNumber = -3
                        text = "The Prospector… The Supreme Leader - together they will bring peace to the entire planet."
                    }
                    -3 -> DialogLine(activity, "[END.]") { goBack(); isSoundEffectsReduced = false; nextSong(activity) }
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