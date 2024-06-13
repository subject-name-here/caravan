package com.unicorns.invisible.caravan.utils

import androidx.compose.ui.graphics.Color
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style


fun getBackgroundColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.DESERT -> Color(activity.getColor(R.color.white))
        Style.PIP_BOY -> Color(activity.getColor(R.color.colorBack))
        Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.white))
        Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlPink))
        Style.OLD_WORLD -> TODO()
        Style.NEW_WORLD -> TODO()
        Style.SIERRA_MADRE -> TODO()
        Style.MADRE_ROJA -> TODO()
        Style.VAULT_21 -> TODO()
        Style.VAULT_22 -> TODO()
    }
}

fun getTextColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.DESERT -> Color(activity.getColor(R.color.colorPrimaryDark))
        Style.PIP_BOY -> Color(activity.getColor(R.color.colorText))
        Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaBlue))
        Style.PIP_GIRL -> Color(activity.getColor(R.color.white))
        Style.OLD_WORLD -> TODO()
        Style.NEW_WORLD -> TODO()
        Style.SIERRA_MADRE -> TODO()
        Style.MADRE_ROJA -> TODO()
        Style.VAULT_21 -> TODO()
        Style.VAULT_22 -> TODO()
    }
}

fun getTextBackgroundColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.DESERT -> Color(activity.getColor(R.color.colorPrimary))
        Style.PIP_BOY -> Color(activity.getColor(R.color.colorLightBack))
        Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaLightBlue))
        Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlBlue))
        Style.OLD_WORLD -> TODO()
        Style.NEW_WORLD -> TODO()
        Style.SIERRA_MADRE -> TODO()
        Style.MADRE_ROJA -> TODO()
        Style.VAULT_21 -> TODO()
        Style.VAULT_22 -> TODO()
    }
}

fun getDividerColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.DESERT -> Color(activity.getColor(R.color.colorPrimaryDark))
        Style.PIP_BOY -> Color(activity.getColor(R.color.colorText))
        Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaBlue))
        Style.PIP_GIRL -> Color(activity.getColor(R.color.white))
        Style.OLD_WORLD -> TODO()
        Style.NEW_WORLD -> TODO()
        Style.SIERRA_MADRE -> TODO()
        Style.MADRE_ROJA -> TODO()
        Style.VAULT_21 -> TODO()
        Style.VAULT_22 -> TODO()
    }
}

fun getCheckBoxBorderColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.DESERT -> Color(activity.getColor(R.color.colorPrimaryDark))
        Style.PIP_BOY -> Color(activity.getColor(R.color.colorText))
        Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaBlue))
        Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlBlue))
        Style.OLD_WORLD -> TODO()
        Style.NEW_WORLD -> TODO()
        Style.SIERRA_MADRE -> TODO()
        Style.MADRE_ROJA -> TODO()
        Style.VAULT_21 -> TODO()
        Style.VAULT_22 -> TODO()
    }
}

fun getTrackColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.DESERT -> Color(activity.getColor(R.color.colorPrimary))
        Style.PIP_BOY -> Color(activity.getColor(R.color.colorLightBack))
        Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaLightBlue))
        Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlBlue))
        Style.OLD_WORLD -> TODO()
        Style.NEW_WORLD -> TODO()
        Style.SIERRA_MADRE -> TODO()
        Style.MADRE_ROJA -> TODO()
        Style.VAULT_21 -> TODO()
        Style.VAULT_22 -> TODO()
    }
}

fun getKnobColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.DESERT -> Color(activity.getColor(R.color.colorPrimaryDark))
        Style.PIP_BOY -> Color(activity.getColor(R.color.colorText))
        Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaBlue))
        Style.PIP_GIRL -> Color(activity.getColor(R.color.white))
        Style.OLD_WORLD -> TODO()
        Style.NEW_WORLD -> TODO()
        Style.SIERRA_MADRE -> TODO()
        Style.MADRE_ROJA -> TODO()
        Style.VAULT_21 -> TODO()
        Style.VAULT_22 -> TODO()
    }
}

fun getAccentColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.DESERT -> Color(activity.getColor(R.color.colorAccent))
        Style.PIP_BOY -> Color(activity.getColor(R.color.colorText))
        Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.alaskaLightBlue))
        Style.PIP_GIRL -> Color(activity.getColor(R.color.pipGirlBlue))
        Style.OLD_WORLD -> TODO()
        Style.NEW_WORLD -> TODO()
        Style.SIERRA_MADRE -> TODO()
        Style.MADRE_ROJA -> TODO()
        Style.VAULT_21 -> TODO()
        Style.VAULT_22 -> TODO()
    }
}

fun getGameTextColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.DESERT -> Color(activity.getColor(R.color.colorAccent))
        Style.PIP_BOY -> Color(activity.getColor(R.color.colorText))
        Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.white))
        Style.PIP_GIRL -> Color(activity.getColor(R.color.white))
        Style.OLD_WORLD -> TODO()
        Style.NEW_WORLD -> TODO()
        Style.SIERRA_MADRE -> TODO()
        Style.MADRE_ROJA -> TODO()
        Style.VAULT_21 -> TODO()
        Style.VAULT_22 -> TODO()
    }
}

fun getGameTextBackgroundColor(activity: MainActivity): Color {
    return when (activity.styleId) {
        Style.DESERT -> Color(activity.getColor(R.color.colorPrimaryHalfTransparent))
        Style.PIP_BOY -> Color(activity.getColor(R.color.colorLightBackHalfTransparent))
        Style.ALASKA_FRONTIER -> Color(activity.getColor(R.color.whiteHalfTransparent))
        Style.PIP_GIRL -> Color(activity.getColor(R.color.whiteHalfTransparent))
        Style.OLD_WORLD -> TODO()
        Style.NEW_WORLD -> TODO()
        Style.SIERRA_MADRE -> TODO()
        Style.MADRE_ROJA -> TODO()
        Style.VAULT_21 -> TODO()
        Style.VAULT_22 -> TODO()
    }
}

fun getGrayTransparent(activity: MainActivity): Color {
    return Color(activity.getColor(R.color.grayHalfTransparent))
}
