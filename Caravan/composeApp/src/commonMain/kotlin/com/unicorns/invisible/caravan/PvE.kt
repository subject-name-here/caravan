package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.check_back_to_menu
import caravan.composeapp.generated.resources.check_back_to_menu_body
import caravan.composeapp.generated.resources.custom_deck
import caravan.composeapp.generated.resources.deck_o_54
import caravan.composeapp.generated.resources.enemy_name
import caravan.composeapp.generated.resources.enemy_s_bet
import caravan.composeapp.generated.resources.enemy_with_caps
import caravan.composeapp.generated.resources.enemy_with_card
import caravan.composeapp.generated.resources.enter_your_bet
import caravan.composeapp.generated.resources.let_s_go
import caravan.composeapp.generated.resources.menu_pve
import caravan.composeapp.generated.resources.monofont
import caravan.composeapp.generated.resources.prize_caps
import caravan.composeapp.generated.resources.prize_chips
import caravan.composeapp.generated.resources.pve_caps_bet
import caravan.composeapp.generated.resources.pve_caps_won
import caravan.composeapp.generated.resources.pve_finished_to_started
import caravan.composeapp.generated.resources.pve_games_finished
import caravan.composeapp.generated.resources.pve_games_started
import caravan.composeapp.generated.resources.pve_games_won
import caravan.composeapp.generated.resources.pve_percentiles
import caravan.composeapp.generated.resources.pve_stats
import caravan.composeapp.generated.resources.pve_w_to_finished
import caravan.composeapp.generated.resources.pve_w_to_l
import caravan.composeapp.generated.resources.pve_w_to_started
import caravan.composeapp.generated.resources.result
import caravan.composeapp.generated.resources.select_enemy
import caravan.composeapp.generated.resources.story_mode
import caravan.composeapp.generated.resources.time_limit_anyone
import caravan.composeapp.generated.resources.tower
import caravan.composeapp.generated.resources.tutorial
import caravan.composeapp.generated.resources.wild_wasteland
import caravan.composeapp.generated.resources.xtras
import caravan.composeapp.generated.resources.you_have_won
import caravan.composeapp.generated.resources.you_lose
import caravan.composeapp.generated.resources.you_win
import caravan.composeapp.generated.resources.your_expected_reward
import caravan.composeapp.generated.resources.your_expected_reward_chips
import caravan.composeapp.generated.resources.your_reward_reward_caps
import caravan.composeapp.generated.resources.your_reward_reward_chips
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
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playVatsEnter
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.playWinSoundAlone
import com.unicorns.invisible.caravan.utils.startAmbient
import com.unicorns.invisible.caravan.utils.stopAmbient
import com.unicorns.invisible.caravan.utils.toString
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import kotlin.math.max


@Composable
fun ShowSelectPvE(
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
            ShowStats { showStats = false }
            return
        }
        showSelectEnemy -> {
            ShowPvE(showAlertDialog) { showSelectEnemy = false }
            return
        }
        showTower -> {
            TowerScreen(showAlertDialog) { showTower = false }
            return
        }
        showStory -> {
            ShowStoryList(showAlertDialog) { showStory = false }
            return
        }
        showTutorial -> {
            Tutorial { showTutorial = false }
            return
        }
    }

    MenuItemOpen(stringResource(Res.string.menu_pve), "<-", goBack) {
        Column(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            @Composable
            fun SubMenuItem(name: String, onClick: () -> Unit) {
                TextFallout(
                    name,
                    getTextColor(),
                    getTextStrokeColor(),
                    28.sp,
                    Modifier
                        .clickableSelect { onClick() }
                        .background(getTextBackgroundColor())
                        .padding(8.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            SubMenuItem(stringResource(Res.string.select_enemy)) { showSelectEnemy = true }
            Spacer(modifier = Modifier.height(12.dp))
            SubMenuItem(stringResource(Res.string.tower)) { showTower = true }
            Spacer(modifier = Modifier.height(12.dp))
            SubMenuItem(stringResource(Res.string.story_mode)) { showStory = true }
            Spacer(modifier = Modifier.height(12.dp))
            SubMenuItem(stringResource(Res.string.pve_stats)) { showStats = true }
            Spacer(modifier = Modifier.height(12.dp))
            SubMenuItem(stringResource(Res.string.tutorial)) { showTutorial = true }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


@Composable
fun ShowStats(goBack: () -> Unit) {
    MenuItemOpen(stringResource(Res.string.pve_stats), "<-", goBack) {
        val started = save.gamesStarted
        val finished = save.gamesFinished
        val won = save.wins
        val loss = finished - won
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(getBackgroundColor()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            TextFallout(
                stringResource(Res.string.pve_stats),
                getTextColor(),
                getTextStrokeColor(),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(12.dp))
            @Composable
            fun StatsItem(text: String) {
                TextFallout(
                    text,
                    getTextColor(),
                    getTextStrokeColor(),
                    16.sp,
                    Modifier,
                )
            }
            StatsItem(text = stringResource(Res.string.pve_games_started, started))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_games_finished, finished))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_games_won, won))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_caps_bet, save.capsBet))
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(text = stringResource(Res.string.pve_caps_won, save.capsWon))
            Spacer(modifier = Modifier.height(12.dp))
            TextFallout(
                stringResource(Res.string.pve_percentiles),
                getTextColor(),
                getTextStrokeColor(),
                20.sp,
                Modifier,
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatsItem(
                text = stringResource(
                    Res.string.pve_w_to_l,
                    if (loss == 0) "-" else (won.toDouble() / loss).toString(3)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(
                text = stringResource(
                    Res.string.pve_w_to_finished,
                    if (finished == 0) "-" else ((won.toDouble() / finished) * 100).toString(2)
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(
                text = stringResource(
                    Res.string.pve_w_to_started,
                    if (started == 0) "-" else (won.toDouble() / started * 100.0).toString(2)
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            StatsItem(
                text = stringResource(
                    Res.string.pve_finished_to_started,
                    if (started == 0) "-" else (finished.toDouble() / started * 100.0).toString(1)
                ),
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


@Composable
fun ShowPvE(
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit
) {
    var playAgainstEnemy by rememberScoped { mutableIntStateOf(-1) }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    @Composable
    fun StartWithEnemy(enemy: EnemyPve, goBack: () -> Unit) {
        StartGame(
            if (enemy.isEven)
                CollectibleDeck(save.selectedDeck)
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

    MenuItemOpen(stringResource(Res.string.select_enemy), "<-", goBack) {
        Column(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(
                selectedTab, Modifier.fillMaxWidth(),
                containerColor = getBackgroundColor(),
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = getSelectionColor()
                        )
                    }
                },
                divider = {
                    HorizontalDivider(color = getDividerColor())
                }
            ) {
                Tab(selectedTab == 0, { playSelectSound(); selectedTab = 0 },
                    selectedContentColor = getSelectionColor(),
                    unselectedContentColor = getTextBackgroundColor()
                ) {
                    TextFallout(
                        stringResource(Res.string.deck_o_54),
                        getTextColor(),
                        getTextStrokeColor(),
                        16.sp,
                        Modifier.padding(4.dp),
                    )
                }
                Tab(
                    selectedTab == 1, { playSelectSound(); selectedTab = 1 },
                    selectedContentColor = getSelectionColor(),
                    unselectedContentColor = getTextBackgroundColor()
                ) {
                    TextFallout(
                        stringResource(Res.string.custom_deck),
                        getTextColor(),
                        getTextStrokeColor(),
                        16.sp,
                        Modifier.padding(4.dp),
                    )
                }
                Tab(
                    selectedTab == 2, { playSelectSound(); selectedTab = 2 },
                    selectedContentColor = getSelectionColor(),
                    unselectedContentColor = getTextBackgroundColor()
                ) {
                    TextFallout(
                        stringResource(Res.string.wild_wasteland),
                        getTextColor(),
                        getTextStrokeColor(),
                        16.sp,
                        Modifier.padding(4.dp),
                    )
                }
                Tab(
                    selectedTab == 3, { playSelectSound(); selectedTab = 3 },
                    selectedContentColor = getSelectionColor(),
                    unselectedContentColor = getTextBackgroundColor()
                ) {
                    TextFallout(
                        stringResource(Res.string.xtras),
                        getTextColor(),
                        getTextStrokeColor(),
                        16.sp,
                        Modifier.padding(4.dp),
                    )
                }
            }

            @Composable
            fun OpponentItem(enemy: EnemyPve, onClick: () -> Unit) {
                val line = when (enemy) {
                    is EnemyPvENoBank -> {
                        val back = when (enemy) {
                            is EnemyHanlon -> CardBack.NCR
                            is EnemyVulpes -> CardBack.LEGION
                            is EnemyMadnessCardinal -> CardBack.MADNESS
                            is EnemyViqueen -> CardBack.VIKING
                        }
                        stringResource(Res.string.enemy_with_card,
                            stringResource(enemy.nameId),
                            stringResource(back.nameIdWithBackFileName.first)
                        )
                    }
                    is EnemyPvEWithBank -> {
                        stringResource(
                            Res.string.enemy_with_caps,
                            stringResource(enemy.nameId),
                            enemy.bank,
                            enemy.bet
                        )
                    }
                }
                TextFallout(
                    line,
                    getTextColor(),
                    getTextStrokeColor(),
                    18.sp,
                    Modifier
                        .background(getTextBackgroundColor())
                        .clickable { onClick() }
                        .padding(4.dp),
                )
            }

            Column(
                Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val enemies = save.enemiesGroups2[selectedTab]
                Spacer(modifier = Modifier.height(8.dp))
                enemies.forEach {
                    OpponentItem(it) {
                        playVatsEnter()
                        playAgainstEnemy = save.enemiesGroups2.flatten().indexOf(it)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


@Composable
fun StartGame(
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
            enemy as EnemyPvEWithBank, { myBet = it }, { isBlitz = it }, { reward = it }, goBack, {
                showBettingScreen = false
            }
        )
        return
    }
    val game: Game = rememberScoped {
        Game(
            CResources(CustomDeck().apply { addAll(playerDeck) }),
            enemy
        ).also {
            if (myBet > 0 && enemy is EnemyPvEWithBank) {
                enemy.bank -= enemy.bet
            }
            if (isBlitz) {
                save.sierraMadreChips -= myBet
            } else {
                save.capsInHand -= myBet
            }
            save.table += reward

            save.gamesStarted++
            save.capsBet += myBet
            saveData()
            it.startGame()
        }
    }

    LaunchedEffect(Unit) { startAmbient() }
    val scope = rememberCoroutineScope()
    val onQuitPressed = { stopAmbient(); goBack() }

    game.also {
        it.onWin = {
            processChallengesGameOver(it)

            save.gamesFinished++
            save.wins++

            if (reward == 0) {
                playWinSoundAlone()
            } else {
                playWinSound()
            }

            if (enemy is EnemyPvEWithBank) {
                if (isBlitz) {
                    if (reward > 0) {
                        enemy.winsBlitzBet++
                    } else {
                        enemy.winsBlitzNoBet++
                    }
                    save.sierraMadreChips += reward
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
                scope.launch {
                    showAlertDialog(
                        getString(Res.string.result),
                        getString(Res.string.you_win) +
                            if (reward == 0)
                                ""
                            else
                                getString(
                                    if (isBlitz) {
                                        Res.string.your_reward_reward_chips
                                    } else {
                                        Res.string.your_reward_reward_caps
                                    },
                                    reward.toString()
                                ),
                        onQuitPressed
                    )
                }
            } else if (enemy is EnemyPvENoBank) {
                val back = when (enemy) {
                    is EnemyHanlon -> CardBack.NCR
                    is EnemyVulpes -> CardBack.LEGION
                    is EnemyMadnessCardinal -> CardBack.MADNESS
                    is EnemyViqueen -> CardBack.VIKING
                }
                if (isBlitz) {
                    enemy.winsBlitz++
                } else {
                    enemy.wins++
                }
                scope.launch {
                    val rewardCard = winCard(back, isBlitz)
                    showAlertDialog(
                        getString(Res.string.result),
                        getString(Res.string.you_win) + rewardCard,
                        onQuitPressed
                    )
                }
            }

            saveData()
        }
        it.onLose = {
            playLoseSound()

            save.table -= reward
            if (enemy is EnemyPvEWithBank) {
                enemy.bank += reward
            }

            save.gamesFinished++
            saveData()

            scope.launch {
                showAlertDialog(
                    getString(Res.string.result),
                    getString(Res.string.you_lose),
                    onQuitPressed
                )
            }
        }
    }

    ShowGame(game, isBlitz) {
        if (game.isOver()) {
            onQuitPressed()
        } else {
            scope.launch {
                showAlertDialog(
                    getString(Res.string.check_back_to_menu),
                    getString(Res.string.check_back_to_menu_body),
                    onQuitPressed
                )
            }
        }
    }
}


@Composable
fun ShowBettingScreen(
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

    MenuItemOpen("$$$", "<-", { goBack() }) {
        Box(Modifier.fillMaxSize().getTableBackground().padding(bottom = 8.dp), contentAlignment = Alignment.Center) {
            Column(
                Modifier
                    .fillMaxHeight().wrapContentWidth()
                    .background(getBackgroundColor()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TextFallout(
                    stringResource(Res.string.enemy_name, enemyName),
                    getTextColor(),
                    getTextStrokeColor(),
                    16.sp,
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                )
                TextFallout(
                    stringResource(Res.string.enemy_s_bet, enemyBet),
                    getTextColor(),
                    getTextStrokeColor(),
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
                        color = getTextColor(),
                        fontFamily = FontFamily(Font(Res.font.monofont))
                    ),
                    label = {
                        TextFallout(
                            text = stringResource(
                                Res.string.enter_your_bet,
                                enemyBet,
                                if (isBlitz) {
                                    save.sierraMadreChips
                                } else {
                                    save.capsInHand
                                }
                            ),
                            getTextColor(),
                            getTextStrokeColor(),
                            14.sp,
                            Modifier,
                        )
                    },
                    colors = TextFieldDefaults.colors().copy(
                        cursorColor = getTextColor(),
                        focusedContainerColor = getTextBackgroundColor(),
                        unfocusedContainerColor = getTextBackgroundColor(),
                        disabledContainerColor = getBackgroundColor(),
                    )
                )

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    TextFallout(
                        stringResource(Res.string.time_limit_anyone),
                        getTextColor(),
                        getTextStrokeColor(),
                        18.sp,
                        Modifier.padding(8.dp),
                    )
                    CheckboxCustom({ isBlitz }, { isBlitz = it; bet = "" }) { true }

                }

                TextFallout(
                    stringResource(
                        if (isBlitz)
                            Res.string.your_expected_reward_chips
                        else
                            Res.string.your_expected_reward,
                        countRewardLocal()
                    ),
                    getTextColor(),
                    getTextStrokeColor(),
                    18.sp,
                    Modifier.fillMaxWidth().padding(8.dp),
                )

                val modifier = if (bet == "" || bet.toIntOrNull().let {
                        it != null && it >= enemyBet &&
                                it <= if (isBlitz) save.sierraMadreChips else save.capsInHand
                    }) {
                    Modifier
                        .clickableOk() {
                            setIsBlitz(isBlitz)
                            setBet(bet.toIntOrNull() ?: 0)
                            setReward(countRewardLocal())
                            saveData()
                            goForward()
                        }
                        .background(getTextBackgroundColor())
                        .padding(8.dp)
                } else {
                    Modifier
                        .padding(8.dp)
                }
                TextFallout(
                    stringResource(Res.string.let_s_go),
                    getTextColor(),
                    getTextStrokeColor(),
                    24.sp,
                    modifier,
                )
                Spacer(Modifier.height(12.dp))
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

suspend fun winCard(back: CardBack, isBlitz: Boolean): String {
    fun isCardNew(card: CardWithPrice): Boolean {
        return !save.isCardAvailableAlready(card)
    }

    val isNew = if (back == CardBack.STANDARD) true else ((0..3).random() > 0)
    val deck = CollectibleDeck(back)
    val card = if (isNew) {
        deck.toList().filter(::isCardNew).randomOrNull()
    } else {
        null
    }

    val message = if (card != null) {
        save.addCard(card)
        val rankSuit = when (card) {
            is CardFaceSuited -> getString(card.rank.nameId) to getString(card.suit.nameId)
            is CardJoker -> getString(card.rank.nameId) to card.number.n.toString()
            is CardNumber -> getString(card.rank.nameId) to getString(card.suit.nameId)
        }
        val backName = card.getBack().nameIdWithBackFileName.first
        "${rankSuit.first} ${rankSuit.second} ${getString(backName)}"
    } else {
        val prize = (back.getRarityMult() * 10.0).toInt()
        if (isBlitz) {
            save.sierraMadreChips += prize
            getString(Res.string.prize_chips, prize.toString())
        } else {
            save.capsInHand += prize
            getString(Res.string.prize_caps, prize.toString())
        }
    }
    saveData()

    return getString(Res.string.you_have_won, message)
}