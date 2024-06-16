package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableCancel
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.getBackByStyle
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getMusicPanelColorByStyle
import com.unicorns.invisible.caravan.utils.getStrokeColorByStyle
import com.unicorns.invisible.caravan.utils.getTextBackByStyle
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playPimpBoySound
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowSettings(
    activity: MainActivity,
    getStyle: () -> Style,
    selectStyle: (Int) -> Unit,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit
) {
    var styleInt by rememberSaveable { mutableStateOf(getStyle()) }
    val mainState = rememberLazyListState()
    LaunchedEffect(styleInt) {}
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
                .fillMaxHeight(0.9f)
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
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        TextFallout(
                            "Themes",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            32.sp,
                            Alignment.Center,
                            Modifier.fillMaxWidth(0.5f),
                            TextAlign.Center
                        )
                        TextFallout(
                            "CAPS: ${activity.save!!.caps}",
                            getTextColor(activity),
                            getTextStrokeColor(activity),
                            24.sp,
                            Alignment.Center,
                            Modifier.fillMaxWidth(),
                            TextAlign.Center
                        )
                    }

                    Style.entries.forEach { style ->
                        ShowStyle(activity, style,
                            style in activity.save!!.ownedStyles,
                            style.ordinal == activity.save!!.styleId
                        ) {
                            if (style !in activity.save!!.ownedStyles) {
                                if (activity.save!!.caps >= style.price) {
                                    activity.save!!.ownedStyles.add(style)
                                    activity.save!!.caps -= style.price
                                    styleInt = style
                                    selectStyle(style.ordinal)
                                    showAlertDialog("Transaction succeeded!", "You have bought ${style.styleName} style!")
                                    playPimpBoySound(activity)
                                } else {
                                    showAlertDialog("Transaction failed!", "Not enough caps!")
                                }
                            } else if (style.ordinal != activity.save!!.styleId) {
                                styleInt = style
                                selectStyle(style.ordinal)
                            }
                        }
                    }
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
fun ShowStyle(
    activity: MainActivity,
    style: Style,
    isStyleBought: Boolean,
    isStyleUsed: Boolean,
    onClick: (Int) -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        TextFallout(
            style.styleName,
            getTextColorByStyle(activity, style),
            getStrokeColorByStyle(activity, style),
            18.sp,
            Alignment.CenterStart,
            Modifier.fillMaxWidth(0.66f).padding(horizontal = 4.dp).background(getBackByStyle(activity, style)).padding(4.dp),
            TextAlign.Start
        )
        TextFallout(
            if (isStyleBought) (if (isStyleUsed) "OK" else "SELECT") else "BUY",
            getTextColorByStyle(activity, style),
            getTextColorByStyle(activity, style),
            16.sp,
            Alignment.Center,
            Modifier.fillMaxWidth(0.5f)
                .background(getMusicPanelColorByStyle(activity, style))
                .padding(6.dp)
                .background(getTextBackByStyle(activity, style))
                .clickableOk(activity) {
                    onClick(style.ordinal)
                },
            TextAlign.Center
        )

        if (!isStyleBought) {
            TextFallout(
                style.price.toString(),
                getTextColorByStyle(activity, style),
                getStrokeColorByStyle(activity, style),
                18.sp,
                Alignment.Center,
                Modifier.fillMaxWidth()
                    .background(getBackByStyle(activity, style))
                    .padding(6.dp)
                    .background(getTextBackByStyle(activity, style)),
                TextAlign.Center
            )
        }
    }
}