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
import com.unicorns.invisible.caravan.model.enemy.EnemyPriestess
import com.unicorns.invisible.caravan.model.enemy.EnemyStory1
import com.unicorns.invisible.caravan.model.enemy.EnemyStory2
import com.unicorns.invisible.caravan.model.enemy.EnemyStory3
import com.unicorns.invisible.caravan.model.enemy.EnemyStory4
import com.unicorns.invisible.caravan.model.enemy.EnemyStory6
import com.unicorns.invisible.caravan.model.enemy.EnemyStory7
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
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
import com.unicorns.invisible.caravan.utils.nextSong
import com.unicorns.invisible.caravan.utils.playCardFlipSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playNotificationSound
import com.unicorns.invisible.caravan.utils.playNukeBlownSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playTowerFailed
import com.unicorns.invisible.caravan.utils.playVatsReady
import com.unicorns.invisible.caravan.utils.playWWSound
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.stopAmbient
import com.unicorns.invisible.caravan.utils.stopRadio
import kotlinx.coroutines.delay


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
                        it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 1)
                    }
                    saveOnGD(activity)
                }
            }) { showChapter = null }
            return
        }
        1 -> {
            ShowStoryChapter2(activity, showAlertDialog, {
                activity.save?.let {
                    it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 2)
                    saveOnGD(activity)
                }
            }) { showChapter = null }
            return
        }
        2 -> {
            ShowStoryChapter3(activity, showAlertDialog, {
                activity.save?.let {
                    it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 3)
                    saveOnGD(activity)
                }
            }) { showChapter = null }
            return
        }
        3 -> {
            ShowStoryChapter4(activity, showAlertDialog, {
                activity.save?.let {
                    it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 4)
                    saveOnGD(activity)
                }
            }) { showChapter = null }
            return
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
            return
        }
        5 -> {
            ShowStoryChapter6(activity, showAlertDialog, {
                activity.save?.let {
                    it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 6)
                    saveOnGD(activity)
                }
            }) { showChapter = null }
            return
        }
        6 -> {
            ShowStoryChapter7(activity, showAlertDialog, {
                activity.save?.let {
                    it.storyChaptersProgress = maxOf(it.storyChaptersProgress, 7)
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
                        1 -> "Chapter 2: Patrolling the Mojave..."
                        2 -> "Chapter 3: Shake Hands With Danger."
                        3 -> "Chapter 4: Can't Play Dead."
                        4 -> "Chapter 5: It's a Mad, Mad, Mad, Mad World."
                        5 -> "Chapter 6: Paradise Lost."
                        6 -> "Chapter 7: Jailhouse Rock."
                        7 -> "Chapter 8: The Day He Didn't Die?"
                        8 -> "Chapter 9: Duel of the Fates."
                        9 -> "The End Slides."
                        10 -> "Chapter 9A: Duel of the Fates."
                        11 -> "Future Built Upon Graveyard."
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
                repeat(activity.save?.altStoryChaptersProgress ?: 0) {
                    Chapter(it + 10) { playSelectSound(activity); showChapter = it }
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
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
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
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
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
                        text = "The Prospector had realized that the safety the secret treasure of Alaska " +
                                "can provide would be the best experience in his entire life. And so, the Prospector set his mind to the north..."
                    }
                    2 -> DialogLine("Finally, adventure begins!") {
                        lineNumber = 3
                        text = "And yet, on his way through Mojave he was stopped by the border patrol of the NCR."

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

@Composable
fun ShowStoryChapter3(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("The road north was eerily silent, which made the Prospector feel odd. " +
            "He wondered why, and soon he received his answer. A massive radioactive rain storm was approaching...") }
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
                        text = "The Prospector managed to fend off most of the Cazadores, trapping them " +
                                "within the halls of the vault. Yet, he was stung a lot. The poison started " +
                                "kicking in, and consciousness was fading.\n\n" +
                                "It seems that the Prospector has found his end, dying infamously in the vault..."
                        lineNumber = -1
                    }
                    -1 -> {
                        LaunchedEffect(Unit) { playTowerFailed(activity) }
                        text = "The Prospector failed to overcome the danger. His body, full of poison, fell on the floor, unable to move. " +
                                "Soon the Prospector became food for the insects."
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine("Oh no! Run, Prospector, run!") {
                        lineNumber = 1
                        text =
                            "As the Prospector was nearly lost to the chaotic will of the approaching disaster, an entry to a vault showed on the horizon."
                    }
                    1 -> DialogLine("Finally, safe heaven!") {
                        lineNumber = 2
                        text = "Upon the entry, the Prospector discovered that the gates were open, " +
                                "and the power was off, only emergency lights were shimmering. " +
                                "He found it odd, yet proceeded anyways."
                    }
                    2 -> DialogLine("Ermm, maybe he shouldn't go inside...") {
                        lineNumber = 3
                        text = "Deep within the halls of the vault, he found multiple dismembered and " +
                                "chewed on corpses, maimed beyond recognition. He decided to not " +
                                "venture further, to not find the cause of death of these people..."
                    }
                    3 -> DialogLine("Oh no. No-no-no-no-no-no!") {
                        lineNumber = 4
                        text = "...however the cause found him on its own. A swarm of Cazadors appeared!"
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
fun ShowStoryChapter4(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    LaunchedEffect(Unit) {
        stopRadio()
        isFinalBossSequence = true
    }

    var text by rememberSaveable { mutableStateOf("Aarrghhhhhhhh… Uuuuuu… Mmmmmphhhhhhhh…") }
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
                    "You hear the heartbeat...",
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
                    0 -> DialogLine("Someone's coming, Prospector.") {
                        lineNumber = 1
                        text = "(grim reaper approaches)"
                    }
                    -1 -> DialogLine("...") {
                        lineNumber = -2
                        gameResult = 0
                        text = "Upon awakening, the Prospector remembered what happened, and broke " +
                                "the door that leads to the deeper sections of the vault, making sure the " +
                                "swarm would take no new lives. The Prospector made use of the remaining " +
                                "available rooms, using them for recovery."
                    }
                    -2 -> DialogLine("You made me worry.") {
                        lineNumber = -3
                        text = "After a few days, the rain ended, and the Prospector could continue his way..."
                    }
                    -3 -> DialogLine("[FINISH]") { isFinalBossSequence = false; nextSong(activity); goBack() }
                    else -> {
                        DialogLine("[FINISH]") { isGame = true; gameResult = -1 }
                    }
                }
            }
        }
    }
}



@Composable
fun ShowStoryChapter5(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    advanceChapter: () -> Unit,
    goBack: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("Weeks, maybe months passed… " +
            "The Prospector reached the northern territories of the former Canada, where it borders with Alaska.") }
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
                    1, -1 -> {
                        text = "The Madness Priestess was joyous. She liked the match a lot, and " +
                                "awarded the Prospector with a deck that once belonged to one of her flock."
                        lineNumber = -1
                    }
                    else -> {}
                }

                when (lineNumber) {
                    0 -> DialogLine("Well, he's not far from Oasis.") {
                        lineNumber = 1
                        text =
                            "To his surprise, a bizarre scene unfolded before him: a gigantic wall of trash, " +
                                    "tens of meters high. He attempted to traverse around them, but to no avail – the wall had no end."
                    }
                    1 -> DialogLine("Wall of trash? Who put it here?") {
                        lineNumber = 2
                        text = "Yet it was not the end of his adventure, for he was discovered by a woman " +
                                "who was fixing the wall with the trash she brought from afar."
                    }
                    2 -> DialogLine("A woman? Why would she do that?") {
                        lineNumber = 3
                        text = "She explained " +
                                "that the wall is meant to keep the people safe from what lays beyond, " +
                                "elaborating on which she refused."
                    }
                    3 -> DialogLine("So, how did Prospector proceed?") {
                        lineNumber = 4
                        text = "He didn't. Turned back and returned home."
                    }
                    4 -> DialogLine("Very funny.") {
                        lineNumber = 5
                        text = "Okay, she agreed to assist the Prospector and lead him through the secret passage, " +
                                "be he to entertain her in the friendly game of Caravan…"
                    }
                    -1 -> DialogLine("Yeah, but the Prospector arrived not to play Caravan...") {
                        lineNumber = -2
                        gameResult = 0
                        text = "The Prospector was lead beyond the wall. Yet what he saw there astonished " +
                                "him, and not in a good way…"
                    }
                    -2 -> DialogLine("[FINISH]") { goBack() }
                    else -> {
                        DialogLine("[FINISH]") { isGame = true; gameResult = -1 }
                    }
                }
            }
        }
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
                        1 -> "Put your hands in the air! Do not try to attack!"
                        2 -> "Last warning! Stay dormant and do nothing suspicious!"
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
                    0 -> {
                        DialogLine("What was in the boxes?") {
                            lineNumber = 1
                            text =
                                "Most of them had the Caravan card decks, some of them artificially aged. " +
                                        "The Prospector understood everything: of course, how " +
                                        "could the food and such unnecessary and quick to decay things as cards " +
                                        "could be found in the wastes, if they were not dropped out by the Chinese?"
                        }
                        DialogLine("Wait, Chinese?") {
                            lineNumber = 3
                            text =
                                "Yes. The Chinese. And lots of them. Most ghoulified, but plenty of them pure. " +
                                        "They have turned the Oasis into their base of operations on the North American continent."
                        }
                    }
                    1 -> {
                        DialogLine("[INT 6/8] But why flooding America with food and stuff?") {
                            lineNumber = 2
                            text = "You'll understand in time."
                        }
                        DialogLine("Wait, Chinese?") {
                            lineNumber = 3
                            text =
                                "Yes. The Chinese. And lots of them. Most ghoulified, but plenty of them pure. " +
                                        "They have turned the Oasis into their base of operations on the North American continent."
                        }
                    }
                    2 -> DialogLine("Wait, Chinese?") {
                        lineNumber = 3
                        text =
                            "Yes. The Chinese. And lots of them. Most ghoulified, but plenty of them pure. " +
                                    "They have turned the Oasis into their base of operations on the North American continent."
                    }
                    3 -> DialogLine("What did the Prospector do? Has he killed everyone and saved the world?") {
                        lineNumber = 4
                        text = "Not quite. The Prospector had thought it was too late to go back, and so he decided " +
                                "to wait for the dark and sneak onto one of the returning cargo ships."
                    }
                    4 -> DialogLine("[Sneak 69/70] Yay, ninja time!") {
                        lineNumber = 5
                        text = "Unfortunately, the Prospector was noticed by one of the guards, who has alerted EVERYONE around."
                    }
                    5 -> DialogLine("And then he defeated everyone?") {
                        lineNumber = 6
                        text = "See for yourself."
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
                    0 -> DialogLine("What about the Prospector?") {
                        lineNumber = 1
                        text =
                            "The guards lead the Prospector to one of the city prisons, where he was interrogated. " +
                                    "Initially, they believed that he was one of the spies from " +
                                    "the many \"savage tribes\" that inhabited the fallen America, but once they were " +
                                    "proven wrong, their interest in the Prospector died."
                    }
                    1 -> DialogLine("So, did they let him go?") {
                        lineNumber = 2
                        text = "No, for the time being he was left in a cell with another convict, " +
                                "where they would wait for whatever comes their way. " +
                                "The inmate offered a friendly game of caravan to kill time…"
                    }
                    -1 -> DialogLine("[FINISH]") { goBack() }
                    -2 -> DialogLine("So, who is he?") {
                        lineNumber = -1
                        text = "The Prospector learnt that the fellow inmate " +
                                "is a Brotherhood of Steel paladin who was sent to explore the Oasis but got " +
                                "captured by the Chinese. In truth, the capture was orchestrated – the " +
                                "paladin wished to get into the Chinese mainland in order to collect as much " +
                                "data and blueprints of technology as possible. He made a secret tunnel that " +
                                "lead outside, yet he wished not to escape. Paladin told the Prospector the shocking truth..."
                    }
                    else -> {
                        DialogLine("[FINISH]") { isGame = true; gameResult = -1 }
                    }
                }
            }
        }
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

    var selectedCard by remember { mutableStateOf<Int?>(null) }
    var selectedCaravan by remember { mutableIntStateOf(-1) }

    var caravansKey by rememberSaveable { mutableStateOf(true) }
    var enemyHandKey by rememberSaveable { mutableIntStateOf(0) }

    val animationSpeed = activity.animationSpeed.value ?: AnimationSpeed.NORMAL

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
        game.afterPlayerMove(
            { updateEnemyHand(); updateCaravans() },
            animationSpeed
        )
    }

    fun dropCaravan() {
        val selectedCaravanNN = selectedCaravan
        if (selectedCaravanNN == -1) return
        playVatsReady(activity)
        activity.processChallengesMove(Challenge.Move(moveCode = 1), game)
        game.playerCaravans[selectedCaravanNN].dropCaravan()
        resetSelected()
        game.afterPlayerMove(
            { updateEnemyHand(); updateCaravans() },
            animationSpeed
        )
    }

    fun addCardToCaravan(caravan: Caravan, position: Int, isEnemy: Boolean) {
        fun onCaravanCardInserted() {
            resetSelected()
            game.afterPlayerMove(
                { updateEnemyHand(); updateCaravans() },
                animationSpeed
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
                    activity.processChallengesMove(
                        Challenge.Move(
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
                        activity.processChallengesMove(
                            Challenge.Move(
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
                return@ShowGameRaw
            }

            showAlertDialog(activity.getString(R.string.check_back_to_menu),
                activity.getString(R.string.tower_progress_will_be_lost))
        },
        animationSpeed,
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
}