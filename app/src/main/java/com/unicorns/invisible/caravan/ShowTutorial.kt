package com.unicorns.invisible.caravan

import androidx.compose.foundation.border
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.EnemyTutorial
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.getDialogBackground
import com.unicorns.invisible.caravan.utils.getDialogTextColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.stopAmbient


// TODO: check all the things!!
@Composable
fun Tutorial(
    activity: MainActivity,
    goBack: () -> Unit
) {
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

    var updater by remember { mutableStateOf(false) }
    val game: Game = rememberScoped {
        Game(
            CResources(CustomDeck().apply {
                add(Card(Rank.JACK, Suit.HEARTS, CardBack.STANDARD, false))
                add(Card(Rank.QUEEN, Suit.CLUBS, CardBack.STANDARD, false))
                add(Card(Rank.KING, Suit.DIAMONDS, CardBack.STANDARD, false))
                add(Card(Rank.JOKER, Suit.HEARTS, CardBack.STANDARD, false))
                add(Card(Rank.TWO, Suit.HEARTS, CardBack.STANDARD, false))
                add(Card(Rank.TWO, Suit.DIAMONDS, CardBack.STANDARD, false))
                add(Card(Rank.TWO, Suit.CLUBS, CardBack.STANDARD, false))
                add(Card(Rank.THREE, Suit.HEARTS, CardBack.STANDARD, false))
                add(Card(Rank.FOUR, Suit.DIAMONDS, CardBack.STANDARD, false))
                add(Card(Rank.ACE, Suit.DIAMONDS, CardBack.STANDARD, false))
            }),
            EnemyTutorial().apply {
                onRemoveFromHand = { updater = !updater }
            }
        ).also { it.startGame() }
    }

    ShowGame(activity = activity, game = game) { stopAmbient(); goBack() }

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
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_5))
            }

            4 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_6_1) +
                            stringResource(R.string.tutorial_6_2) +
                            stringResource(R.string.tutorial_6_3)
                )
            }

            5 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_7))
            }

            6 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_8_1)
                )
            }

            7 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_9_1) + stringResource(R.string.tutorial_9_2)
                )
            }

            8 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_10))
            }

            9 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_11))
            }

            10 -> {
                if (game.playerCaravans.all { it.getValue() > 0 }) {
                    tutorialKey++
                }
            }

            11 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_12))
            }

            12 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_13))
            }

            13 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_14))
            }

            14 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_15_1) + stringResource(R.string.tutorial_15_2)
                )
            }

            15 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_16))
            }

            16 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_17))
            }

            17 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_18))
            }

            18 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_19))
            }

            19 -> {
                showAlertDialog(
                    stringResource(R.string.tutorial_warning),
                    stringResource(R.string.tutorial_20_1) +
                            stringResource(R.string.tutorial_20_2)
                )
            }

            20 -> {
                if (game.playerCResources.deckSize < 2) {
                    tutorialKey++
                }
            }

            21 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_21_1) +
                            stringResource(R.string.tutorial_21_2)
                )
            }

            22 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_22))
            }

            23 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_23))
            }

            24 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_24))
            }

            25 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_25))
            }

            26 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_26_1) + stringResource(R.string.tutorial_26_2)
                )
            }

            27 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_27_1) +
                            stringResource(R.string.tutorial_27_2) +
                            stringResource(R.string.tutorial_27_3) +
                            stringResource(R.string.tutorial_27_4)
                )
            }

            28 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_28))
            }

            29 -> {
                tutorialKey++
                updater = !updater
            }

            30 -> {
                if (game.playerCResources.hand.all { it.isModifier() }) {
                    tutorialKey++
                }
            }

            31 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_30))
            }

            32 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_31))
            }

            33 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_32))
            }

            34 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_33))
            }

            35 -> {
                if (game.playerCaravans.all { it.getValue() == 0 }) {
                    tutorialKey++
                }
            }

            36 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_34))
            }

            37 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_35))
            }

            38 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_36))
            }

            39 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_37))
            }

            40 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_38))
            }

            41 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_39))
            }

            42 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_40))
            }

            43 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_41))
            }

            44 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_42))
            }

            45 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_43))
            }

            46 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_44))
            }

            47 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_45))
            }

            48 -> {
                if (game.playerCResources.hand.isEmpty()) {
                    tutorialKey++
                }
            }

            49 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_46))
            }

            50 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_47))
            }

            51 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_48))
            }

            52 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_8_2)
                )
            }

            53 -> {
                showAlertDialog(
                    stringResource(R.string.kyle),
                    stringResource(R.string.tutorial_49_1) + stringResource(R.string.tutorial_49_2)
                )
            }

            54 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_50))
            }

            55 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_51))
            }

            56 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_52))
            }

            57 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_53))
            }

            58 -> {
                showAlertDialog(stringResource(R.string.kyle), stringResource(R.string.tutorial_56))
            }

            else -> {
                goBack()
            }
        }
    }

    if (showAlertDialog) {
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = getTextColor(activity)),
            onDismissRequest = { hideAlertDialog() },
            confirmButton = {
                TextFallout(
                    "OK",
                    getDialogTextColor(activity),
                    getDialogTextColor(activity),
                    14.sp,
                    Alignment.CenterEnd,
                    Modifier.clickableCancel(activity) { hideAlertDialog() },
                    TextAlign.End
                )
            },
            title = {
                TextFallout(
                    alertDialogHeader,
                    getDialogTextColor(activity),
                    getDialogTextColor(activity),
                    24.sp,
                    Alignment.CenterEnd,
                    Modifier,
                    TextAlign.End
                )
            },
            text = {
                TextFallout(
                    alertDialogMessage,
                    getDialogTextColor(activity),
                    getDialogTextColor(activity),
                    18.sp,
                    Alignment.CenterStart,
                    Modifier,
                    TextAlign.Start
                )
            },
            containerColor = getDialogBackground(activity),
            textContentColor = getDialogTextColor(activity),
            shape = RectangleShape,
        )
    }
}