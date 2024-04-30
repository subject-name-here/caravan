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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
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
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.save
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
                    text = "Select the Enemy",
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ultra-Luxe visitor",
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                    modifier = Modifier.clickable {
                        showGameEasy = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Thug from Gomorrah",
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                    modifier = Modifier.clickable {
                        showGameMedium = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Securitron P373",
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                    modifier = Modifier.clickable {
                        showGame38 = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "The Courier Six",
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
                Text(modifier = Modifier.fillMaxWidth(0.7f), text = "Use Custom Deck")

                // TODO: checkbox color!
                Checkbox(checked = checkedCustomDeck, onCheckedChange = {
                    checkedCustomDeck = !checkedCustomDeck
                    activity.save?.let {
                        it.useCustomDeck = checkedCustomDeck
                        save(activity, it)
                    }
                }, colors = CheckboxColors(
                    checkedCheckmarkColor = Color(activity.getColor(R.color.colorPrimaryDark)),
                    uncheckedCheckmarkColor = Color.Transparent,
                    checkedBoxColor = Color(activity.getColor(R.color.colorPrimary)),
                    uncheckedBoxColor = Color.Transparent,
                    disabledCheckedBoxColor = Color.Red,
                    disabledUncheckedBoxColor = Color.Red,
                    disabledIndeterminateBoxColor = Color.Red,
                    checkedBorderColor = Color(activity.getColor(R.color.colorPrimaryDark)),
                    uncheckedBorderColor = Color(activity.getColor(R.color.colorPrimaryDark)),
                    disabledBorderColor = Color.Red,
                    disabledUncheckedBorderColor = Color.Red,
                    disabledIndeterminateBorderColor = Color.Red,
                ))
            }

            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Column(Modifier.fillMaxSize(0.9f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Stats",
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Games started: ${activity.save?.gamesStarted ?: 0}",
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Games finished: ${activity.save?.gamesFinished ?: 0}",
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Games won: ${activity.save?.wins ?: 0}",
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 20.sp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Back to Menu",
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
        showAlertDialog("Custom deck is bad!", "Deck has less than 30 cards or less than 15 numbered cards!")
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
            var message = "You win!"
            activity.save?.let { save ->
                if (!isCustom) {
                    enemy.getRewardDeck()?.let { cardBack ->
                        if (save.availableDecks[cardBack] != true) {
                            save.availableDecks[cardBack] = true
                            message += "\nYou have unlocked ${cardBack.getDeckName()}!"
                        }
                        message += winCard(save, cardBack, 5)
                    }
                } else {
                    enemy.getRewardDeck()?.let { cardBack ->
                        message += if (save.availableDecks[cardBack] == true) {
                            winCard(save, cardBack, (1..2).random())
                        } else {
                            "\nTo have chance of winning card from ${cardBack.name} deck, you must defeat the enemy w/o custom deck!"
                        }
                    }
                }
                save.gamesFinished++
                save.wins++
                save(activity, save)
            }
            showAlertDialog("Result", message)
        }
        it.onLose = {
            activity.save?.let { save ->
                save.gamesFinished++
                save(activity, save)
            }
            showAlertDialog("Result", "You lose!")
        }
    }
    ShowGame(activity, game) { goBack() }
}

fun winCard(save: Save, cardBack: CardBack, numberOfCards: Int): String {
    fun checkCard(card: Card) = save.availableCards.none { aCard -> aCard.rank == card.rank && aCard.suit == card.suit && aCard.back == card.back }
    fun getCardName(card: Card) = "${card.rank} of ${if (card.rank != Rank.JOKER) card.suit else (card.suit.ordinal + 1)}"
    val deck = CustomDeck(cardBack)
    val reward = deck.takeRandom(numberOfCards)
    var result = "\nYour prize - cards from ${cardBack.getDeckName()}:\n"
    reward.forEach { card ->
        result += if (checkCard(card)) {
            save.availableCards.add(card)
            "New: ${getCardName(card)}.\n"
        } else {
            "Old: ${getCardName(card)}.\n"
        }
    }
    return result
}