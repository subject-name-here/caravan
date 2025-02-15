package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep

data object CheatLaggySpeed : Cheat {
    override fun getCode() = 190

    override fun onEnter(activity: MainActivity, showAlertDialog: (String, String) -> Unit) {
        if (save.animationSpeed == AnimationSpeed.LAGGY) {
            save.animationSpeed = AnimationSpeed.NORMAL
            showAlertDialog(
                activity.getString(R.string.easter_egg),
                activity.getString(R.string.laggy_speed_off)
            )
        } else {
            save.animationSpeed = AnimationSpeed.LAGGY
            showAlertDialog(
                activity.getString(R.string.easter_egg),
                activity.getString(R.string.laggy_speed_on)
            )
        }

        saveData(activity)
        playYesBeep(activity)
    }
}