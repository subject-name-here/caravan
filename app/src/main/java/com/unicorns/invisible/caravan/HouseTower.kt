package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyFrank
import com.unicorns.invisible.caravan.model.enemy.EnemyTower1
import com.unicorns.invisible.caravan.model.enemy.EnemyTower3
import com.unicorns.invisible.caravan.model.enemy.EnemyTower3A
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.story.DialogEdge
import com.unicorns.invisible.caravan.story.DialogGraph
import com.unicorns.invisible.caravan.story.DialogState
import com.unicorns.invisible.caravan.story.StoryShow
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextClassic
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
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
                11 -> {
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
        in 1..10 -> {
            showTower(
                when (playLevel) {
                    1 -> EnemyTower1
                    2 -> EnemyTower1
                    3 -> if (save.papaSmurfActive) EnemyTower3A else EnemyTower3
                    4 -> EnemyTower1
                    5 -> EnemyTower1
                    6 -> EnemyTower1
                    7 -> EnemyTower1
                    8 -> EnemyTower1
                    9 -> EnemyTower1
                    else -> EnemyTower1
                }
            ) { playLevel = 0 }
            return
        }
        12 -> {
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
                    18.sp,
                    Modifier
                        .background(Color(activity.getColor(R.color.colorText)))
                        .clickableOk(activity) {
                            showFrankWarning = false; playLevel = 11
                        }
                        .padding(4.dp)
                )
            },
            dismissButton = {
                TextClassic(
                    stringResource(R.string.frank_think_button),
                    Color(activity.getColor(R.color.colorTextBack)),
                    Color(activity.getColor(R.color.colorTextBack)),
                    18.sp,
                    Modifier
                        .background(Color(activity.getColor(R.color.colorText)))
                        .clickableCancel(activity) { showFrankWarning = false }
                        .padding(4.dp)
                )
            },
            title = {
                TextClassic(
                    stringResource(R.string.frank_think_header),
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    24.sp, Modifier,
                )
            },
            text = {
                TextClassic(
                    stringResource(R.string.frank_think_body),
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    16.sp, Modifier
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
                Modifier,
            )
            TextFallout(
                stringResource(R.string.tower),
                getTextColor(activity),
                getTextStrokeColor(activity),
                40.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextFallout(
                stringResource(R.string.tower_starring_1),
                getTextColor(activity),
                getTextStrokeColor(activity),
                24.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextFallout(
                stringResource(R.string.tower_starring_2),
                getTextColor(activity),
                getTextStrokeColor(activity),
                18.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(24.dp))
            TextFallout(
                stringResource(R.string.tickets_please_you_have_tickets, save.tickets),
                getTextColor(activity),
                getTextStrokeColor(activity),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextFallout(
                stringResource(R.string.pay_1_ticket),
                getTextColor(activity),
                getTextStrokeColor(activity),
                20.sp,
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
                    Modifier,
                )
                Spacer(Modifier.height(8.dp))
                TextFallout(
                    stringResource(R.string.you_can_change_custom_deck_between_games),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    16.sp,
                    Modifier,
                )
                Spacer(Modifier.height(8.dp))
                TextFallout(
                    stringResource(R.string.progress_is_saved_between_sessions),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Modifier,
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
                        Modifier,
                    )
                    TextFallout(
                        stringResource(R.string.currently_in_bank_caps, inBank),
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        24.sp,
                        Modifier,
                    )
                    TextFallout(
                        stringResource(R.string.enemy, enemyName),
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        24.sp,
                        Modifier,
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
                    Modifier
                        .clickableOk(activity) {
                            setLevelZero()
                        }
                        .background(getTextBackgroundColor(activity))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )

                TextFallout(
                    stringResource(R.string.en_garde),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Modifier
                        .clickableOk(activity) {
                            startLevel()
                        }
                        .background(getTextBackgroundColor(activity))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
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
                    Modifier,
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
                    Modifier,
                )
                TextFallout(
                    stringResource(R.string.currently_in_bank_caps, inBank),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Modifier,
                )
                TextFallout(
                    stringResource(R.string.enemy, stringResource(R.string.tower_enemy_10A)),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Modifier,
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
                    Modifier
                        .background(getTextBackgroundColor(activity))
                        .clickableOk(activity) {
                            setLevelZero()
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )

                TextFallout(
                    stringResource(R.string.en_f_garde),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Modifier
                        .background(getTextBackgroundColor(activity))
                        .clickableOk(activity) {
                            startLevel()
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
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
            Modifier
                .clickableOk(activity) {
                    setLevelZero()
                }
                .background(getTextBackgroundColor(activity))
                .padding(horizontal = 8.dp, vertical = 4.dp),
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
                    18.sp,
                    Modifier
                        .background(Color(activity.getColor(R.color.colorText)))
                        .clickableCancel(activity) { showIntro = false }
                        .padding(4.dp)
                )
            },
            title = {
                TextClassic(
                    stringResource(R.string.you_re_not_a_hero_you_re_just_a_walking_corpse),
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    24.sp, Modifier
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
                    16.sp, Modifier
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
                    18.sp,
                    Modifier
                        .background(Color(activity.getColor(R.color.colorText)))
                        .clickableCancel(activity) { showFrankOutro = false }
                        .padding(4.dp)
                )
            },
            title = {
                TextClassic(
                    activity.getString(R.string.you_win),
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    24.sp, Modifier
                )
            },
            text = {
                TextClassic(
                    stringResource(R.string.frank_final_words),
                    Color(activity.getColor(R.color.colorText)),
                    Color(activity.getColor(R.color.colorText)),
                    16.sp, Modifier
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
            enemy.onVictory(false)
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

@Composable
fun ShowFrank(activity: MainActivity, goBack: () -> Unit) {
    StoryShow(activity, DialogGraph(
        states = listOf(
            DialogState(R.drawable.black_back, listOf(1)),
            DialogState(R.drawable.black_back, listOf(2)),
            DialogState(R.drawable.black_back, listOf(3)),
            DialogState(R.drawable.black_back, listOf()),
            DialogState(R.drawable.frank_head, listOf()),
            DialogState(R.drawable.frank_head, listOf(4, 5, 6, 7)),
            DialogState(R.drawable.frank_head, listOf(7)),
            DialogState(R.drawable.frank_head, listOf(8)),
        ),
        edges = listOf(
            DialogEdge(0, R.string.craig_1, 0),
            DialogEdge(R.string.craig_continue, R.string.craig_2, 1),
            DialogEdge(R.string.craig_continue, R.string.craig_3, 2),
            DialogEdge(R.string.craig_continue, R.string.frank_welcome, 3),
            DialogEdge(R.string.frank_who, R.string.frank_horrigan_that_s_who, 5),
            DialogEdge(R.string.wait_let_s_talk, R.string.we_just_did_time_for_talking_s_over, 6),
            DialogEdge(R.string.you_don_t_even_know_who_i_am, R.string.you_re_just_another_mutant_that_needs_to_be_put_down, 5),
            DialogEdge(R.string.i_challenge_you_to_the_game_of_caravan, R.string.you_mutant_scum, 7),
            DialogEdge(R.string.finish, 0, -1),
        ),
        onEdgeVisitedMap = mapOf(
            2 to {
                stopRadio()
                soundReduced = true
                playMinigunSound(activity)
            },
            3 to lambda@{
                playFrankPhrase(activity, R.raw.frank_on_welcome)
                CoroutineScope(Dispatchers.Unconfined).launch {
                    delay(3000L)
                    this@lambda.currentState = 4
                    delay(10000L)
                    this@lambda.currentState = 5
                }
            },
            4 to {
                playFrankPhrase(activity, R.raw.frank_who_are_you)
            },
            5 to {
                playFrankPhrase(activity, R.raw.frank_lets_talk)
            },
            6 to {
                playFrankPhrase(activity, R.raw.frank_lets_talk)
            },
            7 to {
                playFrankPhrase(activity, R.raw.frank_i_challenge_you)
            },
        )
    ), goBack)
}