package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.Card
import com.unicorns.invisible.caravan.model.primitives.Rank
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.utils.playYesBeep

data object CheatHalfEnclaveDeck : Cheat {
    override fun getCode() = 2077

    override fun onEnter(
        activity: MainActivity,
        showAlertDialog: (String, String) -> Unit
    ) {
        save.let {
            Rank.entries.forEach { rank ->
                it.addCard(Card(rank, Suit.HEARTS, CardBack.ENCLAVE, false))
                it.addCard(Card(rank, Suit.CLUBS, CardBack.ENCLAVE, false))
            }
        }
        saveData(activity)
        playYesBeep(activity)
        showAlertDialog(
            activity.getString(R.string.congrats),
            activity.getString(R.string.you_have_found_cards_from_enclave_deck)
        )
    }
}