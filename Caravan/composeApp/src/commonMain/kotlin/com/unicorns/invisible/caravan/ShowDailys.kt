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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.achievements
import caravan.composeapp.generated.resources.daily_missions
import caravan.composeapp.generated.resources.infinite_missions
import caravan.composeapp.generated.resources.missions
import caravan.composeapp.generated.resources.no_more_challenges
import caravan.composeapp.generated.resources.one_time_missions
import caravan.composeapp.generated.resources.one_time_missions_requiem
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
import org.jetbrains.compose.resources.stringResource


@Composable
fun ShowDailys(
    goBack: () -> Unit
) {
    var updateKey by remember { mutableStateOf(false) }

    MenuItemOpen(stringResource(Res.string.missions), "<-", Alignment.TopCenter, goBack) {
        key(updateKey) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(getBackgroundColor())
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
                        stringResource(Res.string.achievements),
                        getTextColor(),
                        getTextStrokeColor(),
                        18.sp,
                        Modifier
                            .fillMaxWidth(0.66f)
                            .padding(horizontal = 8.dp)
                            .background(getTextBackgroundColor())
                            .padding(4.dp)
                            .clickableOk { openAchievements() },
                        boxAlignment = Alignment.Center
                    )
                }
                Spacer(Modifier.height(16.dp))

                @Composable
                fun Challenges(typeName: String, challenges: List<Challenge>) {
                    HorizontalDivider(color = getDividerColor())
                    Spacer(Modifier.height(16.dp))
                    TextFallout(
                        typeName,
                        getTextColor(),
                        getTextStrokeColor(),
                        22.sp,
                        Modifier.fillMaxWidth(),
                        boxAlignment = Alignment.Center,
                        textAlignment = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (challenges.isEmpty()) {
                        TextFallout(
                            stringResource(Res.string.no_more_challenges),
                            getTextColor(),
                            getTextStrokeColor(),
                            18.sp,
                            Modifier.fillMaxWidth(),
                            boxAlignment = Alignment.Center,
                            textAlignment = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                    } else {
                        challenges.forEach { challenge ->
                            ShowChallenge(challenge, challenge.isCompleted()) {
                                updateKey = !updateKey
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }

                Challenges(stringResource(Res.string.daily_missions), saveGlobal.challengesDaily)
                Challenges(stringResource(Res.string.infinite_missions), saveGlobal.challengesInf)
                Challenges(stringResource(Res.string.one_time_missions), saveGlobal.challenges1)
                Challenges(stringResource(Res.string.one_time_missions_requiem), saveGlobal.challenges2)
            }
        }
    }
}

@Composable
fun ShowChallenge(challenge: Challenge, isCompleted: Boolean, updater: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        Row {
            TextFallout(
                stringResource(challenge.getName()) + " (${challenge.getXp()} XP)",
                getTextColor(),
                getTextStrokeColor(),
                22.sp,
                Modifier.fillMaxWidth(0.66f),
                boxAlignment = Alignment.CenterStart,
                textAlignment = TextAlign.Start
            )
            TextFallout(
                challenge.getProgress(),
                getTextColor(),
                getTextStrokeColor(),
                22.sp,
                Modifier.fillMaxWidth(),
                boxAlignment = Alignment.CenterEnd
            )
        }

        val descr by produceState("") {
            value = challenge.getDescription()
        }
        TextFallout(
            descr,
            getTextColor(),
            getTextStrokeColor(),
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
                    save.challengesDaily.remove(challenge)
                }
                save.challengesCompleted++
                save.increaseXp(challenge.getXp())
                saveData()
                playDailyCompleted()
                updater()
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                val reward by produceState(emptyList()) {
                    value = challenge.reward()
                }
                reward.forEach {
                    TextFallout(
                        it.first,
                        getTextColor(),
                        getTextStrokeColor(),
                        20.sp,
                        Modifier
                            .background(getTextBackgroundColor())
                            .clickableOk {
                                it.second()
                                dailyCompleted(saveGlobal)
                            }
                            .padding(8.dp),
                    )
                }
            }
        }
    }
}