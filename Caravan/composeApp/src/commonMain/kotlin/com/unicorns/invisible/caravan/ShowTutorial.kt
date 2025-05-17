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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.kyle
import caravan.composeapp.generated.resources.tutorial_10
import caravan.composeapp.generated.resources.tutorial_11
import caravan.composeapp.generated.resources.tutorial_12
import caravan.composeapp.generated.resources.tutorial_13
import caravan.composeapp.generated.resources.tutorial_14
import caravan.composeapp.generated.resources.tutorial_15_1
import caravan.composeapp.generated.resources.tutorial_15_2
import caravan.composeapp.generated.resources.tutorial_16
import caravan.composeapp.generated.resources.tutorial_17
import caravan.composeapp.generated.resources.tutorial_18
import caravan.composeapp.generated.resources.tutorial_19
import caravan.composeapp.generated.resources.tutorial_1_1
import caravan.composeapp.generated.resources.tutorial_1_2
import caravan.composeapp.generated.resources.tutorial_2
import caravan.composeapp.generated.resources.tutorial_20_1
import caravan.composeapp.generated.resources.tutorial_20_2
import caravan.composeapp.generated.resources.tutorial_21_1
import caravan.composeapp.generated.resources.tutorial_21_2
import caravan.composeapp.generated.resources.tutorial_22
import caravan.composeapp.generated.resources.tutorial_23
import caravan.composeapp.generated.resources.tutorial_24
import caravan.composeapp.generated.resources.tutorial_25
import caravan.composeapp.generated.resources.tutorial_26_1
import caravan.composeapp.generated.resources.tutorial_26_2
import caravan.composeapp.generated.resources.tutorial_27_1
import caravan.composeapp.generated.resources.tutorial_27_2
import caravan.composeapp.generated.resources.tutorial_27_3
import caravan.composeapp.generated.resources.tutorial_27_4
import caravan.composeapp.generated.resources.tutorial_28
import caravan.composeapp.generated.resources.tutorial_3
import caravan.composeapp.generated.resources.tutorial_30
import caravan.composeapp.generated.resources.tutorial_31
import caravan.composeapp.generated.resources.tutorial_32
import caravan.composeapp.generated.resources.tutorial_33
import caravan.composeapp.generated.resources.tutorial_34
import caravan.composeapp.generated.resources.tutorial_35
import caravan.composeapp.generated.resources.tutorial_36
import caravan.composeapp.generated.resources.tutorial_37
import caravan.composeapp.generated.resources.tutorial_38
import caravan.composeapp.generated.resources.tutorial_39
import caravan.composeapp.generated.resources.tutorial_40
import caravan.composeapp.generated.resources.tutorial_41
import caravan.composeapp.generated.resources.tutorial_42
import caravan.composeapp.generated.resources.tutorial_43
import caravan.composeapp.generated.resources.tutorial_44
import caravan.composeapp.generated.resources.tutorial_45
import caravan.composeapp.generated.resources.tutorial_46
import caravan.composeapp.generated.resources.tutorial_47
import caravan.composeapp.generated.resources.tutorial_48
import caravan.composeapp.generated.resources.tutorial_49_1
import caravan.composeapp.generated.resources.tutorial_49_2
import caravan.composeapp.generated.resources.tutorial_5
import caravan.composeapp.generated.resources.tutorial_50
import caravan.composeapp.generated.resources.tutorial_51
import caravan.composeapp.generated.resources.tutorial_52
import caravan.composeapp.generated.resources.tutorial_53
import caravan.composeapp.generated.resources.tutorial_56
import caravan.composeapp.generated.resources.tutorial_6_1
import caravan.composeapp.generated.resources.tutorial_6_2
import caravan.composeapp.generated.resources.tutorial_6_3
import caravan.composeapp.generated.resources.tutorial_7
import caravan.composeapp.generated.resources.tutorial_8_1
import caravan.composeapp.generated.resources.tutorial_8_2
import caravan.composeapp.generated.resources.tutorial_9_1
import caravan.composeapp.generated.resources.tutorial_9_2
import caravan.composeapp.generated.resources.tutorial_warning
import com.sebaslogen.resaca.rememberScoped
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.enemy.EnemyTutorial
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CardFaceSuited
import com.unicorns.invisible.caravan.model.primitives.CardJoker
import com.unicorns.invisible.caravan.model.primitives.CardModifier
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.CustomDeck
import com.unicorns.invisible.caravan.model.primitives.RankFace
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.getDialogBackground
import com.unicorns.invisible.caravan.utils.getDialogTextColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.stopAmbient
import org.jetbrains.compose.resources.stringResource


// TODO 3.0: check all the things!!
@Composable
fun Tutorial(
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
                add(CardFaceSuited(RankFace.JACK, Suit.HEARTS, CardBack.STANDARD))
                add(CardFaceSuited(RankFace.QUEEN, Suit.CLUBS, CardBack.STANDARD))
                add(CardFaceSuited(RankFace.KING, Suit.DIAMONDS, CardBack.STANDARD))
                add(CardJoker(CardJoker.Number.TWO, CardBack.STANDARD))
                add(CardNumber(RankNumber.TWO, Suit.HEARTS, CardBack.STANDARD))
                add(CardNumber(RankNumber.TWO, Suit.DIAMONDS, CardBack.STANDARD))
                add(CardNumber(RankNumber.TWO, Suit.CLUBS, CardBack.STANDARD))
                add(CardNumber(RankNumber.THREE, Suit.HEARTS, CardBack.STANDARD))
                add(CardNumber(RankNumber.FOUR, Suit.DIAMONDS, CardBack.STANDARD))
                add(CardNumber(RankNumber.ACE, Suit.DIAMONDS, CardBack.STANDARD))
            }),
            EnemyTutorial().apply {
                onRemoveFromHand = { updater = !updater }
            }
        ).also { it.startGame() }
    }

    ShowGame(game) { stopAmbient(); goBack() }

    key(updater) {
        when (tutorialKey) {
            0 -> {
                showAlertDialog(
                    stringResource(Res.string.kyle),
                    stringResource(Res.string.tutorial_1_1) + stringResource(Res.string.tutorial_1_2)
                )
            }

            1 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_2))
            }

            2 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_3))
            }

            3 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_5))
            }

            4 -> {
                showAlertDialog(
                    stringResource(Res.string.kyle),
                    stringResource(Res.string.tutorial_6_1) +
                            stringResource(Res.string.tutorial_6_2) +
                            stringResource(Res.string.tutorial_6_3)
                )
            }

            5 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_7))
            }

            6 -> {
                showAlertDialog(
                    stringResource(Res.string.kyle),
                    stringResource(Res.string.tutorial_8_1)
                )
            }

            7 -> {
                showAlertDialog(
                    stringResource(Res.string.kyle),
                    stringResource(Res.string.tutorial_9_1) + stringResource(Res.string.tutorial_9_2)
                )
            }

            8 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_10))
            }

            9 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_11))
            }

            10 -> {
                if (game.playerCaravans.all { it.getValue() > 0 }) {
                    tutorialKey++
                }
            }

            11 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_12))
            }

            12 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_13))
            }

            13 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_14))
            }

            14 -> {
                showAlertDialog(
                    stringResource(Res.string.kyle),
                    stringResource(Res.string.tutorial_15_1) + stringResource(Res.string.tutorial_15_2)
                )
            }

            15 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_16))
            }

            16 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_17))
            }

            17 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_18))
            }

            18 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_19))
            }

            19 -> {
                showAlertDialog(
                    stringResource(Res.string.tutorial_warning),
                    stringResource(Res.string.tutorial_20_1) +
                            stringResource(Res.string.tutorial_20_2)
                )
            }

            20 -> {
                if (game.playerCResources.deckSize < 2) {
                    tutorialKey++
                }
            }

            21 -> {
                showAlertDialog(
                    stringResource(Res.string.kyle),
                    stringResource(Res.string.tutorial_21_1) +
                            stringResource(Res.string.tutorial_21_2)
                )
            }

            22 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_22))
            }

            23 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_23))
            }

            24 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_24))
            }

            25 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_25))
            }

            26 -> {
                showAlertDialog(
                    stringResource(Res.string.kyle),
                    stringResource(Res.string.tutorial_26_1) + stringResource(Res.string.tutorial_26_2)
                )
            }

            27 -> {
                showAlertDialog(
                    stringResource(Res.string.kyle),
                    stringResource(Res.string.tutorial_27_1) +
                            stringResource(Res.string.tutorial_27_2) +
                            stringResource(Res.string.tutorial_27_3) +
                            stringResource(Res.string.tutorial_27_4)
                )
            }

            28 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_28))
            }

            29 -> {
                tutorialKey++
                updater = !updater
            }

            30 -> {
                if (game.playerCResources.hand.all { it is CardModifier }) {
                    tutorialKey++
                }
            }

            31 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_30))
            }

            32 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_31))
            }

            33 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_32))
            }

            34 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_33))
            }

            35 -> {
                if (game.playerCaravans.all { it.getValue() == 0 }) {
                    tutorialKey++
                }
            }

            36 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_34))
            }

            37 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_35))
            }

            38 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_36))
            }

            39 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_37))
            }

            40 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_38))
            }

            41 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_39))
            }

            42 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_40))
            }

            43 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_41))
            }

            44 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_42))
            }

            45 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_43))
            }

            46 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_44))
            }

            47 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_45))
            }

            48 -> {
                if (game.playerCResources.hand.isEmpty()) {
                    tutorialKey++
                }
            }

            49 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_46))
            }

            50 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_47))
            }

            51 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_48))
            }

            52 -> {
                showAlertDialog(
                    stringResource(Res.string.kyle),
                    stringResource(Res.string.tutorial_8_2)
                )
            }

            53 -> {
                showAlertDialog(
                    stringResource(Res.string.kyle),
                    stringResource(Res.string.tutorial_49_1) + stringResource(Res.string.tutorial_49_2)
                )
            }

            54 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_50))
            }

            55 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_51))
            }

            56 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_52))
            }

            57 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_53))
            }

            58 -> {
                showAlertDialog(stringResource(Res.string.kyle), stringResource(Res.string.tutorial_56))
            }

            else -> {
                goBack()
            }
        }
    }

    if (showAlertDialog) {
        AlertDialog(
            modifier = Modifier.border(width = 4.dp, color = getTextColor()),
            onDismissRequest = {},
            confirmButton = {
                TextFallout(
                    "OK",
                    getDialogTextColor(),
                    getDialogTextColor(),
                    14.sp,
                    Modifier.clickableCancel { hideAlertDialog() }
                )
            },
            title = {
                TextFallout(
                    alertDialogHeader,
                    getDialogTextColor(),
                    getDialogTextColor(),
                    24.sp,
                    Modifier,
                )
            },
            text = {
                TextFallout(
                    alertDialogMessage,
                    getDialogTextColor(),
                    getDialogTextColor(),
                    18.sp,
                    Modifier,
                    textAlignment = TextAlign.Start
                )
            },
            containerColor = getDialogBackground(),
            textContentColor = getDialogTextColor(),
            shape = RectangleShape,
        )
    }
}