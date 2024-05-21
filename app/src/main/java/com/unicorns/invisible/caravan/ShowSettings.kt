package com.unicorns.invisible.caravan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ShowSettings(
    activity: MainActivity,
    getPinging: () -> Int,
    setPinging: (Int) -> Unit,
    goBack: () -> Unit
) {
    var isPinging by rememberSaveable { mutableStateOf(getPinging() != 0) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.fillMaxWidth(0.66f),
                text = "???",
                style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 20.sp, textAlign = TextAlign.Center)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Switch(checked = isPinging, onCheckedChange = {
                isPinging = it
                setPinging(if (it) 2 else 0)
            }, colors = SwitchColors(
                checkedThumbColor = colorResource(R.color.colorPrimaryDark),
                checkedTrackColor = colorResource(R.color.colorPrimary),
                checkedBorderColor = Color.Transparent,
                checkedIconColor = Color.Transparent,
                uncheckedThumbColor = colorResource(R.color.colorPrimary),
                uncheckedTrackColor = colorResource(R.color.colorAccent),
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


        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = stringResource(R.string.menu_back),
            modifier = Modifier.clickable {
                goBack()
            },
            style = TextStyle(color = Color(activity.getColor(R.color.colorPrimaryDark)), fontSize = 24.sp)
        )
    }
}