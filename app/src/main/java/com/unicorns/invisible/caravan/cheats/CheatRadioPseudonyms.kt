package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.playingSongName
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.getSongByIndex
import com.unicorns.invisible.caravan.utils.playYesBeep

data object CheatRadioPseudonyms : Cheat {
    override fun getCode() = 809608

    override fun onEnter(
        activity: MainActivity,
        showAlertDialog: (String, String) -> Unit
    ) {
        save.isRadioUsesPseudonyms = !save.isRadioUsesPseudonyms
        saveData(activity)
        playYesBeep(activity)

        playingSongName.postValue(getSongByIndex(activity)?.second)
        if (save.isRadioUsesPseudonyms) {
            showAlertDialog(
                activity.getString(R.string.easter_egg),
                activity.getString(R.string.radio_uses_pseudonyms_now)
            )
        } else {
            showAlertDialog(
                activity.getString(R.string.easter_egg),
                activity.getString(R.string.radio_uses_real_names_now)
            )
        }
    }
}