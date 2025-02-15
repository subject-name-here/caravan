package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.MainActivity

interface Cheat {
    fun getCode(): Int
    fun onEnter(activity: MainActivity, showAlertDialog: (String, String) -> Unit)
}