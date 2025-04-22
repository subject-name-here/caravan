package com.unicorns.invisible.caravan.cheats

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.easter_egg
import caravan.composeapp.generated.resources.radio_uses_pseudonyms_now
import caravan.composeapp.generated.resources.radio_uses_real_names_now
import com.unicorns.invisible.caravan.playingSongName
import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.getSongByIndex
import com.unicorns.invisible.caravan.utils.playYesBeep
import org.jetbrains.compose.resources.getString


data object CheatRadioPseudonyms : Cheat {
    override fun getCode() = 809608

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        saveGlobal.isRadioUsesPseudonyms = !saveGlobal.isRadioUsesPseudonyms
        saveData()
        playYesBeep()

        playingSongName = getSongByIndex()?.second ?: ""
        if (saveGlobal.isRadioUsesPseudonyms) {
            showAlertDialog(
                getString(Res.string.easter_egg),
                getString(Res.string.radio_uses_pseudonyms_now)
            )
        } else {
            showAlertDialog(
                getString(Res.string.easter_egg),
                getString(Res.string.radio_uses_real_names_now)
            )
        }
    }
}