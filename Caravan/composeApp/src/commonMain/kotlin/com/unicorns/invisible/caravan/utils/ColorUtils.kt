package com.unicorns.invisible.caravan.utils

import androidx.compose.ui.graphics.Color
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.color.Colors
import com.unicorns.invisible.caravan.styleId


// TODO: test it all

fun getBackgroundColor(): Color {
    return getBackByStyle(styleId)
}
fun getBackByStyle(styleId: Style) = when (styleId) {
    Style.DESERT -> Colors.DesertBack
    Style.PIP_BOY -> Colors.ColorBack
    Style.ALASKA_FRONTIER -> Colors.AlaskaWhite
    Style.PIP_GIRL -> Colors.PipGirlBlue
    Style.OLD_WORLD -> Colors.OldWorldBack
    Style.NEW_WORLD -> Colors.NewWorldBack
    Style.SIERRA_MADRE -> Colors.SierraMadreBack
    Style.MADRE_ROJA -> Colors.MadreRojaBack
    Style.VAULT_21 -> Colors.Vault21Back
    Style.VAULT_22 -> Colors.Vault22Back
    Style.ENCLAVE -> Colors.EnclaveBack
    Style.BLACK -> Color.Black
    Style.NCR -> Colors.NcrBack
    Style.LEGION -> Colors.LegionBack
}

fun getTextColor(): Color {
    return getTextColorByStyle(styleId)
}
fun getTextColorByStyle(styleId: Style) = when (styleId) {
    Style.DESERT -> Colors.DesertText
    Style.PIP_BOY -> Colors.ColorText
    Style.ALASKA_FRONTIER -> Colors.AlaskaBlue
    Style.PIP_GIRL -> Colors.PipGirlBlack
    Style.OLD_WORLD -> Colors.OldWorldText
    Style.NEW_WORLD -> Colors.NewWorldText
    Style.SIERRA_MADRE -> Colors.SierraMadreText
    Style.MADRE_ROJA -> Colors.MadreRojaText
    Style.VAULT_21 -> Colors.Vault21Text
    Style.VAULT_22 -> Colors.Vault22Text
    Style.ENCLAVE -> Colors.EnclaveText
    Style.BLACK -> Color.White
    Style.NCR -> Colors.NcrText
    Style.LEGION -> Colors.LegionText
}

fun getTextStrokeColor(): Color {
    return getStrokeColorByStyle(styleId)
}
fun getStrokeColorByStyle(styleId: Style) = when (styleId) {
    Style.PIP_BOY -> Colors.ColorTextStroke
    Style.NEW_WORLD -> Colors.NewWorldStroke
    Style.VAULT_21 -> Colors.Vault21Stroke
    Style.VAULT_22 -> Colors.Vault22Stroke
    Style.ENCLAVE -> Colors.EnclaveStroke
    Style.LEGION -> Colors.LegionStroke
    else -> getTextColorByStyle(styleId)
}

fun getTextBackgroundColor(): Color {
    return getTextBackByStyle(styleId)
}
fun getTextBackByStyle(styleId: Style) = when (styleId) {
    Style.DESERT -> Colors.DesertTextBack
    Style.PIP_BOY -> Colors.ColorTextBack
    Style.ALASKA_FRONTIER -> Colors.AlaskaLightBlue
    Style.PIP_GIRL -> Colors.PipGirlWhite
    Style.OLD_WORLD -> Colors.OldWorldTextBack
    Style.NEW_WORLD -> Colors.NewWorldTextBack
    Style.SIERRA_MADRE -> Colors.SierraMadreTextBack
    Style.MADRE_ROJA -> Colors.MadreRojaTextBack
    Style.VAULT_21 -> Colors.Vault21TextBack
    Style.VAULT_22 -> Colors.Vault22TextBack
    Style.ENCLAVE -> Colors.EnclaveTextBack
    Style.BLACK -> Color.Gray
    Style.NCR -> Colors.NcrTextBack
    Style.LEGION -> Colors.LegionTextBack
}

fun getSelectionColor(): Color {
    return getSelectionColorByStyle(styleId)
}
fun getSelectionColorByStyle(styleId: Style) = when (styleId) {
    Style.DESERT -> Colors.DesertTextBack
    Style.PIP_BOY -> Colors.ColorTextStroke
    Style.ALASKA_FRONTIER -> Colors.AlaskaYellow
    Style.PIP_GIRL -> Colors.PipGirlBlack
    Style.OLD_WORLD -> Colors.OldWorldText
    Style.NEW_WORLD -> Colors.NewWorldStroke
    Style.SIERRA_MADRE -> Colors.SierraMadreAccent
    Style.MADRE_ROJA -> Colors.MadreRojaAccent
    Style.VAULT_21 -> Colors.Vault21Accent
    Style.VAULT_22 -> Colors.Vault22Stroke
    Style.ENCLAVE -> Colors.EnclaveAccent
    Style.BLACK -> Color.Gray
    Style.NCR -> Colors.NcrAccent
    Style.LEGION -> Colors.LegionAccent
}

fun getMusicPanelColor(): Color {
    return getMusicPanelColorByStyle(styleId)
}
fun getMusicPanelColorByStyle(styleId: Style) = when (styleId) {
    Style.DESERT -> Colors.DesertAccent
    Style.PIP_BOY -> Colors.ColorText
    Style.ALASKA_FRONTIER -> Colors.AlaskaBlue
    Style.PIP_GIRL -> Colors.PipGirlPink
    Style.OLD_WORLD -> Colors.OldWorldTextBack2
    Style.NEW_WORLD -> Colors.NewWorldAccent
    Style.SIERRA_MADRE -> Colors.SierraMadreText
    Style.MADRE_ROJA -> Colors.MadreRojaText
    Style.VAULT_21 -> Colors.Vault21Stroke
    Style.VAULT_22 -> Colors.Vault22Accent
    Style.ENCLAVE -> Colors.EnclaveText
    Style.BLACK -> Color.White
    Style.NCR -> Colors.NcrText
    Style.LEGION -> Colors.LegionStroke
}

fun getMusicTextColor(): Color {
    return when (styleId) {
        Style.DESERT -> getTextColor()
        Style.PIP_BOY -> Colors.ColorText
        Style.ALASKA_FRONTIER -> Colors.AlaskaYellow
        Style.PIP_GIRL -> Colors.PipGirlBlack
        Style.OLD_WORLD -> Colors.OldWorldText
        Style.NEW_WORLD -> Colors.NewWorldText
        Style.SIERRA_MADRE -> Colors.SierraMadreText
        Style.MADRE_ROJA -> Colors.MadreRojaText
        Style.VAULT_21 -> Colors.Vault21Text
        Style.VAULT_22 -> Colors.Vault22Stroke
        Style.ENCLAVE -> Colors.EnclaveText
        Style.BLACK -> Color.White
        Style.NCR -> Colors.NcrText
        Style.LEGION -> Colors.LegionText
    }
}
fun getMusicTextBackColor(): Color {
    return when (styleId) {
        Style.PIP_BOY -> Colors.ColorTextBack
        Style.VAULT_22 -> Colors.Vault22Back
        Style.BLACK -> Color.Black
        Style.NCR -> Colors.NcrBack
        else -> getTextBackgroundColor()
    }
}


fun getMusicMarqueesColor(): Color {
    return when (styleId) {
        Style.DESERT -> Colors.DesertText
        Style.PIP_BOY -> Colors.ColorBack
        Style.ALASKA_FRONTIER -> Colors.AlaskaYellow
        Style.PIP_GIRL -> Colors.PipGirlBlack
        Style.OLD_WORLD -> Colors.OldWorldText
        Style.NEW_WORLD -> Colors.NewWorldText
        Style.SIERRA_MADRE -> Colors.SierraMadreTextBack
        Style.MADRE_ROJA -> Colors.MadreRojaTextBack
        Style.VAULT_21 -> Colors.Vault21Text
        Style.VAULT_22 -> Colors.Vault22Stroke
        Style.ENCLAVE -> Colors.EnclaveBack
        Style.BLACK -> Color.Black
        Style.NCR -> Colors.NcrBack
        Style.LEGION -> Colors.LegionText
    }
}

fun getDialogBackground(): Color {
    return when (styleId) {
        Style.DESERT -> Colors.DesertAccent
        Style.PIP_BOY -> Colors.ColorTextBack
        Style.ALASKA_FRONTIER -> Colors.AlaskaWhite
        Style.PIP_GIRL -> Colors.PipGirlWhite
        Style.OLD_WORLD -> Colors.OldWorldTextBack2
        Style.NEW_WORLD -> Colors.NewWorldBack
        Style.SIERRA_MADRE -> Colors.SierraMadreAccent
        Style.MADRE_ROJA -> Colors.MadreRojaAccent
        Style.VAULT_21 -> Colors.Vault21Accent
        Style.VAULT_22 -> Colors.Vault22Accent
        Style.ENCLAVE -> Colors.EnclaveTextBack
        Style.BLACK -> Color.Black
        Style.NCR -> Colors.NcrTextBack
        Style.LEGION -> Colors.LegionAccent
    }
}

fun getDialogTextColor(): Color {
    return when (styleId) {
        Style.SIERRA_MADRE -> getBackgroundColor()
        Style.NEW_WORLD -> Colors.NewWorldTextBack
        Style.NCR -> Colors.NcrText
        Style.LEGION -> Colors.LegionText
        else -> getTextColor()
    }
}

fun getDividerColor(): Color = getTextColor()
fun getDividerColorByStyle(style: Style): Color = getTextColorByStyle(style)

fun getCheckBoxBorderColor(): Color = getSelectionColor()

fun getCheckBoxFillColor(): Color = when (styleId) {
    Style.DESERT -> Colors.DesertText
    Style.PIP_BOY -> Colors.ColorTextBack
    Style.ALASKA_FRONTIER -> Colors.AlaskaLightBlue
    Style.PIP_GIRL -> Colors.PipGirlPink
    Style.OLD_WORLD -> Colors.OldWorldTextBack
    Style.NEW_WORLD -> Colors.NewWorldText
    Style.SIERRA_MADRE -> Colors.SierraMadreTextBack
    Style.MADRE_ROJA -> Colors.MadreRojaTextBack
    Style.VAULT_21 -> Colors.Vault21Stroke
    Style.VAULT_22 -> Colors.Vault22Accent
    Style.ENCLAVE -> Colors.EnclaveStroke
    Style.BLACK -> Color.White
    Style.NCR -> Colors.NcrText
    Style.LEGION -> Colors.LegionStroke
}

fun getTrackColor(): Color = getDividerColor()
fun getTrackColorByStyle(style: Style): Color = when (style) {
    Style.DESERT -> Colors.DesertAccent
    Style.PIP_BOY -> Colors.ColorTextBack
    Style.ALASKA_FRONTIER -> Colors.AlaskaYellow
    Style.PIP_GIRL -> Colors.PipGirlWhite
    Style.OLD_WORLD -> Colors.OldWorldTextBack
    Style.NEW_WORLD -> Colors.NewWorldStroke
    Style.SIERRA_MADRE -> Colors.SierraMadreTextBack
    Style.MADRE_ROJA -> Colors.MadreRojaTextBack
    Style.VAULT_21 -> Colors.Vault21Accent
    Style.VAULT_22 -> Colors.Vault22Accent
    Style.ENCLAVE -> Colors.EnclaveStroke
    Style.BLACK -> Color.Black
    Style.NCR -> Colors.NcrAccent
    Style.LEGION -> Colors.LegionTextBack
}
fun getKnobColor(): Color = getKnobColorByStyle(styleId)
fun getKnobColorByStyle(style: Style): Color = when (style) {
    Style.DESERT -> Colors.DesertAccent
    Style.PIP_BOY -> Colors.ColorTextStroke
    Style.ALASKA_FRONTIER -> Colors.AlaskaYellow
    Style.PIP_GIRL -> Colors.PipGirlWhite
    Style.OLD_WORLD -> Colors.OldWorldTextBack
    Style.NEW_WORLD -> Colors.NewWorldStroke
    Style.SIERRA_MADRE -> Colors.SierraMadreTextBack
    Style.MADRE_ROJA -> Colors.MadreRojaTextBack
    Style.VAULT_21 -> Colors.Vault21Accent
    Style.VAULT_22 -> Colors.Vault22Accent
    Style.ENCLAVE -> Colors.EnclaveStroke
    Style.BLACK -> Color.Black
    Style.NCR -> Colors.NcrAccent
    Style.LEGION -> Colors.LegionTextBack
}

fun getSwitchTrackColor(): Color = when (styleId) {
    Style.ALASKA_FRONTIER -> getTextColor()
    Style.NEW_WORLD -> getTextBackgroundColor()
    Style.BLACK -> getTextColor()
    else -> getBackgroundColor()
}
fun getSwitchThumbColor(): Color = when (styleId) {
    Style.DESERT -> Colors.DesertText
    Style.PIP_BOY -> Colors.ColorTextStroke
    Style.ALASKA_FRONTIER -> Colors.AlaskaYellow
    Style.PIP_GIRL -> Colors.PipGirlPink
    Style.OLD_WORLD -> Colors.OldWorldText
    Style.NEW_WORLD -> Colors.NewWorldStroke
    Style.SIERRA_MADRE -> Colors.SierraMadreTextBack
    Style.MADRE_ROJA -> Colors.MadreRojaText
    Style.VAULT_21 -> Colors.Vault21Text
    Style.VAULT_22 -> Colors.Vault22Stroke
    Style.ENCLAVE -> Colors.EnclaveText
    Style.BLACK -> Color.White
    Style.NCR -> Colors.NcrText
    Style.LEGION -> Colors.LegionTextBack
}

fun getSliderTrackColor(): Color = getSwitchTrackColor()
fun getSliderThumbColor(): Color = getSwitchThumbColor()

fun getGameScoreColor(): Color {
    return when (styleId) {
        Style.PIP_BOY -> Colors.ColorTextStroke
        Style.PIP_GIRL -> Colors.PipGirlWhite
        Style.NEW_WORLD -> Colors.NewWorldAccent
        Style.NCR -> Colors.NcrTextBack
        Style.LEGION -> Colors.LegionStroke
        else -> getTextColor()
    }
}

fun getGameSelectionColor(): Color {
    return when (styleId) {
        Style.DESERT -> Colors.DesertAccent
        Style.ALASKA_FRONTIER -> Colors.AlaskaBlue
        Style.OLD_WORLD -> Colors.OldWorldBack
        Style.MADRE_ROJA -> Colors.MadreRojaTextBack
        Style.VAULT_21 -> Colors.Vault21Accent
        Style.NCR -> Colors.NcrAccent
        Style.LEGION -> Colors.LegionAccent
        else -> getTextColor()
    }
}

fun getGrayTransparent(): Color {
    return Colors.GrayHalfTransparent
}
