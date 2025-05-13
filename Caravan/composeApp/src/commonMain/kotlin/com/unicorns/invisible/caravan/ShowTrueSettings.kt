package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.animation_in_game
import caravan.composeapp.generated.resources.cheats
import caravan.composeapp.generated.resources.intro_music
import caravan.composeapp.generated.resources.menu_settings
import caravan.composeapp.generated.resources.monofont
import caravan.composeapp.generated.resources.non_stop_radio
import caravan.composeapp.generated.resources.save
import com.unicorns.invisible.caravan.cheats.CheatBackToSquare1
import com.unicorns.invisible.caravan.cheats.CheatChangeTrack
import com.unicorns.invisible.caravan.cheats.CheatEraseStoryProgress
import com.unicorns.invisible.caravan.cheats.CheatFinalBossesHeroicMusic
import com.unicorns.invisible.caravan.cheats.CheatFrankChallenge
import com.unicorns.invisible.caravan.cheats.CheatLaggySpeed
import com.unicorns.invisible.caravan.cheats.CheatLevel10
import com.unicorns.invisible.caravan.cheats.CheatPapaSmurf
import com.unicorns.invisible.caravan.cheats.CheatRadioPseudonyms
import com.unicorns.invisible.caravan.cheats.stash.CheatStash1234
import com.unicorns.invisible.caravan.cheats.stash.CheatStash1969
import com.unicorns.invisible.caravan.cheats.stash.CheatStashCumpleanos
import com.unicorns.invisible.caravan.cheats.stash.CheatStashEventfulYear
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.SwitchCustomUsualBackground
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playClickSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource


@Composable
fun ShowTrueSettings(
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit
) {
    var speed by remember { mutableStateOf(saveGlobal.animationSpeed) }
    var intro by remember { mutableStateOf(saveGlobal.useCaravanIntro) }
    var playInBack by remember { mutableStateOf(saveGlobal.playRadioInBack) }
    val scope = rememberCoroutineScope()

    MenuItemOpen(stringResource(Res.string.menu_settings), stringResource(Res.string.save), Alignment.TopCenter, {
        saveGlobal.animationSpeed = speed
        saveGlobal.useCaravanIntro = intro
        saveGlobal.playRadioInBack = playInBack
        saveData()
        goBack()
    }) {
        Column(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor()),
        ) {
            Spacer(Modifier.height(16.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                @Composable
                fun SwitchSetting(
                    name: String,
                    getFlag: () -> Boolean,
                    switch: () -> Unit,
                    isAvailable: () -> Boolean = { true }
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFallout(
                            name,
                            getTextColor(),
                            getTextStrokeColor(),
                            18.sp,
                            Modifier.weight(1f),
                            textAlignment = TextAlign.Center,
                            boxAlignment = Alignment.CenterEnd
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            SwitchCustomUsualBackground(getFlag, {
                                switch()
                                if (getFlag()) {
                                    playClickSound()
                                } else {
                                    playCloseSound()
                                }
                            }, isAvailable)
                        }
                    }
                }

                SwitchSetting(stringResource(Res.string.animation_in_game), {
                    speed == AnimationSpeed.NORMAL
                }, {
                    speed = if (speed == AnimationSpeed.NORMAL) {
                        AnimationSpeed.NONE
                    } else {
                        AnimationSpeed.NORMAL
                    }
                }) { saveGlobal.animationSpeed != AnimationSpeed.LAGGY }
                Spacer(Modifier.height(12.dp))
                SwitchSetting(stringResource(Res.string.intro_music), { intro }, { intro = !intro })
                Spacer(Modifier.height(12.dp))
                SwitchSetting(stringResource(Res.string.non_stop_radio), { playInBack }, { playInBack = !playInBack })
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.height(96.dp).fillMaxWidth(0.8f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    var secretCode by remember { mutableStateOf<Int?>(null) }
                    val cheats = listOf(
                        CheatChangeTrack,
                        CheatEraseStoryProgress,
                        CheatFinalBossesHeroicMusic,
                        CheatLevel10,
                        CheatPapaSmurf,
                        CheatRadioPseudonyms,
                        CheatStash1234,
                        CheatStash1969,
                        CheatStashEventfulYear,
                        CheatStashCumpleanos,
                        CheatLaggySpeed,
                        CheatBackToSquare1,
                        CheatFrankChallenge
                    )
                    TextFallout(
                        text = stringResource(Res.string.cheats),
                        getTextColor(),
                        getTextStrokeColor(),
                        14.sp,
                        Modifier
                            .weight(1f)
                            .clickableOk {
                                cheats.forEach {
                                    if (secretCode == it.getCode()) {
                                        scope.launch {
                                            it.onEnter { p1, p2 ->
                                                showAlertDialog(p1, p2, null)
                                            }
                                        }
                                        return@clickableOk
                                    }
                                }
                                playCloseSound()
                            }
                            .background(getTextBackgroundColor())
                            .padding(4.dp),
                        textAlignment = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    TextField(
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        enabled = true,
                        value = secretCode?.toString() ?: "",
                        onValueChange = { secretCode = it.toIntOrNull() },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = getTextColor(),
                            fontFamily = FontFamily(Font(Res.font.monofont))
                        ),
                        label = {
                            TextFallout(
                                text = "???",
                                getTextColor(),
                                getTextStrokeColor(),
                                11.sp,
                                Modifier,
                            )
                        },
                        colors = TextFieldDefaults.colors().copy(
                            cursorColor = getTextColor(),
                            focusedContainerColor = getTextBackgroundColor(),
                            unfocusedContainerColor = getTextBackgroundColor(),
                            disabledContainerColor = getBackgroundColor(),
                        )
                    )
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}