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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.cheats.Cheat666
import com.unicorns.invisible.caravan.cheats.CheatChangeTrack
import com.unicorns.invisible.caravan.cheats.CheatChina
import com.unicorns.invisible.caravan.cheats.CheatEraseStoryProgress
import com.unicorns.invisible.caravan.cheats.CheatFinalBossesHeroicMusic
import com.unicorns.invisible.caravan.cheats.CheatLaggySpeed
import com.unicorns.invisible.caravan.cheats.CheatLevel10
import com.unicorns.invisible.caravan.cheats.CheatPapaSmurf
import com.unicorns.invisible.caravan.cheats.CheatRadioPseudonyms
import com.unicorns.invisible.caravan.cheats.CheatUltimate
import com.unicorns.invisible.caravan.cheats.stash.CheatStash1234
import com.unicorns.invisible.caravan.cheats.stash.CheatStash1969
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


@Composable
fun ShowTrueSettings(
    activity: MainActivity,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit
) {
    var speed by remember { mutableStateOf(save.animationSpeed) }
    var intro by remember { mutableStateOf(save.useCaravanIntro) }
    var playInBack by remember { mutableStateOf(save.playRadioInBack) }

    MenuItemOpen(activity, stringResource(R.string.menu_settings), stringResource(R.string.save), {
        save.animationSpeed = speed
        save.useCaravanIntro = intro
        save.playRadioInBack = playInBack
        saveData(activity)
        goBack()
    }) {
        Column(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity)),
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
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            18.sp,
                            Modifier.weight(1f),
                            textAlignment = TextAlign.Center,
                            boxAlignment = Alignment.CenterEnd
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            SwitchCustomUsualBackground(activity, getFlag, {
                                switch()
                                if (getFlag()) {
                                    playClickSound(activity)
                                } else {
                                    playCloseSound(activity)
                                }
                            }, isAvailable)
                        }
                    }
                }

                SwitchSetting(stringResource(R.string.animation_in_game), {
                    speed == AnimationSpeed.NORMAL
                }, {
                    speed = if (speed == AnimationSpeed.NORMAL) {
                        AnimationSpeed.NONE
                    } else {
                        AnimationSpeed.NORMAL
                    }
                }) { save.animationSpeed != AnimationSpeed.LAGGY }
                Spacer(Modifier.height(12.dp))
                SwitchSetting(stringResource(R.string.intro_music), { intro }, { intro = !intro })
                Spacer(Modifier.height(12.dp))
                SwitchSetting(stringResource(R.string.non_stop_radio), { playInBack }, { playInBack = !playInBack })
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.height(96.dp).fillMaxWidth(0.8f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    var secretCode by remember { mutableStateOf<Int?>(null) }
                    val cheats = listOf(
                        CheatChangeTrack,
                        CheatChina,
                        CheatEraseStoryProgress,
                        CheatFinalBossesHeroicMusic,
                        CheatLevel10,
                        CheatPapaSmurf,
                        CheatRadioPseudonyms,
                        CheatStash1234,
                        CheatStash1969,
                        CheatStashEventfulYear,
                        CheatLaggySpeed,
                        Cheat666,
                        CheatUltimate,
                    )
                    TextFallout(
                        text = stringResource(R.string.cheats),
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        14.sp,
                        Modifier
                            .weight(1f)
                            .clickableOk(activity) {
                                cheats.forEach {
                                    if (secretCode == it.getCode()) {
                                        it.onEnter(activity) { p1, p2 ->
                                            showAlertDialog(p1, p2, null)
                                        }
                                        return@clickableOk
                                    }
                                }
                                playCloseSound(activity)
                            }
                            .background(getTextBackgroundColor(activity))
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
                            color = getTextColor(activity),
                            fontFamily = FontFamily(Font(R.font.monofont))
                        ),
                        label = {
                            TextFallout(
                                text = "???",
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                11.sp,
                                Modifier,
                            )
                        },
                        colors = TextFieldDefaults.colors().copy(
                            cursorColor = getTextColor(activity),
                            focusedContainerColor = getTextBackgroundColor(activity),
                            unfocusedContainerColor = getTextBackgroundColor(activity),
                            disabledContainerColor = getBackgroundColor(activity),
                        )
                    )
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}