package com.unicorns.invisible.caravan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getKnobColor
import com.unicorns.invisible.caravan.utils.getTextBackgroundColor
import com.unicorns.invisible.caravan.utils.getTextColor
import com.unicorns.invisible.caravan.utils.getTrackColor
import com.unicorns.invisible.caravan.utils.scrollbar


@Composable
fun ShowRules(activity: MainActivity, goBack: () -> Unit) {
    val rules = stringResource(R.string.rules)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(getBackgroundColor(activity))
            .padding(horizontal = 12.dp)
    ) {
        val state = rememberLazyListState()
        LazyColumn(
            Modifier
                .fillMaxHeight(0.85f)
                .scrollbar(state, horizontal = false, knobColor = getKnobColor(activity), trackColor = getTrackColor(activity), padding = 4.dp), state = state) {
            item {
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    fontFamily = FontFamily(Font(R.font.monofont)),
                    text = rules,
                    style = TextStyle(color = getTextColor(activity), fontSize = 20.sp, textAlign = TextAlign.Center)
                )
            }
        }

        Spacer(modifier = Modifier.fillMaxHeight(0.2f))
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