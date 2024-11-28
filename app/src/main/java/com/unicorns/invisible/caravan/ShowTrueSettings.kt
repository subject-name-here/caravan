package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.model.trading.ChineseTrader
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
import com.unicorns.invisible.caravan.utils.playPimpBoySound
import com.unicorns.invisible.caravan.utils.playYesBeep
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowTrueSettings(
    activity: MainActivity,
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
                .padding(horizontal = 16.dp)
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
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFallout(
                            stringResource(R.string.animation_length),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            18.sp,
                            Alignment.CenterStart,
                            Modifier.fillMaxWidth(0.5f),
                            TextAlign.Start
                        )

                        fun changeSpeed(change: () -> AnimationSpeed) {
                            val tmpSpeed = change()
                            if (tmpSpeed == speed) {
                                playCloseSound(activity)
                            } else {
                                playClickSound(activity)
                                speed = tmpSpeed
                            }
                        }
                        Row {
                            TextFallout(
                                "<",
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                18.sp,
                                Alignment.Center,
                                Modifier
                                    .weight(1f)
                                    .background(getTextBackgroundColor(activity))
                                    .clickable {
                                        changeSpeed { speed.prev() }
                                    },
                                TextAlign.Center
                            )
                            TextFallout(
                                speed.name,
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                18.sp,
                                Alignment.Center,
                                Modifier.weight(1.5f),
                                TextAlign.Center
                            )
                            TextFallout(
                                ">",
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                18.sp,
                                Alignment.Center,
                                Modifier
                                    .weight(1f)
                                    .background(getTextBackgroundColor(activity))
                                    .clickable {
                                        changeSpeed { speed.next() }
                                    },
                                TextAlign.Center
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFallout(
                            stringResource(R.string.intro_music),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            18.sp,
                            Alignment.CenterStart,
                            Modifier.fillMaxWidth(0.5f),
                            TextAlign.Start
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            SwitchCustomUsualBackground(activity, { intro }) {
                                intro = !intro
                                if (intro) {
                                    playClickSound(activity)
                                } else {
                                    playCloseSound(activity)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFallout(
                            stringResource(R.string.non_stop_radio),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            18.sp,
                            Alignment.CenterStart,
                            Modifier.fillMaxWidth(0.5f),
                            TextAlign.Start
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            SwitchCustomUsualBackground(activity, { playInBack }) {
                                playInBack = !playInBack
                                if (playInBack) {
                                    playClickSound(activity)
                                } else {
                                    playCloseSound(activity)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.height(96.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        var secretCode by remember { mutableStateOf<Int?>(null) }
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
                                    when (secretCode) {
                                        65537 -> {
                                            save.towerLevel = 10
                                            saveData(activity)
                                            playYesBeep(activity)
                                        }
//
//                                        666 -> {
//                                            save?.let {
//                                                it.soOpen = true
//                                                saveOnGD(activity)
//                                                playYesBeep(activity)
//                                            }
//                                        }

                                        1337 -> {
                                            playPimpBoySound(activity)
                                        }

                                        62869 -> {
                                            save.let {
                                                if (!it.prize1Activated) {
                                                    it.prize1Activated = true
                                                    it.capsInHand += 1969
                                                    saveData(activity)
                                                    playYesBeep(activity)
                                                }
                                            }
                                        }

                                        50724 -> {
                                            save.let {
                                                if (!it.prize2Activated) {
                                                    it.prize2Activated = true
                                                    it.capsInHand += 2024
                                                    saveData(activity)
                                                    playYesBeep(activity)
                                                }
                                            }
                                        }

                                        404 -> {
                                            save.let {
                                                it.storyChaptersProgress = 0
                                                it.altStoryChaptersProgress = 0
                                                saveData(activity)
                                                playYesBeep(activity)
                                            }
                                        }

                                        9009 -> {
                                            save.let {
                                                listOf(Suit.HEARTS, Suit.SPADES).forEach { suit ->
                                                    Rank.entries.forEach { rank ->
                                                        if (rank != Rank.JOKER) {
                                                            it.availableCards.add(Card(rank, suit, CardBack.STANDARD, true))
                                                        }
                                                    }
                                                }

                                                saveData(activity)
                                                playYesBeep(activity)
                                            }
                                        }

                                        1921 -> {
                                            save.let {
                                                it.traders
                                                    .filterIsInstance<ChineseTrader>()
                                                    .forEach { trader ->
                                                        trader.is1921Entered = true
                                                    }

                                                saveData(activity)
                                                playYesBeep(activity)
                                            }
                                        }

                                        110811 -> {
                                            save.papaSmurfActive = !save.papaSmurfActive
                                            saveData(activity)
                                            playYesBeep(activity)
                                        }

                                        // TODO: more cheats
                                    }
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
                                    text = "Enter the Code:",
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
