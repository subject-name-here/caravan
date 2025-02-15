package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep

data object CheatPapaSmurf : Cheat {
    override fun getCode() = 72911

    override fun onEnter(
        activity: MainActivity,
        showAlertDialog: (String, String) -> Unit
    ) {
        save.papaSmurfActive = !save.papaSmurfActive
        saveData(activity)
        playYesBeep(activity)

        if (save.papaSmurfActive) {
            showAlertDialog(
                activity.getString(R.string.easter_egg),
                activity.getString(R.string.papa_khan_was_replaced_by_papa_smurf)
            )
        } else {
            showAlertDialog(
                activity.getString(R.string.easter_egg),
                activity.getString(R.string.papa_smurf_was_replaced_by_papa_khan)
            )
        }
    }
}