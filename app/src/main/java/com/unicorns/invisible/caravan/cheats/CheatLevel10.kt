package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep

data object CheatLevel10 : Cheat {
    override fun getCode() = 65537

    override fun onEnter(activity: MainActivity, showAlertDialog: (String, String) -> Unit) {
        save.towerLevel = 10
        saveData(activity)
        playYesBeep(activity)
    }
}