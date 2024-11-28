package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.unicorns.invisible.caravan.utils.getTextColorByStyle
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowStyles(
    activity: MainActivity,
    selectStyle: (Int) -> Unit,
    goBack: () -> Unit
) {
    var styleInt by rememberSaveable { mutableIntStateOf(save.styleId) }
    val mainState = rememberLazyListState()

    key(styleInt) {
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
                    Style.entries.forEach { style ->
                        ShowStyle(activity, style, style.ordinal == styleInt) {
                            if (styleInt != style.ordinal) {
                                styleInt = style.ordinal
                                selectStyle(style.ordinal)
                            }
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
    isStyleUsed: Boolean,
    onClick: (Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
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
        if (style in save.ownedStyles) {
            TextFallout(
                if (isStyleUsed) "OK" else stringResource(R.string.select),
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
        } else {
            Box(Modifier.fillMaxWidth(0.5f)) {}
        }
    }
}