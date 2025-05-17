package com.unicorns.invisible.caravan.cheats

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.progress_is_deleted
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.utils.playYesBeep
import org.jetbrains.compose.resources.getString


data object CheatEraseProgress : Cheat {
    override fun getCode() = 404404

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        saveGlobal.dropProgress()
        saveData()
        playYesBeep()
        showAlertDialog(
            "why.",
            getString(Res.string.progress_is_deleted)
        )
    }
}