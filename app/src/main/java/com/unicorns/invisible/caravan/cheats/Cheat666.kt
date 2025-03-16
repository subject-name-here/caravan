package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.MainActivity


data object Cheat666 : Cheat {
    override fun getCode() = 666

    override fun onEnter(activity: MainActivity, showAlertDialog: (String, String) -> Unit) {
        // TODO
    }
}