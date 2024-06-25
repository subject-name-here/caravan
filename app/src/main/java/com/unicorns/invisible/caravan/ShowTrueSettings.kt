package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
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
    getSpeed: () -> AnimationSpeed,
    setSpeed: (AnimationSpeed) -> Unit,
    goBack: () -> Unit
) {
    val mainState = rememberLazyListState()
    var speed by remember { mutableStateOf(getSpeed()) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity))
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth()
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
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        TextFallout(
                            stringResource(R.string.animation_length),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            18.sp,
                            Alignment.CenterStart,
                            Modifier.fillMaxWidth(0.5f),
                            TextAlign.Start
                        )

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
                                        val tmpSpeed = speed.prev()
                                        if (tmpSpeed == speed) {
                                            playCloseSound(activity)
                                        } else {
                                            playClickSound(activity)
                                            speed = tmpSpeed
                                        }
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
                                        val tmpSpeed = speed.next()
                                        if (tmpSpeed == speed) {
                                            playCloseSound(activity)
                                        } else {
                                            playClickSound(activity)
                                            speed = tmpSpeed
                                        }
                                    },
                                TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        TextFallout(
            stringResource(R.string.save),
            getTextColor(activity),
            getTextStrokeColor(activity),
            24.sp,
            Alignment.Center,
            Modifier
                .background(getTextBackgroundColor(activity))
                .clickableCancel(activity) {
                    setSpeed(speed)
                    goBack()
                }
                .padding(8.dp),
            TextAlign.Center
        )
    }
}
