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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.GameSaver
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.EnemyBestest
import com.unicorns.invisible.caravan.model.enemy.EnemyEasy
import com.unicorns.invisible.caravan.model.enemy.EnemyHard
import com.unicorns.invisible.caravan.model.enemy.EnemyMedium
import com.unicorns.invisible.caravan.model.enemy.EnemyNash
import com.unicorns.invisible.caravan.model.enemy.EnemyNoBark
import com.unicorns.invisible.caravan.model.enemy.EnemySecuritron38
import com.unicorns.invisible.caravan.model.enemy.EnemySix
import com.unicorns.invisible.caravan.model.enemy.EnemySwank
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.save
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar
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

    var showGame38 by rememberSaveable { mutableStateOf(false) }
    var showGameCheater by rememberSaveable { mutableStateOf(false) }
    var showGameQueen by rememberSaveable { mutableStateOf(false) }
    var showGameNash by rememberSaveable { mutableStateOf(false) }
    var showGameBest by rememberSaveable { mutableStateOf(false) }

    var checkedCustomDeck by rememberSaveable { mutableStateOf(activity.save?.useCustomDeck ?: false) }
    fun getPlayerDeck(): CResources {
        return if (checkedCustomDeck) CResources(activity.save?.getCustomDeckCopy()!!) else CResources(selectedDeck().first, selectedDeck().second)
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
    } else if (showGameBest) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyNoBark,
            showAlertDialog = showAlertDialog
        ) {
            showGameBest = false
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
    }

    Column(
        Modifier.fillMaxSize().background(getBackgroundColor(activity)),
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
        ) { item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.pve_select_enemy),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(color = getTextColor(activity), fontSize = 22.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.pve_enemy_easy),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(color = getTextColor(activity), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    showGameEasy = true
                }.background(getTextBackgroundColor(activity)).padding(4.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.pve_enemy_medium),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(color = getTextColor(activity), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    showGameMedium = true
                }.background(getTextBackgroundColor(activity)).padding(4.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.pve_enemy_hard),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(color = getTextColor(activity), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    showGameHard = true
                }.background(getTextBackgroundColor(activity)).padding(4.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.pve_enemy_better),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(color = getTextColor(activity), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    showAlertDialog("New enemy", "[CLOSED]")
                }.background(getTextBackgroundColor(activity)).padding(4.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.pve_enemy_best),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(color = getTextColor(activity), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    if (checkedCustomDeck) {
                        showAlertDialog("It's an honest fight.", "You cannot use custom deck against this enemy.")
                    } else {
                        showAlertDialog("New enemy", "[CLOSED]")
                    }
                }.background(getTextBackgroundColor(activity)).padding(4.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.pve_select_enemy_2),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(color = getTextColor(activity), fontSize = 22.sp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.pve_enemy_queen),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(color = getTextColor(activity), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    showGameQueen = true
                }.background(getTextBackgroundColor(activity)).padding(4.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.pve_enemy_38),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(color = getTextColor(activity), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    showGame38 = true
                }.background(getTextBackgroundColor(activity)).padding(4.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.johnson_nash),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(color = getTextColor(activity), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    showGameNash = true
                }.background(getTextBackgroundColor(activity)).padding(4.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.no_bark),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(color = getTextColor(activity), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    showGameBest = true
                }.background(getTextBackgroundColor(activity)).padding(4.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.pve_enemy_cheater),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(color = getTextColor(activity), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    showGameCheater = true
                }.background(getTextBackgroundColor(activity)).padding(4.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        } }
        HorizontalDivider(color = getDividerColor(activity))

        Row(modifier = Modifier
            .height(56.dp)
            .padding(8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(0.7f),
                text = stringResource(R.string.pve_use_custom_deck),
                fontFamily = FontFamily(Font(R.font.monofont)),
                style = TextStyle(
                    color = getTextColor(activity), fontSize = 14.sp
                )
            )
            CheckboxCustom(
                activity,
                { checkedCustomDeck },
                {
                    checkedCustomDeck = !checkedCustomDeck
                    activity.save?.let {
                        it.useCustomDeck = checkedCustomDeck
                        save(activity, it)
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
                Text(
                    text = stringResource(R.string.pve_stats),
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    style = TextStyle(color = getTextColor(activity), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(
                        R.string.pve_games_started,
                        started
                    ),
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = getTextColor(activity), fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.pve_games_finished,
                        finished
                    ),
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = getTextColor(activity), fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.pve_games_won, won),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    style = TextStyle(color = getTextColor(activity), fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.pve_percentiles),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    style = TextStyle(color = getTextColor(activity), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(
                        R.string.pve_w_to_l,
                        if (loss == 0) "-" else String.format(Locale.UK, "%.3f", won.toDouble() / loss)
                    ),
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = getTextColor(activity), fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.pve_w_to_finished,
                        if (finished == 0) "-" else String.format(Locale.UK, "%.2f", (won.toDouble() / finished) * 100)
                    ),
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = getTextColor(activity), fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.pve_w_to_started,
                        if (started == 0) "-" else String.format(Locale.UK, "%.2f", won.toDouble() / started * 100.0)
                    ),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    style = TextStyle(color = getTextColor(activity), fontSize = 14.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.pve_finished_to_started,
                        if (started == 0) "-" else String.format(Locale.UK, "%.1f", finished.toDouble() / started * 100.0)
                    ),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    style = TextStyle(color = getTextColor(activity), fontSize = 14.sp)
                )
            }
        }
        Text(
            text = stringResource(R.string.menu_back),
            fontFamily = FontFamily(Font(R.font.monofont)),
            style = TextStyle(color = getTextColor(activity), fontSize = 24.sp),
            modifier = Modifier.clickable { goBack() }.background(getTextBackgroundColor(activity)).padding(8.dp)
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

    val game by rememberSaveable(stateSaver = GameSaver) {
        mutableStateOf(
            Game(
                playerCResources,
                enemy
            ).also {
                activity.save?.let { save ->
                    save.gamesStarted++
                    save(activity, save)
                }
                it.startGame()
            }
        )
    }
    game.also {
        it.onWin = {
            var message = activity.getString(R.string.you_win)
            activity.save?.let { save ->
                enemy.getRewardBack()?.let { back ->
                    if (!enemy.isAlt()) {
                        if (save.availableDecks[back] != true) {
                            save.availableDecks[back] = true
                            message += activity.getString(
                                R.string.you_have_unlocked_deck,
                                activity.getString(back.getDeckName())
                            )
                        }
                        message += winCard(activity, save, back, 3, isAlt = false, isCustom)
                    } else {
                        if (save.availableDecksAlt[back] != true) {
                            save.availableDecksAlt[back] = true
                            message += activity.getString(
                                R.string.you_have_unlocked_deck_alt,
                                activity.getString(back.getDeckName())
                            )
                        }
                        message += winCard(activity, save, back, 1, isAlt = true, isCustom)
                    }
                }
                save.gamesFinished++
                save.wins++
                save(activity, save)
            }
            if (game.enemy is EnemyBestest) {
                showAlertDialog(activity.getString(R.string.result_ulysses), message)
            } else {
                showAlertDialog(activity.getString(R.string.result), message)
            }
        }
        it.onLose = {
            activity.save?.let { save ->
                save.gamesFinished++
                save(activity, save)
            }
            showAlertDialog(activity.getString(R.string.result), activity.getString(R.string.you_lose))
        }
    }
    activity.goBack = goBack
    ShowGame(activity, game) {
        if (game.isOver()) {
            goBack()
            activity.goBack = null
            return@ShowGame
        }
        showAlertDialog("Are you sure you want to quit?", "")
    }
}

fun winCard(activity: MainActivity, save: Save, back: CardBack, numberOfCards: Int, isAlt: Boolean, isCustom: Boolean): String {
    fun checkCard(card: Card): Boolean {
        return save.availableCards.none { aCard -> aCard.rank == card.rank && aCard.suit == card.suit && aCard.back == card.back && aCard.isAlt == card.isAlt }
    }
    val deck = CustomDeck(back, isAlt)
    val reward = deck.takeRandom(if (isCustom) numberOfCards else 7).sortedByDescending { if (checkCard(it)) 1 else 0 }.take(numberOfCards)
    var result = activity.getString(R.string.your_prize_cards_from)
    reward.forEach { card ->
        val deckName = if (card.back == CardBack.STANDARD && isAlt) {
            activity.getString(card.back.getSierraMadreDeckName())
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
            if (isAlt && card.back != CardBack.STANDARD) {
                activity.getString(R.string.new_card_alt, cardName)
            } else {
                activity.getString(R.string.new_card, cardName)
            }
        } else {
            if (isAlt && card.back != CardBack.STANDARD) {
                activity.getString(R.string.old_card_alt, cardName)
            } else {
                activity.getString(R.string.old_card, cardName)
            }
        }
    }
    return result
}