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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyCliff
import com.unicorns.invisible.caravan.model.enemy.EnemyCrocker
import com.unicorns.invisible.caravan.model.enemy.EnemyHouse
import com.unicorns.invisible.caravan.model.enemy.EnemyKing
import com.unicorns.invisible.caravan.model.enemy.EnemyRingo
import com.unicorns.invisible.caravan.model.enemy.EnemySunny
import com.unicorns.invisible.caravan.model.enemy.EnemySwank
import com.unicorns.invisible.caravan.model.enemy.EnemyYesMan
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Caravan
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.TextClassic
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.frankStopsRadio
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDialogBackground
import com.unicorns.invisible.caravan.utils.getDialogTextColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getStrokeColorByStyle
import com.unicorns.invisible.caravan.utils.getTextBackByStyle
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.nextSong
import com.unicorns.invisible.caravan.utils.playCardFlipSound
import com.unicorns.invisible.caravan.utils.playCashSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playFrankPhrase
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playNotificationSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playTowerCompleted
import com.unicorns.invisible.caravan.utils.playTowerFailed
import com.unicorns.invisible.caravan.utils.playVatsReady
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.radioLock
import com.unicorns.invisible.caravan.utils.radioPlayers
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.startLevel11Theme
import com.unicorns.invisible.caravan.utils.startRadio
import com.unicorns.invisible.caravan.utils.stopAmbient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.withLock


enum class Payment {
    FREE,
    ONE_HUNDRED_CAPS,
    TICKET
}


@Composable
fun TowerScreen(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit,
) {
    var payment by rememberSaveable { mutableStateOf<Payment?>(null) }
    var level by rememberSaveable { mutableIntStateOf(activity.save?.towerLevel ?: 0) }
    var isGameRigged by rememberSaveable { mutableStateOf(activity.save?.isGameRigged ?: false) }
    var startFrank by rememberSaveable { mutableStateOf(false) }
    if (startFrank) {
        ShowFrank(activity) { startFrank = false }
        return
    }

    var showGameLevel1 by rememberSaveable { mutableStateOf(false) }
    var showGameLevel2 by rememberSaveable { mutableStateOf(false) }
    var showGameLevel3 by rememberSaveable { mutableStateOf(false) }
    var showGameLevel4 by rememberSaveable { mutableStateOf(false) }
    var showGameLevel5 by rememberSaveable { mutableStateOf(false) }
    var showGameLevel6 by rememberSaveable { mutableStateOf(false) }
    var showGameLevel7 by rememberSaveable { mutableStateOf(false) }
    var showGameLevel8 by rememberSaveable { mutableStateOf(false) }
    var showGameLevel9 by rememberSaveable { mutableStateOf(false) }
    var showGameLevel10 by rememberSaveable { mutableStateOf(false) }
    var showGameLevel11 by rememberSaveable { mutableStateOf(false) }

    var levelMemory by rememberSaveable { mutableIntStateOf(0) }
    @Composable
    fun showTower(enemy: Enemy, goBack: () -> Unit) {
        StartTowerGame(activity, enemy, showAlertDialog, {
            levelMemory = level
            level = 0
            activity.save?.let {
                it.towerLevel = 0
                saveOnGD(activity)
            }
        }, {
            level = levelMemory + 1
            activity.save?.let {
                it.towerLevel = levelMemory + 1
                saveOnGD(activity)
            }
        }, {
            level = 0
            activity.save?.let {
                it.towerLevel = 0
                saveOnGD(activity)
            }
        }, goBack)
    }
    when {
        showGameLevel1 -> {
            showTower(EnemySunny) {
                showGameLevel1 = false
            }
            return
        }
        showGameLevel2 -> {
            showTower(EnemyRingo) {
                showGameLevel2 = false
            }
            return
        }
        showGameLevel3 -> {
            showTower(EnemyCliff) {
                showGameLevel3 = false
            }
            return
        }
        showGameLevel4 -> {
            showTower(EnemyYesMan) {
                showGameLevel4 = false
            }
            return
        }
        showGameLevel5 -> {
            showTower(EnemySwank) {
                showGameLevel5 = false
            }
            return
        }
        showGameLevel6 -> {
            showTower(EnemyCrocker) {
                showGameLevel6 = false
            }
            return
        }
        showGameLevel7 -> {
            showTower(EnemyKing) {
                showGameLevel7 = false
            }
            return
        }
        showGameLevel8 -> {
            showTower(EnemyHouse) {
                showGameLevel8 = false
            }
            return
        }
        showGameLevel9 -> {
            showTower(EnemyRingo) {
                showGameLevel9 = false
            }
            return
        }

        showGameLevel10 -> {
            showTower(EnemyRingo) {
                showGameLevel10 = false
            }
            return
        }

        showGameLevel11 -> {
            StartTowerGame(activity, EnemyRingo, showAlertDialog, {
                startLevel11Theme(activity)
                playFrankPhrase(activity, R.raw.frank_on_game_start)
                levelMemory = level
                level = 0
                activity.save?.let {
                    it.towerLevel = 0
                    saveOnGD(activity)
                }
            }, {
                level = levelMemory + 1
                activity.save?.let {
                    it.towerLevel = levelMemory + 1
                    it.isEnclaveThemeAvailable = true
                    saveOnGD(activity)
                }
                radioLock.withLock {
                    radioPlayers.forEach {
                        it.stop()
                        radioPlayers.remove(it)
                        it.release()
                    }
                }
                playFrankPhrase(activity, R.raw.frank_on_defeat)
            }, {
                level = 0
                activity.save?.let {
                    it.towerLevel = 0
                    saveOnGD(activity)
                }
                radioLock.withLock {
                    radioPlayers.forEach {
                        it.stop()
                        radioPlayers.remove(it)
                        it.release()
                    }
                }
            }) {
                showGameLevel11 = false
                frankStopsRadio = false
                isGameRigged = false
                activity.save?.let {
                    it.isGameRigged = false
                    saveOnGD(activity)
                }

                CoroutineScope(Dispatchers.Unconfined).launch {
                    if (level == 0) {
                        playTowerFailed(activity)
                    } else {
                        playTowerCompleted(activity)
                    }
                    delay(15000L)
                    nextSong(activity)
                }
            }
            return
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val state2 = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxHeight(0.8f)
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
                Box(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFallout(
                            stringResource(R.string.tower),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            32.sp,
                            Alignment.Center,
                            Modifier,
                            TextAlign.Center
                        )
                    }
                }
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!isGameRigged) {
                        TextFallout(
                            "(Custom deck only!)",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            24.sp,
                            Alignment.Center,
                            Modifier,
                            TextAlign.Center
                        )
                        TextFallout(
                            "(You can change custom deck between games.)",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            16.sp,
                            Alignment.Center,
                            Modifier,
                            TextAlign.Center
                        )
                    } else {
                        TextFallout(
                            "(Deck o' 54 only!)",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            24.sp,
                            Alignment.Center,
                            Modifier,
                            TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    TextFallout(
                        "(Progress is saved between sessions!).",
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        24.sp,
                        Alignment.Center,
                        Modifier,
                        TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))

                if (level == 0) {
                    TextFallout(
                        "Tickets, please!\n(You have tickets: ${activity.save?.tickets ?: 0};\ncaps: ${activity.save?.caps ?: 0}.)",
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        20.sp,
                        Alignment.Center,
                        Modifier,
                        TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                        TextFallout(
                            "Play for free (NO JACKPOT!)",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            20.sp,
                            Alignment.Center,
                            Modifier
                                .border(
                                    BorderStroke(
                                        if (payment == Payment.FREE) 3.dp else (-1).dp,
                                        getSelectionColor(activity)
                                    )
                                )
                                .padding(4.dp)
                                .clickable {
                                    if (payment == Payment.FREE) {
                                        payment = null
                                        playCloseSound(activity)
                                    } else {
                                        payment = Payment.FREE
                                        playSelectSound(activity)
                                    }
                                }
                                .background(getTextBackgroundColor(activity))
                                .padding(4.dp),
                            TextAlign.Center
                        )
                        TextFallout(
                            "Pay 1 ticket",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            20.sp,
                            Alignment.Center,
                            Modifier
                                .border(
                                    BorderStroke(
                                        if (payment == Payment.TICKET) 3.dp else (-1).dp,
                                        getSelectionColor(activity)
                                    )
                                )
                                .padding(4.dp)
                                .clickable {
                                    if (payment == Payment.TICKET) {
                                        payment = null
                                        playCloseSound(activity)
                                    } else {
                                        payment = Payment.TICKET
                                        playSelectSound(activity)
                                    }
                                }
                                .background(getTextBackgroundColor(activity))
                                .padding(4.dp),
                            TextAlign.Center
                        )
                        TextFallout(
                            "Pay 100 caps",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            20.sp,
                            Alignment.Center,
                            Modifier
                                .border(
                                    BorderStroke(
                                        if (payment == Payment.ONE_HUNDRED_CAPS) 3.dp else (-1).dp,
                                        getSelectionColor(activity)
                                    )
                                )
                                .padding(4.dp)
                                .clickable {
                                    if (payment == Payment.ONE_HUNDRED_CAPS) {
                                        payment = null
                                        playCloseSound(activity)
                                    } else {
                                        payment = Payment.ONE_HUNDRED_CAPS
                                        playSelectSound(activity)
                                    }
                                }
                                .background(getTextBackgroundColor(activity))
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                    TextFallout(
                        stringResource(R.string.start),
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        24.sp,
                        Alignment.Center,
                        modifier = Modifier
                            .clickableOk(activity) {
                                if (payment == null) {
                                    showAlertDialog("HEY!", "Select payment method.")
                                    return@clickableOk
                                }

                                when (payment) {
                                    Payment.ONE_HUNDRED_CAPS -> {
                                        if ((activity.save?.caps ?: 0) < 100) {
                                            showAlertDialog("HEY!", "You don't have enough cash, kid.")
                                            return@clickableOk
                                        }
                                    }
                                    Payment.TICKET -> {
                                        if ((activity.save?.tickets ?: 0) < 1) {
                                            showAlertDialog("HEY!", "You don't have a ticket on you.")
                                            return@clickableOk
                                        }
                                    }
                                    else -> {}
                                }

                                level++
                                activity.save?.let {
                                    it.towerLevel++

                                    when (payment) {
                                        Payment.ONE_HUNDRED_CAPS -> {
                                            it.isTowerFree = false
                                            it.caps -= 100
                                        }
                                        Payment.TICKET -> {
                                            it.isTowerFree = false
                                            it.tickets--
                                        }
                                        else -> {
                                            it.isTowerFree = true
                                        }
                                    }

                                    saveOnGD(activity)
                                }
                            }
                            .background(getTextBackgroundColor(activity))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        TextAlign.Center
                    )
                } else {
                    val inBank = if (activity.save?.isTowerFree == false) {
                        when (level) {
                            1 -> 1
                            2 -> 5
                            3 -> 10
                            4 -> 25
                            5 -> 50
                            6 -> 101
                            7 -> 166
                            8 -> 222
                            9 -> 333
                            10 -> 444
                            11 -> 555
                            else -> 999
                        }
                    } else {
                        0
                    }
                    @Composable
                    fun showTowerCard(enemyName: String) {
                        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            if (!(level == 11 && !isGameRigged)) {
                                TextFallout(
                                    "Level: $level / 10",
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    24.sp,
                                    Alignment.Center,
                                    Modifier,
                                    TextAlign.Center
                                )
                                TextFallout(
                                    "Currently in bank: $inBank caps",
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    24.sp,
                                    Alignment.Center,
                                    Modifier,
                                    TextAlign.Center
                                )
                                TextFallout(
                                    "Enemy: $enemyName",
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    24.sp,
                                    Alignment.Center,
                                    Modifier,
                                    TextAlign.Center
                                )
                            } else {
                                TextFallout(
                                    "Your reward: $inBank caps",
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    24.sp,
                                    Alignment.Center,
                                    Modifier,
                                    TextAlign.Center
                                )
                            }
                        }
                    }
                    when (level) {
                        1 -> {
                            showTowerCard("Sunny Smiles")
                        }
                        2 -> {
                            showTowerCard("Ringo")
                        }
                        3 -> {
                            showTowerCard("Cliff Briscoe")
                        }
                        4 -> {
                            showTowerCard("Yes Man")
                        }
                        5 -> {
                            showTowerCard(stringResource(R.string.pve_enemy_queen))
                        }
                        6 -> {
                            showTowerCard("Ambassador Crocker")
                        }
                        7 -> {
                            showTowerCard("The King")
                        }
                        8 -> {
                            showTowerCard(stringResource(R.string.mr_house))
                        }
                        9 -> {
                            showTowerCard("General Lee Oliver")
                        }
                        10 -> {
                            showTowerCard("Caesar")
                        }
                        11 -> {
                            if (isGameRigged) {
                                showTowerCard("Frank Horrigan")
                            } else {
                                showTowerCard("")
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        if (!isGameRigged) {
                            TextFallout(
                                "Take the cash!",
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                24.sp,
                                Alignment.Center,
                                modifier = Modifier
                                    .clickableOk(activity) {
                                        if (isGameRigged) {
                                            isGameRigged = false
                                            frankStopsRadio = false
                                            startRadio(activity)
                                            activity.save?.let {
                                                it.isGameRigged = false
                                                saveOnGD(activity)
                                            }
                                        }
                                        level = 0
                                        payment = null
                                        activity.save?.let {
                                            it.towerLevel = 0
                                            it.caps += inBank
                                            saveOnGD(activity)
                                        }
                                        playCashSound(activity)
                                        showAlertDialog("Congratulations!", "You have earned $inBank caps!")
                                    }
                                    .background(getTextBackgroundColor(activity))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                TextAlign.Center
                            )
                        }
                        if (!(level == 11 && !isGameRigged) && level != 12) {
                            TextFallout(
                                "En garde!",
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                24.sp,
                                Alignment.Center,
                                modifier = Modifier
                                    .clickableOk(activity) {
                                        when (level) {
                                            1 -> { showGameLevel1 = true }
                                            2 -> { showGameLevel2 = true }
                                            3 -> { showGameLevel3 = true }
                                            4 -> { showGameLevel4 = true }
                                            5 -> { showGameLevel5 = true }
                                            6 -> { showGameLevel6 = true }
                                            7 -> { showGameLevel7 = true }
                                            8 -> { showGameLevel8 = true }
                                            9 -> { showGameLevel9 = true }
                                            10 -> { showGameLevel10 = true }
                                            11 -> { showGameLevel11 = true }
                                            else -> {}
                                        }
                                    }
                                    .background(getTextBackgroundColor(activity))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                TextAlign.Center
                            )
                        } else if (level != 12) {
                            var timesClicked by rememberSaveable { mutableIntStateOf(0) }
                            TextClassic(
                                when (timesClicked) {
                                    0 -> "Rig the game"
                                    1 -> "Rig"
                                    2 -> "Oil Rig"
                                    3 -> "Enclave Oil Rig"
                                    4 -> "En Oil Rig"
                                    5 -> "E nORig"
                                    6 -> "F hORig"
                                    else -> "???"
                                },
                                getTextColorByStyle(activity, Style.PIP_BOY),
                                getStrokeColorByStyle(activity, Style.PIP_BOY),
                                18.sp,
                                Alignment.Center,
                                modifier = Modifier
                                    .clickableSelect(activity) {
                                        timesClicked++
                                        if (timesClicked >= 7) {
                                            isGameRigged = true
                                            activity.save?.let {
                                                it.isGameRigged = true
                                                startFrank = true
                                                saveOnGD(activity)
                                            }
                                        }
                                    }
                                    .background(getTextBackByStyle(activity, Style.PIP_BOY))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                TextAlign.Center
                            )
                        }
                    }
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
                .clickableCancel(activity) { if (!isGameRigged) goBack() }
                .background(getTextBackgroundColor(activity))
                .padding(8.dp),
            TextAlign.Center
        )
    }
}

@Composable
fun StartTowerGame(
    activity: MainActivity,
    enemy: Enemy,
    showAlertDialog: (String, String) -> Unit,
    onStart: () -> Unit,
    onWin: () -> Unit,
    onLose: () -> Unit,
    goBack: () -> Unit,
) {
    var showIntro by rememberSaveable { mutableStateOf(false) }
    var showFrankOutro by rememberSaveable { mutableStateOf(false) }
    if (showIntro) {
        playNotificationSound(activity) {}
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = Color(activity.getColor(R.color.colorText))),
            onDismissRequest = { showIntro = false },
            confirmButton = {
                TextClassic(
                    "OK",
                    Color(activity.getColor(R.color.colorTextBack)),
                    Color(activity.getColor(R.color.colorTextBack)),
                    18.sp, Alignment.Center,
                    Modifier
                        .background(Color(activity.getColor(R.color.colorText)))
                        .clickableCancel(activity) { showIntro = false}
                        .padding(4.dp),
                    TextAlign.Center
                )
            },
            title = {
                TextClassic(
                    "You're not a hero. You're just a walking corpse.",
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    24.sp, Alignment.CenterStart, Modifier,
                    TextAlign.Start
                )
            },
            text = {
                TextClassic(
                    "Now playing: Loyalty to Your People - Neon Light Man.",
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

    if (showFrankOutro) {
        playNotificationSound(activity) {}
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = Color(activity.getColor(R.color.colorText))),
            onDismissRequest = { showFrankOutro = false; goBack() },
            confirmButton = {
                TextClassic(
                    "[FINISH]",
                    Color(activity.getColor(R.color.colorTextBack)),
                    Color(activity.getColor(R.color.colorTextBack)),
                    18.sp, Alignment.Center,
                    Modifier
                        .background(Color(activity.getColor(R.color.colorText)))
                        .clickableCancel(activity) { showFrankOutro = false; goBack() }
                        .padding(4.dp),
                    TextAlign.Center
                )
            },
            title = {
                TextClassic(
                    activity.getString(R.string.you_win),
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    24.sp, Alignment.CenterStart, Modifier,
                    TextAlign.Start
                )
            },
            text = {
                TextClassic(
                    "The work will go on. You didn't do nothing here, 'cept seal your own death warrants. Duty, (cough) honor… courage… Semper Fiiiii……",
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

    val playerCResources = if (frankStopsRadio) {
        CResources(CustomDeck(CardBack.STANDARD, false))
    } else {
        CResources(activity.save?.getCustomDeckCopy() ?: CustomDeck(CardBack.STANDARD, false))
    }
    val game = rememberScoped {
        if (frankStopsRadio) {
            showIntro = true
        }
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
            if (frankStopsRadio) {
                showFrankOutro = true
            } else {
                showAlertDialog(
                    activity.getString(R.string.result),
                    activity.getString(R.string.you_win)
                )
            }
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
    }

    activity.goBack = { stopAmbient(); goBack() }

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
                activity.goBack = null
                return@ShowGameRaw
            }

            showAlertDialog(activity.getString(R.string.check_back_to_menu), "Tower progress will be lost!")
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


@Composable
fun ShowFrank(activity: MainActivity, goBack: () -> Unit) {
    var showFrankFlag by rememberSaveable { mutableStateOf(false) }
    var showDialogs by rememberSaveable { mutableStateOf(false) }

    var whoAreYouAsked by rememberSaveable { mutableStateOf(false) }
    var letsTalkAsked by rememberSaveable { mutableStateOf(false) }
    var whoIAmAsked by rememberSaveable { mutableStateOf(false) }
    var iChallengeYou by rememberSaveable { mutableStateOf(false) }

    var text by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (!showDialogs) {
            radioLock.withLock {
                radioPlayers.forEach {
                    it.stop()
                    radioPlayers.remove(it)
                    it.release()
                }
                frankStopsRadio = true
            }
            playFrankPhrase(activity, R.raw.frank_on_welcome)
            text = "You've gotten a lot farther than you should have, but then you haven't met Frank Horrigan either. Your ride's over, mutie. Time to die."
            delay(3000L)
            showFrankFlag = true
            delay(10000L)
            showDialogs = true
        }
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (showFrankFlag) {
            Column {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .paint(painterResource(
                            id = R.drawable.frank_head
                        )))

                Box(Modifier.fillMaxWidth()) {
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
                }

                if (showDialogs) {
                    Spacer(Modifier.height(32.dp))
                    val state = rememberLazyListState()
                    LazyColumn(
                        Modifier.fillMaxSize().scrollbar(
                            state,
                            alignEnd = false,
                            knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                            trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                            horizontal = false
                        ),
                        state = state
                    ) {
                        item {
                            if (!whoAreYouAsked && !letsTalkAsked && !iChallengeYou) {
                                TextClassic(
                                    "Frank who?",
                                    getTextColorByStyle(activity, Style.PIP_BOY),
                                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                                    18.sp,
                                    Alignment.CenterStart,
                                    modifier = Modifier
                                        .clickableSelect(activity) {
                                            whoAreYouAsked = true
                                            text = "Frank Horrigan, that's who. United States Secret Service. You aren't going anywhere from here."
                                            playFrankPhrase(activity, R.raw.frank_who_are_you)
                                        }
                                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    TextAlign.Start
                                )
                                Spacer(Modifier.height(12.dp))
                            }
                            if (!letsTalkAsked && !iChallengeYou) {
                                TextClassic(
                                    "Wait, let's talk!",
                                    getTextColorByStyle(activity, Style.PIP_BOY),
                                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                                    18.sp,
                                    Alignment.CenterStart,
                                    modifier = Modifier
                                        .clickableSelect(activity) {
                                            letsTalkAsked = true
                                            text = "We just did. Time for talking's over."
                                            playFrankPhrase(activity, R.raw.frank_lets_talk)
                                        }
                                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    TextAlign.Start
                                )
                                Spacer(Modifier.height(12.dp))
                            }
                            if (!whoIAmAsked && !letsTalkAsked && !iChallengeYou) {
                                TextClassic(
                                    "You don't even know who I am.",
                                    getTextColorByStyle(activity, Style.PIP_BOY),
                                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                                    18.sp,
                                    Alignment.CenterStart,
                                    modifier = Modifier
                                        .clickableSelect(activity) {
                                            whoIAmAsked = true
                                            text = "You're just another mutant that needs to be put down."
                                            playFrankPhrase(activity, R.raw.frank_you_dont_even_know_who_i_am)
                                        }
                                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    TextAlign.Start
                                )
                                Spacer(Modifier.height(12.dp))
                            }
                            if (!iChallengeYou) {
                                TextClassic(
                                    "I challenge you to the game of Caravan!",
                                    getTextColorByStyle(activity, Style.PIP_BOY),
                                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                                    18.sp,
                                    Alignment.CenterStart,
                                    modifier = Modifier
                                        .clickableSelect(activity) {
                                            iChallengeYou = true
                                            text = "You mutant scum! Just like you to try a trick like that. It won't help you though, nothing will..."
                                            playFrankPhrase(activity, R.raw.frank_i_challenge_you)
                                        }
                                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    TextAlign.Start
                                )
                            } else {
                                TextClassic(
                                    "[FINISH]",
                                    getTextColorByStyle(activity, Style.PIP_BOY),
                                    getStrokeColorByStyle(activity, Style.PIP_BOY),
                                    18.sp,
                                    Alignment.CenterStart,
                                    modifier = Modifier
                                        .clickableSelect(activity) {
                                            goBack()
                                        }
                                        .background(getTextBackByStyle(activity, Style.PIP_BOY))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}