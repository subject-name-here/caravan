package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyCliff
import com.unicorns.invisible.caravan.model.enemy.EnemyEasy
import com.unicorns.invisible.caravan.model.enemy.EnemyMedium
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
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
import com.unicorns.invisible.caravan.utils.playVatsEnter
import com.unicorns.invisible.caravan.utils.playWWSound
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.startAmbient
import com.unicorns.invisible.caravan.utils.stopAmbient
import java.util.Locale


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

    if (showGameNash) {
        StartGame(
            activity = activity,
            playerCResources = CResources(save.getCustomDeckCopy()),
            enemy = EnemyEasy,
            showAlertDialog = showAlertDialog
        ) {
            showGameNash = false
        }
        return
    } else if (showGameNoBark) {
        StartGame(
            activity = activity,
            playerCResources = CResources(save.getCustomDeckCopy()),
            enemy = EnemyMedium,
            showAlertDialog = showAlertDialog
        ) {
            showGameNoBark = false
        }
        return
    } else if (showGameUlysses) {
        StartGame(
            activity = activity,
            playerCResources = CResources(save.selectedDeck.first, save.selectedDeck.second),
            enemy = EnemyCliff,
            showAlertDialog = showAlertDialog
        ) {
            showGameUlysses = false
        }
        return
    }

    MenuItemOpen(activity, stringResource(R.string.menu_pve), "<-", goBack) {
        Column(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity)),
            verticalArrangement = Arrangement.Center,
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
                                OpponentItem(stringResource(R.string.pve_enemy_ulysses)) { playVatsEnter(activity); showGameUlysses = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.no_bark)) { playVatsEnter(activity); showGameNoBark = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.johnson_nash)) { playVatsEnter(activity); showGameNash = true }
                            }
                            1 -> {
                                OpponentItem(stringResource(R.string.no_bark)) { playVatsEnter(activity); showGameNoBark = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.no_bark)) { playVatsEnter(activity); showGameNoBark = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.johnson_nash)) { playVatsEnter(activity); showGameNash = true }
                            }
                            2 -> {
                                OpponentItem(stringResource(R.string.no_bark)) { playVatsEnter(activity); showGameNoBark = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.no_bark)) { playVatsEnter(activity); showGameNoBark = true }
                                Spacer(modifier = Modifier.height(10.dp))
                                OpponentItem(stringResource(R.string.no_bark)) { playVatsEnter(activity); showGameNoBark = true }
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

            HorizontalDivider(color = getDividerColor(activity))
            val started = save.gamesStarted
            val finished = save.gamesFinished
            val won = save.wins
            val loss = finished - won
            val state2 = rememberLazyListState()
            LazyColumn(
                Modifier
                    .fillMaxSize()
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
}

@Composable
fun StartGame(
    activity: MainActivity,
    playerCResources: CResources,
    enemy: Enemy,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit,
) {
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
    val onQuitPressed = { stopAmbient(); goBack() }

    game.also {
        it.onWin = {
            // TODO: achievementys!!!

            activity.processChallengesGameOver(it)

            playWinSound(activity)
            save.gamesFinished++
            save.wins++
            saveData(activity)

            showAlertDialog(
                activity.getString(R.string.result),
                activity.getString(R.string.you_win),
                onQuitPressed
            )
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


fun winCard(
    activity: MainActivity,
    save: Save,
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