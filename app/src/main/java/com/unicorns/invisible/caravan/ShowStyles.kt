package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
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
import com.unicorns.invisible.caravan.utils.MenuItemOpen
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.clickableOk
import com.unicorns.invisible.caravan.utils.getBackByStyle
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getMusicPanelColorByStyle
import com.unicorns.invisible.caravan.utils.getStrokeColorByStyle
import com.unicorns.invisible.caravan.utils.getTextBackByStyle
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.playPimpBoySound
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowStyles(
    activity: MainActivity,
    getStyle: () -> Style,
    selectStyle: (Int) -> Unit,
    showAlertDialog: (String, String) -> Unit,
    goBack: () -> Unit
) {
    var styleInt by rememberSaveable { mutableStateOf(getStyle()) }
    val mainState = rememberLazyListState()
    LaunchedEffect(styleInt) {}

    MenuItemOpen(activity, stringResource(R.string.themes), "<-", goBack) {
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                TextFallout(
                    stringResource(R.string.caps, activity.save?.caps ?: 0),
                    getTextColor(activity),
                    getTextStrokeColor(activity),
                    24.sp,
                    Alignment.Center,
                    Modifier.fillMaxWidth().padding(top = 8.dp),
                    TextAlign.Center
                )

                Style.entries.forEach { style ->
                    if (style == Style.ENCLAVE && activity.save?.isEnclaveThemeAvailable != true) {
                        return@forEach
                    }
                    ShowStyle(
                        activity, style,
                        style in activity.save!!.ownedStyles,
                        style.ordinal == activity.save!!.styleId
                    ) {
                        if (style !in activity.save!!.ownedStyles) {
                            if (activity.save!!.caps >= style.price) {
                                activity.save!!.ownedStyles.add(style)
                                activity.save!!.caps -= style.price
                                styleInt = style
                                selectStyle(style.ordinal)
                                showAlertDialog(
                                    activity.getString(R.string.transaction_succeeded),
                                    activity.getString(
                                        R.string.you_have_bought_style,
                                        activity.getString(style.styleNameId)
                                    )
                                )
                                playPimpBoySound(activity)
                            } else {
                                showAlertDialog(
                                    activity.getString(R.string.transaction_failed),
                                    activity.getString(
                                        R.string.not_enough_caps
                                    )
                                )
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
}

@Composable
fun ShowStyle(
    activity: MainActivity,
    style: Style,
    isStyleBought: Boolean,
    isStyleUsed: Boolean,
    onClick: (Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextFallout(
            activity.getString(style.styleNameId),
            getTextColorByStyle(activity, style),
            getStrokeColorByStyle(activity, style),
            18.sp,
            Alignment.Center,
            Modifier
                .fillMaxWidth(0.5f)
                .padding(horizontal = 4.dp)
                .background(getBackByStyle(activity, style))
                .padding(4.dp),
            TextAlign.Center
        )
        TextFallout(
            when {
                !isStyleBought -> stringResource(R.string.buy)
                !isStyleUsed -> stringResource(R.string.select)
                else -> "OK"
            },
            getTextColorByStyle(activity, style),
            getTextColorByStyle(activity, style),
            22.sp,
            Alignment.Center,
            Modifier
                .fillMaxWidth(0.5f)
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
                22.sp,
                Alignment.Center,
                Modifier
                    .fillMaxWidth()
                    .background(getBackByStyle(activity, style))
                    .padding(6.dp)
                    .background(getTextBackByStyle(activity, style)),
                TextAlign.Center
            )
        }
    }
}