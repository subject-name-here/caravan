package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun TowerScreen(
    activity: MainActivity,
    selectedDeck: () -> Pair<CardBack, Boolean>,
    showAlertDialog: (String, String) -> Unit,
    nextMode: () -> Unit,
    goBack: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .scrollbar(
                    state,
                    knobColor = getKnobColor(activity), trackColor = getTrackColor(activity),
                    horizontal = false,
                )
                .padding(end = 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            state = state
        ) {
            item {
                Box(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFallout(
                            stringResource(R.string.tower),
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            32.sp,
                            Alignment.Center,
                            Modifier,
                            TextAlign.Center
                        )
                        TextFallout(
                            "(?)",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            24.sp,
                            Alignment.Center,
                            Modifier
                                .background(getTextBackgroundColor(activity))
                                .clickableOk(activity) {
                                    showAlertDialog(
                                        activity.getString(R.string.tower_rules),
                                        activity.getString(R.string.tower_rules_body)
                                    )
                                }
                                .padding(4.dp),
                            TextAlign.Center
                        )
                    }
                    TextFallout(
                        ">>>",
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        24.sp,
                        Alignment.CenterEnd,
                        Modifier
                            .align(Alignment.CenterEnd)
                            .background(getTextBackgroundColor(activity))
                            .clickableOk(activity) {
                                nextMode()
                            }
                            .padding(4.dp),
                        TextAlign.End
                    )
                }
            }
        }
    }
}