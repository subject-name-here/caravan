package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.unicorns.invisible.caravan.model.enemy.EnemyBestest
import com.unicorns.invisible.caravan.model.enemy.EnemyBetter
import com.unicorns.invisible.caravan.model.enemy.EnemyCaesar
import com.unicorns.invisible.caravan.model.enemy.EnemyEasy
import com.unicorns.invisible.caravan.model.enemy.EnemyFinalBoss
import com.unicorns.invisible.caravan.model.enemy.EnemyHard
import com.unicorns.invisible.caravan.model.enemy.EnemyKing
import com.unicorns.invisible.caravan.model.enemy.EnemyMedium
import com.unicorns.invisible.caravan.model.enemy.EnemyNash
import com.unicorns.invisible.caravan.model.enemy.EnemyNoBark
import com.unicorns.invisible.caravan.model.enemy.EnemyOliver
import com.unicorns.invisible.caravan.model.enemy.EnemyRingo
import com.unicorns.invisible.caravan.model.enemy.EnemySecuritron38
import com.unicorns.invisible.caravan.model.enemy.EnemySix
import com.unicorns.invisible.caravan.model.enemy.EnemySwank
import com.unicorns.invisible.caravan.model.enemy.EnemyYesMan
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playClickSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.playJokerSounds
import com.unicorns.invisible.caravan.utils.playLoseSound
import com.unicorns.invisible.caravan.utils.playNukeBlownSound
import com.unicorns.invisible.caravan.utils.playVatsEnter
import com.unicorns.invisible.caravan.utils.playWWSound
import com.unicorns.invisible.caravan.utils.playWinSound
import com.unicorns.invisible.caravan.utils.scrollbar
import com.unicorns.invisible.caravan.utils.startFinalBossTheme
import com.unicorns.invisible.caravan.utils.stopAmbient
import java.util.Locale


@Composable
fun ShowPvE(
    activity: MainActivity,
    selectedDeck: () -> Pair<CardBack, Boolean>,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit
) {
    var showGameEasy by rememberSaveable { mutableStateOf(false) }
    var showGameMedium by rememberSaveable { mutableStateOf(false) }
    var showGameHard by rememberSaveable { mutableStateOf(false) }
    var showGameBetter by rememberSaveable { mutableStateOf(false) }
    var showGameUlysses by rememberSaveable { mutableStateOf(false) }

    var showGame38 by rememberSaveable { mutableStateOf(false) }
    var showGameCheater by rememberSaveable { mutableStateOf(false) }
    var showGameQueen by rememberSaveable { mutableStateOf(false) }
    var showGameNash by rememberSaveable { mutableStateOf(false) }
    var showGameNoBark by rememberSaveable { mutableStateOf(false) }

    var showGameRingo by rememberSaveable { mutableStateOf(false) }
    var showGameYesMan by rememberSaveable { mutableStateOf(false) }
    var showGameKing by rememberSaveable { mutableStateOf(false) }
    var showGameOliver by rememberSaveable { mutableStateOf(false) }
    var showGameCaesar by rememberSaveable { mutableStateOf(false) }

    var checkedCustomDeck by rememberSaveable {
        mutableStateOf(
            activity.save?.useCustomDeck ?: false
        )
    }

    fun getPlayerDeck(): CResources {
        return if (checkedCustomDeck)
            CResources(activity.save?.getCustomDeckCopy() ?: CustomDeck(CardBack.STANDARD, false))
        else
            CResources(selectedDeck().first, selectedDeck().second)
    }

    if (showGameEasy) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyEasy,
            showAlertDialog = showAlertDialog
        ) {
            showGameEasy = false
        }
        return
    } else if (showGameMedium) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyMedium,
            showAlertDialog = showAlertDialog
        ) {
            showGameMedium = false
        }
        return
    } else if (showGame38) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemySecuritron38,
            showAlertDialog = showAlertDialog
        ) {
            showGame38 = false
        }
        return
    } else if (showGameCheater) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemySix,
            showAlertDialog = showAlertDialog
        ) {
            showGameCheater = false
        }
        return
    } else if (showGameQueen) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemySwank,
            showAlertDialog = showAlertDialog
        ) {
            showGameQueen = false
        }
        return
    } else if (showGameNash) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyNash,
            showAlertDialog = showAlertDialog
        ) {
            showGameNash = false
        }
        return
    } else if (showGameNoBark) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyNoBark,
            showAlertDialog = showAlertDialog
        ) {
            showGameNoBark = false
        }
        return
    } else if (showGameHard) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyHard,
            showAlertDialog = showAlertDialog
        ) {
            showGameHard = false
        }
        return
    } else if (showGameBetter) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyBetter,
            showAlertDialog = showAlertDialog
        ) {
            showGameBetter = false
        }
        return
    } else if (showGameUlysses) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyBestest,
            showAlertDialog = showAlertDialog
        ) {
            showGameUlysses = false
        }
        return
    } else if (showGameRingo) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyRingo,
            showAlertDialog = showAlertDialog
        ) {
            showGameRingo = false
        }
        return
    } else if (showGameYesMan) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyYesMan,
            showAlertDialog = showAlertDialog
        ) {
            showGameYesMan = false
        }
        return
    } else if (showGameKing) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyKing,
            showAlertDialog = showAlertDialog
        ) {
            showGameKing = false
        }
        return
    } else if (showGameOliver) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyOliver,
            showAlertDialog = showAlertDialog
        ) {
            showGameOliver = false
        }
        return
    } else if (showGameCaesar) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyCaesar,
            showAlertDialog = showAlertDialog
        ) {
            showGameCaesar = false
        }
        return
    }

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
                .fillMaxHeight(0.4f)
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
                TextFallout(
                    stringResource(R.string.pve_select_enemy),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    22.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
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

                OpponentItem(stringResource(R.string.pve_enemy_easy)) { playVatsEnter(activity); showGameEasy = true }
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.pve_enemy_medium)) { playVatsEnter(activity); showGameMedium = true }
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.pve_enemy_hard)) { playVatsEnter(activity); showGameHard = true }
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.pve_enemy_better)) { playVatsEnter(activity); showGameBetter = true }
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.pve_enemy_best)) {
                    if (checkedCustomDeck) {
                        showAlertDialog(
                            activity.getString(R.string.ulysses_fair_fight_header),
                            activity.getString(R.string.ulysses_fair_fight_body)
                        )
                    } else {
                        playVatsEnter(activity)
                        showGameUlysses = true
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextFallout(
                    stringResource(R.string.pve_select_enemy_2),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    22.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                OpponentItem(stringResource(R.string.pve_enemy_queen)) { playVatsEnter(activity); showGameQueen = true }
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.no_bark)) { playVatsEnter(activity); showGameNoBark = true }
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.johnson_nash)) { playVatsEnter(activity); showGameNash = true }
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.pve_enemy_38)) { playVatsEnter(activity); showGame38 = true }
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.pve_enemy_cheater)) { playVatsEnter(activity); showGameCheater = true }
                Spacer(modifier = Modifier.height(16.dp))
                TextFallout(
                    stringResource(R.string.pve_select_enemy_3),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    22.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                OpponentItem(stringResource(R.string.ringo)) { playVatsEnter(activity); showGameRingo = true }
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.yes_man)) { playVatsEnter(activity); showGameYesMan = true }
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.the_king)) { playVatsEnter(activity); showGameKing = true }
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.general_lee_oliver)) { playVatsEnter(activity); showGameOliver = true }
                Spacer(modifier = Modifier.height(10.dp))
                OpponentItem(stringResource(R.string.caesar)) { playVatsEnter(activity); showGameCaesar = true }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        HorizontalDivider(color = getDividerColor(activity))

        Row(
            modifier = Modifier
                .height(56.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextFallout(
                stringResource(R.string.pve_use_custom_deck),
                getTextColor(activity),
                getTextStrokeColor(activity),
                14.sp,
                Alignment.CenterStart,
                Modifier.fillMaxWidth(0.7f),
                TextAlign.Start
            )
            CheckboxCustom(
                activity,
                { checkedCustomDeck },
                {
                    checkedCustomDeck = !checkedCustomDeck
                    if (checkedCustomDeck) {
                        playClickSound(activity)
                    } else {
                        playCloseSound(activity)
                    }
                    activity.save?.let {
                        it.useCustomDeck = checkedCustomDeck
                        saveOnGD(activity)
                    }
                },
                { true }
            )
        }

        HorizontalDivider(color = getDividerColor(activity))
        val started = activity.save?.gamesStarted ?: 0
        val finished = activity.save?.gamesFinished ?: 0
        val won = activity.save?.wins ?: 0
        val loss = finished - won
        val state2 = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxHeight(0.75f)
                .fillMaxWidth()
                .padding(16.dp)
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
                Spacer(modifier = Modifier.height(16.dp))
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
                Spacer(modifier = Modifier.height(16.dp))
                TextFallout(
                    stringResource(R.string.pve_percentiles),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Alignment.Center,
                    Modifier,
                    TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
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
        TextFallout(
            stringResource(R.string.menu_back),
            getTextColor(activity),
            getTextStrokeColor(activity),
            24.sp,
            Alignment.Center,
            modifier = Modifier
                .clickableCancel(activity) { goBack() }
                .background(getTextBackgroundColor(activity))
                .padding(8.dp),
            TextAlign.Center
        )
    }
}

@Composable
fun StartGame(
    activity: MainActivity,
    playerCResources: CResources,
    isCustom: Boolean,
    enemy: Enemy,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit,
) {
    if (!activity.checkIfCustomDeckCanBeUsedInGame(playerCResources)) {
        showAlertDialog(
            stringResource(R.string.custom_deck_is_too_small),
            stringResource(R.string.custom_deck_is_too_small_message)
        )
        goBack()
        return
    }

    val game: Game = rememberScoped {
        if (enemy is EnemyFinalBoss) {
            isFinalBossSequence = true
            startFinalBossTheme(activity)
        }
        Game(
            playerCResources,
            enemy
        ).also {
            activity.save?.let { save ->
                save.gamesStarted++
                saveOnGD(activity)
            }
            it.startGame()
        }
    }

    game.also {
        it.onWin = {
            activity.processChallengesGameOver(it)
            playWinSound(activity)
            var message = activity.getString(R.string.you_win)
            activity.save?.let { save ->
                val rewardBack = enemy.getRewardBack()
                if (rewardBack == null || rewardBack == CardBack.WILD_WASTELAND || rewardBack == CardBack.UNPLAYABLE) {
                    save.caps += 30
                    message += activity.getString(R.string.you_have_earned_caps, 30.toString())
                } else if (rewardBack == CardBack.DECK_13 && enemy.isAlt()) {
                    if (save.availableDecksAlt[rewardBack] != true) {
                        save.availableDecksAlt[rewardBack] = true
                        message += activity.getString(
                            R.string.you_have_unlocked_deck,
                            activity.getString(rewardBack.getMadnessDeckName())
                        )
                    }
                    message += winCard(activity, save, rewardBack, 3, isAlt = true, isCustom)
                } else {
                    if (!enemy.isAlt()) {
                        if (save.availableDecks[rewardBack] != true) {
                            save.availableDecks[rewardBack] = true
                            message += activity.getString(
                                R.string.you_have_unlocked_deck,
                                activity.getString(rewardBack.getDeckName())
                            )
                        }
                        message += winCard(activity, save, rewardBack, 3, isAlt = false, isCustom)
                    } else {
                        if (save.availableDecksAlt[rewardBack] != true) {
                            save.availableDecksAlt[rewardBack] = true
                            message += activity.getString(
                                R.string.you_have_unlocked_deck_alt,
                                activity.getString(rewardBack.getDeckName())
                            )
                        }
                        message += winCard(activity, save, rewardBack, 1, isAlt = true, isCustom)
                    }
                }
                save.gamesFinished++
                save.wins++
                saveOnGD(activity)
            }
            if (game.enemy is EnemyBestest) {
                showAlertDialog(activity.getString(R.string.result_ulysses), message)
            } else {
                showAlertDialog(activity.getString(R.string.result), message)
            }
        }
        it.onLose = {
            playLoseSound(activity)
            activity.save?.let { save ->
                save.gamesFinished++
                saveOnGD(activity)
            }
            if (game.enemy is EnemyBestest) {
                showAlertDialog(
                    activity.getString(R.string.ulysses_victory),
                    activity.getString(R.string.you_lose)
                )
            } else {
                showAlertDialog(
                    activity.getString(R.string.result),
                    activity.getString(R.string.you_lose)
                )
            }
        }
        it.jokerPlayedSound = { playJokerSounds(activity) }
        it.nukeBlownSound = { playNukeBlownSound(activity) }
        it.wildWastelandSound = { playWWSound(activity) }
    }
    activity.goBack = { stopAmbient(); goBack(); activity.goBack = null }
    ShowGame(activity, game) {
        if (game.isOver()) {
            activity.goBack?.invoke()
            return@ShowGame
        }

        if (game.enemy is EnemyBestest) {
            showAlertDialog(activity.getString(R.string.check_back_to_menu_ulysses), "")
        } else {
            showAlertDialog(activity.getString(R.string.check_back_to_menu), "")
        }
    }
}

fun winCard(
    activity: MainActivity,
    save: Save,
    back: CardBack,
    numberOfCards: Int,
    isAlt: Boolean,
    isCustom: Boolean
): String {
    fun checkCard(card: Card): Boolean {
        return save.availableCards.none { aCard -> aCard.rank == card.rank && aCard.suit == card.suit && aCard.back == card.back && aCard.isAlt == card.isAlt }
    }

    val deck = CustomDeck(back, isAlt)
    val deckList = deck.takeRandom(deck.size)
    val deckOld = deckList.filter { !checkCard(it) }
    val deckNew = deckList - deckOld.toSet()
    val prob = when {
        isCustom -> if (numberOfCards == 1) 45 else 35
        else -> if (numberOfCards == 1) 60 else 50
    }
    val reward = run {
        val probs = (0 until numberOfCards).map {
            (0..99).random() < prob
        }
        val newCards = probs.count { it }.coerceAtMost(deckNew.size)
        val oldCards = (numberOfCards - newCards).coerceAtMost(deckOld.size)
        deckNew.take(newCards) + deckOld.take(oldCards)
    }
    var result = activity.getString(R.string.your_prize_cards_from)
    var capsEarned = 0
    reward.forEach { card ->
        val deckName = if (card.back == CardBack.STANDARD && isAlt) {
            activity.getString(card.back.getSierraMadreDeckName())
        } else if (card.back == CardBack.DECK_13 && isAlt) {
            activity.getString(card.back.getMadnessDeckName())
        } else {
            activity.getString(card.back.getDeckName())
        }
        val cardName = if (card.rank == Rank.JOKER) {
            "${activity.getString(card.rank.nameId)} ${card.suit.ordinal + 1}, $deckName"
        } else {
            "${activity.getString(card.rank.nameId)} ${activity.getString(card.suit.nameId)}, $deckName"
        }
        result += if (checkCard(card)) {
            save.availableCards.add(card)
            if (isAlt && !(card.back == CardBack.STANDARD || card.back == CardBack.DECK_13)) {
                activity.getString(R.string.new_card_alt, cardName)
            } else {
                activity.getString(R.string.new_card, cardName)
            }
        } else {
            capsEarned += activity.save?.getCardPrice(card) ?: 0
            activity.save?.let {
                it.soldCards[card.back to isAlt] = (it.soldCards[card.back to isAlt] ?: 0) + 1
            }
            if (isAlt && !(card.back == CardBack.STANDARD || card.back == CardBack.DECK_13)) {
                activity.getString(R.string.old_card_alt, cardName)
            } else {
                activity.getString(R.string.old_card, cardName)
            }
        }
    }
    if (capsEarned > 0) {
        activity.save!!.caps += capsEarned
        result += activity.getString(R.string.caps_earned, capsEarned.toString())
    }

    return result
}