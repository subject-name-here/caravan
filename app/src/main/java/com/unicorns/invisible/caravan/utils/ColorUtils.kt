package com.unicorns.invisible.caravan.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R


fun getBackgroundColor(activity: MainActivity): Color {
    return if (activity.styleId == 0) Color(activity.getColor(R.color.white)) else Color(activity.getColor(R.color.colorBack))
}

fun getTextColor(activity: MainActivity): Color {
    return if (activity.styleId == 0) Color(activity.getColor(R.color.colorPrimaryDark)) else Color(activity.getColor(R.color.colorText))
}

fun getTextBackgroundColor(activity: MainActivity): Color {
    return if (activity.styleId == 0) Color(activity.getColor(R.color.colorPrimary)) else Color(activity.getColor(R.color.colorLightBack))
}

fun getDividerColor(activity: MainActivity): Color {
    return if (activity.styleId == 0) Color(activity.getColor(R.color.colorPrimaryDark)) else Color(activity.getColor(R.color.colorText))
}

fun getCheckBoxBorderColor(activity: MainActivity): Color {
    return if (activity.styleId == 0) Color(activity.getColor(R.color.colorPrimaryDark)) else Color(activity.getColor(R.color.colorText))
}

fun getTrackColor(activity: MainActivity): Color {
    return if (activity.styleId == 0) Color(activity.getColor(R.color.colorPrimary)) else Color(activity.getColor(R.color.colorLightBack))
}

fun getKnobColor(activity: MainActivity): Color {
    return if (activity.styleId == 0) Color(activity.getColor(R.color.colorPrimaryDark)) else Color(activity.getColor(R.color.colorText))
}

fun getAccentColor(activity: MainActivity): Color {
    return if (activity.styleId == 0) Color(activity.getColor(R.color.colorAccent)) else Color(activity.getColor(R.color.colorText))
}


fun getGameBackgroundColor(activity: MainActivity): Color {
    return if (activity.styleId == 0) Color(activity.getColor(R.color.colorPrimaryDark)) else Color(activity.getColor(R.color.colorBack))
}

fun getGameTextColor(activity: MainActivity): Color {
    return if (activity.styleId == 0) Color(activity.getColor(R.color.colorAccent)) else Color(activity.getColor(R.color.colorText))
}

fun getGameTextBackgroundColor(activity: MainActivity): Color {
    return if (activity.styleId == 0) Color(activity.getColor(R.color.colorPrimaryHalfTransparent)) else Color(activity.getColor(R.color.colorLightBackHalfTransparent))
}
