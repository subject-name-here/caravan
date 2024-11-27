package com.unicorns.invisible.caravan

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.RectangleShape
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
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDialogBackground
import com.unicorns.invisible.caravan.utils.getDialogTextColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playNotificationSound
import com.unicorns.invisible.caravan.utils.playNukeBlownSound
import com.unicorns.invisible.caravan.utils.playVatsEnter
import com.unicorns.invisible.caravan.utils.playWWSound
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.startAmbient
import com.unicorns.invisible.caravan.utils.stopAmbient
import java.util.Locale
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
        showTutorial -> {
            // TODO
            // return
        }
        showStory -> {
            ShowStoryList(activity, showAlertDialog) { showStory = false }
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
                Spacer(modifier = Modifier.height(16.dp))

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

                SubMenuItem("Select Enemy") { showSelectEnemy = true }
                Spacer(modifier = Modifier.height(12.dp))
                SubMenuItem(stringResource(R.string.tower)) { showTower = true }
                Spacer(modifier = Modifier.height(12.dp))
                SubMenuItem("Story Mode") { showStory = true }
                Spacer(modifier = Modifier.height(12.dp))
                SubMenuItem(stringResource(R.string.pve_stats)) { showStats = true }
                Spacer(modifier = Modifier.height(12.dp))
                SubMenuItem("Tutorial") {  }
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
        // TODO: more stats
        val started = save.gamesStarted
        val finished = save.gamesFinished
        val won = save.wins
        val loss = finished - won
        val state2 = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity))
                .padding(horizontal = 16.dp, vertical = 16.dp)
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
                        14.sp,
                        Alignment.Center,
                        Modifier,
                        TextAlign.Center,
                    )
                }
                StatsItem(
                    text = stringResource(
                        R.string.pve_games_started,
                        started
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatsItem(
                    text = stringResource(
                        R.string.pve_games_finished,
                        finished
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatsItem(
                    text = stringResource(R.string.pve_games_won, won),
                )
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
    var showGameCrooker by rememberSaveable { mutableStateOf(false) }

    var showGameSnuffles by rememberSaveable { mutableStateOf(false) }
    var showGameEasyPete by rememberSaveable { mutableStateOf(false) }
    var showGameMadnessCardinal by rememberSaveable { mutableStateOf(false) }
    var showGameLuc10 by rememberSaveable { mutableStateOf(false) }
    var showGameDrMobius by rememberSaveable { mutableStateOf(false) }
    var showGameTheManInTheMirror by rememberSaveable { mutableStateOf(false) }

    var showOliverWarning by rememberSaveable { mutableStateOf(false) }

    when {
        showGameOliver -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.selectedDeck.first, save.selectedDeck.second),
                enemy = EnemyOliver,
                showAlertDialog = showAlertDialog
            ) {
                showGameOliver = false
            }
            return
        }
        showGameVeronica -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.selectedDeck.first, save.selectedDeck.second),
                enemy = EnemyVeronica,
                showAlertDialog = showAlertDialog
            ) {
                showGameVeronica = false
            }
            return
        }
        showGameVictor -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.selectedDeck.first, save.selectedDeck.second),
                enemy = EnemyVictor,
                showAlertDialog = showAlertDialog
            ) {
                showGameVictor = false
            }
            return
        }
        showGameChiefHanlon -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.selectedDeck.first, save.selectedDeck.second),
                enemy = EnemyHanlon,
                showAlertDialog = showAlertDialog
            ) {
                showGameChiefHanlon = false
            }
            return
        }
        showGameUlysses -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.selectedDeck.first, save.selectedDeck.second),
                enemy = EnemyUlysses,
                showAlertDialog = showAlertDialog
            ) {
                showGameUlysses = false
            }
            return
        }
        showGameBenny -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.selectedDeck.first, save.selectedDeck.second),
                enemy = EnemyBenny,
                showAlertDialog = showAlertDialog
            ) {
                showGameBenny = false
            }
            return
        }

        showGameNoBark -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.getCustomDeckCopy()),
                enemy = EnemyNoBark,
                showAlertDialog = showAlertDialog
            ) {
                showGameNoBark = false
            }
            return
        }
        showGameNash -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.getCustomDeckCopy()),
                enemy = EnemyNash,
                showAlertDialog = showAlertDialog
            ) {
                showGameNash = false
            }
            return
        }
        showGameTabitha -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.getCustomDeckCopy()),
                enemy = EnemyNash,
                showAlertDialog = showAlertDialog
            ) {
                showGameTabitha = false
            }
            return
        }
        showGameVulpes -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.getCustomDeckCopy()),
                enemy = EnemyVulpes,
                showAlertDialog = showAlertDialog
            ) {
                showGameVulpes = false
            }
            return
        }
        showGameElijah -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.getCustomDeckCopy()),
                enemy = EnemyElijah,
                showAlertDialog = showAlertDialog
            ) {
                showGameElijah = false
            }
            return
        }
        showGameCrooker -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.getCustomDeckCopy()),
                enemy = EnemyCrooker,
                showAlertDialog = showAlertDialog
            ) {
                showGameCrooker = false
            }
            return
        }

        showGameSnuffles -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.getCustomDeckCopy()),
                enemy = EnemySnuffles,
                showAlertDialog = showAlertDialog
            ) {
                showGameSnuffles = false
            }
            return
        }
        showGameEasyPete -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.selectedDeck.first, save.selectedDeck.second),
                enemy = EnemyEasyPete,
                showAlertDialog = showAlertDialog
            ) {
                showGameEasyPete = false
            }
            return
        }
        showGameMadnessCardinal -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.getCustomDeckCopy()),
                enemy = EnemyMadnessCardinal,
                showAlertDialog = showAlertDialog
            ) {
                showGameMadnessCardinal = false
            }
            return
        }
        showGameLuc10 -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.selectedDeck.first, save.selectedDeck.second),
                enemy = EnemyLuc10,
                showAlertDialog = showAlertDialog
            ) {
                showGameLuc10 = false
            }
            return
        }
        showGameDrMobius -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.getCustomDeckCopy()),
                enemy = EnemyDrMobius,
                showAlertDialog = showAlertDialog
            ) {
                showGameDrMobius = false
            }
            return
        }
        showGameTheManInTheMirror -> {
            StartGame(
                activity = activity,
                playerCResources = CResources(save.getCustomDeckCopy()),
                enemy = EnemyTheManInTheMirror,
                showAlertDialog = showAlertDialog
            ) {
                showGameTheManInTheMirror = false
            }
            return
        }
    }

    MenuItemOpen(activity, "Select Enemy", "<-", goBack) {
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
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
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
                    fun OpponentItem(name: String, onClick: () -> Unit) {
                        TextFallout(
                            name,
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            16.sp,
                            Alignment.Center,
                            Modifier
                                .clickable { onClick() }
                                .background(getTextBackgroundColor(activity))
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    }

                    Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                        var selectedTab by rememberSaveable { mutableIntStateOf(0) }
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
                            Tab(selectedTab == 0, { selectedTab = 0 }, 
                                selectedContentColor = getSelectionColor(activity),
                                unselectedContentColor = getTextBackgroundColor(activity)
                            ) {
                                TextFallout(
                                    "Deck o' 54",
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    16.sp,
                                    Alignment.Center,
                                    Modifier.padding(4.dp),
                                    TextAlign.Center
                                )
                            }
                            Tab(
                                selectedTab == 1, { selectedTab = 1 },
                                selectedContentColor = getSelectionColor(activity),
                                unselectedContentColor = getTextBackgroundColor(activity)
                            ) {
                                TextFallout(
                                    "Custom Deck",
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    16.sp,
                                    Alignment.Center,
                                    Modifier.padding(4.dp),
                                    TextAlign.Center
                                )
                            }
                            Tab(
                                selectedTab == 2, { selectedTab = 2 },
                                selectedContentColor = getSelectionColor(activity),
                                unselectedContentColor = getTextBackgroundColor(activity)
                            ) {
                                TextFallout(
                                    "Wild Wasteland",
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    16.sp,
                                    Alignment.Center,
                                    Modifier.padding(4.dp),
                                    TextAlign.Center
                                )
                            }
                            Tab(
                                selectedTab == 3, { selectedTab = 3 },
                                selectedContentColor = getSelectionColor(activity),
                                unselectedContentColor = getTextBackgroundColor(activity)
                            ) {
                                TextFallout(
                                    "Extra Content",
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
                                when (save.oliverStatus) {
                                    0 -> OpponentItem(stringResource(R.string.pve_enemy_oliver_fake)) {
                                        showOliverWarning = true
                                    }
                                    1 -> OpponentItem(stringResource(R.string.pve_enemy_oliver_real)) {
                                        playVatsEnter(activity); showGameOliver = true
                                    }
                                    2 -> OpponentItem(stringResource(R.string.pve_enemy_oliver_real)) {
                                        showOliverWarning = true
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.pve_enemy_veronica)) { playVatsEnter(activity); showGameVeronica = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.pve_enemy_victor)) { playVatsEnter(activity); showGameVictor = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.pve_enemy_chief_hanlon)) { playVatsEnter(activity); showGameChiefHanlon = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.pve_enemy_ulysses)) { playVatsEnter(activity); showGameUlysses = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.benny)) { playVatsEnter(activity); showGameBenny = true }
                            }
                            1 -> {
                                OpponentItem(stringResource(R.string.no_bark)) { playVatsEnter(activity); showGameNoBark = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.johnson_nash)) { playVatsEnter(activity); showGameNash = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.tabitha)) { playVatsEnter(activity); showGameTabitha = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.vulpes)) { playVatsEnter(activity); showGameVulpes = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.elijah)) { playVatsEnter(activity); showGameElijah = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.crooker)) { playVatsEnter(activity); showGameCrooker = true }
                            }
                            2 -> {
                                OpponentItem(stringResource(R.string.snuffles)) { playVatsEnter(activity); showGameSnuffles = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.easy_pete)) { playVatsEnter(activity); showGameEasyPete = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.madness_cardinal)) { playVatsEnter(activity); showGameMadnessCardinal = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.luc10)) { playVatsEnter(activity); showGameLuc10 = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.dr_mobius)) { playVatsEnter(activity); showGameDrMobius = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.man_in_the_mirror)) { playVatsEnter(activity); showGameTheManInTheMirror = true }
                            }
                            3 -> {
                                TextFallout(
                                    "COMING SOON!!?",
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


    if (showOliverWarning) {
        LaunchedEffect(Unit) {
            playNotificationSound(activity) {}
        }
        when (save.oliverStatus) {
            0 -> {
                AlertDialog(
                    modifier = Modifier.border(width = 4.dp, color = getTextColor(activity)),
                    onDismissRequest = {},
                    confirmButton = {
                        TextFallout(
                            stringResource(R.string.close),
                            getDialogBackground(activity),
                            getDialogBackground(activity),
                            18.sp, Alignment.Center,
                            Modifier
                                .background(getDialogTextColor(activity))
                                .clickableCancel(activity) {
                                    save.oliverStatus = 1
                                    saveData(activity)
                                    showOliverWarning = false
                                }
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    },
                    title = {
                        TextFallout(
                            "Oh no!",
                            getDialogTextColor(activity), getDialogTextColor(activity),
                            24.sp, Alignment.CenterStart, Modifier,
                            TextAlign.Start
                        )
                    },
                    text = {
                        TextFallout(
                            "Sorry, but General Lee Oliver is too busy!\n\nVault-Tec offers you the sincerest apologies and replaces your enemy with another one of the same name.",
                            getDialogTextColor(activity),
                            getDialogTextColor(activity),
                            16.sp, Alignment.CenterStart, Modifier,
                            TextAlign.Start
                        )
                    },
                    containerColor = getDialogBackground(activity),
                    textContentColor = getDialogTextColor(activity),
                    shape = RectangleShape,
                )
            }
            2 -> {
                AlertDialog(
                    modifier = Modifier.border(width = 4.dp, color = getTextColor(activity)),
                    onDismissRequest = {},
                    confirmButton = {
                        TextFallout(
                            "...",
                            getDialogBackground(activity),
                            getDialogBackground(activity),
                            18.sp, Alignment.Center,
                            Modifier
                                .background(getDialogTextColor(activity))
                                .clickableCancel(activity) {
                                    showOliverWarning = false
                                }
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    },
                    title = {
                        TextFallout(
                            "But nobody came.",
                            getDialogTextColor(activity), getDialogTextColor(activity),
                            24.sp, Alignment.CenterStart, Modifier,
                            TextAlign.Start
                        )
                    },
                    text = {
                        TextFallout(
                            "Oliver Swanick is dead.",
                            getDialogTextColor(activity),
                            getDialogTextColor(activity),
                            16.sp, Alignment.CenterStart, Modifier,
                            TextAlign.Start
                        )
                    },
                    containerColor = getDialogBackground(activity),
                    textContentColor = getDialogTextColor(activity),
                    shape = RectangleShape,
                )
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

    val isBettingEnemy = enemy !is EnemyMadnessCardinal && enemy !is EnemyLuc10

    var showBettingScreen: Boolean by rememberScoped { mutableStateOf(isBettingEnemy) }

    if (showBettingScreen) {
        ShowBettingScreen(
            activity, enemy, { bet = it }, { isBlitz = it }, { reward = it }
        ) { showBettingScreen = false }
        return
    }

    val game: Game = rememberScoped {
        Game(
            playerCResources,
            enemy
        ).also {
            save.gamesStarted++
            saveData(activity)
            it.startGame()
        }
    }

    LaunchedEffect(Unit) { startAmbient(activity) }
    val onQuitPressed = {
        if (!game.isOver()) {
            if (isBettingEnemy) {
                val enemyCaps = save.enemyCapsLeft[enemy.getBankNumber()] ?: 0
                save.enemyCapsLeft[enemy.getBankNumber()] = enemyCaps + reward
            }
        }
        stopAmbient(); goBack()
    }

    game.also {
        it.onWin = {
            // TODO: achievementys!!!

            activity.processChallengesGameOver(it)

            playWinSound(activity)
            save.gamesFinished++
            save.wins++
            if (enemy is EnemyOliver) {
                save.oliverStatus = 2
            }

            if (isBettingEnemy) {
                save.capsInHand += reward

                showAlertDialog(
                    activity.getString(R.string.result),
                    activity.getString(R.string.you_win) + "Your reward: $reward caps!!",
                    onQuitPressed
                )
            } else {
                when (enemy) {
                    is EnemyMadnessCardinal -> {
                        val rewardCard = winCard(activity, CardBack.MADNESS, false)

                        showAlertDialog(
                            activity.getString(R.string.result),
                            activity.getString(R.string.you_win) + "Your reward: card $rewardCard!!",
                            onQuitPressed
                        )
                    }
                    else -> { onQuitPressed() }
                }
            }

            saveData(activity)

        }
        it.onLose = {
            playLoseSound(activity)
            save.gamesFinished++
            if (isBettingEnemy) {
                val enemyCaps = save.enemyCapsLeft[enemy.getBankNumber()] ?: 0
                save.enemyCapsLeft[enemy.getBankNumber()] = enemyCaps + reward
            }
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

    ShowGame(activity, game) {
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
    setBet: (Int) -> Unit,
    setIsBlitz: (Boolean) -> Unit,
    setReward: (Int) -> Unit,
    goBack: () -> Unit,
) {
    val enemyName = when (enemy) {
        EnemyBenny -> stringResource(R.string.benny)
        EnemyCrooker -> stringResource(R.string.crooker)
        EnemyDrMobius -> stringResource(R.string.dr_mobius)
        EnemyEasyPete -> stringResource(R.string.easy_pete)
        EnemyElijah -> stringResource(R.string.elijah)
        EnemyHanlon -> stringResource(R.string.pve_enemy_chief_hanlon)
        EnemyLuc10 -> stringResource(R.string.luc10)
        EnemyMadnessCardinal -> stringResource(R.string.madness_cardinal)
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
    var bet: Int by rememberScoped { mutableIntStateOf(0) }
    var enemyBet: Int by rememberScoped { mutableIntStateOf(run {
        val capsLeft = save.enemyCapsLeft[enemy.getBankNumber()] ?: 0
        if (capsLeft < 10) {
            capsLeft
        } else {
            capsLeft / 2
        }
    }) }
    var isBlitz: Boolean by rememberScoped { mutableStateOf(false) }

    Scaffold(bottomBar = {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .background(getBackgroundColor(activity))
        ) {
            TextFallout(
                stringResource(R.string.back_to_menu),
                getTextColor(activity),
                getTextStrokeColor(activity),
                16.sp,
                Alignment.Center,
                Modifier
                    .fillMaxWidth()
                    .clickableCancel(activity) {
                        goBack()
                    }
                    .padding(8.dp),
                TextAlign.Center
            )
        }
    }) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .rotate(180f)) {
            Box(
                Modifier
                    .fillMaxSize()
                    .getTableBackground()) {}
        }
        Box(Modifier.padding(innerPadding)) {
            Column(
                Modifier.wrapContentSize().background(getBackgroundColor(activity)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TextFallout(
                    "Enemy: $enemyName",
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
                    "Enemy's bet: $enemyBet",
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    16.sp,
                    Alignment.Center,
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    TextAlign.Center
                )

                TextField(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    singleLine = true,
                    enabled = true,
                    value = bet.toString(),
                    onValueChange = { bet = run {
                        // TODO: make it betteerrrrr
                        val betWritten = it.toIntOrNull() ?: 0
                        if (betWritten != 0) {
                            if (betWritten < enemyBet) {
                                0
                            } else if (betWritten > save.capsInHand) {
                                if (save.capsInHand < enemyBet) {
                                    0
                                } else {
                                    save.capsInHand
                                }
                            } else {
                                betWritten
                            }
                        } else {
                            0
                        }
                    } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = getTextColor(activity),
                        fontFamily = FontFamily(Font(R.font.monofont))
                    ),
                    label = {
                        TextFallout(
                            text = "Enter your bet (either 0 or something between $enemyBet and ${save.capsInHand}):",
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

                CheckboxCustom(activity, { isBlitz }, { isBlitz = it }) { true }

                TextFallout(
                    "Your expected reward: ${countReward(bet, enemyBet, isBlitz)} caps.",
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
                    "LET'S GO!",
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    16.sp,
                    Alignment.Center,
                    Modifier
                        .fillMaxWidth()
                        .clickableOk(activity) {
                            setIsBlitz(isBlitz)
                            setBet(bet)
                            setReward(countReward(bet, enemyBet, isBlitz))

                            save.capsInHand -= bet
                            save.enemyCapsLeft -= enemyBet
                            saveData(activity)

                            goBack()
                        }
                        .padding(8.dp),
                    TextAlign.Center
                )
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
        val k = playerBet.toDouble() / (1.0 + enemyBet)
        val p = k.pow(1.0 / k)
        (playerBet + enemyBet).toDouble().pow(p).toInt()
    }
}

fun winCard(
    activity: MainActivity,
    back: CardBack,
    isAlt: Boolean
): String {
    fun isCardNew(card: Card): Boolean {
        return save.availableCards.none { aCard ->
            aCard.rank == card.rank && aCard.suit == card.suit && aCard.back == card.back && aCard.isAlt == card.isAlt
        }
    }

    val isNew = if (back == CardBack.STANDARD) true else ((0..2).random() > 0)
    val deck = CustomDeck(back, isAlt)
    deck.shuffle()
    val card = if (isNew) {
        deck.toList().firstOrNull(::isCardNew)
    } else {
        null
    }

    // TODO: card
    val message = if (card != null) {
        save.availableCards.add(card)
        "CARD"
    } else {
        save.capsInHand += if (isAlt) 50 else 15
        "CAPS"
    }
    saveData(activity)

    return message
}