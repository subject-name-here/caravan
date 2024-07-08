package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
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
import com.unicorns.invisible.caravan.model.challenge.Challenge
import com.unicorns.invisible.caravan.save.saveOnGD
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowDailys(
    activity: MainActivity,
    goBack: () -> Unit
) {
    val mainState = rememberLazyListState()
    var updateKey by remember { mutableStateOf(false) }
    LaunchedEffect(updateKey) {}
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            Modifier
                .fillMaxWidth().fillMaxHeight(0.8f)
                .scrollbar(
                    mainState,
                    horizontal = false,
                    knobColor = getKnobColor(activity),
                    trackColor = getTrackColor(activity)
                ),
            mainState,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                TextFallout(
                    "Daily missions!",
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    32.sp,
                    Alignment.Center,
                    Modifier.fillMaxWidth(),
                    TextAlign.Center
                )

                activity.save?.challenges?.forEach { challenge ->
                    ShowChallenge(activity, challenge, challenge.isCompleted()) {
                        updateKey = !updateKey
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
        TextFallout(
            stringResource(R.string.menu_back),
            getTextColor(activity),
            getTextStrokeColor(activity),
            24.sp,
            Alignment.Center,
            Modifier
                .background(getTextBackgroundColor(activity))
                .clickableCancel(activity) {
                    goBack()
                }
                .padding(8.dp),
            TextAlign.Center
        )
    }
}

@Composable
fun ShowChallenge(activity: MainActivity, challenge: Challenge, isCompleted: Boolean, updater: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Row {
            TextFallout(
                challenge.getName(activity),
                getTextColor(activity),
                getTextStrokeColor(activity),
                24.sp,
                Alignment.CenterStart,
                Modifier,
                TextAlign.Start
            )

            TextFallout(
                challenge.getProgress(),
                getTextColor(activity),
                getTextStrokeColor(activity),
                24.sp,
                Alignment.CenterEnd,
                Modifier.fillMaxWidth(),
                TextAlign.End
            )
        }

        TextFallout(
            challenge.getDescription(activity),
            getTextColor(activity),
            getTextStrokeColor(activity),
            16.sp,
            Alignment.CenterStart,
            Modifier.fillMaxWidth(),
            TextAlign.Start
        )

        if (isCompleted) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                TextFallout(
                    activity.getString(R.string.claim_tickets),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Alignment.Center,
                    Modifier
                        .background(getTextBackgroundColor(activity))
                        .clickableOk(activity) {
                            activity.save?.let { save ->
                                save.tickets++
                                save.challenges.remove(challenge)
                                saveOnGD(activity)
                                updater()
                            }
                        }
                        .padding(8.dp),
                    TextAlign.Center
                )
                TextFallout(
                    activity.getString(R.string.claim_caps),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Alignment.Center,
                    Modifier
                        .background(getTextBackgroundColor(activity))
                        .clickableOk(activity) {
                            activity.save?.let { save ->
                                save.caps += 50
                                save.challenges.remove(challenge)
                                saveOnGD(activity)
                                updater()
                            }
                        }
                        .padding(8.dp),
                    TextAlign.Center
                )
            }
        }
    }
}