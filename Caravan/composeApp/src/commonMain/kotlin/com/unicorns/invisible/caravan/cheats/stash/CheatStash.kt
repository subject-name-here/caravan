package com.unicorns.invisible.caravan.cheats.stash

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.congrats
import com.unicorns.invisible.caravan.cheats.Cheat
import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


interface CheatStash : Cheat {
    fun getSum(): Int
    fun getPrizeIndex(): Int
    fun getBodyMessageId(): StringResource

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        saveGlobal.let {
            if (getPrizeIndex() !in it.activatedPrizes) {
                it.activatedPrizes.add(getPrizeIndex())
                it.capsInHand += getSum()
                saveData()
                playYesBeep()
                showAlertDialog(
                    getString(Res.string.congrats),
                    getString(getBodyMessageId()),
                )
            }
        }
    }
}