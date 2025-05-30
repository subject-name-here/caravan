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
import caravan.composeapp.generated.resources.*
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.color.Colors
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyStory1
import com.unicorns.invisible.caravan.model.enemy.EnemyStory2
import com.unicorns.invisible.caravan.model.enemy.EnemyStory3
import com.unicorns.invisible.caravan.model.enemy.EnemyStory4
import com.unicorns.invisible.caravan.model.enemy.EnemyStory5
import com.unicorns.invisible.caravan.model.enemy.EnemyStory6
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
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
import com.unicorns.invisible.caravan.utils.playWinSoundAlone
import com.unicorns.invisible.caravan.utils.stopRadio
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource


@Composable
fun ShowXpDialog(xpReward: Int, close: () -> Unit) {
    AlertDialog(
        modifier = Modifier.border(width = 4.dp, color = Colors.ColorText),
        onDismissRequest = { close() },
        confirmButton = {
            TextClassic(
                "OK",
                Colors.ColorTextBack,
                Colors.ColorTextBack,
                18.sp,
                Modifier
                    .background(Colors.ColorText)
                    .clickableCancel { close() }
                    .padding(4.dp)
            )
        },
        title = {
            TextClassic(
                "CHAPTER COMPLETED",
                Colors.ColorText,
                Colors.ColorText,
                24.sp, Modifier, textAlignment = TextAlign.Start
            )
        },
        text = {
            TextClassic(
                "You get: $xpReward XP.",
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


@Composable
fun ShowStoryList(
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit
) {
    var showChapter by rememberScoped { mutableStateOf<Int?>(null) }
    var showXpDialog by rememberScoped { mutableStateOf<Int?>(null) }

    showXpDialog?.let { ShowXpDialog(it) { showXpDialog = null } }

    if (showChapter != null) {
        when (showChapter) {
            0 -> {
                ShowIntro({
                    saveGlobal.storyProgress = maxOf(saveGlobal.storyProgress, 11)
                    saveData()
                }) { showChapter = null }
            }
            11 -> {
                ShowStoryChapter1(getDeck(showChapter!!), showAlertDialog, {
                    val progress = 12
                    val xpReward = 15
                    if (saveGlobal.storyProgress < progress) {
                        saveGlobal.storyProgress = progress
                        saveGlobal.increaseXp(xpReward)
                        showXpDialog = xpReward
                    }
                    saveData()
                }) { showChapter = null }
            }
            12 -> {
                ShowStoryChapter2(getDeck(showChapter!!), showAlertDialog, {
                    val progress = 13
                    val xpReward = 30
                    if (saveGlobal.storyProgress < progress) {
                        saveGlobal.storyProgress = progress
                        saveGlobal.increaseXp(xpReward)
                        showXpDialog = xpReward
                    }
                    saveData()
                }) { showChapter = null }
            }
            13 -> {
                LaunchedEffect(Unit) { stopRadio(); soundReduced = true }
                ShowStoryChapter3(getDeck(showChapter!!), showAlertDialog, {
                    val progress = 19
                    val xpReward = 66
                    if (saveGlobal.storyProgress < progress) {
                        saveGlobal.storyProgress = progress
                        saveGlobal.increaseXp(xpReward)
                        showXpDialog = xpReward
                    }
                    saveData()
                }) { soundReduced = false; nextSong(); showChapter = null }
            }
            19 -> {
                ShowStoryEndOfPart1({
                    val progress = 24
                    if (saveGlobal.storyProgress < progress) {
                        saveGlobal.storyProgress = progress
                    }
                    saveData()
                }) { showChapter = null }
            }
            24 -> {
                ShowStoryChapter4(getDeck(showChapter!!), showAlertDialog, {
                    val progress = 25
                    val xpReward = 50
                    if (saveGlobal.storyProgress < progress) {
                        saveGlobal.storyProgress = progress
                        saveGlobal.increaseXp(xpReward)
                        showXpDialog = xpReward
                    }
                    saveData()
                }) { showChapter = null }
            }
            25 -> {
                ShowStoryChapter5(getDeck(showChapter!!), showAlertDialog, {
                    val progress = 26
                    val xpReward = 75
                    if (saveGlobal.storyProgress < progress) {
                        saveGlobal.storyProgress = progress
                        saveGlobal.increaseXp(xpReward)
                        showXpDialog = xpReward
                    }
                    saveData()
                }) { showChapter = null }
            }
            26 -> {
                ShowStoryChapter6(getDeck(showChapter!!), showAlertDialog, {
                    val progress = 29
                    val xpReward = 100
                    if (saveGlobal.storyProgress < progress) {
                        saveGlobal.storyProgress = progress
                        saveGlobal.increaseXp(xpReward)
                        showXpDialog = xpReward
                    }
                    saveData()
                }) { showChapter = null }
            }
            29 -> {
                LaunchedEffect(Unit) { stopRadio(); soundReduced = true }
                ShowStoryEndOfPart2({
                    val progress = 37
                    if (saveGlobal.storyProgress < progress) {
                        saveGlobal.storyProgress = progress
                    }
                    saveData()
                }) { soundReduced = false; nextSong(); showChapter = null }
            }
            else -> {
                showAlertDialog("[CLOSED]", "The chapter is not done yet.", null)
                showChapter = null
            }
        }
        return
    }

    MenuItemOpen(stringResource(Res.string.story_mode), "<-", Alignment.Center, goBack) {
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
                26.sp,
                Modifier,
            )
            Spacer(Modifier.height(8.dp))

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
            val actsRevealed = when (saveGlobal.storyProgress) {
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
                    Chapter(chapter, chapter <= saveGlobal.storyProgress)
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
        24 -> {
            CustomDeck(CardBack.STANDARD_RARE).apply {
                add(CardNumber(RankNumber.ACE, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.KING, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.QUEEN, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.JACK, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardNumber(RankNumber.TEN, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.KING, Suit.HEARTS, CardBack.STANDARD_UNCOMMON))
                add(CardNumber(RankNumber.TWO, Suit.HEARTS, CardBack.STANDARD))
            }
        }
        25 -> {
            CustomDeck(CardBack.STANDARD_RARE).apply {
                add(CardNumber(RankNumber.ACE, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.KING, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.QUEEN, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.JACK, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardNumber(RankNumber.TEN, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.KING, Suit.HEARTS, CardBack.STANDARD_UNCOMMON))
                add(CardNumber(RankNumber.TWO, Suit.HEARTS, CardBack.STANDARD))
                RankNumber.entries.forEach { rank ->
                    if (rank.value > 4) {
                        Suit.entries.forEach { suit ->
                            add(CardNumber(rank, suit, CardBack.MADNESS))
                        }
                    }
                }
            }
        }
        26 -> {
            CustomDeck(CardBack.STANDARD_RARE).apply {
                add(CardNumber(RankNumber.ACE, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.KING, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.QUEEN, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.JACK, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardNumber(RankNumber.TEN, Suit.SPADES, CardBack.SIERRA_MADRE_CLEAN))
                add(CardFaceSuited(RankFace.KING, Suit.HEARTS, CardBack.STANDARD_UNCOMMON))
                add(CardNumber(RankNumber.TWO, Suit.HEARTS, CardBack.STANDARD))
                RankNumber.entries.forEach { rank ->
                    if (rank.value > 4) {
                        Suit.entries.forEach { suit ->
                            add(CardNumber(rank, suit, CardBack.MADNESS))
                        }
                    }
                }
                RankNumber.entries.forEach { rank ->
                    add(CardNumber(rank, Suit.CLUBS, CardBack.ULTRA_LUXE_CRIME))
                }
                add(CardFaceSuited(RankFace.JACK, Suit.CLUBS, CardBack.ULTRA_LUXE_CRIME))
                add(CardFaceSuited(RankFace.QUEEN, Suit.CLUBS, CardBack.ULTRA_LUXE_CRIME))
                add(CardFaceSuited(RankFace.KING, Suit.CLUBS, CardBack.ULTRA_LUXE_CRIME))
                add(CardNumber(RankNumber.ACE, Suit.SPADES, CardBack.ULTRA_LUXE_CRIME))
                add(CardJoker(CardJoker.Number.ONE, CardBack.ULTRA_LUXE_CRIME))
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
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch2_14),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch2_15),
                    DialogFinishState(DeathCode.ALIVE),
                ),
                listOf(
                    DialogEdge(0, 1, Res.string.ch2_14a),
                    DialogEdge(1, 2, Res.string.finish),
                )
            ), goBack) { goBack() }
            return
        }
        -1 -> {
            StoryShow(DialogGraph(
                listOf(
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch2_13),
                    DialogFinishState(DeathCode.STABBED_BY_CAZADORS)
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
            DialogMiddleState(Res.drawable.black_back, Res.string.ch2_1),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch2_2),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch2_3),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch2_4),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch2_5),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch2_6),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch2_7),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch2_8),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch2_9),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch2_10),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch2_11),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch2_12),
            DialogFinishState(DeathCode.ALIVE),
            DialogFinishState(DeathCode.STABBED_BY_CAZADORS_ON_THE_RUN)
        ),
        listOf(
            DialogEdge(0, 1, Res.string.ch2_1a),
            DialogEdge(0, 2, Res.string.ch2_1b),
            DialogEdge(0, 3, Res.string.ch2_1c),
            DialogEdge(1, 2, Res.string.ch2_1b),
            DialogEdge(1, 3, Res.string.ch2_1c),
            DialogEdge(2, 3, Res.string.ch2_3a),
            DialogEdge(3, 4, Res.string.ch2_4a),
            DialogEdge(3, 5, Res.string.ch2_4b),
            DialogEdge(4, 5, Res.string.ch2_5a),
            DialogEdge(5, 7, Res.string.ch2_6a),
            DialogEdge(5, 6, Res.string.ch2_6b),
            DialogEdge(6, 7, Res.string.ch2_7a),
            DialogEdge(7, 9, Res.string.ch2_8a),
            DialogEdge(7, 8, Res.string.ch2_8b),
            DialogEdge(8, 9, Res.string.ch2_9a),
            DialogEdge(9, 12, Res.string.ch2_10a),
            DialogEdge(9, 10, Res.string.ch2_10b),
            DialogEdge(10, 12, Res.string.ch2_11a),
            DialogEdge(10, 11, Res.string.ch2_11b),
            DialogEdge(11, 13, Res.string.finish),
        )
    ), goBack) {
        isGame = true
    }
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
fun ShowStoryEndOfPart1(
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    StoryShow(DialogGraph(
        listOf(
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_1),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_2),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_3),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_4),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_5),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_6),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_7),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_8),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_9),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_10),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_11),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_12),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_13),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_14),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_15),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_16),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_17),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_18),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_19),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_20),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_21),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_22),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_23),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_24),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_25),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_26),
            DialogMiddleState(Res.drawable.black_back, Res.string.p1e_27),
            DialogFinishState(DeathCode.ALIVE)
        ),
        listOf(
            DialogEdge(0, 1, Res.string.p1e_1a),
            DialogEdge(1, 2, Res.string.p1e_2a),
            DialogEdge(2, 3, Res.string.p1e_3a),
            DialogEdge(3, 4, Res.string.p1e_4a),
            DialogEdge(4, 6, Res.string.p1e_5a),
            DialogEdge(4, 5, Res.string.p1e_5b),
            DialogEdge(5, 6, Res.string.p1e_6a),
            DialogEdge(6, 7, Res.string.p1e_7a),
            DialogEdge(7, 8, Res.string.p1e_8a),
            DialogEdge(8, 9, Res.string.p1e_9a),
            DialogEdge(8, 9, Res.string.p1e_9b),
            DialogEdge(8, 9, Res.string.p1e_9c),
            DialogEdge(9, 10, Res.string.p1e_10a),
            DialogEdge(10, 11, Res.string.p1e_11a),
            DialogEdge(10, 16, Res.string.p1e_11b),
            DialogEdge(11, 12, Res.string.p1e_12a),
            DialogEdge(12, 14, Res.string.p1e_13a),
            DialogEdge(12, 13, Res.string.p1e_13b),
            DialogEdge(13, 14, Res.string.p1e_13a),
            DialogEdge(14, 15, Res.string.p1e_15a),
            DialogEdge(14, 15, Res.string.p1e_15b),
            DialogEdge(14, 15, Res.string.p1e_15c),
            DialogEdge(14, 15, Res.string.p1e_15d),
            DialogEdge(15, 16, Res.string.p1e_11b),
            DialogEdge(16, 17, Res.string.p1e_17a),
            DialogEdge(17, 18, Res.string.p1e_18a),
            DialogEdge(18, 19, Res.string.p1e_19a),
            DialogEdge(19, 20, Res.string.p1e_20a),
            DialogEdge(19, 20, Res.string.p1e_20b),
            DialogEdge(19, 20, Res.string.p1e_20c),
            DialogEdge(20, 21, Res.string.p1e_21a),
            DialogEdge(20, 22, Res.string.p1e_21b),
            DialogEdge(21, 22, Res.string.p1e_21b),
            DialogEdge(22, 23, Res.string.p1e_23a),
            DialogEdge(23, 24, Res.string.p1e_24a),
            DialogEdge(24, 25, Res.string.p1e_25a),
            DialogEdge(25, 26, Res.string.p1e_26a),
            DialogEdge(26, 27, Res.string.finish),
        )
    ), goBack) {
        advanceChapter(); goBack()
    }
}


@Composable
fun ShowStoryChapter4(
    deck: CustomDeck,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }

    if (isGame) {
        StartStoryGame(
            EnemyStory4,
            CResources(deck),
            showAlertDialog,
            { gameResult = 1; advanceChapter() },
            { gameResult = 1; advanceChapter() },
            { if (gameResult == 0) gameResult = -1; isGame = false }
        )
        return
    }

    when (gameResult) {
        1 -> {
            StoryShow(DialogGraph(
                listOf(
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_23),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_24),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_25),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_26),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_27),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_28),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_29),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_30),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_31),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_32),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_33),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_34),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_35),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_36),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_37),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_38),
                    DialogFinishState(DeathCode.ALIVE),
                ),
                listOf(
                    DialogEdge(0, 1, Res.string.ch4_23a),
                    DialogEdge(1, 2, Res.string.ch4_24a),
                    DialogEdge(2, 3, Res.string.ch4_25a),
                    DialogEdge(3, 4, Res.string.ch4_26a),
                    DialogEdge(4, 15, Res.string.ch4_27a),
                    DialogEdge(3, 5, Res.string.ch4_26b),
                    DialogEdge(5, 6, Res.string.ch4_28a),
                    DialogEdge(6, 7, Res.string.ch4_29a),
                    DialogEdge(7, 8, Res.string.ch4_30a),
                    DialogEdge(8, 9, Res.string.ch4_31a),
                    DialogEdge(9, 10, Res.string.ch4_32a),
                    DialogEdge(10, 11, Res.string.ch4_33a),
                    DialogEdge(10, 15, Res.string.ch4_33b),
                    DialogEdge(11, 12, Res.string.ch4_34a),
                    DialogEdge(12, 13, Res.string.ch4_35a),
                    DialogEdge(13, 14, Res.string.ch4_36a),
                    DialogEdge(14, 15, Res.string.ch4_37a),
                    DialogEdge(15, 16, Res.string.finish),
                )
            ), goBack) { goBack() }
            return
        }
        -1 -> {
            StoryShow(DialogGraph(
                listOf(
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch4_minus_1),
                    DialogFinishState(DeathCode.GOT_LOST)
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
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_1),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_2),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_3),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_4),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_5),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_6),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_7),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_8),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_9),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_10),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_11),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_12),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_13),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_14),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_15),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_16),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_17),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_18),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_19),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_20),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_21),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_22),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_221),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_222),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch4_223),
            DialogFinishState(DeathCode.ALIVE),
        ),
        listOf(
            DialogEdge(0, 1, Res.string.ch4_1a),
            DialogEdge(1, 2, Res.string.ch4_2a),
            DialogEdge(2, 3, Res.string.ch4_3a),
            DialogEdge(3, 4, Res.string.ch4_4a),
            DialogEdge(4, 5, Res.string.ch4_5a),
            DialogEdge(5, 6, Res.string.ch4_6a),
            DialogEdge(6, 7, Res.string.ch4_7a),
            DialogEdge(7, 8, Res.string.ch4_8a),
            DialogEdge(8, 9, Res.string.ch4_9a),
            DialogEdge(8, 10, Res.string.ch4_9b),
            DialogEdge(9, 10, Res.string.ch4_9b),
            DialogEdge(10, 11, Res.string.ch4_11a),
            DialogEdge(10, 18, Res.string.ch4_11b),
            DialogEdge(10, 21, Res.string.ch4_11c),
            DialogEdge(11, 12, Res.string.ch4_12a),
            DialogEdge(12, 13, Res.string.ch4_13a),
            DialogEdge(13, 14, Res.string.ch4_14a),
            DialogEdge(14, 15, Res.string.ch4_15a),
            DialogEdge(15, 16, Res.string.ch4_16a),
            DialogEdge(16, 17, Res.string.ch4_17a),
            DialogEdge(17, 11, Res.string.ch4_11a),
            DialogEdge(17, 18, Res.string.ch4_11b),
            DialogEdge(17, 21, Res.string.ch4_11c),
            DialogEdge(18, 19, Res.string.ch4_19a),
            DialogEdge(18, 20, Res.string.ch4_19b),
            DialogEdge(18, 21, Res.string.ch4_19c),
            DialogEdge(19, 20, Res.string.ch4_19b),
            DialogEdge(19, 21, Res.string.ch4_19c),
            DialogEdge(20, 19, Res.string.ch4_19a),
            DialogEdge(20, 21, Res.string.ch4_19c),
            DialogEdge(21, 22, Res.string.ch4_22a),
            DialogEdge(21, 22, Res.string.ch4_22b),
            DialogEdge(21, 22, Res.string.ch4_22c),
            DialogEdge(22, 23, Res.string.ch4_221a),
            DialogEdge(23, 24, Res.string.ch4_222a),
            DialogEdge(24, 25, Res.string.ch4_223a),
        )
    ), goBack) {
        isGame = true
    }
}


@Composable
fun ShowStoryChapter5(
    deck: CustomDeck,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }

    if (isGame) {
        StartStoryGame(
            EnemyStory5,
            CResources(deck),
            showAlertDialog,
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
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch5_45),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch5_46),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch5_47),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch5_48),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch5_49),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch5_50),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch5_51),
                    DialogFinishState(DeathCode.ALIVE),
                ),
                listOf(
                    DialogEdge(0, 1, Res.string.ch5_45a),
                    DialogEdge(1, 2, Res.string.ch5_46a),
                    DialogEdge(2, 3, Res.string.ch5_47a),
                    DialogEdge(3, 4, Res.string.ch5_48a),
                    DialogEdge(4, 5, Res.string.ch5_49a),
                    DialogEdge(5, 6, Res.string.ch5_50a),
                    DialogEdge(6, 7, Res.string.finish),
                )
            ), goBack) { goBack() }
            return
        }
        -1 -> {
            StoryShow(DialogGraph(
                listOf(
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch5_minus),
                    DialogFinishState(DeathCode.SHOT)
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
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_1),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_2),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_3),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_4),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_5),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_6),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_7),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_8),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_9),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_10),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_11),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_12),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_13),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_14),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_15),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_16),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_17),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_18),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_19),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_20),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_21),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_22),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_23),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_24),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_25),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_26),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_27),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_28),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_29),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_30),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_30), // 30
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_30),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_30),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_31),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_32),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_33), // 35
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_34),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_35),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_36),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_37),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_38), // 40
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_39),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_40),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_41),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_42),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_43), // 45
            DialogMiddleState(Res.drawable.black_back, Res.string.ch5_44),
            DialogFinishState(DeathCode.ALIVE),
            DialogFinishState(DeathCode.SHOT) // 48
        ),
        listOf(
            DialogEdge(0, 1, Res.string.ch5_1a),
            DialogEdge(1, 2, Res.string.ch5_2a),
            DialogEdge(2, 3, Res.string.ch5_3a),
            DialogEdge(3, 6, Res.string.ch5_4a),
            DialogEdge(3, 5, Res.string.ch5_4b),
            DialogEdge(3, 4, Res.string.ch5_4c),
            DialogEdge(4, 48, Res.string.finish),
            DialogEdge(5, 6, Res.string.ch5_4a),
            DialogEdge(5, 4, Res.string.ch5_4c),
            DialogEdge(6, 7, Res.string.ch5_7a),
            DialogEdge(7, 8, Res.string.ch5_8a),
            DialogEdge(8, 9, Res.string.ch5_9a),
            DialogEdge(9, 10, Res.string.ch5_10a),
            DialogEdge(10, 11, Res.string.ch5_11a),
            DialogEdge(11, 12, Res.string.ch5_12a),
            DialogEdge(12, 13, Res.string.ch5_13a),
            DialogEdge(13, 14, Res.string.ch5_14a),
            DialogEdge(14, 15, Res.string.ch5_15a),
            DialogEdge(14, 19, Res.string.ch5_15b),
            DialogEdge(14, 17, Res.string.ch5_15c),
            DialogEdge(14, 16, Res.string.ch5_15d),
            DialogEdge(15, 19, Res.string.ch5_15b),
            DialogEdge(15, 17, Res.string.ch5_15c),
            DialogEdge(15, 16, Res.string.ch5_15d),
            DialogEdge(16, 15, Res.string.ch5_15a),
            DialogEdge(16, 19, Res.string.ch5_15b),
            DialogEdge(16, 17, Res.string.ch5_15c),
            DialogEdge(17, 18, Res.string.ch5_18a),
            DialogEdge(17, 19, Res.string.ch5_18b),
            DialogEdge(18, 19, Res.string.ch5_18b),
            DialogEdge(19, 20, Res.string.ch5_20a),
            DialogEdge(20, 21, Res.string.ch5_21a),
            DialogEdge(21, 22, Res.string.ch5_22a),
            DialogEdge(22, 23, Res.string.ch5_23a),
            DialogEdge(23, 24, Res.string.ch5_24a),
            DialogEdge(24, 25, Res.string.ch5_25a),
            DialogEdge(25, 26, Res.string.ch5_26a),
            DialogEdge(26, 27, Res.string.ch5_27a),
            DialogEdge(27, 28, Res.string.ch5_28a),
            DialogEdge(28, 29, Res.string.ch5_29a),
            DialogEdge(28, 30, Res.string.ch5_29b),
            DialogEdge(28, 31, Res.string.ch5_29c),
            DialogEdge(28, 32, Res.string.ch5_29d),
            DialogEdge(28, 38, Res.string.ch5_29e),
            DialogEdge(29, 33, Res.string.ch5_30a),
            DialogEdge(30, 34, Res.string.ch5_30b),
            DialogEdge(31, 35, Res.string.ch5_30c),
            DialogEdge(32, 36, Res.string.ch5_30d),
            DialogEdge(33, 37, Res.string.ch5_34a),
            DialogEdge(34, 37, Res.string.ch5_34a),
            DialogEdge(35, 37, Res.string.ch5_34a),
            DialogEdge(36, 37, Res.string.ch5_34a),
            DialogEdge(37, 38, Res.string.ch5_35a),
            DialogEdge(38, 39, Res.string.ch5_36a),
            DialogEdge(39, 40, Res.string.ch5_37a),
            DialogEdge(40, 41, Res.string.ch5_38a),
            DialogEdge(41, 42, Res.string.ch5_39a),
            DialogEdge(42, 43, Res.string.ch5_40a),
            DialogEdge(43, 44, Res.string.ch5_41a),
            DialogEdge(44, 45, Res.string.ch5_42a),
            DialogEdge(45, 46, Res.string.ch5_43a),
            DialogEdge(46, 47, Res.string.ch5_44a),
        )
    ), goBack) {
        isGame = true
    }
}



@Composable
fun ShowStoryChapter6(
    deck: CustomDeck,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var isGame by rememberSaveable { mutableStateOf(false) }
    var gameResult by rememberSaveable { mutableIntStateOf(0) }

    if (isGame) {
        StartStoryGame(
            EnemyStory6,
            CResources(deck),
            showAlertDialog,
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
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch6_24),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch6_25),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch6_26),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch6_27),
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch6_28),
                    DialogFinishState(DeathCode.ALIVE),
                ),
                listOf(
                    DialogEdge(0, 1, Res.string.ch6_24a),
                    DialogEdge(1, 2, Res.string.ch6_25a),
                    DialogEdge(2, 3, Res.string.ch6_26a),
                    DialogEdge(3, 4, Res.string.ch6_27a),
                    DialogEdge(3, 5, Res.string.finish),
                    DialogEdge(4, 5, Res.string.finish),
                )
            ), goBack) { goBack() }
            return
        }
        -1 -> {
            StoryShow(DialogGraph(
                listOf(
                    DialogMiddleState(Res.drawable.black_back, Res.string.ch6_minus),
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
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_0),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_1),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_2),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_3),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_4),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_5),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_6),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_7),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_8),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_9),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_10),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_11),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_12),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_13),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_14),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_15),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_16),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_17),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_18),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_19),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_20),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_21),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_22),
            DialogMiddleState(Res.drawable.black_back, Res.string.ch6_23),
            DialogFinishState(DeathCode.ALIVE),
            DialogFinishState(DeathCode.SHOT),
            DialogFinishState(DeathCode.PRISON)
        ),
        listOf(
            DialogEdge(0, 1, Res.string.ch6_0a),
            DialogEdge(1, 3, Res.string.ch6_1a),
            DialogEdge(1, 2, Res.string.ch6_1b),
            DialogEdge(2, 3, Res.string.ch6_1a),
            DialogEdge(3, 4, Res.string.ch6_3a),
            DialogEdge(4, 7, Res.string.ch6_4a),
            DialogEdge(4, 5, Res.string.ch6_4b),
            DialogEdge(4, 6, Res.string.ch6_4c),
            DialogEdge(5, 25, Res.string.finish),
            DialogEdge(6, 25, Res.string.finish),
            DialogEdge(7, 8, Res.string.ch6_7a),
            DialogEdge(7, 5, Res.string.ch6_7b),
            DialogEdge(7, 6, Res.string.ch6_7c),
            DialogEdge(8, 9, Res.string.ch6_8a),
            DialogEdge(8, 5, Res.string.ch6_8b),
            DialogEdge(8, 6, Res.string.ch6_8c),
            DialogEdge(9, 10, Res.string.ch6_9a),
            DialogEdge(10, 11, Res.string.ch6_10a),
            DialogEdge(10, 11, Res.string.ch6_10b),
            DialogEdge(10, 11, Res.string.ch6_10c),
            DialogEdge(11, 12, Res.string.ch6_11a),
            DialogEdge(11, 13, Res.string.ch6_11b),
            DialogEdge(11, 14, Res.string.ch6_11c),
            DialogEdge(12, 13, Res.string.ch6_11b),
            DialogEdge(12, 14, Res.string.ch6_11c),
            DialogEdge(13, 12, Res.string.ch6_11a),
            DialogEdge(13, 14, Res.string.ch6_11c),
            DialogEdge(14, 15, Res.string.ch6_14a),
            DialogEdge(15, 16, Res.string.ch6_15a),
            DialogEdge(16, 17, Res.string.ch6_16a),
            DialogEdge(17, 18, Res.string.ch6_17a),
            DialogEdge(18, 23, Res.string.ch6_18a),
            DialogEdge(18, 19, Res.string.ch6_18b),
            DialogEdge(18, 20, Res.string.ch6_18c),
            DialogEdge(19, 20, Res.string.ch6_19a),
            DialogEdge(19, 23, Res.string.ch6_19b),
            DialogEdge(20, 21, Res.string.ch6_20a),
            DialogEdge(21, 24, Res.string.ch6_21a),
            DialogEdge(21, 22, Res.string.ch6_21b),
            DialogEdge(22, 26, Res.string.ch6_21b),
            DialogEdge(23, 24, Res.string.ch6_23a),
            DialogEdge(23, 22, Res.string.ch6_23b),
        )
    ), goBack) {
        isGame = true
    }
}


@Composable
fun ShowStoryEndOfPart2(
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    StoryShow(DialogGraph(
        listOf(
            DialogMiddleState(Res.drawable.black_back, Res.string.p2e_0),
            DialogMiddleState(Res.drawable.black_back, Res.string.p2e_1),
            DialogMiddleState(Res.drawable.black_back, Res.string.p2e_2),
            DialogMiddleState(Res.drawable.black_back, Res.string.p2e_3),
            DialogMiddleState(Res.drawable.black_back, Res.string.p2e_4),
            DialogMiddleState(Res.drawable.black_back, Res.string.p2e_5),
            DialogMiddleState(Res.drawable.black_back, Res.string.p2e_6),
            DialogMiddleState(Res.drawable.black_back, Res.string.p2e_7),
            DialogMiddleState(Res.drawable.black_back, Res.string.p2e_8),
            DialogMiddleState(Res.drawable.black_back, Res.string.p2e_9),
            DialogMiddleState(Res.drawable.black_back, Res.string.p2e_10),
            DialogFinishState(DeathCode.ALIVE)
        ),
        listOf(
            DialogEdge(0, 1, Res.string.p2e_0a),
            DialogEdge(1, 2, Res.string.p2e_1a),
            DialogEdge(1, 2, Res.string.p2e_1b),
            DialogEdge(1, 2, Res.string.p2e_1c),
            DialogEdge(1, 2, Res.string.p2e_1d),
            DialogEdge(2, 3, Res.string.p2e_2a),
            DialogEdge(2, 3, Res.string.p2e_2b),
            DialogEdge(3, 4, Res.string.p2e_3a),
            DialogEdge(3, 10, Res.string.p2e_3b),
            DialogEdge(4, 5, Res.string.p2e_4a),
            DialogEdge(5, 6, Res.string.p2e_5a),
            DialogEdge(6, 8, Res.string.p2e_6a),
            DialogEdge(6, 7, Res.string.p2e_6b),
            DialogEdge(7, 8, Res.string.p2e_6a),
            DialogEdge(8, 9, Res.string.p2e_8a),
            DialogEdge(9, 10, Res.string.p2e_9a),
            DialogEdge(10, 11, Res.string.finish),
        )
    ), goBack) {
        advanceChapter(); goBack()
    }
}


@Composable
fun StartStoryGame(
    enemy: Enemy,
    playerCResources: CResources,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
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
            playWinSoundAlone()
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

    ShowGame(game) {
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