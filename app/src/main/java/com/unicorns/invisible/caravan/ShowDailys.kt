package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playDailyCompleted
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowDailys(
    activity: MainActivity,
    goBack: () -> Unit
) {
    val mainState = rememberLazyListState()
    var updateKey by remember { mutableStateOf(false) }

    MenuItemOpen(activity, stringResource(R.string.missions), "<-", goBack) {
        key(updateKey) {
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
                mainState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(Modifier.height(16.dp))
                    save.challengesNew.forEach { challenge ->
                        ShowChallenge(activity, challenge, challenge.isCompleted(), false) {
                            updateKey = !updateKey
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                    save.challengesInf.forEach { challenge ->
                        ShowChallenge(activity, challenge, challenge.isCompleted(), true) {
                            updateKey = !updateKey
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                    HorizontalDivider(color = getDividerColor(activity))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextFallout(
                            stringResource(R.string.achievements),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            18.sp,
                            Modifier
                                .fillMaxWidth(0.66f)
                                .padding(horizontal = 8.dp)
                                .background(getTextBackgroundColor(activity))
                                .padding(4.dp)
                                .clickableOk(activity) {
                                    activity.achievementsClient?.achievementsIntent?.let {
                                        activity.openAchievements(it)
                                    }
                                },
                            boxAlignment = Alignment.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShowChallenge(activity: MainActivity, challenge: Challenge, isCompleted: Boolean, isInfinite: Boolean, updater: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        Row {
            TextFallout(
                challenge.getName(activity),
                getTextColor(activity),
                getTextStrokeColor(activity),
                24.sp,
                Modifier.fillMaxWidth(0.75f),
                boxAlignment = Alignment.CenterStart
            )
            TextFallout(
                challenge.getProgress(),
                getTextColor(activity),
                getTextStrokeColor(activity),
                24.sp,
                Modifier.fillMaxWidth(),
                boxAlignment = Alignment.CenterEnd
            )
        }

        TextFallout(
            challenge.getDescription(activity),
            getTextColor(activity),
            getTextStrokeColor(activity),
            16.sp,
            Modifier.fillMaxWidth(),
            textAlignment = TextAlign.Start,
            boxAlignment = Alignment.CenterStart
        )

        if (isCompleted) {
            fun dailyCompleted(save: Save) {
                if (isInfinite) {
                    challenge.restartChallenge()
                    saveData(activity)
                    playDailyCompleted(activity)
                    updater()
                } else {
                    save.challengesNew.remove(challenge)
                    saveData(activity)
                    playDailyCompleted(activity)
                    // TODO: redo this achievement!!
                    // TODO: redo all achievements!!
                    if (save.challengesNew.size <= 1) {
                        activity.achievementsClient?.unlock(activity.getString(R.string.achievement_done_for_today))
                    }
                    updater()
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                TextFallout(
                    activity.getString(R.string.claim_tickets),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Modifier
                        .background(getTextBackgroundColor(activity))
                        .clickableOk(activity) {
                            save.tickets += if (isInfinite) 1 else 2
                            dailyCompleted(save)
                        }
                        .padding(8.dp),
                )
                TextFallout(
                    activity.getString(R.string.claim_caps),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    20.sp,
                    Modifier
                        .background(getTextBackgroundColor(activity))
                        .clickableOk(activity) {
                            save.capsInHand += 30
                            dailyCompleted(save)
                        }
                        .padding(8.dp),
                )
            }
        }
    }
}