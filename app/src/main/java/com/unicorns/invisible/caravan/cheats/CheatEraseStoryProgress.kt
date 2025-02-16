package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep

data object CheatEraseStoryProgress : Cheat {
    override fun getCode() = 404

    override fun onEnter(
        activity: MainActivity,
        showAlertDialog: (String, String) -> Unit
    ) {
        save.let {
            it.storyProgress = 0
            it.altStoryProgress = 0
            it.storyCompleted = false
        }
        saveData(activity)
        playYesBeep(activity)
        showAlertDialog(
            "why.",
            activity.getString(R.string.story_progress_is_deleted)
        )
    }
}