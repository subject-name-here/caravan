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
import androidx.compose.material3.Checkbox
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
import com.unicorns.invisible.caravan.model.primitives.Deck
import com.unicorns.invisible.caravan.save.save


@Composable
fun ShowPvE(
    activity: MainActivity,
    selectedDeck: () -> CardBack,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit
) {
    // TODO: horizontal orientation is shit.

    var showGameEasy by rememberSaveable { mutableStateOf(false) }
    var showGameMedium by rememberSaveable { mutableStateOf(false) }
    var showGame38 by rememberSaveable { mutableStateOf(false) }
    var showGameCheater by rememberSaveable { mutableStateOf(false) }

    var checkedCustomDeck by rememberSaveable { mutableStateOf(false) }
    fun getPlayerDeck(): Deck {
        return if (checkedCustomDeck) Deck(activity.save?.customDeck!!) else Deck(selectedDeck())
    }

    if (showGameEasy) {
        StartGame(
            activity = activity,
            playerDeck = getPlayerDeck(),
            enemy = EnemyEasy,
            showAlertDialog = showAlertDialog
        ) {
            showGameEasy = false
        }
        return
    } else if (showGameMedium) {
        StartGame(
            activity = activity,
            playerDeck = getPlayerDeck(),
            enemy = EnemyMedium,
            showAlertDialog = showAlertDialog
        ) {
            showGameMedium = false
        }
        return
    } else if (showGame38) {
        StartGame(
            activity = activity,
            playerDeck = getPlayerDeck(),
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
            playerDeck = getPlayerDeck(),
            enemy = EnemyCheater,
            showAlertDialog = showAlertDialog
        ) {
            showGameCheater = false
        }
        return
    }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                text = "Easy Pete",
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    showGameEasy = true
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Slightly more difficult Peter",
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    showGameMedium = true
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Deck-o'-38 Peterson",
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    showGame38 = true
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Petah the Cheatah",
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                modifier = Modifier.clickable {
                    showGameCheater = true
                }
            )
        }
        HorizontalDivider()

        Row(modifier = Modifier
            .fillMaxHeight(0.1f)
            .padding(8.dp)) {
            Text(modifier = Modifier.fillMaxWidth(0.7f), text = "Use Custom Deck")

            Checkbox(checked = checkedCustomDeck, onCheckedChange = {
                checkedCustomDeck = !checkedCustomDeck
            })
        }

        HorizontalDivider()
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
        Text(
            text = "Back to Menu",
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp),
            modifier = Modifier.clickable { goBack() }
        )
    }
}

@Composable
fun StartGame(
    activity: MainActivity,
    playerDeck: Deck,
    enemy: Enemy,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit,
) {
    val game by rememberSaveable(stateSaver = GameSaver) {
        mutableStateOf(
            Game(
                playerDeck,
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
            activity.save?.let { save ->
                enemy.getRewardDeck()?.let { cardBack ->
                    save.availableDecks[cardBack] = true
                }
                save.gamesFinished++
                save.wins++
                save(activity, save)
            }
            showAlertDialog("Result", "You win!")
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
