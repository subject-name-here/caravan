package com.unicorns.invisible.caravan.utils

import androidx.compose.ui.graphics.Color
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style


fun getBackgroundColor(activity: MainActivity): Color {
    return getBackByStyle(activity, activity.styleId)
}
fun getBackByStyle(activity: MainActivity, styleId: Style) = when (styleId) {
    Style.DESERT -> Color(activity.getColor(R.color.desertBackground))
    Style.PIP_BOY -> Color(activity.getColor(R.color.colorBack))
    Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaWhite))
    Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlBlue))
    Style.OLD_WORLD -> Color(activity.getColor(R.color.oldWorldBack))
    Style.NEW_WORLD -> Color(activity.getColor(R.color.newWorldBack))
    Style.SIERRA_MADRE -> Color(activity.getColor(R.color.sierraMadreBack))
    Style.MADRE_ROJA -> Color(activity.getColor(R.color.madreRojaBack))
    Style.VAULT_21 -> Color(activity.getColor(R.color.vault21Back))
    Style.VAULT_22 -> Color(activity.getColor(R.color.vault22Back))
}

fun getTextColor(activity: MainActivity): Color {
    return getTextColorByStyle(activity, activity.styleId)
}
fun getTextColorByStyle(activity: MainActivity, styleId: Style) = when (styleId) {
    Style.DESERT -> Color(activity.getColor(R.color.desertText))
    Style.PIP_BOY -> Color(activity.getColor(R.color.colorText))
    Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaBlue))
    Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlBlack))
    Style.OLD_WORLD -> Color(activity.getColor(R.color.oldWorldText))
    Style.NEW_WORLD -> Color(activity.getColor(R.color.newWorldText))
    Style.SIERRA_MADRE -> Color(activity.getColor(R.color.sierraMadreText))
    Style.MADRE_ROJA -> Color(activity.getColor(R.color.madreRojaText))
    Style.VAULT_21 -> Color(activity.getColor(R.color.vault21Text))
    Style.VAULT_22 -> Color(activity.getColor(R.color.vault22Text))
}

fun getTextStrokeColor(activity: MainActivity): Color {
    return getStrokeColorByStyle(activity, activity.styleId)
}
fun getStrokeColorByStyle(activity: MainActivity, styleId: Style) = when (styleId) {
    Style.DESERT -> getTextColor(activity)
    Style.PIP_BOY -> Color(activity.getColor(R.color.colorTextStroke))
    Style.ALASKA_FRONTIER -> getTextColor(activity)
    Style.PIP_GIRL -> getTextColor(activity)
    Style.OLD_WORLD -> getTextColor(activity)
    Style.NEW_WORLD -> Color(activity.getColor(R.color.newWorldStroke))
    Style.SIERRA_MADRE -> getTextColor(activity)
    Style.MADRE_ROJA -> getTextColor(activity)
    Style.VAULT_21 -> Color(activity.getColor(R.color.vault21Stroke))
    Style.VAULT_22 -> Color(activity.getColor(R.color.vault22Stroke))
}

fun getTextBackgroundColor(activity: MainActivity): Color {
    return getTextBackByStyle(activity, activity.styleId)
}
fun getTextBackByStyle(activity: MainActivity, styleId: Style) = when (styleId) {
    Style.DESERT -> Color(activity.getColor(R.color.desertTextBackground))
    Style.PIP_BOY -> Color(activity.getColor(R.color.colorTextBack))
    Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaLightBlue))
    Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlWhite))
    Style.OLD_WORLD -> Color(activity.getColor(R.color.oldWorldTextBack))
    Style.NEW_WORLD -> Color(activity.getColor(R.color.newWorldTextBack))
    Style.SIERRA_MADRE -> Color(activity.getColor(R.color.sierraMadreTextBack))
    Style.MADRE_ROJA -> Color(activity.getColor(R.color.madreRojaTextBack))
    Style.VAULT_21 -> Color(activity.getColor(R.color.vault21TextBack))
    Style.VAULT_22 -> Color(activity.getColor(R.color.vault22TextBack))
}

fun getSelectionColor(activity: MainActivity): Color {
    return getSelectionColorByStyle(activity, activity.styleId)
}
fun getSelectionColorByStyle(activity: MainActivity, styleId: Style) = when (styleId) {
    Style.DESERT -> Color(activity.getColor(R.color.desertTextBackground))
    Style.PIP_BOY -> Color(activity.getColor(R.color.colorTextStroke))
    Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaBlue))
    Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlBlack))
    Style.OLD_WORLD -> Color(activity.getColor(R.color.oldWorldText))
    Style.NEW_WORLD -> Color(activity.getColor(R.color.newWorldStroke))
    Style.SIERRA_MADRE -> Color(activity.getColor(R.color.sierraMadreAccent))
    Style.MADRE_ROJA -> Color(activity.getColor(R.color.madreRojaAccent))
    Style.VAULT_21 -> Color(activity.getColor(R.color.vault21Accent))
    Style.VAULT_22 -> Color(activity.getColor(R.color.vault22Stroke))
}

fun getMusicPanelColor(activity: MainActivity): Color {
    return getMusicPanelColorByStyle(activity, activity.styleId)
}
fun getMusicPanelColorByStyle(activity: MainActivity, styleId: Style) = when (styleId) {
    Style.DESERT -> Color(activity.getColor(R.color.desertAccent))
    Style.PIP_BOY -> Color(activity.getColor(R.color.colorText))
    Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaBlue))
    Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlPink))
    Style.OLD_WORLD -> Color(activity.getColor(R.color.oldWorldTextBack2))
    Style.NEW_WORLD -> Color(activity.getColor(R.color.newWorldAccent))
    Style.SIERRA_MADRE -> Color(activity.getColor(R.color.sierraMadreAccent))
    Style.MADRE_ROJA -> Color(activity.getColor(R.color.madreRojaAccent))
    Style.VAULT_21 -> Color(activity.getColor(R.color.vault21Accent))
    Style.VAULT_22 -> Color(activity.getColor(R.color.vault22Accent))
}
fun getMusicTextColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.DESERT -> getTextColor(activity)
        Style.PIP_BOY -> Color(activity.getColor(R.color.colorText))
        Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaYellow))
        Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlBlack))
        Style.OLD_WORLD -> Color(activity.getColor(R.color.oldWorldText))
        Style.NEW_WORLD -> Color(activity.getColor(R.color.newWorldAccent))
        Style.SIERRA_MADRE -> Color(activity.getColor(R.color.sierraMadreText))
        Style.MADRE_ROJA -> Color(activity.getColor(R.color.madreRojaText))
        Style.VAULT_21 -> Color(activity.getColor(R.color.vault21Text))
        Style.VAULT_22 -> Color(activity.getColor(R.color.vault22Text))
    }
}
fun getDialogBackground(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.DESERT -> Color(activity.getColor(R.color.desertAccent))
        Style.PIP_BOY -> Color(activity.getColor(R.color.colorTextBack))
        Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaWhite))
        Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlWhite))
        Style.OLD_WORLD -> Color(activity.getColor(R.color.oldWorldTextBack2))
        Style.NEW_WORLD -> Color(activity.getColor(R.color.newWorldBack))
        Style.SIERRA_MADRE -> Color(activity.getColor(R.color.sierraMadreAccent))
        Style.MADRE_ROJA -> Color(activity.getColor(R.color.madreRojaAccent))
        Style.VAULT_21 -> Color(activity.getColor(R.color.vault21Accent))
        Style.VAULT_22 -> Color(activity.getColor(R.color.vault22Accent))
    }
}
fun getDialogTextColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.SIERRA_MADRE -> getBackgroundColor(activity)
        Style.NEW_WORLD -> Color(activity.getColor(R.color.newWorldTextBack))
        else -> getTextColor(activity)
    }
}

fun getDividerColor(activity: MainActivity): Color = getTextColor(activity)
fun getGameDividerColor(activity: MainActivity): Color = when (activity.styleId) {
    Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaYellow))
    Style.NEW_WORLD -> Color(activity.getColor(R.color.newWorldAccent))
    else -> getTextColor(activity)
}
fun getCheckBoxBorderColor(activity: MainActivity): Color = when (activity.styleId) {
    Style.VAULT_21 -> Color(activity.getColor(R.color.vault21Stroke))
    else -> getSelectionColor(activity)
}
fun getCheckBoxFillColor(activity: MainActivity): Color = when (activity.styleId) {
    Style.DESERT -> Color(activity.getColor(R.color.desertText))
    Style.PIP_BOY -> Color(activity.getColor(R.color.colorTextBack))
    Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaYellow))
    Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlPink))
    Style.OLD_WORLD -> getBackgroundColor(activity)
    Style.NEW_WORLD -> Color(activity.getColor(R.color.newWorldText))
    Style.SIERRA_MADRE -> Color(activity.getColor(R.color.sierraMadreTextBack))
    Style.MADRE_ROJA -> Color(activity.getColor(R.color.madreRojaTextBack))
    Style.VAULT_21 -> Color(activity.getColor(R.color.vault21Accent))
    Style.VAULT_22 -> Color(activity.getColor(R.color.vault22Accent))
}
fun getTrackColor(activity: MainActivity): Color = getTextBackgroundColor(activity)
fun getKnobColor(activity: MainActivity): Color = getTextColor(activity)

fun getSwitchTrackColor(activity: MainActivity): Color = when (activity.styleId) {
    Style.ALASKA_FRONTIER -> getTextColor(activity)
    Style.NEW_WORLD -> getTextBackgroundColor(activity)
    else -> getBackgroundColor(activity)
}
fun getSwitchThumbColor(activity: MainActivity): Color = when (activity.styleId) {
    Style.DESERT -> Color(activity.getColor(R.color.desertText))
    Style.PIP_BOY -> Color(activity.getColor(R.color.colorTextStroke))
    Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaYellow))
    Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlPink))
    Style.OLD_WORLD -> Color(activity.getColor(R.color.oldWorldText))
    Style.NEW_WORLD -> Color(activity.getColor(R.color.newWorldStroke))
    Style.SIERRA_MADRE -> Color(activity.getColor(R.color.sierraMadreTextBack))
    Style.MADRE_ROJA -> Color(activity.getColor(R.color.madreRojaText))
    Style.VAULT_21 -> Color(activity.getColor(R.color.vault21Text))
    Style.VAULT_22 -> Color(activity.getColor(R.color.vault22Stroke))
}
fun getSliderTrackColor(activity: MainActivity): Color = getSwitchTrackColor(activity)
fun getSliderThumbColor(activity: MainActivity): Color = getSwitchThumbColor(activity)

fun getGameScoreColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.PIP_BOY -> Color(activity.getColor(R.color.colorTextStroke))
        Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlWhite))
        Style.NEW_WORLD -> Color(activity.getColor(R.color.newWorldAccent))
        else -> getTextColor(activity)
    }
}

fun getGameSelectionColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.DESERT -> Color(activity.getColor(R.color.desertAccent))
        Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaYellow))
        else -> getTextColor(activity)
    }
}

fun getGrayTransparent(activity: MainActivity): Color {
    return Color(activity.getColor(R.color.grayHalfTransparent))
}
