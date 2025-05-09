package com.unicorns.invisible.caravan.cheats

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.easter_egg
import caravan.composeapp.generated.resources.laggy_speed_off
import caravan.composeapp.generated.resources.laggy_speed_on
import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.utils.playYesBeep
import org.jetbrains.compose.resources.getString


data object CheatLaggySpeed : Cheat {
    override fun getCode() = 190

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        if (saveGlobal.animationSpeed == AnimationSpeed.LAGGY) {
            saveGlobal.animationSpeed = AnimationSpeed.NORMAL
            showAlertDialog(
                getString(Res.string.easter_egg),
                getString(Res.string.laggy_speed_off)
            )
        } else {
            saveGlobal.animationSpeed = AnimationSpeed.LAGGY
            showAlertDialog(
                getString(Res.string.easter_egg),
                getString(Res.string.laggy_speed_on)
            )
        }

        saveData()
        playYesBeep()
    }
}