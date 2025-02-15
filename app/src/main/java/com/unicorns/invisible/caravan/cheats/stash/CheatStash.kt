package com.unicorns.invisible.caravan.cheats.stash

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.cheats.Cheat
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep

interface CheatStash : Cheat {
    fun getSum(): Int
    fun getPrizeIndex(): Int
    fun getBodyMessageId(): Int

    override fun onEnter(activity: MainActivity, showAlertDialog: (String, String) -> Unit) {
        save.let {
            if (getPrizeIndex() !in it.activatedPrizes) {
                it.activatedPrizes.add(getPrizeIndex())
                it.capsInHand += getSum()
                saveData(activity)
                playYesBeep(activity)
                showAlertDialog(
                    activity.getString(R.string.congrats),
                    activity.getString(getBodyMessageId()),
                )
            }
        }
    }
}