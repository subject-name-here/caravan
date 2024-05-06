package com.unicorns.invisible.caravan

import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.GameSaver
import com.unicorns.invisible.caravan.model.enemy.EnemyTutorial
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit

@Composable
fun Tutorial(activity: MainActivity, goBack: () -> Unit) {
    var tutorialKey by rememberSaveable { mutableIntStateOf(0) }

    var showAlertDialog by rememberSaveable { mutableStateOf(false) }
    var alertDialogHeader by rememberSaveable { mutableStateOf("") }
    var alertDialogMessage by rememberSaveable { mutableStateOf("") }

    fun showAlertDialog(header: String, message: String) {
        showAlertDialog = true
        alertDialogHeader = header
        alertDialogMessage = message
    }
    fun hideAlertDialog() {
        showAlertDialog = false
        tutorialKey++
    }

    val enemy = EnemyTutorial
    val game by rememberSaveable(stateSaver = GameSaver) {
        mutableStateOf(
            Game(
                CResources(CustomDeck().apply {
                    add(Card(Rank.JACK, Suit.HEARTS, CardBack.STANDARD))
                    add(Card(Rank.QUEEN, Suit.CLUBS, CardBack.STANDARD))
                    add(Card(Rank.KING, Suit.DIAMONDS, CardBack.STANDARD))
                    add(Card(Rank.JOKER, Suit.HEARTS, CardBack.STANDARD))
                    add(Card(Rank.TWO, Suit.HEARTS, CardBack.STANDARD))
                    add(Card(Rank.TWO, Suit.DIAMONDS, CardBack.STANDARD))
                    add(Card(Rank.TWO, Suit.CLUBS, CardBack.STANDARD))
                    add(Card(Rank.THREE, Suit.HEARTS, CardBack.STANDARD))
                    add(Card(Rank.FOUR, Suit.DIAMONDS, CardBack.STANDARD))
                    add(Card(Rank.ACE, Suit.DIAMONDS, CardBack.STANDARD))
                }),
                enemy
            ).also { it.startGame(maxNumOfFaces = 4) }
        )
    }

    var updater by remember { mutableStateOf(false) }
    game.enemyCResources.onRemoveFromHand = {
        updater = !updater
    }

    ShowGame(activity = activity, game = game) { goBack() }

    key(updater) {
        when (tutorialKey) {
            0 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_1_1) + stringResource(R.string.tutorial_1_2)
                )
            }
            1 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_2))
            }
            2 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_3))
            }
            3 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_4))
            }
            4 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_5))
            }
            5 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_6_1) +
                            stringResource(R.string.tutorial_6_2) +
                            stringResource(R.string.tutorial_6_3)
                )
            }
            6 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_7))
            }
            7 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_8_1))
            }
            8 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_9_1) + stringResource(R.string.tutorial_9_2)
                )
            }
            9 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_10))
            }
            10 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_11))
            }
            11 -> {
                if (game.playerCaravans.all { it.getValue() > 0 }) {
                    tutorialKey++
                }
            }
            12 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_12))
            }
            13 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_13))
            }
            14 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_14))
            }
            15 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_15_1) + stringResource(R.string.tutorial_15_2)
                )
            }
            16 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_16))
            }
            17 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_17))
            }
            18 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_18))
            }
            19 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_19))
            }
            20 -> {
                showAlertDialog(
                    stringResource(R.string.tutorial_warning), stringResource(R.string.tutorial_20_1) +
                            stringResource(R.string.tutorial_20_2)
                )
            }
            21 -> {
                if (game.playerCResources.deckSize < 2) {
                    tutorialKey++
                }
            }
            22 -> {
                showAlertDialog(stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_21_1) +
                            stringResource(R.string.tutorial_21_2)
                )
            }
            23 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_22))
            }
            24 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_23))
            }
            25 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_24))
            }
            26 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_25))
            }
            27 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_26_1) + stringResource(R.string.tutorial_26_2)
                )
            }
            28 -> {
                showAlertDialog(stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_27_1) +
                            stringResource(R.string.tutorial_27_2) +
                            stringResource(R.string.tutorial_27_3) +
                            stringResource(R.string.tutorial_27_4)
                )
            }
            29 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_28))
            }
            30 -> {
                showAlertDialog(stringResource(R.string.tutorial_hint), stringResource(R.string.tutorial_29))
            }
            31 -> {
                if (game.playerCResources.hand.all { it.isFace() }) {
                    tutorialKey++
                }
            }
            32 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_30))
            }
            33 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_31))
            }
            34 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_32))
            }
            35 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_33))
            }
            36 -> {
                if (game.playerCaravans.all { it.getValue() == 0 }) {
                    tutorialKey++
                }
            }
            37 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_34))
            }
            38 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_35))
            }
            39 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_36))
            }
            40 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_37))
            }
            41 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_38))
            }
            42 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_39))
            }
            43 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_40))
            }
            44 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_41))
            }
            45 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_42))
            }
            46 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_43))
            }
            47 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_44))
            }
            48 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_45))
            }
            49 -> {
                if (game.playerCResources.hand.isEmpty()) {
                    tutorialKey++
                }
            }
            50 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_46))
            }
            51 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_47))
            }
            52 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_48))
            }
            53 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_8_2))
            }
            54 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_49_1) + stringResource(R.string.tutorial_49_2)
                )
            }
            55 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_50))
            }
            56 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_51))
            }
            57 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_52))
            }
            58 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_53))
            }
            59 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_56))
            }
            else -> {
                goBack()
            }
        }
    }

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { hideAlertDialog() },
            confirmButton = { Text(text = "OK", modifier = Modifier.clickable { hideAlertDialog() }) },
            title = { Text(text = alertDialogHeader) },
            text = { Text(text = alertDialogMessage) },
        )
    }
}