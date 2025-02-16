package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyFrank
import com.unicorns.invisible.caravan.model.enemy.EnemyTower1
import com.unicorns.invisible.caravan.model.enemy.EnemyTower2
import com.unicorns.invisible.caravan.model.enemy.EnemyTower3
import com.unicorns.invisible.caravan.model.enemy.EnemyTower4
import com.unicorns.invisible.caravan.model.enemy.EnemyTower5
import com.unicorns.invisible.caravan.model.enemy.EnemyTower6
import com.unicorns.invisible.caravan.model.enemy.EnemyTower7
import com.unicorns.invisible.caravan.model.enemy.EnemyTower8
import com.unicorns.invisible.caravan.model.enemy.EnemyTower9
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextClassic
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
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
import com.unicorns.invisible.caravan.utils.playCashSound
import com.unicorns.invisible.caravan.utils.playFrankPhrase
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playMinigunSound
import com.unicorns.invisible.caravan.utils.playNotificationSound
import com.unicorns.invisible.caravan.utils.playNukeBlownSound
import com.unicorns.invisible.caravan.utils.playTowerCompleted
import com.unicorns.invisible.caravan.utils.playTowerFailed
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.startAmbient
import com.unicorns.invisible.caravan.utils.startLevel11Theme
import com.unicorns.invisible.caravan.utils.stopAmbient
import com.unicorns.invisible.caravan.utils.stopRadio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun TowerScreen(
    activity: MainActivity,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit,
) {
    var level by rememberSaveable { mutableIntStateOf(save.towerLevel) }
    var playLevel by rememberScoped { mutableIntStateOf(0) }
    var levelMemory by rememberScoped { mutableIntStateOf(0) }

    var startFrank by rememberSaveable { mutableStateOf(false) }
    if (startFrank) {
        ShowFrank(activity) { startFrank = false }
        return
    }

    @Composable
    fun showTower(enemy: Enemy, goBack: () -> Unit) {
        StartTowerGame(activity, enemy, showAlertDialog, {
            levelMemory = level
            level = 0
            save.towerLevel = 0
            saveData(activity)
        }, {
            level = levelMemory + 1
            save.towerLevel = levelMemory + 1
            levelMemory = 0
            when (level) {
                6 -> {
                    activity.achievementsClient?.unlock(activity.getString(R.string.achievement_five_down_five_to_go))
                }
                10 -> {
                    activity.achievementsClient?.unlock(activity.getString(R.string.achievement_1010))
                }
            }
            saveData(activity)
        }, {
            levelMemory = 0
            level = 0
            save.towerLevel = 0
            saveData(activity)
        }, goBack)
    }

    when (playLevel) {
        in 1..9 -> {
            showTower(
                when (playLevel) {
                    1 -> EnemyTower1
                    2 -> EnemyTower2
                    3 -> EnemyTower3
                    4 -> EnemyTower4
                    5 -> EnemyTower5
                    6 -> EnemyTower6
                    7 -> EnemyTower7
                    8 -> EnemyTower8
                    else -> EnemyTower9
                }
            ) { playLevel = 0 }
            return
        }
        11 -> {
            var capsMemory by rememberScoped { mutableIntStateOf(0) }
            StartTowerGame(activity, EnemyFrank, showAlertDialog, {
                startLevel11Theme(activity)
                playFrankPhrase(activity, R.raw.frank_on_game_start)
                levelMemory = level
                level = 0
                save.towerLevel = 0
                capsMemory = save.capsInHand
                save.capsInHand = 0
                saveData(activity)
            }, {
                level = levelMemory + 1
                save.towerLevel = levelMemory + 1
                levelMemory = 0
                save.capsInHand += capsMemory
                saveData(activity)
                stopRadio()
                playFrankPhrase(activity, R.raw.frank_on_defeat)
                activity.achievementsClient?.unlock(activity.getString(R.string.achievement_aint_no_way_dev))
            }, {
                levelMemory = 0
                level = 0
                save.towerLevel = 0
                saveData(activity)
                stopRadio()
            }) {
                playLevel = 0
                soundReduced = false

                CoroutineScope(Dispatchers.Unconfined).launch {
                    if (level == 0) {
                        playTowerFailed(activity)
                    } else {
                        playTowerCompleted(activity)
                    }
                    delay(14000L)
                    if (!soundReduced) {
                        nextSong(activity)
                    }
                }
            }
            return
        }
    }

    var showFrankWarning by rememberSaveable { mutableStateOf(false) }
    MenuItemOpen(activity, stringResource(R.string.tower), "<-", { if (level != 11) goBack() }) {
        when (level) {
            0 -> {
                StartScreen(activity, { p1, p2 -> showAlertDialog(p1, p2, null) }) {
                    level++
                    save.towerLevel++
                    saveData(activity)
                }
            }
            in 1..10 -> {
                val inBank = when (level) {
                    1 -> 2
                    2 -> 4
                    3 -> 8
                    4 -> 16
                    5 -> 32
                    6 -> 64
                    7 -> 128
                    8 -> 256
                    9 -> 512
                    10 -> 1024
                    else -> 0
                }
                EnemyPresentedScreen(
                    activity,
                    inBank,
                    { level },
                    { playLevel = level },
                    {
                        level = 0
                        save.towerLevel = 0
                        save.capsInHand += inBank
                        saveData(activity)
                        playCashSound(activity)
                        showAlertDialog(
                            activity.getString(R.string.congratulations),
                            activity.getString(
                                R.string.your_reward_caps,
                                inBank.toString()
                            ),
                            null
                        )
                    }
                )
            }
            11 -> {
                FrankPresentedScreen(
                    activity,
                    { showFrankWarning = true },
                    {
                        level = 0
                        if (soundReduced) {
                            soundReduced = false
                            nextSong(activity)
                        }
                        save.towerLevel = 0
                        save.capsInHand += 2048
                        saveData(activity)
                        playCashSound(activity)
                        showAlertDialog(
                            activity.getString(R.string.congratulations),
                            activity.getString(R.string.your_reward_caps_2048),
                            null
                        )
                    }
                )
            }
            else -> {
                FinalScreen(activity) {
                    level = 0
                    save.towerLevel = 0
                    save.capsInHand += 4096
                    saveData(activity)
                    CoroutineScope(Dispatchers.Unconfined).launch {
                        repeat(4) {
                            playCashSound(activity)
                            delay(190L)
                        }
                    }
                    showAlertDialog(
                        activity.getString(R.string.congratulations),
                        activity.getString(R.string.your_reward_caps_4096),
                        null
                    )
                }
            }
        }
    }


    if (showFrankWarning) {
        LaunchedEffect(Unit) {
            playNotificationSound(activity)
        }
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = Color(activity.getColor(R.color.colorText))),
            onDismissRequest = { showFrankWarning = false },
            confirmButton = {
                TextClassic(
                    stringResource(R.string.frank_start_button),
                    Color(activity.getColor(R.color.colorTextBack)),
                    Color(activity.getColor(R.color.colorTextBack)),
                    18.sp, Alignment.Center,
                    Modifier
                        .background(Color(activity.getColor(R.color.colorText)))
                        .clickableOk(activity) {
                            showFrankWarning = false; playLevel = 11
                        }
                        .padding(4.dp),
                    TextAlign.Center
                )
            },
            dismissButton = {
                TextClassic(
                    stringResource(R.string.frank_think_button),
                    Color(activity.getColor(R.color.colorTextBack)),
                    Color(activity.getColor(R.color.colorTextBack)),
                    18.sp, Alignment.Center,
                    Modifier
                        .background(Color(activity.getColor(R.color.colorText)))
                        .clickableCancel(activity) { showFrankWarning = false }
                        .padding(4.dp),
                    TextAlign.Center
                )
            },
            title = {
                TextClassic(
                    stringResource(R.string.frank_think_header),
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    24.sp, Alignment.CenterStart, Modifier,
                    TextAlign.Start
                )
            },
            text = {
                TextClassic(
                    stringResource(R.string.frank_think_body),
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
}

@Composable
fun StartScreen(
    activity: MainActivity,
    showAlertDialog: (String, String) -> Unit,
    nextLevel: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    val state2 = rememberLazyListState()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity))
            .padding(vertical = 8.dp)
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
            TextFallout(
                stringResource(R.string.tower_presents),
                getTextColor(activity),
                getTextStrokeColor(activity),
                18.sp,
                Alignment.Center,
                Modifier,
                TextAlign.Center
            )
            TextFallout(
                stringResource(R.string.tower),
                getTextColor(activity),
                getTextStrokeColor(activity),
                40.sp,
                Alignment.Center,
                Modifier,
                TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextFallout(
                stringResource(R.string.tower_starring_1),
                getTextColor(activity),
                getTextStrokeColor(activity),
                24.sp,
                Alignment.Center,
                Modifier,
                TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextFallout(
                stringResource(R.string.tower_starring_2),
                getTextColor(activity),
                getTextStrokeColor(activity),
                18.sp,
                Alignment.Center,
                Modifier,
                TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            TextFallout(
                stringResource(R.string.tickets_please_you_have_tickets, save.tickets),
                getTextColor(activity),
                getTextStrokeColor(activity),
                20.sp,
                Alignment.Center,
                Modifier,
                TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextFallout(
                stringResource(R.string.pay_1_ticket),
                getTextColor(activity),
                getTextStrokeColor(activity),
                20.sp,
                Alignment.Center,
                Modifier
                    .padding(4.dp)
                    .clickableOk(activity) {
                        if (save.tickets <= 0) {
                            showAlertDialog(
                                activity.getString(R.string.hey),
                                activity.getString(R.string.you_don_t_have_a_ticket_on_you),
                            )
                        } else {
                            save.tickets--
                            nextLevel()
                        }
                    }
                    .background(getTextBackgroundColor(activity))
                    .padding(4.dp),
                TextAlign.Center
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun EnemyPresentedScreen(
    activity: MainActivity,
    inBank: Int,
    getLevel: () -> Int,
    startLevel: () -> Unit,
    setLevelZero: () -> Unit,
) {
    val state2 = rememberLazyListState()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity))
            .padding(vertical = 8.dp)
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
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextFallout(
                    stringResource(R.string.custom_deck_only),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                TextFallout(
                    stringResource(R.string.you_can_change_custom_deck_between_games),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    16.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
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
                Spacer(modifier = Modifier.height(16.dp))
            }

            @Composable
            fun showTowerCard(enemyName: String) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    TextFallout(
                        stringResource(R.string.level_10, getLevel()),
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
                }
            }
            when (getLevel()) {
                1 -> {
                    showTowerCard(stringResource(R.string.tower_enemy_1))
                }
                2 -> {
                    showTowerCard(stringResource(R.string.tower_enemy_2))
                }
                3 -> {
                    if (save.papaSmurfActive) {
                        showTowerCard(stringResource(R.string.tower_enemy_3A))
                    } else {
                        showTowerCard(stringResource(R.string.tower_enemy_3))
                    }
                }
                4 -> {
                    showTowerCard(stringResource(R.string.tower_enemy_4))
                }
                5 -> {
                    showTowerCard(stringResource(R.string.tower_enemy_5))
                }
                6 -> {
                    showTowerCard(stringResource(R.string.tower_enemy_6))
                }
                7 -> {
                    showTowerCard(stringResource(R.string.tower_enemy_7))
                }
                8 -> {
                    showTowerCard(stringResource(R.string.tower_enemy_8))
                }
                9 -> {
                    showTowerCard(stringResource(R.string.tower_enemy_9))
                }
                10 -> {
                    showTowerCard(stringResource(R.string.tower_enemy_10))
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextFallout(
                    stringResource(R.string.take_the_cash),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Alignment.Center,
                    modifier = Modifier
                        .clickableOk(activity) {
                            setLevelZero()
                        }
                        .background(getTextBackgroundColor(activity))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Center
                )

                TextFallout(
                    stringResource(R.string.en_garde),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Alignment.Center,
                    modifier = Modifier
                        .clickableOk(activity) {
                            startLevel()
                        }
                        .background(getTextBackgroundColor(activity))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun FrankPresentedScreen(
    activity: MainActivity,
    startLevel: () -> Unit,
    setLevelZero: () -> Unit,
) {
    val state2 = rememberLazyListState()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity))
            .padding(vertical = 8.dp)
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
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                TextFallout(
                    stringResource(R.string.deck_o_54_only),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            val inBank = 2048
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextFallout(
                    stringResource(R.string.level_10, 10),
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
                    stringResource(R.string.enemy, stringResource(R.string.tower_enemy_10A)),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
            }

            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextFallout(
                    stringResource(R.string.take_the_cash_alt),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Alignment.Center,
                    modifier = Modifier
                        .clickableOk(activity) {
                            setLevelZero()
                        }
                        .background(getTextBackgroundColor(activity))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Center
                )

                TextFallout(
                    stringResource(R.string.en_f_garde),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Alignment.Center,
                    modifier = Modifier
                        .clickableOk(activity) {
                            startLevel()
                        }
                        .background(getTextBackgroundColor(activity))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun FinalScreen(activity: MainActivity, setLevelZero: () -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        TextFallout(
            stringResource(R.string.take_the_cash),
            getTextColor(activity),
            getTextStrokeColor(activity),
            24.sp,
            Alignment.Center,
            modifier = Modifier
                .clickableOk(activity) {
                    setLevelZero()
                }
                .background(getTextBackgroundColor(activity))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            TextAlign.Center
        )
    }
}

@Composable
fun StartTowerGame(
    activity: MainActivity,
    enemy: Enemy,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    onStart: () -> Unit,
    onWin: () -> Unit,
    onLose: () -> Unit,
    goBack: () -> Unit,
) {
    val isFrankSequence = enemy is EnemyFrank
    var showIntro by rememberSaveable { mutableStateOf(false) }
    var showFrankOutro by rememberSaveable { mutableStateOf(false) }
    if (showIntro) {
        playNotificationSound(activity)
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
                    stringResource(
                        if (!save.isHeroic) {
                            R.string.now_playing_loyalty_to_your_people_neon_light_man
                        } else {
                            R.string.now_playing_alt
                        }
                    ),
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
        playNotificationSound(activity)
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = Color(activity.getColor(R.color.colorText))),
            onDismissRequest = { showFrankOutro = false },
            confirmButton = {
                TextClassic(
                    stringResource(R.string.finish),
                    Color(activity.getColor(R.color.colorTextBack)),
                    Color(activity.getColor(R.color.colorTextBack)),
                    18.sp, Alignment.Center,
                    Modifier
                        .background(Color(activity.getColor(R.color.colorText)))
                        .clickableCancel(activity) { showFrankOutro = false }
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

    LaunchedEffect(Unit) { startAmbient(activity) }
    val onQuitPressed = { stopAmbient(); goBack() }
    val playerCResources = CResources(if (isFrankSequence) {
        CustomDeck(save.selectedDeck.first, save.selectedDeck.second)
    } else {
        save.getCustomDeckCopy()
    })
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
                    activity.getString(R.string.you_win),
                    onQuitPressed
                )
            }
            enemy.addVictory()
            saveData(activity)
        }
        it.onLose = {
            playLoseSound(activity)
            onLose()
            showAlertDialog(
                activity.getString(R.string.result),
                activity.getString(R.string.you_lose),
                onQuitPressed
            )
        }
        it.jokerPlayedSound = { playJokerSounds(activity) }
        it.nukeBlownSound = { playNukeBlownSound(activity) }
    }

    ShowGame(
        activity,
        game
    ) {
        if (game.isOver()) {
            onQuitPressed()
        } else {
            val body = if (isFrankSequence) {
                activity.getString(R.string.check_back_to_menu_body_tower_frank)
            } else {
                activity.getString(R.string.check_back_to_menu_body_tower)
            }
            showAlertDialog(
                activity.getString(R.string.check_back_to_menu),
                body,
                onQuitPressed
            )
        }
    }
}

// TODO: move to story sequences
@Composable
fun ShowFrank(activity: MainActivity, goBack: () -> Unit) {
    var craigLine by rememberSaveable { mutableIntStateOf(0) }

    var showFrankFlag by rememberSaveable { mutableStateOf(false) }
    var showDialogs by rememberSaveable { mutableStateOf(true) }

    var whoAreYouAsked by rememberSaveable { mutableStateOf(false) }
    var letsTalkAsked by rememberSaveable { mutableStateOf(false) }
    var whoIAmAsked by rememberSaveable { mutableStateOf(false) }
    var iChallengeYou by rememberSaveable { mutableStateOf(false) }

    var text by rememberScoped { mutableStateOf(activity.getString(R.string.craig_1)) }

    LaunchedEffect(craigLine) {
        if (craigLine == 3) {
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

        Column {
            Box(Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .paint(
                    painterResource(
                        if (showFrankFlag) R.drawable.frank_head else R.drawable.black_back
                    )
                )
            )

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
                        when (craigLine) {
                            0 -> {
                                DialogLine(stringResource(R.string.craig_continue)) {
                                    craigLine++
                                    text = activity.getString(R.string.craig_2)
                                }
                                return@item
                            }
                            1 -> {
                                DialogLine(stringResource(R.string.craig_continue)) {
                                    craigLine++
                                    stopRadio()
                                    soundReduced = true
                                    playMinigunSound(activity)
                                    text = activity.getString(R.string.craig_3)
                                }
                                return@item
                            }
                            2 -> {
                                DialogLine(stringResource(R.string.craig_continue)) {
                                    showDialogs = false
                                    craigLine++
                                    text = ""
                                }
                                return@item
                            }
                        }
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