package com.unicorns.invisible.caravan.cheats.stash

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.prize1_body


data object CheatStash1969 : CheatStash {
    override fun getCode() = 62869

    override fun getSum() = 1969

    override fun getPrizeIndex() = 1

    override fun getBodyMessageId() = Res.string.prize1_body
}