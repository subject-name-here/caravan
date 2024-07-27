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
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyCaesar
import com.unicorns.invisible.caravan.model.enemy.EnemyCliff
import com.unicorns.invisible.caravan.model.enemy.EnemyCrocker
import com.unicorns.invisible.caravan.model.enemy.EnemyFrank
import com.unicorns.invisible.caravan.model.enemy.EnemyHouse
import com.unicorns.invisible.caravan.model.enemy.EnemyKing
import com.unicorns.invisible.caravan.model.enemy.EnemyOliver
import com.unicorns.invisible.caravan.model.enemy.EnemyRingo
import com.unicorns.invisible.caravan.model.enemy.EnemySunny
import com.unicorns.invisible.caravan.model.enemy.EnemySwank
import com.unicorns.invisible.caravan.model.enemy.EnemyYesMan
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextClassic
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getBackgroundColor
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
import com.unicorns.invisible.caravan.utils.playCashSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playFrankPhrase
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playNotificationSound
import com.unicorns.invisible.caravan.utils.playNukeBlownSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playTowerCompleted
import com.unicorns.invisible.caravan.utils.playTowerFailed
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.startLevel11Theme
import com.unicorns.invisible.caravan.utils.stopAmbient
import com.unicorns.invisible.caravan.utils.stopRadio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
    var timesClicked by rememberSaveable { mutableIntStateOf(0) }
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
            showTower(EnemyOliver) {
                showGameLevel9 = false
            }
            return
        }
        showGameLevel10 -> {
            showTower(EnemyCaesar) {
                showGameLevel10 = false
            }
            return
        }

        showGameLevel11 -> {
            StartTowerGame(activity, EnemyFrank, showAlertDialog, {
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
                stopRadio()
                playFrankPhrase(activity, R.raw.frank_on_defeat)
            }, {
                level = 0
                activity.save?.let {
                    it.towerLevel = 0
                    saveOnGD(activity)
                }
                stopRadio()
            }) {
                showGameLevel11 = false
                isSoundEffectsReduced = false
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
                    delay(14000L)
                    nextSong(activity)
                }
            }
            return
        }
    }

    MenuItemOpen(activity, stringResource(R.string.tower), "<-", {
        if (!isGameRigged) goBack()
    }) {
        val state2 = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity))
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
                    if (isGameRigged) {
                        TextFallout(
                            stringResource(R.string.deck_o_54_only),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            24.sp,
                            Alignment.Center,
                            Modifier,
                            TextAlign.Center
                        )
                    } else {
                        TextFallout(
                            stringResource(R.string.custom_deck_only),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            24.sp,
                            Alignment.Center,
                            Modifier,
                            TextAlign.Center
                        )
                        TextFallout(
                            stringResource(R.string.you_can_change_custom_deck_between_games),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            16.sp,
                            Alignment.Center,
                            Modifier,
                            TextAlign.Center
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    TextFallout(
                        stringResource(R.string.progress_is_saved_between_sessions),
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
                        stringResource(
                            R.string.tickets_please_you_have_tickets_caps,
                            activity.save?.tickets ?: 0,
                            activity.save?.caps ?: 0
                        ),
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
                            stringResource(R.string.play_for_free_no_jackpot),
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
                            stringResource(R.string.pay_1_ticket),
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
                            stringResource(R.string.pay_100_caps),
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
                                    showAlertDialog(
                                        activity.getString(R.string.hey),
                                        activity.getString(R.string.select_payment_method)
                                    )
                                    return@clickableOk
                                }

                                when (payment) {
                                    Payment.ONE_HUNDRED_CAPS -> {
                                        if ((activity.save?.caps ?: 0) < 100) {
                                            showAlertDialog(
                                                activity.getString(R.string.hey),
                                                activity.getString(R.string.you_don_t_have_enough_cash_kid)
                                            )
                                            return@clickableOk
                                        }
                                    }

                                    Payment.TICKET -> {
                                        if ((activity.save?.tickets ?: 0) < 1) {
                                            showAlertDialog(
                                                activity.getString(R.string.hey),
                                                activity.getString(R.string.you_don_t_have_a_ticket_on_you)
                                            )
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
                                    stringResource(R.string.level_10, level),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    24.sp,
                                    Alignment.Center,
                                    Modifier,
                                    TextAlign.Center
                                )
                                TextFallout(
                                    stringResource(R.string.currently_in_bank_caps, inBank),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    24.sp,
                                    Alignment.Center,
                                    Modifier,
                                    TextAlign.Center
                                )
                                TextFallout(
                                    stringResource(R.string.enemy, enemyName),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    24.sp,
                                    Alignment.Center,
                                    Modifier,
                                    TextAlign.Center
                                )
                            } else {
                                TextFallout(
                                    stringResource(R.string.your_reward_caps, inBank),
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
                            showTowerCard(stringResource(R.string.sunny_smiles))
                        }
                        2 -> {
                            showTowerCard(stringResource(R.string.ringo))
                        }
                        3 -> {
                            showTowerCard(stringResource(R.string.cliff_briscoe))
                        }
                        4 -> {
                            showTowerCard(stringResource(R.string.yes_man))
                        }
                        5 -> {
                            showTowerCard(stringResource(R.string.pve_enemy_queen))
                        }
                        6 -> {
                            showTowerCard(stringResource(R.string.ambassador_crocker))
                        }
                        7 -> {
                            showTowerCard(stringResource(R.string.the_king))
                        }
                        8 -> {
                            showTowerCard(stringResource(R.string.mr_house))
                        }
                        9 -> {
                            showTowerCard(stringResource(R.string.general_lee_oliver))
                        }
                        10 -> {
                            showTowerCard(stringResource(R.string.caesar))
                        }
                        11 -> {
                            showTowerCard(stringResource(R.string.frank_horrigan))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        if (!isGameRigged) {
                            if (level <= 5) {
                                val cost = inBank * 10
                                TextFallout(
                                    stringResource(R.string.skip_for_caps, cost),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    24.sp,
                                    Alignment.Center,
                                    modifier = Modifier
                                        .clickableOk(activity) {
                                            if ((activity.save?.caps ?: 0) < cost) {
                                                showAlertDialog(
                                                    activity.getString(R.string.hey),
                                                    activity.getString(R.string.you_don_t_have_enough_cash_kid)
                                                )
                                                return@clickableOk
                                            }

                                            level++
                                            activity.save?.let {
                                                it.towerLevel++
                                                it.caps -= cost
                                                saveOnGD(activity)
                                            }
                                            playCashSound(activity)
                                            showAlertDialog(
                                                activity.getString(R.string.skipped),
                                                activity.getString(
                                                    R.string.you_ve_paid_caps,
                                                    cost.toString()
                                                )
                                            )
                                        }
                                        .background(getTextBackgroundColor(activity))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    TextAlign.Center
                                )
                            } else {
                                TextFallout(
                                    stringResource(R.string.take_the_cash),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    24.sp,
                                    Alignment.Center,
                                    modifier = Modifier
                                        .clickableOk(activity) {
                                            level = 0
                                            payment = null
                                            activity.save?.let {
                                                it.towerLevel = 0
                                                it.caps += inBank
                                                saveOnGD(activity)
                                            }
                                            playCashSound(activity)
                                            showAlertDialog(
                                                activity.getString(R.string.congratulations),
                                                activity.getString(
                                                    R.string.you_have_earned_caps,
                                                    inBank.toString()
                                                )
                                            )
                                        }
                                        .background(getTextBackgroundColor(activity))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    TextAlign.Center
                                )
                            }
                        }
                        if (!(level == 11 && !isGameRigged) && level != 12) {
                            TextFallout(
                                stringResource(R.string.en_garde),
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                24.sp,
                                Alignment.Center,
                                modifier = Modifier
                                    .clickableOk(activity) {
                                        when (level) {
                                            1 -> {
                                                showGameLevel1 = true
                                            }

                                            2 -> {
                                                showGameLevel2 = true
                                            }

                                            3 -> {
                                                showGameLevel3 = true
                                            }

                                            4 -> {
                                                showGameLevel4 = true
                                            }

                                            5 -> {
                                                showGameLevel5 = true
                                            }

                                            6 -> {
                                                showGameLevel6 = true
                                            }

                                            7 -> {
                                                showGameLevel7 = true
                                            }

                                            8 -> {
                                                showGameLevel8 = true
                                            }

                                            9 -> {
                                                showGameLevel9 = true
                                            }

                                            10 -> {
                                                showGameLevel10 = true
                                            }

                                            11 -> {
                                                showGameLevel11 = true
                                            }

                                            else -> {}
                                        }
                                    }
                                    .background(getTextBackgroundColor(activity))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                TextAlign.Center
                            )
                        } else if (level != 12) {
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
                                            startFrank = true
                                            activity.save?.let {
                                                it.isGameRigged = true
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
    val isFrankSequence = enemy is EnemyFrank
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
                        .clickableCancel(activity) { showIntro = false }
                        .padding(4.dp),
                    TextAlign.Center
                )
            },
            title = {
                TextClassic(
                    stringResource(R.string.you_re_not_a_hero_you_re_just_a_walking_corpse),
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    24.sp, Alignment.CenterStart, Modifier,
                    TextAlign.Start
                )
            },
            text = {
                TextClassic(
                    stringResource(R.string.now_playing_loyalty_to_your_people_neon_light_man),
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
                    stringResource(R.string.finish),
                    Color(activity.getColor(R.color.colorTextBack)),
                    Color(activity.getColor(R.color.colorTextBack)),
                    18.sp, Alignment.Center,
                    Modifier
                        .background(Color(activity.getColor(R.color.colorText)))
                        .clickableCancel(activity) {
                            showFrankOutro = false; activity.goBack?.invoke()
                        }
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
                    stringResource(R.string.frank_final_words),
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

    val playerCResources = if (isFrankSequence) {
        CResources(CustomDeck(CardBack.STANDARD, false))
    } else {
        CResources(activity.save?.getCustomDeckCopy() ?: CustomDeck(CardBack.STANDARD, false))
    }
    val game = rememberScoped {
        if (isFrankSequence) {
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
            if (isFrankSequence) {
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
            stopRadio()
            isSoundEffectsReduced = true
            playFrankPhrase(activity, R.raw.frank_on_welcome)
            text = activity.getString(R.string.frank_welcome)
            delay(3000L)
            showFrankFlag = true
            delay(10000L)
            showDialogs = true
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)) {
        if (showFrankFlag) {
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
                        Modifier
                            .fillMaxSize()
                            .scrollbar(
                                state,
                                alignEnd = false,
                                knobColor = getTextColorByStyle(activity, Style.PIP_BOY),
                                trackColor = getStrokeColorByStyle(activity, Style.PIP_BOY),
                                horizontal = false
                            ),
                        state = state
                    ) {
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
                        item {
                            if (!whoAreYouAsked && !letsTalkAsked && !iChallengeYou) {
                                DialogLine(stringResource(R.string.frank_who)) {
                                    whoAreYouAsked = true
                                    text = activity.getString(R.string.frank_horrigan_that_s_who)
                                    playFrankPhrase(activity, R.raw.frank_who_are_you)
                                }
                            }
                            if (!letsTalkAsked && !iChallengeYou) {
                                DialogLine(stringResource(R.string.wait_let_s_talk)) {
                                    letsTalkAsked = true
                                    text = activity.getString(R.string.we_just_did_time_for_talking_s_over)
                                    playFrankPhrase(activity, R.raw.frank_lets_talk)
                                }
                            }
                            if (!whoIAmAsked && !letsTalkAsked && !iChallengeYou) {
                                DialogLine(stringResource(R.string.you_don_t_even_know_who_i_am)) {
                                    whoIAmAsked = true
                                    text = activity.getString(R.string.you_re_just_another_mutant_that_needs_to_be_put_down)
                                    playFrankPhrase(activity, R.raw.frank_you_dont_even_know_who_i_am)
                                }
                            }
                            if (!iChallengeYou) {
                                DialogLine(stringResource(R.string.i_challenge_you_to_the_game_of_caravan)) {
                                    iChallengeYou = true
                                    text = activity.getString(R.string.you_mutant_scum)
                                    playFrankPhrase(activity, R.raw.frank_i_challenge_you)
                                }
                            } else {
                                DialogLine(stringResource(R.string.finish)) {
                                    goBack()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}