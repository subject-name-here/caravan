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
import caravan.composeapp.generated.resources.black_back
import caravan.composeapp.generated.resources.check_back_to_menu
import caravan.composeapp.generated.resources.check_back_to_menu_body_tower
import caravan.composeapp.generated.resources.check_back_to_menu_body_tower_frank
import caravan.composeapp.generated.resources.congratulations
import caravan.composeapp.generated.resources.craig_1
import caravan.composeapp.generated.resources.craig_2
import caravan.composeapp.generated.resources.craig_3
import caravan.composeapp.generated.resources.craig_continue
import caravan.composeapp.generated.resources.currently_at_stake
import caravan.composeapp.generated.resources.currently_in_bank_caps
import caravan.composeapp.generated.resources.custom_deck_only
import caravan.composeapp.generated.resources.deck_o_54_only
import caravan.composeapp.generated.resources.empty_string
import caravan.composeapp.generated.resources.en_f_garde
import caravan.composeapp.generated.resources.en_garde
import caravan.composeapp.generated.resources.enemy
import caravan.composeapp.generated.resources.finish
import caravan.composeapp.generated.resources.frank_final_words
import caravan.composeapp.generated.resources.frank_head
import caravan.composeapp.generated.resources.frank_horrigan_that_s_who
import caravan.composeapp.generated.resources.frank_start_button
import caravan.composeapp.generated.resources.frank_think_body
import caravan.composeapp.generated.resources.frank_think_button
import caravan.composeapp.generated.resources.frank_think_header
import caravan.composeapp.generated.resources.frank_welcome
import caravan.composeapp.generated.resources.frank_who
import caravan.composeapp.generated.resources.hey
import caravan.composeapp.generated.resources.i_challenge_you_to_the_game_of_caravan
import caravan.composeapp.generated.resources.level_10
import caravan.composeapp.generated.resources.now_playing_alt
import caravan.composeapp.generated.resources.now_playing_loyalty_to_your_people_neon_light_man
import caravan.composeapp.generated.resources.pay_1_ticket
import caravan.composeapp.generated.resources.progress_is_saved_between_sessions
import caravan.composeapp.generated.resources.result
import caravan.composeapp.generated.resources.take_the_cash
import caravan.composeapp.generated.resources.take_the_cash_alt
import caravan.composeapp.generated.resources.tickets_please_you_have_tickets
import caravan.composeapp.generated.resources.tower
import caravan.composeapp.generated.resources.tower_enemy_1
import caravan.composeapp.generated.resources.tower_enemy_10
import caravan.composeapp.generated.resources.tower_enemy_11
import caravan.composeapp.generated.resources.tower_enemy_12
import caravan.composeapp.generated.resources.tower_enemy_13
import caravan.composeapp.generated.resources.tower_enemy_2
import caravan.composeapp.generated.resources.tower_enemy_3
import caravan.composeapp.generated.resources.tower_enemy_3A
import caravan.composeapp.generated.resources.tower_enemy_4
import caravan.composeapp.generated.resources.tower_enemy_5
import caravan.composeapp.generated.resources.tower_enemy_7
import caravan.composeapp.generated.resources.tower_enemy_8
import caravan.composeapp.generated.resources.tower_enemy_9
import caravan.composeapp.generated.resources.tower_presents
import caravan.composeapp.generated.resources.tower_starring_1
import caravan.composeapp.generated.resources.tower_starring_2
import caravan.composeapp.generated.resources.wait_let_s_talk
import caravan.composeapp.generated.resources.we_just_did_time_for_talking_s_over
import caravan.composeapp.generated.resources.you_can_change_custom_deck_between_games
import caravan.composeapp.generated.resources.you_don_t_even_know_who_i_am
import caravan.composeapp.generated.resources.you_don_t_have_a_ticket_on_you
import caravan.composeapp.generated.resources.you_lose
import caravan.composeapp.generated.resources.you_mutant_scum
import caravan.composeapp.generated.resources.you_re_just_another_mutant_that_needs_to_be_put_down
import caravan.composeapp.generated.resources.you_re_not_a_hero_you_re_just_a_walking_corpse
import caravan.composeapp.generated.resources.you_win
import caravan.composeapp.generated.resources.your_reward_caps
import caravan.composeapp.generated.resources.your_reward_caps_finale
import caravan.composeapp.generated.resources.your_reward_caps_running
import caravan.composeapp.generated.resources.your_reward_reward_xp
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.color.Colors
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyFrank
import com.unicorns.invisible.caravan.model.enemy.EnemyTower1
import com.unicorns.invisible.caravan.model.enemy.EnemyTower2
import com.unicorns.invisible.caravan.model.enemy.EnemyTower3
import com.unicorns.invisible.caravan.model.enemy.EnemyTower3A
import com.unicorns.invisible.caravan.model.enemy.EnemyTower4
import com.unicorns.invisible.caravan.model.enemy.EnemyTower5
import com.unicorns.invisible.caravan.model.enemy.EnemyTower6
import com.unicorns.invisible.caravan.model.enemy.EnemyTower7
import com.unicorns.invisible.caravan.model.enemy.EnemyTower8
import com.unicorns.invisible.caravan.model.enemy.EnemyTower9
import com.unicorns.invisible.caravan.model.enemy.EnemyTowerBonus
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.story.DeathCode
import com.unicorns.invisible.caravan.story.DialogEdge
import com.unicorns.invisible.caravan.story.DialogFinishState
import com.unicorns.invisible.caravan.story.DialogGraph
import com.unicorns.invisible.caravan.story.DialogMiddleState
import com.unicorns.invisible.caravan.story.StoryShow
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextClassic
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.nextSong
import com.unicorns.invisible.caravan.utils.playCashSound
import com.unicorns.invisible.caravan.utils.playFrankPhrase
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playMinigunSound
import com.unicorns.invisible.caravan.utils.playNotificationSound
import com.unicorns.invisible.caravan.utils.playTowerCompleted
import com.unicorns.invisible.caravan.utils.playTowerFailed
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.startAmbient
import com.unicorns.invisible.caravan.utils.startLevel11Theme
import com.unicorns.invisible.caravan.utils.stopAmbient
import com.unicorns.invisible.caravan.utils.stopRadio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource


@Composable
fun TowerScreen(
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit,
) {
    var level by rememberSaveable { mutableIntStateOf(saveGlobal.towerLevel) }
    var cookCook by rememberSaveable { mutableIntStateOf(saveGlobal.cookCookMult) }
    var secondChances by rememberSaveable { mutableIntStateOf(saveGlobal.secondChances) }

    var playLevel by rememberScoped { mutableIntStateOf(0) }
    var levelMemory by rememberScoped { mutableIntStateOf(0) }

    var startFrank by rememberSaveable { mutableStateOf(false) }
    if (startFrank) {
        ShowFrank {
            level++
            playLevel = 0
            startFrank = false
        }
        return
    }

    @Composable
    fun showTower(enemy: Enemy, onWinExtra: () -> Unit = {}) {
        StartTowerGame(enemy, showAlertDialog, {
            levelMemory = level
            level = 0
            saveGlobal.towerLevel = 0
            saveData()
        }, {
            level = levelMemory + 1
            saveGlobal.towerLevel = levelMemory + 1
            onWinExtra()
            levelMemory = 0
            saveData()
        }, {
            if (secondChances > 0) {
                secondChances--
                saveGlobal.secondChances--
                level = levelMemory
                saveGlobal.towerLevel = levelMemory
            }
            levelMemory = 0
            saveData()
        }) { playLevel = 0 }
    }

    when (playLevel) {
        10 -> {
            showTower(EnemyTowerBonus) { cookCook = 2; saveGlobal.cookCookMult = 2 }
            return
        }
        in 1..11 -> {
            val enemy = when (playLevel) {
                1 -> EnemyTower1
                2 -> EnemyTower2
                3 -> if (saveGlobal.papaSmurfActive) EnemyTower3A else EnemyTower3
                4 -> EnemyTower4
                5 -> EnemyTower5
                7 -> EnemyTower6
                8 -> EnemyTower7
                9 -> EnemyTower8
                11 -> EnemyTower9
                else -> {
                    playLevel = 0
                    return
                }
            }
            showTower(enemy)
            return
        }
        12 -> {
            LaunchedEffect(Unit) {
                startFrank = true
            }
            return
        }
        13 -> {
            var capsMemory by rememberScoped { mutableIntStateOf(0) }
            StartTowerGame(EnemyFrank, showAlertDialog, {
                startLevel11Theme()
                playFrankPhrase("files/raw/frank_on_game_start.ogg")
                levelMemory = level
                level = 0
                saveGlobal.towerLevel = 0
                capsMemory = saveGlobal.capsInHand
                saveGlobal.capsInHand = 0
                saveData()
            }, {
                level = levelMemory + 1
                saveGlobal.towerLevel = levelMemory + 1
                levelMemory = 0
                saveGlobal.capsInHand += capsMemory
                saveData()
                stopRadio()
                playFrankPhrase("files/raw/frank_on_defeat.ogg")
            }, {
                levelMemory = 0
                level = 0
                saveGlobal.towerLevel = 0
                saveData()
                stopRadio()
            }) {
                playLevel = 0
                soundReduced = false

                CoroutineScope(Dispatchers.Unconfined).launch {
                    if (level == 0) {
                        playTowerFailed()
                    } else {
                        playTowerCompleted()
                    }
                    delay(14000L)
                    if (!soundReduced) {
                        nextSong()
                    }
                }
            }
            return
        }
    }

    var showFrankWarning by rememberSaveable { mutableStateOf(false) }
    MenuItemOpen(stringResource(Res.string.tower), "<-", Alignment.Center, { if (level != 13) goBack() }) {
        val scope = rememberCoroutineScope()
        when (level) {
            0 -> {
                StartScreen({ p1, p2 -> showAlertDialog(p1, p2, null) }) {
                    level++
                    saveGlobal.towerLevel++
                    saveData()
                }
            }
            6 -> {
                RestScreen {
                    level = 7
                    saveGlobal.towerLevel = 7
                    secondChances = 1
                    saveGlobal.secondChances = 1
                    levelMemory = 0
                    saveData()
                }
            }
            10 -> {
                CookCookPresentedScreen(
                    256,
                    { level = 11; saveGlobal.towerLevel = 11; saveData() },
                    { playLevel = level }
                )
            }
            in 1..12 -> {
                val inBank = when (level) {
                    1 -> 1
                    2 -> 2
                    3 -> 4
                    4 -> 8
                    5 -> 16
                    7 -> 32
                    8 -> 64
                    9 -> 128
                    11 -> 256
                    12 -> 512
                    else -> 0
                } * cookCook
                EnemyPresentedScreen(
                    inBank,
                    { level },
                    { playLevel = level },
                    {
                        level = 0
                        saveGlobal.towerLevel = 0
                        saveGlobal.capsInHand += inBank
                        saveGlobal.increaseXp(inBank)
                        saveData()
                        playCashSound()
                        scope.launch {
                            showAlertDialog(
                                getString(Res.string.congratulations),
                                getString(
                                    Res.string.your_reward_caps,
                                    inBank.toString()
                                ) + getString(
                                    Res.string.your_reward_reward_xp,
                                    inBank.toString()
                                ),
                                null
                            )
                        }
                    }
                )
            }
            13 -> {
                FrankPresentedScreen(
                    { showFrankWarning = true },
                    {
                        level = 0
                        if (soundReduced) {
                            soundReduced = false
                            nextSong()
                        }
                        saveGlobal.towerLevel = 0
                        val inBank = if (cookCook == 2) 1024 else 512
                        saveGlobal.capsInHand += inBank
                        saveGlobal.increaseXp(inBank)
                        saveData()
                        playCashSound()
                        scope.launch {
                            showAlertDialog(
                                getString(Res.string.congratulations),
                                getString(Res.string.your_reward_caps_running, inBank.toString()),
                                null
                            )
                        }
                    }
                )
            }
            else -> {
                FinalScreen {
                    level = 0
                    saveGlobal.towerLevel = 0
                    val inBank = if (cookCook == 2) 2048 else 1536
                    saveGlobal.capsInHand += inBank
                    saveGlobal.increaseXp(inBank)
                    saveGlobal.towerBeatenN = true
                    saveData()
                    CoroutineScope(Dispatchers.Unconfined).launch {
                        repeat(4) {
                            playCashSound()
                            delay(190L)
                        }
                    }
                    scope.launch {
                        showAlertDialog(
                            getString(Res.string.congratulations),
                            getString(
                                Res.string.your_reward_caps_finale, inBank.toString()
                            ) + getString(
                                Res.string.your_reward_reward_xp, inBank.toString()
                            ),
                            null
                        )
                    }
                }
            }
        }
    }


    if (showFrankWarning) {
        LaunchedEffect(Unit) {
            playNotificationSound()
        }
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = Colors.ColorText),
            onDismissRequest = { showFrankWarning = false },
            confirmButton = {
                TextClassic(
                    stringResource(Res.string.frank_start_button),
                    Colors.ColorTextBack,
                    Colors.ColorTextBack,
                    18.sp,
                    Modifier
                        .background(Colors.ColorText)
                        .clickableOk {
                            showFrankWarning = false; playLevel = 13
                        }
                        .padding(4.dp)
                )
            },
            dismissButton = {
                TextClassic(
                    stringResource(Res.string.frank_think_button),
                    Colors.ColorTextBack,
                    Colors.ColorTextBack,
                    18.sp,
                    Modifier
                        .background(Colors.ColorText)
                        .clickableCancel { showFrankWarning = false }
                        .padding(4.dp)
                )
            },
            title = {
                TextClassic(
                    stringResource(Res.string.frank_think_header),
                    Colors.ColorText,
                    Colors.ColorText,
                    24.sp, Modifier,
                )
            },
            text = {
                TextClassic(
                    stringResource(Res.string.frank_think_body),
                    Colors.ColorText,
                    Colors.ColorText,
                    16.sp, Modifier
                )
            },
            containerColor = Color.Black,
            textContentColor = Colors.ColorText,
            shape = RectangleShape,
        )
    }
}

@Composable
fun StartScreen(
    showAlertDialog: (String, String) -> Unit,
    nextLevel: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    Column(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFallout(
            stringResource(Res.string.tower_presents),
            getTextColor(),
            getTextStrokeColor(),
            18.sp,
            Modifier,
        )
        TextFallout(
            stringResource(Res.string.tower),
            getTextColor(),
            getTextStrokeColor(),
            40.sp,
            Modifier,
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextFallout(
            stringResource(Res.string.tower_starring_1),
            getTextColor(),
            getTextStrokeColor(),
            24.sp,
            Modifier,
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextFallout(
            stringResource(Res.string.tower_starring_2),
            getTextColor(),
            getTextStrokeColor(),
            18.sp,
            Modifier,
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextFallout(
            stringResource(Res.string.tickets_please_you_have_tickets, saveGlobal.tickets),
            getTextColor(),
            getTextStrokeColor(),
            20.sp,
            Modifier,
        )
        Spacer(modifier = Modifier.height(8.dp))
        val scope = rememberCoroutineScope()
        TextFallout(
            stringResource(Res.string.pay_1_ticket),
            getTextColor(),
            getTextStrokeColor(),
            20.sp,
            Modifier
                .padding(4.dp)
                .clickableOk {
                    if (saveGlobal.tickets <= 0) {
                        scope.launch {
                            showAlertDialog(
                                getString(Res.string.hey),
                                getString(Res.string.you_don_t_have_a_ticket_on_you),
                            )
                        }
                    } else {
                        saveGlobal.tickets--
                        nextLevel()
                    }
                }
                .background(getTextBackgroundColor())
                .padding(4.dp),
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun EnemyPresentedScreen(
    inBank: Int,
    getLevel: () -> Int,
    startLevel: () -> Unit,
    setLevelZero: () -> Unit,
) {
    val innerLevel = when (val lvl = getLevel()) {
        in 1..5 -> lvl
        in 7..9 -> lvl - 1
        in 11..12 -> lvl - 2
        else -> 0
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFallout(
                stringResource(Res.string.custom_deck_only),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier,
            )
            Spacer(Modifier.height(8.dp))
            TextFallout(
                stringResource(Res.string.you_can_change_custom_deck_between_games),
                getTextColor(),
                getTextStrokeColor(),
                16.sp,
                Modifier,
            )
            Spacer(Modifier.height(8.dp))
            TextFallout(
                stringResource(Res.string.progress_is_saved_between_sessions),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        @Composable
        fun showTowerCard(enemyName: String) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                TextFallout(
                    stringResource(Res.string.level_10, innerLevel),
                    getTextColor(),
                    getTextStrokeColor(),
                    24.sp,
                    Modifier,
                )
                TextFallout(
                    stringResource(Res.string.currently_in_bank_caps, inBank),
                    getTextColor(),
                    getTextStrokeColor(),
                    24.sp,
                    Modifier,
                )
                TextFallout(
                    stringResource(Res.string.enemy, enemyName),
                    getTextColor(),
                    getTextStrokeColor(),
                    24.sp,
                    Modifier,
                )
            }
        }
        when (innerLevel) {
            1 -> {
                showTowerCard(stringResource(Res.string.tower_enemy_1))
            }
            2 -> {
                showTowerCard(stringResource(Res.string.tower_enemy_2))
            }
            3 -> {
                if (saveGlobal.papaSmurfActive) {
                    showTowerCard(stringResource(Res.string.tower_enemy_3A))
                } else {
                    showTowerCard(stringResource(Res.string.tower_enemy_3))
                }
            }
            4 -> {
                showTowerCard(stringResource(Res.string.tower_enemy_4))
            }
            5 -> {
                showTowerCard(stringResource(Res.string.tower_enemy_5))
            }
            6 -> {
                showTowerCard(stringResource(Res.string.tower_enemy_7))
            }
            7 -> {
                showTowerCard(stringResource(Res.string.tower_enemy_8))
            }
            8 -> {
                showTowerCard(stringResource(Res.string.tower_enemy_9))
            }
            9 -> {
                showTowerCard(stringResource(Res.string.tower_enemy_11))
            }
            10 -> {
                showTowerCard(stringResource(Res.string.tower_enemy_12))
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextFallout(
                stringResource(Res.string.take_the_cash),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier
                    .clickableOk {
                        setLevelZero()
                    }
                    .background(getTextBackgroundColor())
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )

            TextFallout(
                stringResource(Res.string.en_garde),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier
                    .clickableOk {
                        startLevel()
                    }
                    .background(getTextBackgroundColor())
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}


@Composable
fun CookCookPresentedScreen(
    inBank: Int,
    skip: () -> Unit,
    startLevel: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFallout(
                stringResource(Res.string.custom_deck_only),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier,
            )
            Spacer(Modifier.height(8.dp))
            TextFallout(
                stringResource(Res.string.you_can_change_custom_deck_between_games),
                getTextColor(),
                getTextStrokeColor(),
                16.sp,
                Modifier,
            )
            Spacer(Modifier.height(8.dp))
            TextFallout(
                stringResource(Res.string.progress_is_saved_between_sessions),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            TextFallout(
                "BONUS ROUND!",
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier,
            )
            TextFallout(
                stringResource(Res.string.currently_in_bank_caps, inBank),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier,
            )
            TextFallout(
                stringResource(Res.string.enemy, stringResource(Res.string.tower_enemy_10)),
                getTextColor(),
                getTextStrokeColor(),
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
                "Skip",
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier
                    .clickableOk {
                        skip()
                    }
                    .background(getTextBackgroundColor())
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )

            TextFallout(
                stringResource(Res.string.en_garde),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier
                    .clickableOk {
                        startLevel()
                    }
                    .background(getTextBackgroundColor())
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun RestScreen(
    nextLevel: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFallout(
                "Welcome to the REST level!",
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier,
            )
            Spacer(Modifier.height(8.dp))
            TextFallout(
                "You have found: a Second Chance.",
                getTextColor(),
                getTextStrokeColor(),
                16.sp,
                Modifier,
            )
            Spacer(Modifier.height(8.dp))
            TextFallout(
                "If you'll lose, you can try again, but only once.",
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextFallout(
                "Let's go!",
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier
                    .clickableOk {
                        nextLevel()
                    }
                    .background(getTextBackgroundColor())
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun FrankPresentedScreen(
    startLevel: () -> Unit,
    setLevelZero: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor())
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            TextFallout(
                stringResource(Res.string.deck_o_54_only),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextFallout(
                "NO SECOND CHANCES!",
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextFallout(
                stringResource(Res.string.level_10, 10),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier,
            )
            TextFallout(
                stringResource(Res.string.currently_at_stake, "your life"),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier,
            )
            TextFallout(
                stringResource(Res.string.enemy, stringResource(Res.string.tower_enemy_13)),
                getTextColor(),
                getTextStrokeColor(),
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
                stringResource(Res.string.take_the_cash_alt),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier
                    .background(getTextBackgroundColor())
                    .clickableOk {
                        setLevelZero()
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )

            TextFallout(
                stringResource(Res.string.en_f_garde),
                getTextColor(),
                getTextStrokeColor(),
                24.sp,
                Modifier
                    .background(getTextBackgroundColor())
                    .clickableOk {
                        startLevel()
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }
    }
}

@Composable
fun FinalScreen(setLevelZero: () -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        TextFallout(
            stringResource(Res.string.take_the_cash),
            getTextColor(),
            getTextStrokeColor(),
            24.sp,
            Modifier
                .clickableOk {
                    setLevelZero()
                }
                .background(getTextBackgroundColor())
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
fun StartTowerGame(
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
        playNotificationSound()
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = Colors.ColorText),
            onDismissRequest = { showIntro = false },
            confirmButton = {
                TextClassic(
                    "OK",
                    Colors.ColorTextBack,
                    Colors.ColorTextBack,
                    18.sp,
                    Modifier
                        .background(Colors.ColorText)
                        .clickableCancel { showIntro = false }
                        .padding(4.dp),
                )
            },
            title = {
                TextClassic(
                    stringResource(Res.string.you_re_not_a_hero_you_re_just_a_walking_corpse),
                    Colors.ColorText,
                    Colors.ColorText,
                    24.sp, Modifier,
                    textAlignment = TextAlign.Start
                )
            },
            text = {
                TextClassic(
                    stringResource(
                        if (!saveGlobal.isHeroic) {
                            Res.string.now_playing_loyalty_to_your_people_neon_light_man
                        } else {
                            Res.string.now_playing_alt
                        }
                    ),
                    Colors.ColorText,
                    Colors.ColorText,
                    16.sp, Modifier,
                    textAlignment = TextAlign.Start
                )
            },
            containerColor = Color.Black,
            textContentColor = Colors.ColorText,
            shape = RectangleShape,
        )
    }

    if (showFrankOutro) {
        playNotificationSound()
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = Colors.ColorText),
            onDismissRequest = { showFrankOutro = false },
            confirmButton = {
                TextClassic(
                    stringResource(Res.string.finish),
                    Colors.ColorTextBack,
                    Colors.ColorTextBack,
                    18.sp,
                    Modifier
                        .background(Colors.ColorText)
                        .clickableCancel { showFrankOutro = false }
                        .padding(4.dp)
                )
            },
            title = {
                TextClassic(
                    stringResource(Res.string.you_win),
                    Colors.ColorText,
                    Colors.ColorText,
                    24.sp, Modifier,
                    textAlignment = TextAlign.Start
                )
            },
            text = {
                TextClassic(
                    stringResource(Res.string.frank_final_words),
                    Colors.ColorText,
                    Colors.ColorText,
                    16.sp, Modifier,
                    textAlignment = TextAlign.Start
                )
            },
            containerColor = Color.Black,
            textContentColor = Colors.ColorText,
            shape = RectangleShape,
        )
    }

    LaunchedEffect(Unit) { startAmbient() }
    val playerCResources = CResources(if (isFrankSequence) {
        CustomDeck(saveGlobal.selectedDeck)
    } else {
        CustomDeck().apply { addAll(saveGlobal.getCurrentDeckCopy()) }
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
    val onQuitPressed = {
        if (!game.isOver()) {
            onLose()
        }
        stopAmbient()
        goBack()
    }

    val scope = rememberCoroutineScope()
    game.also {
        it.onWin = {
            processChallengesGameOver(it)
            playWinSound()
            onWin()
            if (isFrankSequence) {
                showFrankOutro = true
            } else {
                scope.launch {
                    showAlertDialog(
                        getString(Res.string.result),
                        getString(Res.string.you_win),
                        onQuitPressed
                    )
                }
            }
            saveData()
        }
        it.onLose = {
            playLoseSound()
            onLose()
            scope.launch {
                showAlertDialog(
                    getString(Res.string.result),
                    getString(Res.string.you_lose),
                    onQuitPressed
                )
            }
        }
    }

    ShowGame(
        game
    ) {
        if (game.isOver()) {
            onQuitPressed()
        } else {
            scope.launch {
                val body = if (isFrankSequence) {
                    getString(Res.string.check_back_to_menu_body_tower_frank)
                } else {
                    getString(Res.string.check_back_to_menu_body_tower)
                }
                showAlertDialog(
                    getString(Res.string.check_back_to_menu),
                    body,
                    onQuitPressed
                )
            }
        }
    }
}

@Composable
fun ShowFrank(goBack: () -> Unit) {
    StoryShow(DialogGraph(
        states = listOf(
            DialogMiddleState(Res.drawable.black_back, Res.string.craig_1),
            DialogMiddleState(Res.drawable.black_back, Res.string.craig_2),
            DialogMiddleState(Res.drawable.black_back, Res.string.craig_3),
            DialogMiddleState(Res.drawable.black_back, Res.string.empty_string),
            DialogMiddleState(Res.drawable.frank_head, Res.string.empty_string),
            DialogMiddleState(Res.drawable.frank_head, Res.string.frank_welcome),
            DialogMiddleState(Res.drawable.frank_head, Res.string.frank_horrigan_that_s_who),
            DialogMiddleState(Res.drawable.frank_head, Res.string.we_just_did_time_for_talking_s_over),
            DialogMiddleState(Res.drawable.frank_head, Res.string.you_re_just_another_mutant_that_needs_to_be_put_down),
            DialogMiddleState(Res.drawable.frank_head, Res.string.you_mutant_scum),
            DialogFinishState(DeathCode.ALIVE),
            DialogMiddleState(Res.drawable.frank_head, Res.string.frank_welcome),
        ),
        edges = listOf(
            DialogEdge(0, 1, Res.string.craig_continue),
            DialogEdge(1, 2, Res.string.craig_continue) {
                stopRadio()
                soundReduced = true
                playMinigunSound()
                -1
            },
            DialogEdge(2, 3, Res.string.craig_continue) {
                playFrankPhrase("files/raw/frank_on_welcome.ogg")
                delay(3000L)
                3
            },
            DialogEdge(3, 4, Res.string.empty_string) {
                delay(3000L)
                4
            },
            DialogEdge(4, 11, Res.string.empty_string) {
                delay(10000L)
                5
            },
            DialogEdge(11, 5, Res.string.empty_string),
            DialogEdge(5, 6, Res.string.frank_who) {
                playFrankPhrase("files/raw/frank_who_are_you.ogg")
                -1
            },
            DialogEdge(5, 7, Res.string.wait_let_s_talk) {
                playFrankPhrase("files/raw/frank_lets_talk.ogg")
                -1
            },
            DialogEdge(5, 8, Res.string.you_don_t_even_know_who_i_am) {
                playFrankPhrase("files/raw/frank_you_dont_even_know_who_i_am.ogg")
                -1
            },
            DialogEdge(5, 9, Res.string.i_challenge_you_to_the_game_of_caravan) {
                playFrankPhrase("files/raw/frank_i_challenge_you.ogg")
                -1
            },
            DialogEdge(6, 7, Res.string.wait_let_s_talk) {
                playFrankPhrase("files/raw/frank_lets_talk.ogg")
                -1
            },
            DialogEdge(6, 8, Res.string.you_don_t_even_know_who_i_am) {
                playFrankPhrase("files/raw/frank_you_dont_even_know_who_i_am.ogg")
                -1
            },
            DialogEdge(6, 9, Res.string.i_challenge_you_to_the_game_of_caravan) {
                playFrankPhrase("files/raw/frank_i_challenge_you.ogg")
                -1
            },
            DialogEdge(7, 9, Res.string.i_challenge_you_to_the_game_of_caravan) {
                playFrankPhrase("files/raw/frank_i_challenge_you.ogg")
                -1
            },
            DialogEdge(8, 6, Res.string.frank_who) {
                playFrankPhrase("files/raw/frank_who_are_you.ogg")
                -1
            },
            DialogEdge(8, 7, Res.string.wait_let_s_talk) {
                playFrankPhrase("files/raw/frank_lets_talk.ogg")
                -1
            },
            DialogEdge(8, 9, Res.string.i_challenge_you_to_the_game_of_caravan) {
                playFrankPhrase("files/raw/frank_i_challenge_you.ogg")
                -1
            },
            DialogEdge(9, 10, Res.string.finish)
        )
    ), goBack, goBack)
}