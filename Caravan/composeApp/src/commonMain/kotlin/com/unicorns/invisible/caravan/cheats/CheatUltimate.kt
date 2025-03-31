package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CollectibleDeck
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep


data object CheatUltimate : Cheat {
    override fun getCode() = 45102

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        CardBack.entries.forEach { back ->
            val deck = CollectibleDeck(back)
            save.addCard(deck.toList().random())
            save.addCard(deck.toList().random())
        }

        saveData()
        playYesBeep()

        showAlertDialog(
            "!!!",
            "FOR DEVELOPER PURPOSE ONLY",
        )
    }
}