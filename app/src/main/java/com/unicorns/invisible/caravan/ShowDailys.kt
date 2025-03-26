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
import com.unicorns.invisible.caravan.model.challenge.ChallengeInfinite
import com.unicorns.invisible.caravan.save.Save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.playDailyCompleted


@Composable
fun ShowDailys(
    activity: MainActivity,
    goBack: () -> Unit
) {
    var updateKey by remember { mutableStateOf(false) }

    MenuItemOpen(activity, stringResource(R.string.missions), "<-", goBack) {
        key(updateKey) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(getBackgroundColor(activity))
                    .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                Spacer(Modifier.height(16.dp))

                @Composable
                fun Challenges(typeName: String, challenges: List<Challenge>) {
                    HorizontalDivider(color = getDividerColor(activity))
                    Spacer(Modifier.height(16.dp))
                    TextFallout(
                        typeName,
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        22.sp,
                        Modifier.fillMaxWidth(),
                        boxAlignment = Alignment.Center,
                        textAlignment = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (challenges.isEmpty()) {
                        TextFallout(
                            stringResource(R.string.no_more_challenges),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            18.sp,
                            Modifier.fillMaxWidth(),
                            boxAlignment = Alignment.Center,
                            textAlignment = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                    } else {
                        challenges.forEach { challenge ->
                            ShowChallenge(activity, challenge, challenge.isCompleted()) {
                                updateKey = !updateKey
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }

                Challenges(stringResource(R.string.daily_missions), save.challengesNew)
                Challenges(stringResource(R.string.infinite_missions), save.challengesInf)
                Challenges(stringResource(R.string.one_time_missions), save.challenges1)
                Challenges(stringResource(R.string.one_time_missions_requiem), save.challenges2)
            }
        }
    }
}

@Composable
fun ShowChallenge(activity: MainActivity, challenge: Challenge, isCompleted: Boolean, updater: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        Row {
            TextFallout(
                challenge.getName(activity),
                getTextColor(activity),
                getTextStrokeColor(activity),
                22.sp,
                Modifier.fillMaxWidth(0.66f),
                boxAlignment = Alignment.CenterStart,
                textAlignment = TextAlign.Start
            )
            TextFallout(
                challenge.getProgress(),
                getTextColor(activity),
                getTextStrokeColor(activity),
                22.sp,
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
                if (challenge is ChallengeInfinite) {
                    challenge.restartChallenge()
                } else {
                    save.challengesNew.remove(challenge)
                }
                saveData(activity)
                playDailyCompleted(activity)
                updater()
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                challenge.reward(activity).forEach {
                    TextFallout(
                        it.first,
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        20.sp,
                        Modifier
                            .background(getTextBackgroundColor(activity))
                            .clickableOk(activity) {
                                it.second()
                                dailyCompleted(save)
                            }
                            .padding(8.dp),
                    )
                }
            }
        }
    }
}