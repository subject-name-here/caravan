package com.unicorns.invisible.caravan.cheats

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.easter_egg
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.utils.playYesBeep
import org.jetbrains.compose.resources.getString

data object CheatFrankChallenge : Cheat {
    override fun getCode() = 1999

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        saveGlobal.frankChallenge = !saveGlobal.frankChallenge
        saveData()
        playYesBeep()

        if (saveGlobal.frankChallenge) {
            showAlertDialog(
                getString(Res.string.easter_egg),
                "Frank challenge is available in Single Player!"
            )
        } else {
            showAlertDialog(
                getString(Res.string.easter_egg),
                "Frank challenge is hidden!"
            )
        }
    }
}