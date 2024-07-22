package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyStory1
import com.unicorns.invisible.caravan.model.enemy.EnemyStory2
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.TextClassic
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getStrokeColorByStyle
import com.unicorns.invisible.caravan.utils.getTextBackByStyle
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playCardFlipSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playFrankPhrase
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playNotificationSound
import com.unicorns.invisible.caravan.utils.playNukeBlownSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playVatsReady
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.stopAmbient


@Composable
fun ShowStoryList(activity: MainActivity, showAlertDialog: (String, String) -> Unit, goBack: () -> Unit) {
    var showChapter by rememberScoped { mutableStateOf<Int?>(null) }

    when (showChapter) {
        0 -> {
            ShowStoryChapter1(activity, showAlertDialog, {
                activity.save?.let {
                    if (it.storyChaptersProgress == 0) {
                        it.availableDecks[CardBack.DECK_13] = true
                        it.availableCards.addAll(CustomDeck(CardBack.DECK_13, false).toList())
                        it.storyChaptersProgress++
                    }
                    saveOnGD(activity)
                }
            }) { showChapter = null }
            return
        }
        1 -> {
            ShowStoryChapter2(activity, showAlertDialog, {
                activity.save?.let {
                    it.storyChaptersProgress++
                    saveOnGD(activity)
                }
            }) { showChapter = null }
            return
        }
        else -> {}
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
                .fillMaxHeight(0.85f)
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
                fun Chapter(number: Int, onClick: () -> Unit) {
                    val isAvailable = number <= (activity.save?.storyChaptersProgress ?: 0)
                    val text = if (!isAvailable) {
                        "???"
                    } else when (number) {
                        0 -> "Chapter 1: Humble Beginnings."
                        1 -> "Chapter 2: Obstacle 1."
                        2 -> "Chapter 3: "
                        3 -> "Chapter 4: "
                        4 -> "Chapter 5: "
                        5 -> "Chapter 6: "
                        6 -> "Chapter 7: "
                        7 -> "Chapter 8: "
                        8 -> "Chapter 9: "
                        9 -> "Chapter 10: "
                        10 -> "Chapter 11: "
                        11 -> "Chapter 12: "
                        12 -> "Chapter 13: "
                        13 -> "Chapter 14: "
                        14 -> "The End Slides."
                        else -> "???"
                    }
                    TextFallout(
                        text,
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Alignment.Center,
                        if (isAvailable) {
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

                repeat(14) {
                    Chapter(it) { playSelectSound(activity); showChapter = it }
                    Spacer(modifier = Modifier.height(10.dp))
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
fun ShowStoryChapter1(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("It is a story of the Prospector. A child of the Mojave Wasteland, " +
            "hardened by the harsh life in the desert and teased by the luxuries of the neon lights of New Vegas, " +
            "doors of which are closed to ones of his kind.") }
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
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = R.drawable.frank_head
                        )
                    ))

            @Composable
            fun DialogLine(line: String, onClick: () -> Unit) {
                TextClassic(
                    line,
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    18.sp,
                    Alignment.CenterStart,
                    modifier = Modifier
                        .clickableSelect(activity) {
                            onClick()
                        }
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Start
                )
                Spacer(Modifier.height(12.dp))
            }

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
                        text = "Defeated, the hologuard shrugged his shoulders and vanished, leaving behind brand new Sierra Madre Deck!"
                        lineNumber = -1
                    }
                    -1 -> {
                        text = "Trying to find anything useful in Sierra Madre, you get lost in Cloud and die. Your body is never found."
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine("Ooh, poor guy! What did he do?") {
                        lineNumber = 1
                        text =
                                "To change his life for good, the Prospector had become a vulture " +
                                "for the rotting parts of the Old world, " +
                                "seeking to salvage, repair and sell off to whoever might find them a better use."
                    }
                    1 -> DialogLine("Huh, where did he go, I wonder?") {
                        lineNumber = 2
                        text = "Naturally, the first place that captivated him was the Sierra Madre casino, of which so many legends reached his ears."
                    }
                    2 -> DialogLine("Sierra Madre? I thought the place is off limits.") {
                        lineNumber = 3
                        text = "Since the legendary Courier Six cleared the Sierra Madre villa, " +
                                "countless gangs and punks took everything of value to a primitive mind: " +
                                "water, food, weapons, clothing, medical supplies. " +
                                "Sierra Madre chips and bottle caps were gone, yet the technology remained untouched."
                    }
                    3 -> DialogLine("So what did the Prospector find?") {
                        lineNumber = 4
                        text = "One of the working RobCo terminals, so promising to be informative, " +
                                "was protected by a hologuard, who was not aggressive " +
                                "and offered the Prospector a challenge in the friendly game of Caravan, wagering the information he preserves."
                    }
                    -1 -> DialogLine("[FINISH]") { goBack() }
                    else -> {
                        DialogLine("[FINISH]") { isGame = true; gameResult = -1 }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowStoryChapter2(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("After hologuard disappeared, The Prospector was able to proceed to the terminal.") }
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
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .paint(
                        painterResource(
                            id = R.drawable.frank_head
                        )
                    ))

            @Composable
            fun DialogLine(line: String, onClick: () -> Unit) {
                TextClassic(
                    line,
                    getTextColorByStyle(activity, Style.PIP_BOY),
                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                    18.sp,
                    Alignment.CenterStart,
                    modifier = Modifier
                        .clickableSelect(activity) {
                            onClick()
                        }
                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Start
                )
                Spacer(Modifier.height(12.dp))
            }

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
                        text = "...almost caught, the Prospector sneaked by the guards, venturing into the vast unknown of the North."
                        lineNumber = -1
                    }
                    -1 -> {
                        text = "...caught by guards, the Prospector was thrown into prison, where he died of some nasty disease."
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine("So, what was there?") {
                        lineNumber = 1
                        text =
                            "On it, lots of secret documents regarding constructed vaults were located. " +
                                    "But one of them caught the Prospector's attention the most — a document regarding a place called \"Oasis\", " +
                                    "located in Alaska. A small valley surrounded by rocks, equipped with water purifiers, GECKs, anti-missile systems " +
                                    "and lots of robotic staff to take care of the area."
                    }
                    1 -> DialogLine("Huh, sounds good. What did the Prospector do?") {
                        lineNumber = 2
                        text = "The Prospector had realized that the leisure and safety the secret treasure of Alaska " +
                                "can provide would be the best experience if his entire life. And so, the Prospector set his mind to the north..."
                    }
                    2 -> DialogLine("Finally, adventure begins!") {
                        lineNumber = 3
                        text = "And yet, he was stopped by the border patrol of the NCR."

                    }
                    3 -> DialogLine("Oh.") {
                        lineNumber = 4
                        text = "The guard didn't allow the Prospector to leave, yet the Prospector was not planning on giving up. " +
                                "He attempted to sneak past the guards..."
                    }
                    -1 -> DialogLine("[FINISH]") { goBack() }
                    else -> {
                        DialogLine("[FINISH]") { isGame = true; gameResult = -1 }
                    }
                }
            }
        }
    }
}