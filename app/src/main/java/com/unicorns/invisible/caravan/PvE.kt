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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.EnemyHanlon
import com.unicorns.invisible.caravan.model.enemy.EnemyMadnessCardinal
import com.unicorns.invisible.caravan.model.enemy.EnemyPvENoBank
import com.unicorns.invisible.caravan.model.enemy.EnemyPvEWithBank
import com.unicorns.invisible.caravan.model.enemy.EnemyPve
import com.unicorns.invisible.caravan.model.enemy.EnemyViqueen
import com.unicorns.invisible.caravan.model.enemy.EnemyVulpes
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CardWithPrice
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
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
import com.unicorns.invisible.caravan.utils.playWinSoundAlone
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.startAmbient
import com.unicorns.invisible.caravan.utils.stopAmbient
import java.util.Locale
import kotlin.math.max


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
                        Modifier
                            .clickableSelect(activity) { onClick() }
                            .background(getTextBackgroundColor(activity))
                            .padding(8.dp),
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
                    Modifier,
                )
                Spacer(modifier = Modifier.height(12.dp))
                @Composable
                fun StatsItem(text: String) {
                    TextFallout(
                        text,
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Modifier,
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
                    Modifier,
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
    var playAgainstEnemy by rememberScoped { mutableIntStateOf(-1) }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    @Composable
    fun StartWithEnemy(enemy: EnemyPve, goBack: () -> Unit) {
        StartGame(
            activity, if (enemy.isEven)
                CollectibleDeck(save.selectedDeck.first, save.selectedDeck.second)
            else
                save.getCurrentDeckCopy(),
            enemy, showAlertDialog, goBack
        )
    }

    if (playAgainstEnemy != -1) {
        val enemyList = save.enemiesGroups2.flatten()
        if (playAgainstEnemy !in enemyList.indices) {
            playAgainstEnemy = -1
            return
        }
        StartWithEnemy(enemyList[playAgainstEnemy]) { playAgainstEnemy = -1 }
        return
    }

    MenuItemOpen(activity, stringResource(R.string.select_enemy), "<-", goBack) {
        Column(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                        Modifier.padding(4.dp),
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
                        Modifier.padding(4.dp),
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
                        Modifier.padding(4.dp),
                    )
                }
                Tab(
                    selectedTab == 3, { playSelectSound(activity); selectedTab = 3 },
                    selectedContentColor = getSelectionColor(activity),
                    unselectedContentColor = getTextBackgroundColor(activity)
                ) {
                    TextFallout(
                        stringResource(R.string.xtras),
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        16.sp,
                        Modifier.padding(4.dp),
                    )
                }
            }

            @Composable
            fun OpponentItem(enemy: EnemyPve, onClick: () -> Unit) {
                val line = when (enemy) {
                    is EnemyPvENoBank -> {
                        val (back, backNumber) = when (enemy) {
                            is EnemyHanlon -> CardBack.FNV_FACTION to 0
                            is EnemyVulpes -> CardBack.FNV_FACTION to 1
                            is EnemyMadnessCardinal -> CardBack.MADNESS to 0
                            is EnemyViqueen -> CardBack.VIKING to 0
                        }
                        stringResource(R.string.enemy_with_card,
                            stringResource(enemy.nameId),
                            stringResource(back.nameIdWithBackFileName[backNumber].first)
                        )
                    }
                    is EnemyPvEWithBank -> {
                        stringResource(
                            R.string.enemy_with_caps,
                            stringResource(enemy.nameId),
                            enemy.bank,
                            enemy.bet
                        )
                    }
                }
                TextFallout(
                    line,
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    18.sp,
                    Modifier
                        .background(getTextBackgroundColor(activity))
                        .clickable { onClick() }
                        .padding(4.dp),
                )
            }

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
                    val enemies = save.enemiesGroups2[selectedTab]
                    Spacer(modifier = Modifier.height(8.dp))
                    enemies.forEach {
                        OpponentItem(it) {
                            playVatsEnter(activity)
                            playAgainstEnemy = save.enemiesGroups2.flatten().indexOf(it)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun StartGame(
    activity: MainActivity,
    playerDeck: CollectibleDeck,
    enemy: EnemyPve,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit,
) {
    var showBettingScreen by rememberScoped { mutableStateOf(enemy is EnemyPvEWithBank) }
    var myBet by rememberScoped { mutableIntStateOf(0) }
    var reward: Int by rememberScoped { mutableIntStateOf(0) }
    var isBlitz: Boolean by rememberScoped { mutableStateOf(false) }

    if (showBettingScreen) {
        ShowBettingScreen(
            activity, enemy as EnemyPvEWithBank, { myBet = it }, { isBlitz = it }, { reward = it }, goBack, {
                showBettingScreen = false
            }
        )
        return
    }

    val isDeckCourier6 by rememberScoped { mutableStateOf(playerDeck.isDeckCourier6()) }
    val game: Game = rememberScoped {
        Game(
            CResources(CustomDeck().apply { addAll(playerDeck) }),
            enemy
        ).also {
            if (myBet > 0 && enemy is EnemyPvEWithBank) {
                enemy.bank -= enemy.bet
            }
            if (isBlitz) {
                save.silverRushChips -= myBet
            } else {
                save.capsInHand -= myBet
            }
            save.table += reward

            save.gamesStarted++
            save.capsBet += myBet
            saveData(activity)
            it.startGame()
        }
    }

    LaunchedEffect(Unit) { startAmbient(activity) }
    val onQuitPressed = { stopAmbient(); goBack() }

    game.also {
        it.onWin = {
            if (isDeckCourier6) {
                activity.achievementsClient?.unlock(activity.getString(
                    R.string.achievement_just_load_everything_up_with_sixes_and_tens_and_kings
                ))
            }

            activity.processChallengesGameOver(it)

            save.gamesFinished++
            save.wins++

            if (reward == 0) {
                playWinSoundAlone(activity)
            } else {
                playWinSound(activity)
            }

            if (enemy is EnemyPvEWithBank) {
                if (isBlitz) {
                    if (reward > 0) {
                        enemy.winsBlitzBet++
                    } else {
                        enemy.winsBlitzNoBet++
                    }
                    save.silverRushChips += reward
                } else {
                    if (reward > 0) {
                        enemy.winsBet++
                    } else {
                        enemy.winsNoBet++
                    }
                    save.capsInHand += reward
                }
                save.table -= reward

                save.capsWon += reward
                save.maxBetWon = max(save.maxBetWon, reward)
                showAlertDialog(
                    activity.getString(R.string.result),
                    activity.getString(R.string.you_win) +
                        if (reward == 0)
                            ""
                        else
                            activity.getString(
                                if (isBlitz) {
                                    R.string.your_reward_reward_chips
                                } else {
                                    R.string.your_reward_reward_caps
                                },
                                reward.toString()
                            ),
                    onQuitPressed
                )
            } else if (enemy is EnemyPvENoBank) {
                val (back, number) = when (enemy) {
                    is EnemyHanlon -> CardBack.FNV_FACTION to 0
                    is EnemyVulpes -> CardBack.FNV_FACTION to 1
                    is EnemyMadnessCardinal -> CardBack.MADNESS to 0
                    is EnemyViqueen -> CardBack.VIKING to 0
                }
                if (isBlitz) {
                    enemy.winsBlitz++
                } else {
                    enemy.wins++
                }
                val rewardCard = winCard(activity, back, number, isBlitz)

                showAlertDialog(
                    activity.getString(R.string.result),
                    activity.getString(R.string.you_win) + rewardCard,
                    onQuitPressed
                )
            }

            saveData(activity)
        }
        it.onLose = {
            playLoseSound(activity)

            save.table -= reward
            if (enemy is EnemyPvEWithBank) {
                enemy.bank += reward
            }

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
    enemy: EnemyPvEWithBank,
    setBet: (Int) -> Unit,
    setIsBlitz: (Boolean) -> Unit,
    setReward: (Int) -> Unit,
    goBack: () -> Unit,
    goForward: () -> Unit
) {
    val enemyName = stringResource(enemy.nameId)
    var bet by rememberScoped { mutableStateOf("") }
    val enemyBet by rememberScoped { mutableIntStateOf(enemy.bet) }
    var isBlitz: Boolean by rememberScoped { mutableStateOf(false) }

    fun countRewardLocal(): Int {
        return bet.toIntOrNull()?.let { countReward(it, enemyBet) } ?: 0
    }

    val state = rememberLazyListState()
    MenuItemOpen(activity, "$$$", "<-", { goBack() }) {
        Box(Modifier.fillMaxSize().padding(bottom = 8.dp), contentAlignment = Alignment.Center) {
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
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                        )
                        TextFallout(
                            stringResource(R.string.enemy_s_bet, enemyBet),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            16.sp,
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
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
                                        if (isBlitz) {
                                            save.silverRushChips
                                        } else {
                                            save.capsInHand
                                        }
                                    ),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    14.sp,
                                    Modifier,
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
                                Modifier.padding(8.dp),
                            )
                            CheckboxCustom(activity, { isBlitz }, { isBlitz = it; bet = "" }) { true }

                        }

                        TextFallout(
                            stringResource(
                                if (isBlitz)
                                    R.string.your_expected_reward_chips
                                else
                                    R.string.your_expected_reward,
                                countRewardLocal()
                            ),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            18.sp,
                            Modifier.fillMaxWidth().padding(8.dp),
                        )

                        val modifier = if (bet == "" || bet.toIntOrNull().let {
                            it != null && it >= enemyBet &&
                                    it <= if (isBlitz) save.silverRushChips else save.capsInHand
                            }) {
                            Modifier
                                .clickableOk(activity) {
                                    setIsBlitz(isBlitz)
                                    setBet(bet.toIntOrNull() ?: 0)
                                    setReward(countRewardLocal())
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
                            modifier,
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}


fun countReward(playerBet: Int, enemyBet: Int): Int {
    return if (playerBet == 0) {
        0
    } else {
        playerBet + enemyBet
    }
}

fun winCard(activity: MainActivity, back: CardBack, number: Int, isBlitz: Boolean): String {
    fun isCardNew(card: CardWithPrice): Boolean {
        return !save.isCardAvailableAlready(card)
    }

    val isNew = if (back == CardBack.STANDARD) true else ((0..3).random() > 0)
    val deck = CollectibleDeck(back, number)
    val card = if (isNew) {
        deck.toList().filter(::isCardNew).randomOrNull()
    } else {
        null
    }

    val message = if (card != null) {
        save.addCard(card)
        val rankSuit = when (card) {
            is CardFaceSuited -> activity.getString(card.rank.nameId) to activity.getString(card.suit.nameId)
            is CardJoker -> activity.getString(card.rank.nameId) to card.number.n.toString()
            is CardNumber -> activity.getString(card.rank.nameId) to activity.getString(card.suit.nameId)
        }
        val backName = card.getBack().nameIdWithBackFileName[card.getBackNumber()].first
        "${rankSuit.first} ${rankSuit.second} ${activity.getString(backName)}"
    } else {
        val prize = (back.getRarityMult(number) * 10.0).toInt()
        if (isBlitz) {
            save.silverRushChips += prize
            activity.getString(R.string.prize_chips, prize.toString())
        } else {
            save.capsInHand += prize
            activity.getString(R.string.prize_caps, prize.toString())
        }
    }
    saveData(activity)

    return activity.getString(R.string.you_have_won, message)
}