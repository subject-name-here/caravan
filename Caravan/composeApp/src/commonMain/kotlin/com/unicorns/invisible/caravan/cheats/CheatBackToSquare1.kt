package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.primitives.CardNumber
import com.unicorns.invisible.caravan.model.primitives.RankNumber
import com.unicorns.invisible.caravan.model.primitives.Suit
import com.unicorns.invisible.caravan.save.saveData
import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.utils.playYesBeep


data object CheatBackToSquare1 : Cheat {
    override fun getCode() = 111111

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        saveGlobal.dropProgress()
        Suit.entries.forEach { suit ->
            saveGlobal.removeCard(CardNumber(RankNumber.TWO, suit, CardBack.STANDARD_RARE))
            saveGlobal.removeCard(CardNumber(RankNumber.FOUR, suit, CardBack.STANDARD_RARE))
            saveGlobal.removeCard(CardNumber(RankNumber.SEVEN, suit, CardBack.STANDARD_RARE))
            saveGlobal.removeCard(CardNumber(RankNumber.TWO, suit, CardBack.STANDARD_UNCOMMON))
            saveGlobal.removeCard(CardNumber(RankNumber.FOUR, suit, CardBack.STANDARD_UNCOMMON))
            saveGlobal.removeCard(CardNumber(RankNumber.SEVEN, suit, CardBack.STANDARD_UNCOMMON))
        }
        saveGlobal.ownedStyles.clear()
        saveData()
        playYesBeep()
    }
}