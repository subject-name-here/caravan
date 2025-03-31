package com.unicorns.invisible.caravan.cheats

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources._1921_is_the_year_ccp_was_formed
import com.unicorns.invisible.caravan.model.trading.ChineseTrader
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep
import org.jetbrains.compose.resources.getString


data object CheatChina : Cheat {
    override fun getCode() = 1921

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        save.traders
            .filterIsInstance<ChineseTrader>()
            .forEach { t -> t.is1921Entered = true }

        saveData()
        playYesBeep()

        showAlertDialog(
            "!!!",
            getString(Res.string._1921_is_the_year_ccp_was_formed),
        )
    }
}