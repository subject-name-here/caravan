package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.utils.TextFallout
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTextStrokeColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowSettings(
    activity: MainActivity,
    getStyle: () -> Style,
    selectStyle: (Int) -> Unit,
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
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    TextFallout(
                        "Theme",
                        getTextColor(activity),
                        getTextStrokeColor(activity),
                        20.sp,
                        Alignment.CenterStart,
                        Modifier.fillMaxWidth(0.66f),
                        TextAlign.Start
                    )

                    Spacer(modifier = Modifier.width(16.dp))
                    // TODO!!! Extract also.
                    Text(
                        text = "TOGGLE!",
                        modifier = Modifier.background(getTextBackgroundColor(activity)).padding(4.dp).clickable {
                            styleInt = Style.entries[(styleInt.ordinal + 1) % Style.entries.size]
                            selectStyle(styleInt.ordinal)
                        },
                        fontFamily = FontFamily(Font(R.font.monofont)),
                        fontWeight = FontWeight.ExtraBold,
                        style = TextStyle(color = getTextColor(activity), fontSize = 20.sp, textAlign = TextAlign.Center)
                    )
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
                .clickable {
                    goBack()
                }
                .padding(8.dp),
            TextAlign.Center
        )
    }
}