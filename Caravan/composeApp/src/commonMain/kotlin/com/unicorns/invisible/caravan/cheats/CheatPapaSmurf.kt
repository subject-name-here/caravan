package com.unicorns.invisible.caravan.cheats

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.easter_egg
import caravan.composeapp.generated.resources.papa_khan_was_replaced_by_papa_smurf
import caravan.composeapp.generated.resources.papa_smurf_was_replaced_by_papa_khan
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.utils.playYesBeep
import org.jetbrains.compose.resources.getString


data object CheatPapaSmurf : Cheat {
    override fun getCode() = 72911

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        saveGlobal.papaSmurfActive = !saveGlobal.papaSmurfActive
        saveData()
        playYesBeep()

        if (saveGlobal.papaSmurfActive) {
            showAlertDialog(
                getString(Res.string.easter_egg),
                getString(Res.string.papa_khan_was_replaced_by_papa_smurf)
            )
        } else {
            showAlertDialog(
                getString(Res.string.easter_egg),
                getString(Res.string.papa_smurf_was_replaced_by_papa_khan)
            )
        }
    }
}