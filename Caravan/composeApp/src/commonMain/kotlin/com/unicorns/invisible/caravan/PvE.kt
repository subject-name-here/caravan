package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.bet_call
import caravan.composeapp.generated.resources.bet_raise2
import caravan.composeapp.generated.resources.check_back_to_menu
import caravan.composeapp.generated.resources.check_back_to_menu_body
import caravan.composeapp.generated.resources.closed
import caravan.composeapp.generated.resources.custom_deck
import caravan.composeapp.generated.resources.deck_o_54
import caravan.composeapp.generated.resources.enemy_name
import caravan.composeapp.generated.resources.enemy_s_bet
import caravan.composeapp.generated.resources.enemy_s_bet_can_raise
import caravan.composeapp.generated.resources.enemy_with_caps
import caravan.composeapp.generated.resources.enemy_with_card
import caravan.composeapp.generated.resources.let_s_go
import caravan.composeapp.generated.resources.lvl_is_not_enough
import caravan.composeapp.generated.resources.menu_pve
import caravan.composeapp.generated.resources.prize_caps
import caravan.composeapp.generated.resources.prize_chips
import caravan.composeapp.generated.resources.result
import caravan.composeapp.generated.resources.select_enemy
import caravan.composeapp.generated.resources.story_mode
import caravan.composeapp.generated.resources.time_limit_anyone
import caravan.composeapp.generated.resources.tower
import caravan.composeapp.generated.resources.tutorial
import caravan.composeapp.generated.resources.you_have_won
import caravan.composeapp.generated.resources.you_lose
import caravan.composeapp.generated.resources.you_win
import caravan.composeapp.generated.resources.your_caps
import caravan.composeapp.generated.resources.your_chips
import caravan.composeapp.generated.resources.your_expected_reward
import caravan.composeapp.generated.resources.your_expected_reward_chips
import caravan.composeapp.generated.resources.your_reward_reward_caps
import caravan.composeapp.generated.resources.your_reward_reward_chips
import caravan.composeapp.generated.resources.your_reward_reward_xp
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Currency
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
import com.unicorns.invisible.caravan.utils.MenuItemOpenNoScroll
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.VertScrollbar
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.playVatsEnter
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.playWinSoundAlone
import com.unicorns.invisible.caravan.utils.startAmbient
import com.unicorns.invisible.caravan.utils.stopAmbient
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource


@Composable
fun ShowSelectPvE(
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit
) {
    var showStory by rememberScoped { mutableStateOf(false) }
    var showSelectEnemy by rememberScoped { mutableStateOf(false) }
    var showTower by rememberScoped { mutableStateOf(false) }
    var showTutorial by rememberScoped { mutableStateOf(false) }

    when {
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
            showAlertDialog("[CLOSED]", "This content is unavailable.", null)
            showTutorial = false
            //Tutorial { showTutorial = false }
            //return
        }
    }

    MenuItemOpen(stringResource(Res.string.menu_pve), "<-", Alignment.Center, goBack) {
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
            SubMenuItem(stringResource(Res.string.tutorial)) { showTutorial = true }
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
                CollectibleDeck(saveGlobal.selectedDeck)
            else
                saveGlobal.getCurrentDeckCopy(),
            enemy, showAlertDialog, goBack
        )
    }

    if (playAgainstEnemy != -1) {
        val enemyList = saveGlobal.enemiesGroups4.flatten()
        if (playAgainstEnemy !in enemyList.indices) {
            playAgainstEnemy = -1
            return
        }
        StartWithEnemy(enemyList[playAgainstEnemy]) { playAgainstEnemy = -1 }
        return
    }

    MenuItemOpen(stringResource(Res.string.select_enemy), "<-", Alignment.Center, goBack) {
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
                @Composable
                fun LevelTab(lvl: Int) {
                    Tab(selectedTab == lvl - 1,
                        {
                            if (saveGlobal.lvl >= lvl) {
                                playSelectSound(); selectedTab = lvl - 1
                            } else {
                                playCloseSound()
                            }
                        },
                        selectedContentColor = getSelectionColor(),
                        unselectedContentColor = getTextBackgroundColor()
                    ) {
                        TextFallout(
                            "LVL $lvl",
                            getTextColor(),
                            getTextStrokeColor(),
                            16.sp,
                            Modifier.padding(4.dp),
                        )
                    }
                }

                repeat(6) {
                    LevelTab(it + 1)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            @Composable
            fun OpponentItem(enemy: EnemyPve, onClick: () -> Unit) {
                if (!enemy.isAvailable) {
                    TextFallout(
                        "NOT AVAILABLE IN BETA",
                        getTextColor(),
                        getTextStrokeColor(),
                        18.sp,
                        Modifier
                            .background(getTextBackgroundColor())
                            .padding(4.dp),
                    )
                    return
                }
                val line = if (enemy.isEven) {
                    stringResource(Res.string.deck_o_54)
                } else {
                    stringResource(Res.string.custom_deck)
                } + ": " + when (enemy) {
                    is EnemyPvENoBank -> {
                        val back = when (enemy) {
                            is EnemyHanlon -> CardBack.NCR
                            is EnemyVulpes -> CardBack.LEGION
                            is EnemyMadnessCardinal -> CardBack.MADNESS
                            is EnemyViqueen -> CardBack.VIKING
                        }
                        stringResource(Res.string.enemy_with_card,
                            stringResource(enemy.nameId),
                            enemy.curCards,
                            stringResource(back.nameIdWithBackFileName.first)
                        )
                    }
                    is EnemyPvEWithBank -> {
                        stringResource(
                            Res.string.enemy_with_caps,
                            stringResource(enemy.nameId),
                            enemy.curBets,
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
                val enemies = saveGlobal.enemiesGroups4.flatten().filter { it.level == selectedTab + 1 }
                Spacer(modifier = Modifier.height(8.dp))
                val m1 = stringResource(Res.string.closed)
                val m2 = "This content is unavailable."
                val m3 = stringResource(Res.string.lvl_is_not_enough)
                enemies.forEach {
                    OpponentItem(it) {
                        if (it.level != 0 && it.level <= saveGlobal.lvl) {
                            playVatsEnter()
                            playAgainstEnemy = saveGlobal.enemiesGroups4.flatten().indexOf(it)
                        } else {
                            if (it.level == 0) {
                                showAlertDialog(m1, m2, null)
                            } else {
                                showAlertDialog(m1, m3, null)
                            }
                        }
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
    var showBettingScreen by rememberScoped { mutableStateOf(true) }
    var myBet by rememberScoped { mutableIntStateOf(0) }
    var enemyBet: Int by rememberScoped { mutableIntStateOf(0) }
    var isBlitz: Boolean by rememberScoped { mutableStateOf(false) }

    if (showBettingScreen) {
        ShowBettingScreen(
            enemy, { p1, p2 -> showAlertDialog(p1, p2, null) },
            { myBet = it }, { enemyBet = it }, { isBlitz = it }, goBack, {
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
            when (enemy) {
                is EnemyPvEWithBank -> {
                    if (isBlitz) {
                        saveGlobal.sierraMadreChips -= myBet
                    } else {
                        saveGlobal.capsInHand -= myBet
                    }
                    saveGlobal.capsBet += myBet
                    saveGlobal.table += myBet + enemyBet
                    enemy.curBets -= enemyBet / enemy.bet
                }

                is EnemyPvENoBank -> {
                    if (enemy.curCards > 0) {
                        enemy.curCards--
                        enemyBet = 1
                    }
                }
            }

            saveGlobal.gamesStarted++
            saveData()
            it.startGame()
        }
    }

    LaunchedEffect(Unit) { startAmbient() }
    val scope = rememberCoroutineScope()
    val onQuitPressed = { stopAmbient(); goBack() }

    val enemyName = stringResource(enemy.nameId)
    val blitzMult = if (isBlitz) 3.0 / 2.0 else 1.0
    val mult = when (enemy) {
        is EnemyPvENoBank -> 5.0 / 4.0
        is EnemyPvEWithBank -> {
            when (enemyBet / enemy.bet) {
                1 -> 1.0
                2 -> 8.0 / 5.0
                else -> 1.0 / 5.0
            }
        }
    } * blitzMult
    game.also {
        it.onWin = {
            processChallengesGameOver(it)

            saveGlobal.gamesFinished++
            saveGlobal.wins++
            saveGlobal.table -= myBet + enemyBet

            if (myBet == 0) {
                playWinSoundAlone()
            } else {
                playWinSound()
            }

            val xpReward = saveGlobal.increaseXpFromDefeatingEnemy(enemy.level, mult)
            if (enemy is EnemyPvEWithBank) {
                val reward = myBet + enemyBet
                if (isBlitz) {
                    if (myBet > 0) {
                        enemy.winsBlitzBet++
                    } else {
                        enemy.winsBlitzNoBet++
                    }
                    saveGlobal.sierraMadreChips += reward
                } else {
                    if (myBet > 0) {
                        enemy.winsBet++
                    } else {
                        enemy.winsNoBet++
                    }
                    saveGlobal.capsInHand += reward
                }

                saveGlobal.capsWon += reward
                if (reward > 0) {
                    saveGlobal.winsWithBet++
                }

                scope.launch {
                    val rewardLine = if (reward == 0) {
                        ""
                    } else {
                        getString(
                            if (isBlitz) {
                                Res.string.your_reward_reward_chips
                            } else {
                                Res.string.your_reward_reward_caps
                            },
                            reward.toString()
                        )
                    }
                    showAlertDialog(
                        getString(Res.string.result),
                        getString(Res.string.you_win) + rewardLine + getString(Res.string.your_reward_reward_xp, xpReward),
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
                    if (enemyBet > 0) {
                        val rewardCard = winCard(back, isBlitz)
                        showAlertDialog(
                            getString(Res.string.result),
                            getString(Res.string.you_win) + rewardCard + getString(Res.string.your_reward_reward_xp, xpReward),
                            onQuitPressed
                        )
                    } else {
                        val prize = (back.getRarityMult() * 10.0).toInt()
                        val rewardMessage = getString(Res.string.you_have_won, if (isBlitz) {
                            saveGlobal.sierraMadreChips += prize
                            getString(Res.string.prize_chips, prize.toString())
                        } else {
                            saveGlobal.capsInHand += prize
                            getString(Res.string.prize_caps, prize.toString())
                        })
                        showAlertDialog(
                            getString(Res.string.result),
                            getString(Res.string.you_win) + rewardMessage + getString(Res.string.your_reward_reward_xp, xpReward),
                            onQuitPressed
                        )
                    }
                }
            }

            if (enemyName !in saveGlobal.enemiesDefeated) {
                saveGlobal.enemiesDefeated.add(enemyName)
                saveGlobal.currentStrike++
            }
            saveData()
        }
        it.onLose = {
            playLoseSound()
            if (enemy is EnemyPvEWithBank) {
                saveGlobal.table -= myBet + enemyBet
                enemy.curBets += enemyBet / enemy.bet
            }

            saveGlobal.gamesFinished++
            val xpReward = saveGlobal.increaseXpFromLosingToEnemy(enemy.level, mult / 10)
            saveGlobal.currentStrike = 0
            saveGlobal.enemiesDefeated.clear()
            saveData()

            scope.launch {
                showAlertDialog(
                    getString(Res.string.result),
                    getString(Res.string.you_lose) + getString(Res.string.your_reward_reward_xp, xpReward),
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
    enemy: EnemyPve,
    showAlertDialog: (String, String) -> Unit,
    setBet: (Int) -> Unit,
    setEnemyBet: (Int) -> Unit,
    setIsBlitz: (Boolean) -> Unit,
    goBack: () -> Unit,
    goForward: () -> Unit
) {
    val enemyName = stringResource(enemy.nameId)
    var isBlitz: Boolean by rememberScoped { mutableStateOf(false) }
    var selectedOption by rememberScoped { mutableIntStateOf(0) }

    fun getMyBet() = if (enemy !is EnemyPvEWithBank) {
        0
    } else {
        when (selectedOption) {
            1 -> enemy.bet
            2 -> enemy.bet * 2
            else -> 0
        }
    }
    fun getEnemyBet() = if (enemy !is EnemyPvEWithBank) {
        0
    } else {
        when (selectedOption) {
            1 -> if (enemy.curBets == 0) 0 else enemy.bet
            2 -> if (enemy.curBets == 0) 0 else if (enemy.curBets == 1) enemy.bet else enemy.bet * 2
            else -> 0
        }
    }

    MenuItemOpenNoScroll("$$$", "<-", { goBack() }) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(Modifier.fillMaxSize().rotate(180f).getTableBackground()) {}
            BoxWithConstraints(
                Modifier.fillMaxSize().padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                val verticalScrollState = rememberScrollState()
                key(selectedOption) {
                    Column(
                        Modifier
                            .background(getBackgroundColor())
                            .verticalScroll(state = verticalScrollState).padding(horizontal = 4.dp),
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

                        if (enemy is EnemyPvEWithBank) {
                            if (enemy.curBets > 1) {
                                TextFallout(
                                    stringResource(Res.string.enemy_s_bet_can_raise, enemy.bet, enemy.bet * 2),
                                    getTextColor(),
                                    getTextStrokeColor(),
                                    16.sp,
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                )
                            } else {
                                TextFallout(
                                    stringResource(Res.string.enemy_s_bet, if (enemy.curBets == 0) 0 else enemy.bet),
                                    getTextColor(),
                                    getTextStrokeColor(),
                                    16.sp,
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                )
                            }
                            if (!isBlitz) {
                                TextFallout(
                                    stringResource(Res.string.your_caps, saveGlobal.capsInHand),
                                    getTextColor(),
                                    getTextStrokeColor(),
                                    16.sp,
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                )
                            } else {
                                TextFallout(
                                    stringResource(Res.string.your_chips, saveGlobal.sierraMadreChips),
                                    getTextColor(),
                                    getTextStrokeColor(),
                                    16.sp,
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                )
                            }
                            Spacer(Modifier.height(16.dp))

                            TextFallout(
                                stringResource(Res.string.bet_call),
                                getTextColor(),
                                getTextStrokeColor(),
                                24.sp,
                                Modifier
                                    .let {
                                        if (selectedOption == 1)
                                            it.border(width = 3.dp, color = getTextBackgroundColor())
                                        else
                                            it
                                    }
                                    .padding(4.dp)
                                    .clickable {
                                        if (selectedOption == 1) {
                                            selectedOption = 0
                                        } else if (isBlitz) {
                                            if (enemy.bet <= saveGlobal.sierraMadreChips) {
                                                playSelectSound()
                                                selectedOption = 1
                                            } else {
                                                showAlertDialog("CAN'T BET!", "You don't have enough chips!")
                                            }
                                        } else {
                                            if (enemy.bet <= saveGlobal.capsInHand) {
                                                playSelectSound()
                                                selectedOption = 1
                                            } else {
                                                showAlertDialog("CAN'T BET!", "You don't have enough caps!")
                                            }
                                        }
                                    }
                                    .background(getTextBackgroundColor())
                                    .padding(8.dp),
                            )
                            Spacer(Modifier.height(8.dp))

                            TextFallout(
                                stringResource(Res.string.bet_raise2),
                                getTextColor(),
                                getTextStrokeColor(),
                                24.sp,
                                Modifier
                                    .let {
                                        if (selectedOption == 2)
                                            it.border(width = 3.dp, color = getTextBackgroundColor())
                                        else
                                            it
                                    }
                                    .padding(4.dp)
                                    .clickable {
                                        if (selectedOption == 2) {
                                            selectedOption = 0
                                        } else if (isBlitz) {
                                            if (enemy.bet * 2 <= saveGlobal.sierraMadreChips) {
                                                playSelectSound()
                                                selectedOption = 2
                                            } else {
                                                showAlertDialog("CAN'T BET!", "You don't have enough chips!")
                                            }
                                        } else {
                                            if (enemy.bet * 2 <= saveGlobal.capsInHand) {
                                                playSelectSound()
                                                selectedOption = 2
                                            } else {
                                                showAlertDialog("CAN'T BET!", "You don't have enough caps!")
                                            }
                                        }
                                    }
                                    .background(getTextBackgroundColor())
                                    .padding(8.dp),
                            )
                        }

                        Spacer(Modifier.height(16.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextFallout(
                                stringResource(Res.string.time_limit_anyone),
                                getTextColor(),
                                getTextStrokeColor(),
                                18.sp,
                                Modifier.padding(8.dp),
                            )
                            CheckboxCustom({ isBlitz }, { isBlitz = it; selectedOption = 0 }) { true }

                        }

                        if (enemy is EnemyPvEWithBank) {
                            TextFallout(
                                stringResource(
                                    if (isBlitz)
                                        Res.string.your_expected_reward_chips
                                    else
                                        Res.string.your_expected_reward,
                                    getMyBet() + getEnemyBet()
                                ),
                                getTextColor(),
                                getTextStrokeColor(),
                                18.sp,
                                Modifier.fillMaxWidth().padding(8.dp),
                            )
                        }
                        TextFallout(
                            stringResource(Res.string.let_s_go),
                            getTextColor(),
                            getTextStrokeColor(),
                            24.sp,
                            Modifier
                                .clickableOk {
                                    setIsBlitz(isBlitz)
                                    setBet(getMyBet())
                                    setEnemyBet(getEnemyBet())
                                    saveData()
                                    goForward()
                                }
                                .background(getTextBackgroundColor())
                                .padding(8.dp),
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }

                VertScrollbar(verticalScrollState)
            }
        }
    }
}


suspend fun winCard(back: CardBack, isBlitz: Boolean): String {
    fun isCardNew(card: CardWithPrice): Boolean {
        return !saveGlobal.isCardAvailableAlready(card)
    }

    val isNew = if (back == CardBack.STANDARD_RARE) true else ((0..3).random() > 0)
    val deck = CollectibleDeck(back)
    val card = if (isNew) {
        deck.toList().filter(::isCardNew).randomOrNull()
    } else {
        null
    }

    val message = if (card != null) {
        saveGlobal.addCard(card)
        val rankSuit = when (card) {
            is CardFaceSuited -> getString(card.rank.nameId) to getString(card.suit.nameId)
            is CardJoker -> getString(card.rank.nameId) to card.number.n.toString()
            is CardNumber -> getString(card.rank.nameId) to getString(card.suit.nameId)
        }
        val backName = card.getBack().nameIdWithBackFileName.first
        "${rankSuit.first} ${rankSuit.second} (${getString(backName)})"
    } else {
        val prize = (back.getRarityMult() * 10.0).toInt()
        if (back.currency == Currency.SIERRA_MADRE_CHIPS || isBlitz) {
            saveGlobal.sierraMadreChips += prize
            getString(Res.string.prize_chips, prize.toString())
        } else {
            saveGlobal.capsInHand += prize
            getString(Res.string.prize_caps, prize.toString())
        }
    }
    saveData()

    return getString(Res.string.you_have_won, message)
}