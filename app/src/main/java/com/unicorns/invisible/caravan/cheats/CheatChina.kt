package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.trading.ChineseTrader
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep

data object CheatChina : Cheat {
    override fun getCode() = 1921

    override fun onEnter(
        activity: MainActivity,
        showAlertDialog: (String, String) -> Unit
    ) {
        save.traders
            .filterIsInstance<ChineseTrader>()
            .forEach { t -> t.is1921Entered = true }

        saveData(activity)
        playYesBeep(activity)

        showAlertDialog(
            "!!!",
            activity.getString(R.string._1921_is_the_year_ccp_was_formed),
        )
    }
}