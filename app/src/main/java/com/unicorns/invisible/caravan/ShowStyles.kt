package com.unicorns.invisible.caravan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.clickableSelect
import com.unicorns.invisible.caravan.utils.getBackByStyle
import com.unicorns.invisible.caravan.utils.getKnobColorByStyle
import com.unicorns.invisible.caravan.utils.getMusicPanelColorByStyle
import com.unicorns.invisible.caravan.utils.getSelectionColorByStyle
import com.unicorns.invisible.caravan.utils.getStrokeColorByStyle
import com.unicorns.invisible.caravan.utils.getTextBackByStyle
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColorByStyle
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowStyles(
    activity: MainActivity,
    selectStyle: (Int) -> Unit,
    goBack: () -> Unit
) {
    var styleInt by rememberSaveable { mutableIntStateOf(save.styleId) }
    var currentlyWatchedStyle by rememberSaveable { mutableIntStateOf(save.styleId) }

    @Composable
    fun ChangeWatchedStyle(text: String, operation: (Int) -> Int) {
        TextFallout(
            text,
            getTextColor(activity),
            getTextStrokeColor(activity),
            24.sp,
            Modifier
                .fillMaxWidth()
                .background(getTextBackgroundColor(activity))
                .clickableSelect(activity) {
                    currentlyWatchedStyle = operation(currentlyWatchedStyle)
                    if (currentlyWatchedStyle < 0) {
                        currentlyWatchedStyle = Style.entries.lastIndex
                    } else if (currentlyWatchedStyle > Style.entries.lastIndex) {
                        currentlyWatchedStyle = 0
                    }
                }
                .padding(4.dp),
        )
    }

    key(styleInt) {
        MenuItemOpen(activity, stringResource(R.string.themes), "<-", goBack) {
            key(currentlyWatchedStyle) {
                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    Column(
                        Modifier.fillMaxHeight().weight(0.1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(Modifier.fillMaxWidth().fillMaxHeight(0.1f)) {}
                        ChangeWatchedStyle("<", Int::dec)
                    }
                    val watchedStyle = Style.entries.getOrNull(currentlyWatchedStyle) ?: Style.PIP_BOY
                    Column(
                        Modifier.fillMaxHeight(0.8f).weight(0.8f)
                            .padding(8.dp)
                            .border(
                                BorderStroke(4.dp, getSelectionColorByStyle(activity, watchedStyle))
                            )
                            .padding(4.dp)
                    ) {
                        Row(Modifier
                            .fillMaxWidth().fillMaxHeight(0.1f)
                            .background(getMusicPanelColorByStyle(activity, watchedStyle))
                        ) {}

                        val state = rememberLazyListState()
                        LazyColumn(
                            Modifier
                                .fillMaxSize()
                                .background(getBackByStyle(activity, watchedStyle))
                                .scrollbar(
                                    state,
                                    knobColor = getKnobColorByStyle(activity, watchedStyle),
                                    trackColor = getTrackColorByStyle(activity, watchedStyle),
                                    horizontal = false,
                                ),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            state = state
                        ) {
                            item {
                                Column(
                                    Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceEvenly,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    TextFallout(
                                        stringResource(watchedStyle.styleNameId),
                                        getTextColorByStyle(activity, watchedStyle),
                                        getStrokeColorByStyle(activity, watchedStyle),
                                        24.sp,
                                        Modifier.padding(4.dp),
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    if (watchedStyle in save.ownedStyles) {
                                        TextFallout(
                                            stringResource(R.string.select),
                                            getTextColorByStyle(activity, watchedStyle),
                                            getStrokeColorByStyle(activity, watchedStyle),
                                            18.sp,
                                            Modifier
                                                .background(getTextBackByStyle(activity, watchedStyle))
                                                .clickableOk(activity) {
                                                    styleInt = watchedStyle.ordinal
                                                    selectStyle(watchedStyle.ordinal)
                                                }
                                                .padding(4.dp),
                                        )
                                    } else {
                                        TextFallout(
                                            stringResource(watchedStyle.conditionToOpenId),
                                            getTextColorByStyle(activity, watchedStyle),
                                            getStrokeColorByStyle(activity, watchedStyle),
                                            20.sp,
                                            Modifier.padding(4.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        Modifier.fillMaxHeight().weight(0.1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(Modifier.fillMaxWidth().fillMaxHeight(0.1f)) {}
                        ChangeWatchedStyle(">", Int::inc)
                    }
                }
            }
        }
    }
}