package com.unicorns.invisible.caravan

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.GameSaver
import com.unicorns.invisible.caravan.model.enemy.Enemy
import com.unicorns.invisible.caravan.model.enemy.Enemy38
import com.unicorns.invisible.caravan.model.enemy.EnemyCheater
import com.unicorns.invisible.caravan.model.enemy.EnemyEasy
import com.unicorns.invisible.caravan.model.enemy.EnemyMedium
import com.unicorns.invisible.caravan.model.enemy.EnemyQueen
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.save
import com.unicorns.invisible.caravan.utils.CheckboxCustom
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowPvE(
    activity: MainActivity,
    selectedDeck: () -> CardBack,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit
) {
    var showGameEasy by rememberSaveable { mutableStateOf(false) }
    var showGameMedium by rememberSaveable { mutableStateOf(false) }
    var showGame38 by rememberSaveable { mutableStateOf(false) }
    var showGameCheater by rememberSaveable { mutableStateOf(false) }
    var showGameQueen by rememberSaveable { mutableStateOf(false) }

    var checkedCustomDeck by rememberSaveable { mutableStateOf(activity.save?.useCustomDeck ?: false) }
    fun getPlayerDeck(): CResources {
        return if (checkedCustomDeck) CResources(activity.save?.getCustomDeckCopy()!!) else CResources(selectedDeck())
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
            enemy = Enemy38,
            showAlertDialog = showAlertDialog
        ) {
            showGame38 = false
        }
        return
    }
    else if (showGameCheater) {
        StartGame(
            activity = activity,
            playerCResources = getPlayerDeck(),
            isCustom = checkedCustomDeck,
            enemy = EnemyCheater,
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
            enemy = EnemyQueen,
            showAlertDialog = showAlertDialog
        ) {
            showGameQueen = false
        }
        return
    }

    val state = rememberLazyListState()
    LazyColumn(
        Modifier
            .fillMaxSize()
            .scrollbar(state, horizontal = false),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                Modifier
                    .fillMaxHeight(0.5f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.pve_select_enemy),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 22.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.pve_enemy_easy),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                    modifier = Modifier.clickable {
                        showGameEasy = true
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.pve_enemy_medium),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                    modifier = Modifier.clickable {
                        showGameMedium = true
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.pve_enemy_queen),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                    modifier = Modifier.clickable {
                        showGameQueen = true
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.pve_enemy_38),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                    modifier = Modifier.clickable {
                        showGame38 = true
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.pve_enemy_cheater),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                    modifier = Modifier.clickable {
                        showGameCheater = true
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()

            Row(modifier = Modifier
                .fillMaxHeight(0.1f)
                .padding(8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.fillMaxWidth(0.7f), text = stringResource(R.string.pve_use_custom_deck))
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

            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Column(Modifier.fillMaxSize(0.9f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.pve_stats),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(
                        R.string.pve_games_started,
                        activity.save?.gamesStarted ?: 0
                    ),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(
                        R.string.pve_games_finished,
                        activity.save?.gamesFinished ?: 0
                    ),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.pve_games_won, activity.save?.wins ?: 0),
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 20.sp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.menu_back),
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp),
                modifier = Modifier.clickable { goBack() }
            )
        }
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
                if (!isCustom) {
                    enemy.getRewardDeck()?.let { cardBack ->
                        if (save.availableDecks[cardBack] != true) {
                            save.availableDecks[cardBack] = true
                            message += activity.getString(
                                R.string.you_have_unlocked_deck,
                                activity.getString(cardBack.getDeckName())
                            )
                        }
                        message += winCard(activity, save, cardBack, 4)
                    }
                } else {
                    enemy.getRewardDeck()?.let { cardBack ->
                        message += if (save.availableDecks[cardBack] == true) {
                            winCard(activity, save, cardBack, (1..2).random())
                        } else {
                            activity.getString(R.string.you_must_unlock_deck, cardBack.name)
                        }
                    }
                }
                save.gamesFinished++
                save.wins++
                save(activity, save)
            }
            showAlertDialog(activity.getString(R.string.result), message)
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
    ShowGame(activity, game) { goBack() }
}

fun winCard(activity: MainActivity, save: Save, cardBack: CardBack, numberOfCards: Int): String {
    fun checkCard(card: Card) = save.availableCards.none { aCard -> aCard.rank == card.rank && aCard.suit == card.suit && aCard.back == card.back }
    val deck = CustomDeck(cardBack)
    val reward = deck.takeRandom(numberOfCards)
    var result = activity.getString(R.string.your_prize_cards_from, activity.getString(cardBack.getDeckName()))
    reward.forEach { card ->
        val cardName = "${activity.getString(card.rank.nameId)} ${activity.getString(card.suit.nameId)}"
        result += if (checkCard(card)) {
            save.availableCards.add(card)
            activity.getString(R.string.new_card, cardName)
        } else {
            activity.getString(R.string.old_card, cardName)
        }
    }
    return result
}