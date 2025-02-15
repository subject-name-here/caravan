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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.unicorns.invisible.caravan.cheats.*
import com.unicorns.invisible.caravan.cheats.stash.*
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.SwitchCustomUsualBackground
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playClickSound
import com.unicorns.invisible.caravan.utils.playCloseSound
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowTrueSettings(
    activity: MainActivity,
    showAlertDialog: (String, String, (() -> Unit)?) -> Unit,
    goBack: () -> Unit
) {
    val mainState = rememberLazyListState()
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
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity))
                .scrollbar(
                    mainState,
                    horizontal = false,
                    knobColor = getKnobColor(activity),
                    trackColor = getTrackColor(activity)
                ),
            mainState
        ) {
            item {
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
                                Alignment.CenterStart,
                                Modifier.fillMaxWidth(0.5f),
                                TextAlign.Start
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.height(96.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        var secretCode by remember { mutableStateOf<Int?>(null) }
                        val cheats = listOf(
                            CheatChangeTrack,
                            CheatChina,
                            CheatEraseStoryProgress,
                            CheatFinalBossesHeroicMusic,
                            CheatHalfEnclaveDeck,
                            CheatHalfStandardAltDeck,
                            CheatLevel10,
                            CheatOldSaveRestore,
                            CheatPapaSmurf,
                            CheatRadioPseudonyms,
                            CheatStash1234,
                            CheatStash1969,
                            CheatStashEventfulMay,
                            CheatStashLove,
                            CheatLaggySpeed,
                        )
                        TextFallout(
                            text = stringResource(R.string.cheats),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            14.sp,
                            Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth(0.33f)
                                .padding(horizontal = 8.dp)
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
                            textAlign = TextAlign.Center,
                        )
                        TextField(
                            modifier = Modifier.fillMaxWidth(0.5f),
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
                                    Alignment.Center,
                                    Modifier,
                                    TextAlign.Center
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
                }
            }
        }
    }
}