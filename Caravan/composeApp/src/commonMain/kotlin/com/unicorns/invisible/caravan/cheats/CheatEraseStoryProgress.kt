package com.unicorns.invisible.caravan.cheats

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.story_progress_is_deleted
import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep
import org.jetbrains.compose.resources.getString


data object CheatEraseStoryProgress : Cheat {
    override fun getCode() = 404

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        saveGlobal.let {
            it.storyProgress = 0
            it.storyCompleted = false
        }
        saveData()
        playYesBeep()
        showAlertDialog(
            "why.",
            getString(Res.string.story_progress_is_deleted)
        )
    }
}