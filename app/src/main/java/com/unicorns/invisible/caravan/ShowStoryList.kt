package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyStory1
import com.unicorns.invisible.caravan.model.enemy.EnemyStory2
import com.unicorns.invisible.caravan.model.enemy.EnemyStory3
import com.unicorns.invisible.caravan.model.enemy.EnemyStory4
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.story.DialogEdge
import com.unicorns.invisible.caravan.story.DialogGraph
import com.unicorns.invisible.caravan.story.DialogState
import com.unicorns.invisible.caravan.story.StoryShow
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextClassic
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.nextSong
import com.unicorns.invisible.caravan.utils.playHeartbeatSound
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playNukeBlownSound
import com.unicorns.invisible.caravan.utils.playTowerFailed
import com.unicorns.invisible.caravan.utils.playWWSound
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.stopRadio
import kotlinx.coroutines.delay


@Composable
fun ShowStoryList(
    activity: MainActivity,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit
) {
    var showChapter by rememberScoped { mutableStateOf<Int?>(null) }

    if (showChapter != null) {
        when (showChapter) {
            0 -> {
                ShowStoryChapter1(activity, getDeck(showChapter!!), showAlertDialog, {
                    save.storyProgress = maxOf(save.storyProgress, 10)
                    saveData(activity)
                    activity.achievementsClient?.unlock(activity.getString(R.string.achievement_madre_roja))
                }) { showChapter = null }
            }
            10 -> {
                ShowStoryChapter2(activity, getDeck(showChapter!!), showAlertDialog, {
                    save.storyProgress = maxOf(save.storyProgress, 20)
                    saveData(activity)
                }) { showChapter = null }
            }
            20 -> {
                ShowStoryChapter3(activity, getDeck(showChapter!!), showAlertDialog, {
                    save.storyProgress = maxOf(save.storyProgress, 30)
                    saveData(activity)
                }) { showChapter = null }
            }
            30 -> {
                LaunchedEffect(Unit) {
                    stopRadio()
                    soundReduced = true
                }
                ShowStoryChapter4(activity, getDeck(showChapter!!), showAlertDialog, {
                    save.storyProgress = maxOf(save.storyProgress, 39)
                    saveData(activity)
                    activity.achievementsClient?.unlock(activity.getString(R.string.achievement_what_do_we_say_to_the_god_of_death))
                }) { showChapter = null; soundReduced = false; nextSong(activity) }
            }
            else -> { showChapter = null }
        }
        return
    }

    MenuItemOpen(activity, stringResource(R.string.story_mode), "<-", goBack) {
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
                    Modifier,
                )
                Spacer(modifier = Modifier.height(16.dp))

                @Composable
                fun Act(number: Int) {
                    val text = when (number) {
                        0 -> stringResource(R.string.act_1_name)
                        1 -> stringResource(R.string.act_2_name)
                        2 -> stringResource(R.string.act_3_name)
                        3 -> stringResource(R.string.act_4_name)
                        4 -> stringResource(R.string.act_5_name)
                        else -> "???"
                    }
                    TextFallout(
                        text,
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        20.sp,
                        Modifier.padding(4.dp)
                    )
                }

                @Composable
                fun Chapter(number: Int, isAvailable: Boolean) {
                    val text = if (!isAvailable) {
                        "???"
                    } else when (number) {
                        0 -> stringResource(R.string.chapter_1_name)
                        10 -> stringResource(R.string.chapter_2_name)
                        20 -> stringResource(R.string.chapter_3_name)
                        30 -> stringResource(R.string.chapter_4_name)
                        39 -> stringResource(R.string.end_1_name)
                        41 -> stringResource(R.string.chapter_5_1_name)
                        42 -> stringResource(R.string.chapter_5_2_name)
                        43 -> stringResource(R.string.chapter_5_3_name)
                        49 -> stringResource(R.string.end_2_name)
                        50 -> stringResource(R.string.chapter_6_name)
                        60 -> stringResource(R.string.chapter_7_name)
                        69 -> stringResource(R.string.end_3_name)
                        70 -> stringResource(R.string.chapter_8_0_name)
                        71 -> stringResource(R.string.chapter_8_1_name)
                        72 -> stringResource(R.string.chapter_8_2_name)
                        73 -> stringResource(R.string.chapter_8_3_name)
                        79 -> stringResource(R.string.end_4_name)
                        80 -> stringResource(R.string.chapter_9_name)
                        90 -> stringResource(R.string.chapter_end_name)
                        else -> "???"
                    }
                    TextFallout(
                        text,
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        if (isAvailable) {
                            Modifier
                                .padding(4.dp)
                                .clickableSelect(activity) { showChapter = number }
                                .background(getTextBackgroundColor(activity))
                        } else {
                            Modifier
                        }
                            .padding(4.dp)
                    )
                }

                val acts = listOf(
                    listOf(0, 10, 20, 30, 39),
                    listOf(41, 42, 43, 49),
                    listOf(50, 60, 69),
                    listOf(70, 71, 72, 73, 79),
                    listOf(80, 90)
                )
                val actsRevealed = when (save.storyProgress) {
                    in (80..99) -> 5
                    in (70..79) -> 4
                    in (50..69) -> 3
                    in (40..49) -> 2
                    else -> 1
                }
                repeat(actsRevealed) {
                    Act(it)
                    for (chapter in acts[it]) {
                        Chapter(chapter, chapter <= save.storyProgress)
                    }
                }
            }
        }
    }
}


fun getDeck(chapterNumber: Int): CustomDeck {
    return when (chapterNumber) {
        0 -> {
            CustomDeck(CardBack.STANDARD, false)
        }
        10, 20 -> {
            CustomDeck(CardBack.STANDARD, false).apply {
                add(Card(Rank.ACE, Suit.SPADES, CardBack.SIERRA_MADRE, true))
                add(Card(Rank.KING, Suit.SPADES, CardBack.SIERRA_MADRE, true))
                add(Card(Rank.QUEEN, Suit.SPADES, CardBack.SIERRA_MADRE, true))
                add(Card(Rank.JACK, Suit.SPADES, CardBack.SIERRA_MADRE, true))
                add(Card(Rank.TEN, Suit.SPADES, CardBack.SIERRA_MADRE, true))
            }
        }
        30 -> {
            CustomDeck().apply {
                add(Card(Rank.ACE, Suit.SPADES, CardBack.GOMORRAH, false))
                add(Card(Rank.ACE, Suit.SPADES, CardBack.ULTRA_LUXE, false))
                add(Card(Rank.ACE, Suit.SPADES, CardBack.TOPS, false))
            }
        }
        else -> CustomDeck()
    }
}

@Composable
fun ShowStoryChapter1(
    activity: MainActivity,
    deck: CustomDeck,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }
    if (isGame) {
        StartStoryGame(
            activity,
            EnemyStory1,
            CResources(deck),
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            { gameResult = -1 },
            { isGame = false }
        )
        return
    }

    // TODO: rewrite this chapter!
    when (gameResult) {
        1 -> {
            StoryShow(activity, DialogGraph(
                states = listOf(
                    DialogState(R.drawable.ch1_5, listOf(1)),
                ),
                edges = listOf(
                    DialogEdge(0, R.string.chapter_1_on_win, 0),
                    DialogEdge(R.string.finish, 0, -1),
                )
            )) { goBack() }
            return
        }
        -1 -> {
            LaunchedEffect(Unit) { playTowerFailed(activity) }
            StoryShow(activity, DialogGraph(
                states = listOf(
                    DialogState(R.drawable.black_back, listOf(1)),
                ),
                edges = listOf(
                    DialogEdge(0, R.string.chapter_1_on_lose, 0),
                    DialogEdge(R.string.finish, 0, -1),
                )
            )) { goBack() }
            return
        }
        else -> {}
    }

    StoryShow(activity, DialogGraph(
        states = listOf(
            DialogState(R.drawable.ch1_1, listOf(1)),
            DialogState(R.drawable.ch1_2, listOf(2)),
            DialogState(R.drawable.ch1_3, listOf(3)),
            DialogState(R.drawable.ch1_4, listOf(4)),
            DialogState(R.drawable.ch1_5, listOf(5)),
        ),
        edges = listOf(
            DialogEdge(0, R.string.chapter_1_text_1, 0),
            DialogEdge(R.string.chapter_1_q_1, R.string.chapter_1_text_2, 1),
            DialogEdge(R.string.chapter_1_q_2, R.string.chapter_1_text_3, 2),
            DialogEdge(R.string.chapter_1_q_3, R.string.chapter_1_text_4, 3),
            DialogEdge(R.string.chapter_1_q_4, R.string.chapter_1_text_5, 4),
            DialogEdge(R.string.finish, 0, -1),
        )
    )) {
        isGame = true
    }
}

@Composable
fun ShowStoryChapter2(
    activity: MainActivity,
    deck: CustomDeck,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var isGame by rememberSaveable { mutableStateOf(true) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }
    if (isGame) {
        StartStoryGame(
            activity,
            EnemyStory2,
            CResources(deck),
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            { gameResult = -1 },
            { isGame = false }
        )
        return
    }

    goBack()
    // TODO: write this chapter!
}

@Composable
fun ShowStoryChapter3(
    activity: MainActivity,
    deck: CustomDeck,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var isGame by rememberSaveable { mutableStateOf(true) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }
    if (isGame) {
        StartStoryGame(
            activity,
            EnemyStory3(),
            CResources(deck),
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            { gameResult = -1 },
            { isGame = false }
        )
        return
    }

    goBack()
    // TODO: write this chapter!
}

@Composable
fun ShowStoryChapter4(
    activity: MainActivity,
    deck: CustomDeck,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
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
                    18.sp,
                    Modifier
                        .background(Color(activity.getColor(R.color.colorText)))
                        .clickableCancel(activity) { messageNumber = -1 }
                        .padding(4.dp)
                )
            },
            title = {
                TextClassic(
                    stringResource(R.string.ch4_mh),
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    24.sp, Modifier, textAlignment = TextAlign.Start
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
                    16.sp, Modifier, textAlignment = TextAlign.Start
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
            CResources(deck),
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            { gameResult = -1 },
            { isGame = false }
        )
        return
    }

    when (gameResult) {
        1 -> {
            StoryShow(activity, DialogGraph(
                states = listOf(
                    DialogState(R.drawable.ch4_1, listOf(1)),
                ),
                edges = listOf(
                    DialogEdge(0, R.string.ch4_w1, 0),
                    DialogEdge(R.string.finish, 0, -1),
                )
            )) { goBack() }
            return
        }
        -1 -> {
            LaunchedEffect(Unit) { playTowerFailed(activity) }
            StoryShow(activity, DialogGraph(
                states = listOf(
                    DialogState(R.drawable.black_back, listOf(1)),
                ),
                edges = listOf(
                    DialogEdge(0, R.string.ch4_l1, 0),
                    DialogEdge(R.string.finish, 0, -1),
                )
            )) { goBack() }
            return
        }
        else -> {}
    }

    StoryShow(activity, DialogGraph(
        states = listOf(
            DialogState(R.drawable.black_back, listOf(1)),
            DialogState(R.drawable.black_back, listOf(2)),
        ),
        edges = listOf(
            DialogEdge(0, R.string.c4_t1, 0),
            DialogEdge(R.string.ch4_q1, R.string.ch4_t2, 1),
            DialogEdge(R.string.finish, 0, -1)
        )
    )) {
        isGame = true
    }
}


@Composable
fun StartStoryGame(
    activity: MainActivity,
    enemy: Enemy,
    playerCResources: CResources,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    onMove: (Card?) -> Unit = {},
    onWin: () -> Unit,
    onLose: () -> Unit,
    goBack: () -> Unit,
) {
    val game = rememberScoped {
        Game(
            playerCResources,
            enemy
        ).also {
            it.startGame()
        }
    }

    game.also {
        it.onWin = {
            if (enemy is EnemyStory3) {
                val isPoisonedInEveryCaravan = game.playerCaravans.all { caravan ->
                    val cards = caravan.cards.map { it.card }
                    cards.any { it.back == CardBack.MADNESS && it.isAlt }
                }
                if (isPoisonedInEveryCaravan) {
                    activity.achievementsClient?.unlock(activity.getString(R.string.achievement_float_like_a_butterfly___))
                }
            }

            activity.processChallengesGameOver(it)
            playWinSound(activity)
            onWin()
            showAlertDialog(
                activity.getString(R.string.result),
                activity.getString(R.string.you_win),
                goBack
            )
        }
        it.onLose = {
            playLoseSound(activity)
            onLose()
            showAlertDialog(
                activity.getString(R.string.result),
                activity.getString(R.string.you_lose),
                goBack
            )
        }
        it.jokerPlayedSound = { playJokerSounds(activity) }
        it.wildWastelandSound = { playWWSound(activity) }
        it.nukeBlownSound = { playNukeBlownSound(activity) }
    }

    ShowGame(activity, game, onMove = onMove) {
        if (game.isOver()) {
            goBack()
            return@ShowGame
        }
        showAlertDialog(
            activity.getString(R.string.check_back_to_menu),
            activity.getString(R.string.this_game_will_be_counted_as_lost),
            goBack
        )
    }
}