package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep


data object CheatUltimate : Cheat {
    override fun getCode() = 45102

    override fun onEnter(
        activity: MainActivity,
        showAlertDialog: (String, String) -> Unit
    ) {
        CardBack.entries.forEach { back ->
            val deck = CollectibleDeck(back)
            save.addCard(deck.toList().random())
            save.addCard(deck.toList().random())
        }

        saveData(activity)
        playYesBeep(activity)

        showAlertDialog(
            "!!!",
            "FOR DEVELOPER PURPOSE ONLY",
        )
    }
}