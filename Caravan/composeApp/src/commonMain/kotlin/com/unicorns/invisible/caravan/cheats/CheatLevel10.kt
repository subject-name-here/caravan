package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.utils.playYesBeep


data object CheatLevel10 : Cheat {
    override fun getCode() = 65537

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        saveGlobal.towerLevel = 12
        saveData()
        playYesBeep()
    }
}