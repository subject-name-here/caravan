package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.utils.getAccentColor
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowSettings(
    activity: MainActivity,
    getStyle: () -> Int,
    toggleStyle: () -> Unit,
    getIntro: () -> Boolean,
    toggleIntro: () -> Unit,
    goBack: () -> Unit
) {
    var styleInt by rememberSaveable { mutableIntStateOf(getStyle()) }
    var intro by rememberSaveable { mutableStateOf(getIntro()) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity))
            .padding(horizontal = 16.dp)
    ) {
        val mainState = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxHeight(0.9f).fillMaxWidth()
                .scrollbar(mainState, horizontal = false, knobColor = getKnobColor(activity), trackColor = getTrackColor(activity)),
            mainState
        ) {
            item {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.66f),
                        text = "Shtyle",
                        fontFamily = FontFamily(Font(R.font.monofont)),
                        style = TextStyle(color = getTextColor(activity), fontSize = 20.sp, textAlign = TextAlign.Center)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(checked = styleInt == 0, onCheckedChange = {
                        styleInt = 1 - styleInt
                        toggleStyle()
                    }, colors = SwitchColors(
                        checkedThumbColor = getAccentColor(activity),
                        checkedTrackColor = getTextBackgroundColor(activity),
                        checkedBorderColor = Color.Transparent,
                        checkedIconColor = Color.Transparent,
                        uncheckedThumbColor = getAccentColor(activity),
                        uncheckedTrackColor = getTextBackgroundColor(activity),
                        uncheckedBorderColor = Color.Transparent,
                        uncheckedIconColor = Color.Transparent,
                        disabledCheckedThumbColor = colorResource(R.color.red),
                        disabledCheckedTrackColor = colorResource(R.color.white),
                        disabledCheckedBorderColor = Color.Transparent,
                        disabledCheckedIconColor = Color.Transparent,
                        disabledUncheckedThumbColor = colorResource(R.color.red),
                        disabledUncheckedTrackColor = colorResource(R.color.white),
                        disabledUncheckedBorderColor = Color.Transparent,
                        disabledUncheckedIconColor = Color.Transparent,
                    ))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.66f),
                        text = "Intro",
                        fontFamily = FontFamily(Font(R.font.monofont)),
                        style = TextStyle(color = getTextColor(activity), fontSize = 20.sp, textAlign = TextAlign.Center)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Switch(checked = intro, onCheckedChange = {
                        intro = !intro
                        toggleIntro()
                    }, colors = SwitchColors(
                        checkedThumbColor = getAccentColor(activity),
                        checkedTrackColor = getTextBackgroundColor(activity),
                        checkedBorderColor = Color.Transparent,
                        checkedIconColor = Color.Transparent,
                        uncheckedThumbColor = getAccentColor(activity),
                        uncheckedTrackColor = getTextBackgroundColor(activity),
                        uncheckedBorderColor = Color.Transparent,
                        uncheckedIconColor = Color.Transparent,
                        disabledCheckedThumbColor = colorResource(R.color.red),
                        disabledCheckedTrackColor = colorResource(R.color.white),
                        disabledCheckedBorderColor = Color.Transparent,
                        disabledCheckedIconColor = Color.Transparent,
                        disabledUncheckedThumbColor = colorResource(R.color.red),
                        disabledUncheckedTrackColor = colorResource(R.color.white),
                        disabledUncheckedBorderColor = Color.Transparent,
                        disabledUncheckedIconColor = Color.Transparent,
                    ))
                }
            }
        }

        Text(
            text = stringResource(R.string.menu_back),
            modifier = Modifier.clickable {
                goBack()
            }.background(getTextBackgroundColor(activity)).padding(8.dp),
            fontFamily = FontFamily(Font(R.font.monofont)),
            style = TextStyle(color = getTextColor(activity), fontSize = 24.sp)
        )
    }
}