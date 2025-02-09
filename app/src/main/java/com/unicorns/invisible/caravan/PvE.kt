package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyBenny
import com.unicorns.invisible.caravan.model.enemy.EnemyCrooker
import com.unicorns.invisible.caravan.model.enemy.EnemyDrMobius
import com.unicorns.invisible.caravan.model.enemy.EnemyEasyPete
import com.unicorns.invisible.caravan.model.enemy.EnemyElijah
import com.unicorns.invisible.caravan.model.enemy.EnemyHanlon
import com.unicorns.invisible.caravan.model.enemy.EnemyLuc10
import com.unicorns.invisible.caravan.model.enemy.EnemyMadnessCardinal
import com.unicorns.invisible.caravan.model.enemy.EnemyNash
import com.unicorns.invisible.caravan.model.enemy.EnemyNoBark
import com.unicorns.invisible.caravan.model.enemy.EnemyOliver
import com.unicorns.invisible.caravan.model.enemy.EnemySnuffles
import com.unicorns.invisible.caravan.model.enemy.EnemyTabitha
import com.unicorns.invisible.caravan.model.enemy.EnemyTheManInTheMirror
import com.unicorns.invisible.caravan.model.enemy.EnemyUlysses
import com.unicorns.invisible.caravan.model.enemy.EnemyVeronica
import com.unicorns.invisible.caravan.model.enemy.EnemyVictor
import com.unicorns.invisible.caravan.model.enemy.EnemyVulpes
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playNukeBlownSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playVatsEnter
import com.unicorns.invisible.caravan.utils.playWWSound
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.startAmbient
import com.unicorns.invisible.caravan.utils.stopAmbient
import java.util.Locale
import kotlin.math.min
import kotlin.math.pow


@Composable
fun ShowSelectPvE(
    activity: MainActivity,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit
) {
    var showStats by rememberScoped { mutableStateOf(false) }
    var showStory by rememberScoped { mutableStateOf(false) }
    var showSelectEnemy by rememberScoped { mutableStateOf(false) }
    var showTower by rememberScoped { mutableStateOf(false) }
    var showTutorial by rememberScoped { mutableStateOf(false) }

    when {
        showStats -> {
            ShowStats(activity) { showStats = false }
            return
        }
        showSelectEnemy -> {
            ShowPvE(activity, showAlertDialog) { showSelectEnemy = false }
            return
        }
        showTower -> {
            TowerScreen(activity, showAlertDialog) { showTower = false }
            return
        }
        showStory -> {
            ShowStoryList(activity, showAlertDialog) { showStory = false }
            return
        }
        showTutorial -> {
            Tutorial(activity) { showTutorial = false }
            return
        }
    }

    MenuItemOpen(activity, stringResource(R.string.menu_pve), "<-", goBack) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity))
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
                @Composable
                fun SubMenuItem(name: String, onClick: () -> Unit) {
                    TextFallout(
                        name,
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        28.sp,
                        Alignment.Center,
                        Modifier
                            .clickableSelect(activity) { onClick() }
                            .background(getTextBackgroundColor(activity))
                            .padding(8.dp),
                        TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                SubMenuItem(stringResource(R.string.select_enemy)) { showSelectEnemy = true }
                Spacer(modifier = Modifier.height(12.dp))
                SubMenuItem(stringResource(R.string.tower)) { showTower = true }
                Spacer(modifier = Modifier.height(12.dp))
                SubMenuItem(stringResource(R.string.story_mode)) { showStory = true }
                Spacer(modifier = Modifier.height(12.dp))
                SubMenuItem(stringResource(R.string.pve_stats)) { showStats = true }
                Spacer(modifier = Modifier.height(12.dp))
                SubMenuItem(stringResource(R.string.tutorial)) { showTutorial = true }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@Composable
fun ShowStats(
    activity: MainActivity,
    goBack: () -> Unit
) {
    MenuItemOpen(activity, stringResource(R.string.pve_stats), "<-", goBack) {
        val started = save.gamesStarted
        val finished = save.gamesFinished
        val won = save.wins
        val loss = finished - won
        val state2 = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity))
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
                Spacer(modifier = Modifier.height(12.dp))
                TextFallout(
                    stringResource(R.string.pve_stats),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                @Composable
                fun StatsItem(text: String) {
                    TextFallout(
                        text,
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Alignment.Center,
                        Modifier,
                        TextAlign.Center,
                    )
                }
                StatsItem(text = stringResource(R.string.pve_games_started, started))
                Spacer(modifier = Modifier.height(8.dp))
                StatsItem(text = stringResource(R.string.pve_games_finished, finished))
                Spacer(modifier = Modifier.height(8.dp))
                StatsItem(text = stringResource(R.string.pve_games_won, won))
                Spacer(modifier = Modifier.height(8.dp))
                StatsItem(text = stringResource(R.string.pve_caps_bet, save.capsBet))
                Spacer(modifier = Modifier.height(8.dp))
                StatsItem(text = stringResource(R.string.pve_caps_won, save.capsWon))
                Spacer(modifier = Modifier.height(12.dp))
                TextFallout(
                    stringResource(R.string.pve_percentiles),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                StatsItem(
                    text = stringResource(
                        R.string.pve_w_to_l,
                        if (loss == 0) "-" else String.format(
                            Locale.UK,
                            "%.3f",
                            won.toDouble() / loss
                        )
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatsItem(
                    text = stringResource(
                        R.string.pve_w_to_finished,
                        if (finished == 0) "-" else String.format(
                            Locale.UK,
                            "%.2f",
                            (won.toDouble() / finished) * 100
                        )
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatsItem(
                    text = stringResource(
                        R.string.pve_w_to_started,
                        if (started == 0) "-" else String.format(
                            Locale.UK,
                            "%.2f",
                            won.toDouble() / started * 100.0
                        )
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatsItem(
                    text = stringResource(
                        R.string.pve_finished_to_started,
                        if (started == 0) "-" else String.format(
                            Locale.UK,
                            "%.1f",
                            finished.toDouble() / started * 100.0
                        )
                    ),
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@Composable
fun ShowPvE(
    activity: MainActivity,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit
) {
    var showGameOliver by rememberSaveable { mutableStateOf(false) }
    var showGameVeronica by rememberSaveable { mutableStateOf(false) }
    var showGameVictor by rememberSaveable { mutableStateOf(false) }
    var showGameChiefHanlon by rememberSaveable { mutableStateOf(false) }
    var showGameUlysses by rememberSaveable { mutableStateOf(false) }
    var showGameBenny by rememberSaveable { mutableStateOf(false) }
    
    var showGameNoBark by rememberSaveable { mutableStateOf(false) }
    var showGameNash by rememberSaveable { mutableStateOf(false) }
    var showGameTabitha by rememberSaveable { mutableStateOf(false) }
    var showGameVulpes by rememberSaveable { mutableStateOf(false) }
    var showGameElijah by rememberSaveable { mutableStateOf(false) }
    var showGameCrocker by rememberSaveable { mutableStateOf(false) }

    var showGameSnuffles by rememberSaveable { mutableStateOf(false) }
    var showGameEasyPete by rememberSaveable { mutableStateOf(false) }
    var showGameMadnessCardinal by rememberSaveable { mutableStateOf(false) }
    var showGameLuc10 by rememberSaveable { mutableStateOf(false) }
    var showGameDrMobius by rememberSaveable { mutableStateOf(false) }
    var showGameTheManInTheMirror by rememberSaveable { mutableStateOf(false) }

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    @Composable
    fun StartWithEnemy(enemy: Enemy, isEven: Boolean, goBack: () -> Unit) {
        StartGame(
            activity, if (isEven)
                CResources(save.selectedDeck.first, save.selectedDeck.second)
            else
                CResources(save.getCustomDeckCopy()),
            enemy, showAlertDialog, goBack
        )
    }

    when {
        showGameOliver -> {
            StartWithEnemy(EnemyOliver, true) { showGameOliver = false }
            return
        }
        showGameVeronica -> {
            StartWithEnemy(EnemyVeronica, true) { showGameVeronica = false }
            return
        }
        showGameVictor -> {
            StartWithEnemy(EnemyVictor, true) { showGameVictor = false }
            return
        }
        showGameChiefHanlon -> {
            StartWithEnemy(EnemyHanlon, true) { showGameChiefHanlon = false }
            return
        }
        showGameUlysses -> {
            StartWithEnemy(EnemyUlysses, true) { showGameUlysses = false }
            return
        }
        showGameBenny -> {
            StartWithEnemy(EnemyBenny, true) { showGameBenny = false }
            return
        }

        showGameNoBark -> {
            StartWithEnemy(EnemyNoBark, false) { showGameNoBark = false }
            return
        }
        showGameNash -> {
            StartWithEnemy(EnemyNash, false) { showGameNash = false }
            return
        }
        showGameTabitha -> {
            StartWithEnemy(EnemyTabitha, false) { showGameTabitha = false }
            return
        }
        showGameVulpes -> {
            StartWithEnemy(EnemyVulpes, false) { showGameVulpes = false }
            return
        }
        showGameElijah -> {
            StartWithEnemy(EnemyElijah, false) { showGameElijah = false }
            return
        }
        showGameCrocker -> {
            StartWithEnemy(EnemyCrooker, false) { showGameCrocker = false }
            return
        }

        showGameSnuffles -> {
            StartWithEnemy(EnemySnuffles, false) { showGameSnuffles = false }
            return
        }
        showGameEasyPete -> {
            StartWithEnemy(EnemyEasyPete, true) { showGameEasyPete = false }
            return
        }
        showGameMadnessCardinal -> {
            StartWithEnemy(EnemyMadnessCardinal, false) { showGameMadnessCardinal = false }
            return
        }
        showGameLuc10 -> {
            StartWithEnemy(EnemyLuc10, true) { showGameLuc10 = false }
            return
        }
        showGameDrMobius -> {
            StartWithEnemy(EnemyDrMobius, false) { showGameDrMobius = false }
            return
        }
        showGameTheManInTheMirror -> {
            StartWithEnemy(EnemyTheManInTheMirror, false) { showGameTheManInTheMirror = false }
            return
        }
    }

    MenuItemOpen(activity, stringResource(R.string.select_enemy), "<-", goBack) {
        Column(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val state = rememberLazyListState()
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .scrollbar(
                        state,
                        knobColor = getKnobColor(activity), trackColor = getTrackColor(activity),
                        horizontal = false,
                    ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                state = state
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    @Composable
                    fun OpponentItem(name: String, number: Int, onClick: () -> Unit) {
                        val line = if (number !in save.enemyCapsLeft.indices)
                            stringResource(R.string.enemy_without_caps, name)
                        else
                            stringResource(R.string.enemy_with_caps, name, save.enemyCapsLeft[number])
                        TextFallout(
                            line,
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .clickable { onClick() }
                                .background(getTextBackgroundColor(activity))
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    }

                    Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                        TabRow(
                            selectedTab, Modifier.fillMaxWidth(),
                            containerColor = getBackgroundColor(activity),
                            indicator = { tabPositions ->
                                if (selectedTab < tabPositions.size) {
                                    TabRowDefaults.SecondaryIndicator(
                                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                        color = getSelectionColor(activity)
                                    )
                                }
                            },
                            divider = {
                                HorizontalDivider(color = getDividerColor(activity))
                            }
                        ) {
                            Tab(selectedTab == 0, { playSelectSound(activity); selectedTab = 0 },
                                selectedContentColor = getSelectionColor(activity),
                                unselectedContentColor = getTextBackgroundColor(activity)
                            ) {
                                TextFallout(
                                    stringResource(R.string.deck_o_54),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    16.sp,
                                    Alignment.Center,
                                    Modifier.padding(4.dp),
                                    TextAlign.Center
                                )
                            }
                            Tab(
                                selectedTab == 1, { playSelectSound(activity); selectedTab = 1 },
                                selectedContentColor = getSelectionColor(activity),
                                unselectedContentColor = getTextBackgroundColor(activity)
                            ) {
                                TextFallout(
                                    stringResource(R.string.custom_deck),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    16.sp,
                                    Alignment.Center,
                                    Modifier.padding(4.dp),
                                    TextAlign.Center
                                )
                            }
                            Tab(
                                selectedTab == 2, { playSelectSound(activity); selectedTab = 2 },
                                selectedContentColor = getSelectionColor(activity),
                                unselectedContentColor = getTextBackgroundColor(activity)
                            ) {
                                TextFallout(
                                    stringResource(R.string.wild_wasteland),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    16.sp,
                                    Alignment.Center,
                                    Modifier.padding(4.dp),
                                    TextAlign.Center
                                )
                            }
                            Tab(
                                selectedTab == 3, { playSelectSound(activity); selectedTab = 3 },
                                selectedContentColor = getSelectionColor(activity),
                                unselectedContentColor = getTextBackgroundColor(activity)
                            ) {
                                TextFallout(
                                    stringResource(R.string.tba),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    16.sp,
                                    Alignment.Center,
                                    Modifier.padding(4.dp),
                                    TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        when (selectedTab) {
                            0 -> {
                                OpponentItem(stringResource(R.string.pve_enemy_oliver_real), 0) { playVatsEnter(activity); showGameOliver = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.pve_enemy_veronica), 1) { playVatsEnter(activity); showGameVeronica = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.pve_enemy_victor), 2) { playVatsEnter(activity); showGameVictor = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.pve_enemy_chief_hanlon), 3) { playVatsEnter(activity); showGameChiefHanlon = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.pve_enemy_ulysses), 4) { playVatsEnter(activity); showGameUlysses = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.benny), 5) { playVatsEnter(activity); showGameBenny = true }
                            }
                            1 -> {
                                OpponentItem(stringResource(R.string.no_bark), 6) { playVatsEnter(activity); showGameNoBark = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.johnson_nash), 7) { playVatsEnter(activity); showGameNash = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.tabitha), 8) { playVatsEnter(activity); showGameTabitha = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.vulpes), 9) { playVatsEnter(activity); showGameVulpes = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.elijah), 10) { playVatsEnter(activity); showGameElijah = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.crooker), 11) { playVatsEnter(activity); showGameCrocker = true }
                            }
                            2 -> {
                                OpponentItem(stringResource(R.string.snuffles), 12) { playVatsEnter(activity); showGameSnuffles = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.easy_pete), 13) { playVatsEnter(activity); showGameEasyPete = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.madness_cardinal), -1) { playVatsEnter(activity); showGameMadnessCardinal = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.man_in_the_mirror), 15) { playVatsEnter(activity); showGameTheManInTheMirror = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.dr_mobius), 16) { playVatsEnter(activity); showGameDrMobius = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.luc10), 17) { playVatsEnter(activity); showGameLuc10 = true }
                            }
                            3 -> {
                                TextFallout(
                                    stringResource(R.string.coming_soon),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    16.sp,
                                    Alignment.Center,
                                    Modifier.padding(4.dp),
                                    TextAlign.Center
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun StartGame(
    activity: MainActivity,
    playerCResources: CResources,
    enemy: Enemy,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit,
) {
    var bet: Int by rememberScoped { mutableIntStateOf(0) }
    var reward: Int by rememberScoped { mutableIntStateOf(0) }
    var isBlitz: Boolean by rememberScoped { mutableStateOf(false) }

    val isBettingEnemy = enemy !is EnemyMadnessCardinal
    var showBettingScreen: Boolean by rememberScoped { mutableStateOf(isBettingEnemy) }

    val capsLeft by rememberScoped {
        mutableIntStateOf(save.enemyCapsLeft.getOrNull(enemy.getBankNumber()) ?: 0)
    }

    var enemyBet: Int by rememberScoped { mutableIntStateOf(
        if (enemy.getBankNumber() % 6 == 5) {
            30
        } else {
            min(capsLeft, 10)
        }
    ) }

    if (showBettingScreen) {
        ShowBettingScreen(
            activity, enemy, enemyBet, { bet = it }, { isBlitz = it }, { reward = it },
            { showBettingScreen = false; goBack() }, {
                if (isBettingEnemy && reward > 0 && enemy.getBankNumber() in save.enemyCapsLeft.indices) {
                    save.enemyCapsLeft[enemy.getBankNumber()] = capsLeft - enemyBet + reward
                }
                showBettingScreen = false
            }
        )
        return
    }

    val isDeckCourier6 by rememberScoped { mutableStateOf(playerCResources.isDeckCourier6()) }

    val game: Game = rememberScoped {
        Game(
            playerCResources,
            enemy
        ).also {
            save.gamesStarted++
            save.capsBet += bet
            saveData(activity)
            it.startGame()
        }
    }

    LaunchedEffect(Unit) { startAmbient(activity) }
    val onQuitPressed = { stopAmbient(); goBack() }

    game.also {
        it.onWin = {
            if (enemy is EnemyUlysses) {
                activity.achievementsClient?.unlock(activity.getString(R.string.achievement_who_are_you_that_do_not_know_your_history))
            } else if (enemy is EnemyVictor && isBlitz) {
                activity.achievementsClient?.unlock(activity.getString(R.string.achievement_bravo_))
            } else if (enemy is EnemyTheManInTheMirror) {
                activity.achievementsClient?.unlock(activity.getString(R.string.achievement_lookalike))
            }
            if (isDeckCourier6) {
                activity.achievementsClient?.unlock(activity.getString(R.string.achievement_just_load_everything_up_with_sixes_and_tens_and_kings))
            }

            activity.processChallengesGameOver(it)

            playWinSound(activity)
            save.gamesFinished++
            save.wins++

            if (isBettingEnemy) {
                val enemyCaps = save.enemyCapsLeft.getOrNull(enemy.getBankNumber()) ?: 0
                if (reward > 0 && enemy.getBankNumber() in save.enemyCapsLeft.indices) {
                    save.enemyCapsLeft[enemy.getBankNumber()] = enemyCaps - reward
                }
                save.capsInHand += reward
                save.capsWon += reward

                showAlertDialog(
                    activity.getString(R.string.result),
                    activity.getString(R.string.you_win) + activity.getString(
                        R.string.your_reward_reward_caps,
                        reward.toString()
                    ),
                    onQuitPressed
                )
            } else if (enemy is EnemyMadnessCardinal) {
                val rewardCard = winCard(activity, CardBack.MADNESS, false)

                showAlertDialog(
                    activity.getString(R.string.result),
                    activity.getString(R.string.you_win) + rewardCard,
                    onQuitPressed
                )
            } else {
                showAlertDialog(
                    activity.getString(R.string.result),
                    activity.getString(R.string.you_win),
                    onQuitPressed
                )
            }

            enemy.onVictory()
            saveData(activity)
        }
        it.onLose = {
            playLoseSound(activity)
            save.gamesFinished++
            saveData(activity)

            showAlertDialog(
                activity.getString(R.string.result),
                activity.getString(R.string.you_lose),
                onQuitPressed
            )
        }
        it.jokerPlayedSound = { playJokerSounds(activity) }
        it.nukeBlownSound = { playNukeBlownSound(activity) }
        it.wildWastelandSound = { playWWSound(activity) }
    }

    ShowGame(activity, game, isBlitz) {
        if (game.isOver()) {
            onQuitPressed()
        } else {
            showAlertDialog(
                activity.getString(R.string.check_back_to_menu),
                activity.getString(R.string.check_back_to_menu_body),
                onQuitPressed
            )
        }
    }
}


@Composable
fun ShowBettingScreen(
    activity: MainActivity,
    enemy: Enemy,
    enemyBet: Int,
    setBet: (Int) -> Unit,
    setIsBlitz: (Boolean) -> Unit,
    setReward: (Int) -> Unit,
    goBack: () -> Unit,
    goForward: () -> Unit
) {
    val enemyName = when (enemy) {
        EnemyBenny -> stringResource(R.string.benny)
        EnemyCrooker -> stringResource(R.string.crooker)
        EnemyDrMobius -> stringResource(R.string.dr_mobius)
        EnemyEasyPete -> stringResource(R.string.easy_pete)
        EnemyElijah -> stringResource(R.string.elijah)
        EnemyHanlon -> stringResource(R.string.pve_enemy_chief_hanlon)
        EnemyLuc10 -> stringResource(R.string.luc10)
        EnemyNash -> stringResource(R.string.johnson_nash)
        EnemyNoBark -> stringResource(R.string.no_bark)
        EnemyOliver -> stringResource(R.string.pve_enemy_oliver_real)
        EnemySnuffles -> stringResource(R.string.snuffles)
        EnemyTabitha -> stringResource(R.string.tabitha)
        EnemyTheManInTheMirror -> stringResource(R.string.man_in_the_mirror)
        EnemyUlysses -> stringResource(R.string.pve_enemy_ulysses)
        EnemyVeronica -> stringResource(R.string.pve_enemy_veronica)
        EnemyVictor -> stringResource(R.string.pve_enemy_victor)
        EnemyVulpes -> stringResource(R.string.vulpes)
        else -> "?!?"
    }
    var bet by rememberScoped { mutableStateOf("") }
    var isBlitz: Boolean by rememberScoped { mutableStateOf(false) }

    fun countRewardLocal(): Int {
        return bet.toIntOrNull()?.let { countReward(it, enemyBet, isBlitz) } ?: 0
    }

    val state = rememberLazyListState()
    MenuItemOpen(activity, "$$$", "<-", { goBack() }) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(Modifier.fillMaxSize().rotate(180f)) {
                Box(Modifier.fillMaxSize().getTableBackground()) {}
            }
            LazyColumn(
                Modifier
                    .background(getBackgroundColor(activity))
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
                    Column(
                        Modifier.background(getBackgroundColor(activity)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        TextFallout(
                            stringResource(R.string.enemy_name, enemyName),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            16.sp,
                            Alignment.Center,
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            TextAlign.Center
                        )
                        TextFallout(
                            stringResource(R.string.enemy_s_bet, enemyBet),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            16.sp,
                            Alignment.Center,
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            TextAlign.Center
                        )

                        Spacer(Modifier.height(16.dp))

                        TextField(
                            modifier = Modifier.fillMaxWidth(0.5f),
                            singleLine = true,
                            enabled = true,
                            value = bet,
                            onValueChange = { bet = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                color = getTextColor(activity),
                                fontFamily = FontFamily(Font(R.font.monofont))
                            ),
                            label = {
                                TextFallout(
                                    text = stringResource(
                                        R.string.enter_your_bet,
                                        enemyBet,
                                        save.capsInHand
                                    ),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    14.sp,
                                    Alignment.Center,
                                    Modifier,
                                    TextAlign.Center
                                )
                            },
                            colors = TextFieldDefaults.colors().copy(
                                cursorColor = getTextColor(activity),
                                focusedContainerColor = getTextBackgroundColor(activity),
                                unfocusedContainerColor = getTextBackgroundColor(activity),
                                disabledContainerColor = getBackgroundColor(activity),
                            )
                        )

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            TextFallout(
                                stringResource(R.string.time_limit_anyone),
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                18.sp,
                                Alignment.Center,
                                Modifier
                                    .padding(8.dp),
                                TextAlign.Center
                            )
                            CheckboxCustom(activity, { isBlitz }, { isBlitz = it }) { true }

                        }

                        TextFallout(
                            stringResource(R.string.your_expected_reward, countRewardLocal()),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            18.sp,
                            Alignment.Center,
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            TextAlign.Center
                        )

                        val modifier = if (bet == "" || bet.toIntOrNull().let { it != null && it >= enemyBet && it <= save.capsInHand }) {
                            Modifier
                                .clickableOk(activity) {
                                    setIsBlitz(isBlitz)
                                    setBet(bet.toIntOrNull() ?: 0)
                                    setReward(countRewardLocal())

                                    save.capsInHand -= (bet.toIntOrNull() ?: 0)
                                    saveData(activity)

                                    goForward()
                                }
                                .background(getTextBackgroundColor(activity))
                                .padding(8.dp)
                        } else {
                            Modifier
                                .padding(8.dp)
                        }
                        TextFallout(
                            stringResource(R.string.let_s_go),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            24.sp,
                            Alignment.Center,
                            modifier,
                            TextAlign.Center
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}


fun countReward(playerBet: Int, enemyBet: Int, isBlitz: Boolean): Int {
    return if (playerBet == 0) {
        0
    } else if (!isBlitz) {
        playerBet + enemyBet
    } else {
        // Gain is O(log^2(k))
        val k = (playerBet + enemyBet).toDouble()
        k.pow(k.pow(1.0 / k)).toInt()
    }
}

fun winCard(activity: MainActivity, back: CardBack, isAlt: Boolean): String {
    fun isCardNew(card: Card): Boolean {
        return !save.isCardAvailableAlready(card)
    }

    val isNew = if (back == CardBack.STANDARD) true else ((0..3).random() > 0)
    val deck = CustomDeck(back, isAlt)
    deck.shuffle()
    val card = if (isNew) {
        deck.toList().firstOrNull(::isCardNew)
    } else {
        null
    }

    val message = if (card != null) {
        save.addCard(card)
        val suit = if (card.rank == Rank.JOKER) {
            (card.suit.ordinal + 1).toString()
        } else {
            activity.getString(card.suit.nameId)
        }
        val isAlt = if (isAlt) " (ALT!)" else ""
        "${activity.getString(card.rank.nameId)} $suit, ${activity.getString(back.getDeckName())}$isAlt"
    } else {
        val prize = if (isAlt) 50 else 15
        save.capsInHand += prize
        activity.getString(R.string.prize_caps, prize.toString())
    }
    saveData(activity)

    return activity.getString(R.string.you_have_won, message)
}