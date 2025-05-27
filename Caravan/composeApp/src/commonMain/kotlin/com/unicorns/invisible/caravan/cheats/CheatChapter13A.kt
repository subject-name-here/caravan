package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.utils.playYesBeep


data object CheatChapter13A : Cheat {
    override fun getCode() = 18931976

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        playYesBeep()
        if (!saveGlobal.storyCompleted) {
            showAlertDialog("No way for you yet!", "You have to complete the story to open chapter 13A.")
        } else {
            // TODO 3.0!!!
            showAlertDialog("No way for you yet!", "Wait until the release 3.0.")
        }
    }
}