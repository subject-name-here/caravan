package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.act_1_name
import caravan.composeapp.generated.resources.act_2_name
import caravan.composeapp.generated.resources.act_3_name
import caravan.composeapp.generated.resources.act_4_name
import caravan.composeapp.generated.resources.act_5_name
import caravan.composeapp.generated.resources.black_back
import caravan.composeapp.generated.resources.c4_t1
import caravan.composeapp.generated.resources.ch1_1
import caravan.composeapp.generated.resources.ch1_10
import caravan.composeapp.generated.resources.ch1_10a
import caravan.composeapp.generated.resources.ch1_11
import caravan.composeapp.generated.resources.ch1_11a
import caravan.composeapp.generated.resources.ch1_12
import caravan.composeapp.generated.resources.ch1_12a
import caravan.composeapp.generated.resources.ch1_13
import caravan.composeapp.generated.resources.ch1_1a
import caravan.composeapp.generated.resources.ch1_1b
import caravan.composeapp.generated.resources.ch1_2
import caravan.composeapp.generated.resources.ch1_2a
import caravan.composeapp.generated.resources.ch1_2b
import caravan.composeapp.generated.resources.ch1_3
import caravan.composeapp.generated.resources.ch1_4
import caravan.composeapp.generated.resources.ch1_4a
import caravan.composeapp.generated.resources.ch1_4b
import caravan.composeapp.generated.resources.ch1_5
import caravan.composeapp.generated.resources.ch1_6
import caravan.composeapp.generated.resources.ch1_6a
import caravan.composeapp.generated.resources.ch1_6b
import caravan.composeapp.generated.resources.ch1_6c
import caravan.composeapp.generated.resources.ch1_7
import caravan.composeapp.generated.resources.ch1_8
import caravan.composeapp.generated.resources.ch1_9
import caravan.composeapp.generated.resources.ch4_m1
import caravan.composeapp.generated.resources.ch4_m2
import caravan.composeapp.generated.resources.ch4_m3
import caravan.composeapp.generated.resources.ch4_m4
import caravan.composeapp.generated.resources.ch4_mh
import caravan.composeapp.generated.resources.ch4_q1
import caravan.composeapp.generated.resources.ch4_r1
import caravan.composeapp.generated.resources.ch4_r2
import caravan.composeapp.generated.resources.ch4_r3
import caravan.composeapp.generated.resources.ch4_r4
import caravan.composeapp.generated.resources.ch4_t2
import caravan.composeapp.generated.resources.chapter_10_name
import caravan.composeapp.generated.resources.chapter_11_name
import caravan.composeapp.generated.resources.chapter_12_name
import caravan.composeapp.generated.resources.chapter_13_name
import caravan.composeapp.generated.resources.chapter_1_name
import caravan.composeapp.generated.resources.chapter_2_name
import caravan.composeapp.generated.resources.chapter_3_name
import caravan.composeapp.generated.resources.chapter_4_name
import caravan.composeapp.generated.resources.chapter_5_name
import caravan.composeapp.generated.resources.chapter_6_name
import caravan.composeapp.generated.resources.chapter_7_name
import caravan.composeapp.generated.resources.chapter_8_name
import caravan.composeapp.generated.resources.chapter_9_name
import caravan.composeapp.generated.resources.chapter_end_name
import caravan.composeapp.generated.resources.check_back_to_menu
import caravan.composeapp.generated.resources.end_1_name
import caravan.composeapp.generated.resources.end_2_name
import caravan.composeapp.generated.resources.end_3_name
import caravan.composeapp.generated.resources.end_4_name
import caravan.composeapp.generated.resources.finish
import caravan.composeapp.generated.resources.intro
import caravan.composeapp.generated.resources.intro_0
import caravan.composeapp.generated.resources.intro_1
import caravan.composeapp.generated.resources.intro_10
import caravan.composeapp.generated.resources.intro_2
import caravan.composeapp.generated.resources.intro_3
import caravan.composeapp.generated.resources.intro_4
import caravan.composeapp.generated.resources.intro_5
import caravan.composeapp.generated.resources.intro_6
import caravan.composeapp.generated.resources.intro_7
import caravan.composeapp.generated.resources.intro_8
import caravan.composeapp.generated.resources.intro_9
import caravan.composeapp.generated.resources.next_slide
import caravan.composeapp.generated.resources.result
import caravan.composeapp.generated.resources.select_the_chapter
import caravan.composeapp.generated.resources.story_mode
import caravan.composeapp.generated.resources.this_game_will_be_counted_as_lost
import caravan.composeapp.generated.resources.you_lose
import caravan.composeapp.generated.resources.you_win
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.color.Colors
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyStory1
import com.unicorns.invisible.caravan.model.enemy.EnemyStory2
import com.unicorns.invisible.caravan.model.enemy.EnemyStory3
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.story.DeathCode
import com.unicorns.invisible.caravan.story.DialogEdge
import com.unicorns.invisible.caravan.story.DialogFinishState
import com.unicorns.invisible.caravan.story.DialogGraph
import com.unicorns.invisible.caravan.story.DialogMiddleState
import com.unicorns.invisible.caravan.story.PicEffect
import com.unicorns.invisible.caravan.story.StoryShow
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextClassic
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.nextSong
import com.unicorns.invisible.caravan.utils.playHeartbeatSound
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.stopRadio
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource


@Composable
fun ShowStoryList(
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit
) {
    var showChapter by rememberScoped { mutableStateOf<Int?>(null) }

    if (showChapter != null) {
        when (showChapter) {
            0 -> {
                ShowIntro({
                    save.storyProgress = maxOf(save.storyProgress, 11)
                    saveData()
                }) { showChapter = null }
            }
            11 -> {
                ShowStoryChapter1(getDeck(showChapter!!), showAlertDialog, {
                    save.storyProgress = maxOf(save.storyProgress, 12)
                    saveData()
                }) { showChapter = null }
            }
            12 -> {
                ShowStoryChapter2(getDeck(showChapter!!), showAlertDialog, {
                    save.storyProgress = maxOf(save.storyProgress, 13)
                    saveData()
                }) { showChapter = null }
            }
            13 -> {
                LaunchedEffect(Unit) { stopRadio(); soundReduced = true }
                ShowStoryChapter3(getDeck(showChapter!!), showAlertDialog, {
                    save.storyProgress = maxOf(save.storyProgress, 19)
                    saveData()
                }) { soundReduced = false; nextSong(); showChapter = null }
            }
            else -> { showChapter = null }
        }
        return
    }

    MenuItemOpen(stringResource(Res.string.story_mode), "<-", goBack) {
        Column(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            TextFallout(
                stringResource(Res.string.select_the_chapter),
                getTextColor(),
                getTextStrokeColor(),
                22.sp,
                Modifier,
            )

            @Composable
            fun Intro() {
                TextFallout(
                    stringResource(Res.string.intro),
                    getTextColor(),
                    getTextStrokeColor(),
                    16.sp,
                    Modifier
                        .padding(4.dp)
                        .clickableSelect { showChapter = 0 }
                        .background(getTextBackgroundColor())
                        .padding(4.dp)
                )
            }

            @Composable
            fun Act(number: Int) {
                val text = when (number) {
                    0 -> stringResource(Res.string.act_1_name)
                    1 -> stringResource(Res.string.act_2_name)
                    2 -> stringResource(Res.string.act_3_name)
                    3 -> stringResource(Res.string.act_4_name)
                    4 -> stringResource(Res.string.act_5_name)
                    else -> "???"
                }
                TextFallout(
                    text,
                    getTextColor(),
                    getTextStrokeColor(),
                    20.sp,
                    Modifier.padding(4.dp)
                )
            }

            @Composable
            fun Chapter(number: Int, isAvailable: Boolean) {
                val text = if (!isAvailable) {
                    "???"
                } else when (number) {
                    11 -> stringResource(Res.string.chapter_1_name)
                    12 -> stringResource(Res.string.chapter_2_name)
                    13 -> stringResource(Res.string.chapter_3_name)
                    19 -> stringResource(Res.string.end_1_name)
                    24 -> stringResource(Res.string.chapter_4_name)
                    25 -> stringResource(Res.string.chapter_5_name)
                    26 -> stringResource(Res.string.chapter_6_name)
                    29 -> stringResource(Res.string.end_2_name)
                    37 -> stringResource(Res.string.chapter_7_name)
                    38 -> stringResource(Res.string.chapter_8_name)
                    39 -> stringResource(Res.string.end_3_name)
                    409 -> stringResource(Res.string.chapter_9_name)
                    410 -> stringResource(Res.string.chapter_10_name)
                    411 -> stringResource(Res.string.chapter_11_name)
                    412-> stringResource(Res.string.chapter_12_name)
                    499 -> stringResource(Res.string.end_4_name)
                    513 -> stringResource(Res.string.chapter_13_name)
                    999 -> stringResource(Res.string.chapter_end_name)
                    else -> "???"
                }
                TextFallout(
                    text,
                    getTextColor(),
                    getTextStrokeColor(),
                    16.sp,
                    if (isAvailable) {
                        Modifier
                            .padding(4.dp)
                            .clickableSelect { showChapter = number }
                            .background(getTextBackgroundColor())
                    } else {
                        Modifier
                    }
                        .padding(4.dp)
                )
            }

            val acts = listOf(
                listOf(11, 12, 13, 19),
                listOf(24, 25, 26, 29),
                listOf(37, 38, 39),
                listOf(409, 410, 411, 412, 499),
                listOf(513, 999)
            )
            val actsRevealed = when (save.storyProgress) {
                in (500..999) -> 5
                in (400..499) -> 4
                in (30..39) -> 3
                in (20..29) -> 2
                in (11..19) -> 1
                else -> 0
            }
            Intro()
            repeat(actsRevealed) {
                Act(it)
                for (chapter in acts[it]) {
                    Chapter(chapter, chapter <= save.storyProgress)
                }
            }
        }
    }
}


fun getDeck(chapterNumber: Int): CustomDeck {
    return when (chapterNumber) {
        11 -> {
            CustomDeck(CardBack.STANDARD)
        }
        12 -> {
            CustomDeck(CardBack.STANDARD).apply {
                add(CardNumber(RankNumber.ACE, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.KING, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.QUEEN, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.JACK, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardNumber(RankNumber.TEN, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
            }
        }
        13 -> {
            CustomDeck().apply {
                add(CardNumber(RankNumber.ACE, Suit.SPADES, CardBack.GOMORRAH))
                add(CardNumber(RankNumber.ACE, Suit.SPADES, CardBack.ULTRA_LUXE))
                add(CardNumber(RankNumber.ACE, Suit.SPADES, CardBack.TOPS))
            }
        }
        else -> CustomDeck()
    }
}

@Composable
fun ShowIntro(
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    StoryShow(DialogGraph(
        listOf(
            DialogMiddleState(Res.drawable.black_back, Res.string.intro_0, intro = PicEffect.NONE, outro = PicEffect.NONE),
            DialogMiddleState(Res.drawable.intro_1, Res.string.intro_1, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(Res.drawable.intro_2, Res.string.intro_2, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(Res.drawable.intro_3, Res.string.intro_3, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(Res.drawable.intro_4, Res.string.intro_4, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(Res.drawable.intro_5, Res.string.intro_5, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(Res.drawable.intro_6, Res.string.intro_6, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(Res.drawable.intro_7, Res.string.intro_7, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(Res.drawable.intro_8, Res.string.intro_8, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(Res.drawable.intro_9, Res.string.intro_9, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(Res.drawable.black_back, Res.string.intro_10, intro = PicEffect.NONE, outro = PicEffect.SELECT),
            DialogFinishState(DeathCode.ALIVE)
        ),
        listOf(
            DialogEdge(0, 1, Res.string.next_slide),
            DialogEdge(1, 2, Res.string.next_slide),
            DialogEdge(2, 3, Res.string.next_slide),
            DialogEdge(3, 4, Res.string.next_slide),
            DialogEdge(4, 5, Res.string.next_slide),
            DialogEdge(5, 6, Res.string.next_slide),
            DialogEdge(6, 7, Res.string.next_slide),
            DialogEdge(7, 8, Res.string.next_slide),
            DialogEdge(8, 9, Res.string.next_slide),
            DialogEdge(9, 10, Res.string.next_slide),
            DialogEdge(10, 11, Res.string.finish)
        )
    ), goBack) {
        advanceChapter()
        goBack()
    }
}

@Composable
fun ShowStoryChapter1(
    deck: CustomDeck,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }

    if (isGame) {
        StartStoryGame(
            EnemyStory1,
            CResources(deck),
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            { gameResult = -1 },
            { if (gameResult == 0) gameResult = -1; isGame = false }
        )
        return
    }

    when (gameResult) {
        1 -> {
            StoryShow(DialogGraph(
                listOf(
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch1_10),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch1_11),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch1_12),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch1_13),
                    DialogFinishState(DeathCode.ALIVE),
                ),
                listOf(
                    DialogEdge(0, 1, Res.string.ch1_10a),
                    DialogEdge(1, 2, Res.string.ch1_11a),
                    DialogEdge(2, 3, Res.string.ch1_12a),
                    DialogEdge(3, 4, Res.string.finish),
                )
            ), goBack) { goBack() }
            return
        }
        -1 -> {
            StoryShow(DialogGraph(
                listOf(
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch1_9),
                    DialogFinishState(DeathCode.EXPLODED)
                ),
                listOf(
                    DialogEdge(0, 1, Res.string.finish),
                )
            ), goBack) { goBack() }
            return
        }
        else -> {}
    }

    StoryShow(DialogGraph(
        listOf(
            DialogMiddleState(Res.drawable.black_back, Res.string.ch1_1),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch1_2),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch1_3),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch1_4),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch1_5),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch1_6),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch1_7),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch1_8),
            DialogFinishState(DeathCode.ALIVE),
            DialogFinishState(DeathCode.EXPLODED)
        ),
        listOf(
            DialogEdge(0, 3, Res.string.ch1_1a),
            DialogEdge(0, 1, Res.string.ch1_1b),
            DialogEdge(1, 2, Res.string.ch1_2b),
            DialogEdge(2, 9, Res.string.finish),
            DialogEdge(1, 3, Res.string.ch1_2a),
            DialogEdge(3, 4, Res.string.ch1_4a),
            DialogEdge(4, 9, Res.string.finish),
            DialogEdge(3, 5, Res.string.ch1_4b),
            DialogEdge(5, 6, Res.string.ch1_6a),
            DialogEdge(5, 7, Res.string.ch1_6b),
            DialogEdge(5, 8, Res.string.ch1_6c),
            DialogEdge(6, 7, Res.string.ch1_6b),
            DialogEdge(6, 8, Res.string.ch1_6c),
            DialogEdge(7, 6, Res.string.ch1_6a),
            DialogEdge(7, 8, Res.string.ch1_6c),
        )
    ), goBack) {
        isGame = true
    }
}

@Composable
fun ShowStoryChapter2(
    deck: CustomDeck,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }
    if (isGame) {
        StartStoryGame(
            EnemyStory2(),
            CResources(deck),
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            { gameResult = -1 },
            { goBack() }
        )
        return
    }

    LaunchedEffect(Unit) { isGame = true }
}

@Composable
fun ShowStoryChapter3(
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
            playHeartbeatSound()
            when (messageNumber) {
                2, 3 -> {
                    delay(500L)
                    playHeartbeatSound()
                }
                4 -> {
                    repeat(3) {
                        delay(500L)
                        playHeartbeatSound()
                    }
                }
            }
        }
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = Colors.ColorText),
            onDismissRequest = { messageNumber = -1 },
            confirmButton = {
                TextClassic(
                    when (messageNumber) {
                        1 -> stringResource(Res.string.ch4_r1)
                        2 -> stringResource(Res.string.ch4_r2)
                        3 -> stringResource(Res.string.ch4_r3)
                        else -> stringResource(Res.string.ch4_r4)
                    },
                    Colors.ColorTextBack,
                    Colors.ColorTextBack,
                    18.sp,
                    Modifier
                        .background(Colors.ColorText)
                        .clickableCancel { messageNumber = -1 }
                        .padding(4.dp)
                )
            },
            title = {
                TextClassic(
                    stringResource(Res.string.ch4_mh),
                    Colors.ColorText,
                    Colors.ColorText,
                    24.sp, Modifier, textAlignment = TextAlign.Start
                )
            },
            text = {
                TextClassic(
                    when (messageNumber) {
                        1 -> stringResource(Res.string.ch4_m1)
                        2 -> stringResource(Res.string.ch4_m2)
                        3 -> stringResource(Res.string.ch4_m3)
                        else -> stringResource(Res.string.ch4_m4)
                    },
                    Colors.ColorText,
                    Colors.ColorText,
                    16.sp, Modifier, textAlignment = TextAlign.Start
                )
            },
            containerColor = Color.Black,
            textContentColor = Colors.ColorText,
            shape = RectangleShape,
        )
    }

    if (isGame) {
        StartStoryGame(
            EnemyStory3 { messageNumber = it },
            CResources(deck),
            showAlertDialog,
            {},
            { gameResult = 1; advanceChapter() },
            { gameResult = -1 },
            { if (gameResult == 0) gameResult = -1; isGame = false }
        )
        return
    }

    when (gameResult) {
        1 -> {
            StoryShow(DialogGraph(
                states = listOf(DialogFinishState(DeathCode.ALIVE)),
                edges = emptyList(),
            ), goBack) { goBack() }
            return
        }
        -1 -> {
            StoryShow(DialogGraph(
                states = listOf(DialogFinishState(DeathCode.AGAINST_DEATH)),
                edges = emptyList(),
            ), goBack) { goBack() }
            return
        }
        else -> {}
    }

    StoryShow(DialogGraph(
        listOf(
            DialogMiddleState(Res.drawable.black_back, Res.string.c4_t1),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_t2),
            DialogFinishState(DeathCode.ALIVE)
        ),
        listOf(
            DialogEdge(0, 1, Res.string.ch4_q1),
            DialogEdge(1, 2, Res.string.finish),
        )
    ), goBack) {
        isGame = true
    }
}


@Composable
fun StartStoryGame(
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

    val scope = rememberCoroutineScope()
    game.also {
        it.onWin = {
            processChallengesGameOver(it)
            playWinSound()
            onWin()
            scope.launch {
                showAlertDialog(
                    getString(Res.string.result),
                    getString(Res.string.you_win),
                    goBack
                )
            }
        }
        it.onLose = {
            playLoseSound()
            onLose()
            scope.launch {
                showAlertDialog(
                    getString(Res.string.result),
                    getString(Res.string.you_lose),
                    goBack
                )
            }
        }
    }

    ShowGame(game, onMove = onMove) {
        if (game.isOver()) {
            goBack()
            return@ShowGame
        }
        scope.launch {
            showAlertDialog(
                getString(Res.string.check_back_to_menu),
                getString(Res.string.this_game_will_be_counted_as_lost),
                goBack
            )
        }
    }
}