package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getDividerColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getSelectionColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playSelectSound
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowRules(activity: MainActivity, goBack: () -> Unit) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val rules = stringResource(R.string.rules)
    // TODO: test Russian version!!
    MenuItemOpen(activity, stringResource(R.string.menu_rules), "<-", goBack) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(getBackgroundColor(activity))
                .scrollbar(
                    state,
                    horizontal = false,
                    knobColor = getKnobColor(activity),
                    trackColor = getTrackColor(activity),
                    padding = 4.dp
                ), state = state
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                    TabRow(
                        selectedTab, Modifier.fillMaxWidth(),
                        containerColor = getBackgroundColor(activity),
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = getSelectionColor(activity)
                                )
                            }
                        },
                        divider = {
                            HorizontalDivider(color = getDividerColor(activity))
                        }
                    ) {
                        Tab(
                            selectedTab == 0, { playSelectSound(activity); selectedTab = 0 },
                            selectedContentColor = getSelectionColor(activity),
                            unselectedContentColor = getTextBackgroundColor(activity)
                        ) {
                            TextFallout(
                                stringResource(R.string.better_rules),
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                16.sp,
                                Alignment.Center,
                                Modifier.padding(4.dp),
                                TextAlign.Center
                            )
                        }
                        Tab(
                            selectedTab == 1, { playSelectSound(activity); selectedTab = 1 },
                            selectedContentColor = getSelectionColor(activity),
                            unselectedContentColor = getTextBackgroundColor(activity)
                        ) {
                            TextFallout(
                                stringResource(R.string.og_rules),
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                16.sp,
                                Alignment.Center,
                                Modifier.padding(4.dp),
                                TextAlign.Center
                            )
                        }
                        Tab(
                            selectedTab == 2, { playSelectSound(activity); selectedTab = 2 },
                            selectedContentColor = getSelectionColor(activity),
                            unselectedContentColor = getTextBackgroundColor(activity)
                        ) {
                            TextFallout(
                                stringResource(R.string.faq),
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                16.sp,
                                Alignment.Center,
                                Modifier.padding(4.dp),
                                TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    when (selectedTab) {
                        0 -> {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))
                                TextFallout(
                                    stringResource(R.string.better_rules_body_1),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    20.sp,
                                    Alignment.CenterStart,
                                    Modifier.padding(horizontal = 8.dp),
                                    TextAlign.Start
                                )
                                Spacer(modifier = Modifier.height(32.dp))
                                TextFallout(
                                    stringResource(R.string.better_rules_body_2),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    20.sp,
                                    Alignment.CenterStart,
                                    Modifier.padding(horizontal = 8.dp),
                                    TextAlign.Start
                                )
                                Spacer(modifier = Modifier.height(32.dp))
                                TextFallout(
                                    stringResource(R.string.better_rules_body_3),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    20.sp,
                                    Alignment.CenterStart,
                                    Modifier.padding(horizontal = 8.dp),
                                    TextAlign.Start
                                )
                                Spacer(modifier = Modifier.height(32.dp))
                                TextFallout(
                                    stringResource(R.string.better_rules_body_4),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    20.sp,
                                    Alignment.CenterStart,
                                    Modifier.padding(horizontal = 8.dp),
                                    TextAlign.Start
                                )
                                Spacer(modifier = Modifier.height(32.dp))
                                TextFallout(
                                    stringResource(R.string.better_rules_body_5),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    20.sp,
                                    Alignment.CenterStart,
                                    Modifier.padding(horizontal = 8.dp),
                                    TextAlign.Start
                                )
                                Spacer(modifier = Modifier.height(32.dp))
                                TextFallout(
                                    stringResource(R.string.better_rules_body_6),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    20.sp,
                                    Alignment.CenterStart,
                                    Modifier.padding(horizontal = 8.dp),
                                    TextAlign.Start
                                )
                                // TODO: Wild wasteland rules!!!!!!!!
                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = getDividerColor(activity))
                                Spacer(modifier = Modifier.height(16.dp))
                                TextFallout(
                                    stringResource(R.string.better_rules_body_finale),
                                    getTextColor(activity),
                                    getTextStrokeColor(activity),
                                    20.sp,
                                    Alignment.CenterStart,
                                    Modifier.padding(horizontal = 8.dp),
                                    TextAlign.Start
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        1 -> {
                            TextFallout(
                                rules,
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                20.sp,
                                Alignment.Center,
                                Modifier.padding(horizontal = 8.dp),
                                TextAlign.Center
                            )
                        }
                        2 -> {
                            // TODO: !!!!
                            TextFallout(
                                stringResource(R.string.coming_soon),
                                getTextColor(activity),
                                getTextStrokeColor(activity),
                                20.sp,
                                Alignment.Center,
                                Modifier.padding(horizontal = 8.dp),
                                TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}