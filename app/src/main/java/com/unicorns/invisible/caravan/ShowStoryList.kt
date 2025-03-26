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
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playNukeBlownSound
import com.unicorns.invisible.caravan.utils.playWWSound
import com.unicorns.invisible.caravan.utils.playWinSound
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
                ShowIntro(activity, {
                    save.storyProgress = maxOf(save.storyProgress, 11)
                    saveData(activity)
                }) { showChapter = null }
            }
            11 -> {
                ShowStoryChapter1(activity, getDeck(showChapter!!), showAlertDialog, {
                    save.storyProgress = maxOf(save.storyProgress, 12)
                    saveData(activity)
                    activity.achievementsClient?.unlock(activity.getString(R.string.achievement_madre_roja))
                }) { showChapter = null }
            }
            12 -> {
                ShowStoryChapter2(activity, getDeck(showChapter!!), showAlertDialog, {
                    save.storyProgress = maxOf(save.storyProgress, 13)
                    saveData(activity)
                }) { showChapter = null }
            }
            13 -> {
                LaunchedEffect(Unit) { stopRadio(); soundReduced = true }
                ShowStoryChapter3(activity, getDeck(showChapter!!), showAlertDialog, {
                    save.storyProgress = maxOf(save.storyProgress, 19)
                    saveData(activity)
                }) { soundReduced = false; nextSong(activity); showChapter = null }
            }
            else -> { showChapter = null }
        }
        return
    }

    MenuItemOpen(activity, stringResource(R.string.story_mode), "<-", goBack) {
        Column(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            TextFallout(
                stringResource(R.string.select_the_chapter),
                getTextColor(activity),
                getTextStrokeColor(activity),
                22.sp,
                Modifier,
            )

            @Composable
            fun Intro() {
                TextFallout(
                    stringResource(R.string.intro),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    16.sp,
                    Modifier
                        .padding(4.dp)
                        .clickableSelect(activity) { showChapter = 0 }
                        .background(getTextBackgroundColor(activity))
                        .padding(4.dp)
                )
            }

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
                    11 -> stringResource(R.string.chapter_1_name)
                    12 -> stringResource(R.string.chapter_2_name)
                    13 -> stringResource(R.string.chapter_3_name)
                    19 -> stringResource(R.string.end_1_name)
                    24 -> stringResource(R.string.chapter_4_name)
                    25 -> stringResource(R.string.chapter_5_name)
                    26 -> stringResource(R.string.chapter_6_name)
                    29 -> stringResource(R.string.end_2_name)
                    37 -> stringResource(R.string.chapter_7_name)
                    38 -> stringResource(R.string.chapter_8_name)
                    39 -> stringResource(R.string.end_3_name)
                    409 -> stringResource(R.string.chapter_9_name)
                    410 -> stringResource(R.string.chapter_10_name)
                    411 -> stringResource(R.string.chapter_11_name)
                    412-> stringResource(R.string.chapter_12_name)
                    499 -> stringResource(R.string.end_4_name)
                    513 -> stringResource(R.string.chapter_13_name)
                    999 -> stringResource(R.string.chapter_end_name)
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
    activity: MainActivity,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    StoryShow(activity, DialogGraph(
        listOf(
            DialogMiddleState(R.drawable.black_back, R.string.intro_0, intro = PicEffect.NONE, outro = PicEffect.NONE),
            DialogMiddleState(R.drawable.intro_1, R.string.intro_1, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(R.drawable.intro_2, R.string.intro_2, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(R.drawable.intro_3, R.string.intro_3, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(R.drawable.intro_4, R.string.intro_4, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(R.drawable.intro_5, R.string.intro_5, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(R.drawable.intro_6, R.string.intro_6, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(R.drawable.intro_7, R.string.intro_7, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(R.drawable.intro_8, R.string.intro_8, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(R.drawable.intro_9, R.string.intro_9, intro = PicEffect.SLIDE, outro = PicEffect.SLIDE),
            DialogMiddleState(R.drawable.black_back, R.string.intro_10, intro = PicEffect.NONE, outro = PicEffect.SELECT),
            DialogFinishState(DeathCode.ALIVE)
        ),
        listOf(
            DialogEdge(0, 1, R.string.next_slide),
            DialogEdge(1, 2, R.string.next_slide),
            DialogEdge(2, 3, R.string.next_slide),
            DialogEdge(3, 4, R.string.next_slide),
            DialogEdge(4, 5, R.string.next_slide),
            DialogEdge(5, 6, R.string.next_slide),
            DialogEdge(6, 7, R.string.next_slide),
            DialogEdge(7, 8, R.string.next_slide),
            DialogEdge(8, 9, R.string.next_slide),
            DialogEdge(9, 10, R.string.next_slide),
            DialogEdge(10, 11, R.string.finish)
        )
    )) {
        advanceChapter()
        goBack()
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
            { goBack() }
        )
        return
    }

    LaunchedEffect(Unit) { isGame = true }
}

@Composable
fun ShowStoryChapter2(
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
            StoryShow(activity, DialogGraph(
                states = listOf(DialogFinishState(DeathCode.ALIVE)),
                edges = emptyList(),
            )) { goBack() }
            return
        }
        -1 -> {
            StoryShow(activity, DialogGraph(
                states = listOf(DialogFinishState(DeathCode.AGAINST_DEATH)),
                edges = emptyList(),
            )) { goBack() }
            return
        }
        else -> {}
    }

    StoryShow(activity, DialogGraph(
        listOf(
            DialogMiddleState(R.drawable.black_back, R.string.c4_t1),
            DialogMiddleState(R.drawable.black_back, R.string.ch4_t2),
            DialogFinishState(DeathCode.ALIVE)
        ),
        listOf(
            DialogEdge(0, 1, R.string.ch4_q1),
            DialogEdge(1, 2, R.string.finish),
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