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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.GameSaver
import com.unicorns.invisible.caravan.model.enemy.EnemyEasy
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.save.save
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ShowPvP(
    activity: MainActivity,
    selectedDeck: () -> CardBack,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit
) {
    var roomNumber by rememberSaveable { mutableStateOf("") }
    var checkedCustomDeck by rememberSaveable { mutableStateOf(false) }
    var checkedPrivate by rememberSaveable { mutableStateOf(false) }
    var isRoomCreated by rememberSaveable { mutableIntStateOf(0) }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.height(100.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text(
                    text = when (isRoomCreated) {
                        0 -> "Create Room"
                        1 -> "Awaiting server response......"
                        2 -> "Timeout! Room is being destroyed."
                        else -> "Your Room is $isRoomCreated. Awaiting the Opponent....."
                    },
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .clickable {
                            if (isRoomCreated != 0) {
                                return@clickable
                            }

                            isRoomCreated = 1

                            CoroutineScope(Dispatchers.IO).launch {
                                delay(1000L)
                                // TODO: send message
                                // TODO: receive room number
                                isRoomCreated = 22229

                                // TODO: await the response

                                delay(3000L)
                                val response = 0
                                if (response != 0) {

                                } else {
                                    isRoomCreated = 2
                                    // TODO: send message
                                    delay(1000L)
                                    isRoomCreated = 0
                                }
                            }
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(modifier = Modifier
                        .fillMaxHeight(0.5f)
                        .padding(8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(modifier = Modifier.fillMaxWidth(0.7f), text = stringResource(R.string.pve_use_custom_deck))

                        Checkbox(checked = checkedCustomDeck, onCheckedChange = {
                            checkedCustomDeck = !checkedCustomDeck
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
                        ), enabled = isRoomCreated == 0)
                    }
                    Row(modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(),
                        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(modifier = Modifier.fillMaxWidth(0.7f), text = "Private room")

                        Checkbox(checked = checkedPrivate, onCheckedChange = {
                            checkedPrivate = !checkedPrivate
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
                        ), enabled = isRoomCreated == 0)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.height(100.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                TextField(
                    singleLine = true,
                    value = roomNumber,
                    onValueChange = { roomNumber = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(text = "Join Room:", style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp)) },
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Join!!!",
                    modifier = Modifier.clickable {
                        if (isRoomCreated != 0 || roomNumber.toIntOrNull() in listOf(0, 1, 2, null)) {
                            return@clickable
                        }

                        isRoomCreated = roomNumber.toIntOrNull() ?: return@clickable

                        // TODO: send room number

                        CoroutineScope(Dispatchers.IO).launch {
                            delay(1000L)
                            // TODO: receive response
                            val response = 0
                            if (response != 0) {
                                // TODO: start game
                            } else {
                                isRoomCreated = 0
                            }
                        }
                    },
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 16.sp)
                )
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))
        Column(Modifier.fillMaxSize(0.75f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "No stats here: the game's for fun, not for numbers!",
                textAlign = TextAlign.Center,
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "So, you wanna play with people. Two ways:\n" +
                        "1) you join Room. Two ways of getting room number: either from someone (then enter the number to 'Join Room' field) " +
                        "or use a number provided by server (if there are public rooms without opponent, the room number will be written in 'Join Room' field). " +
                        "Finally, press 'Join!!!'. If you didn't wait for too long, the game should start.\n" +
                        "2) You create room. Select whether to use custom deck or not, select privacy (private room number is never provided by server to other people). " +
                        "Finally, press 'Create Room', then wait.\n" +
                        "\n" +
                        "Room number is a number from 10 to 1_000_000.\n" +
                        "Time limit for awaiting opponent is 38 seconds, then room is destroyed.\n" +
                        "Good luck!",
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimary)), fontSize = 12.sp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.menu_back),
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp),
            modifier = Modifier.clickable { goBack() }
        )
    }
}

@Composable
fun StartPvP(
    activity: MainActivity,
    playerCResources: CResources,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit,
) {
    val game by rememberSaveable(stateSaver = GameSaver) {
        mutableStateOf(
            Game(
                playerCResources,
                EnemyEasy // EnemyPlayer()
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
            showAlertDialog(activity.getString(R.string.result), activity.getString(R.string.you_win))
        }
        it.onLose = {
            showAlertDialog(activity.getString(R.string.result), activity.getString(R.string.you_lose))
        }
    }
    ShowGame(activity, game) { goBack() }
}