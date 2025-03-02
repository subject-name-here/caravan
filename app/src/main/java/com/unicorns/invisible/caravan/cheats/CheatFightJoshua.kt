package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.MainActivity


data object CheatFightJoshua : Cheat {
    override fun getCode() = 45100

    override fun onEnter(activity: MainActivity, showAlertDialog: (String, String) -> Unit) {
        // TODO
    }
}