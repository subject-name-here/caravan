package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.save.processOldSave
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep

data object CheatOldSaveRestore : Cheat {
    override fun getCode() = 140597

    override fun onEnter(
        activity: MainActivity,
        showAlertDialog: (String, String) -> Unit
    ) {
        processOldSave(activity)
        saveData(activity)
        playYesBeep(activity)
    }
}