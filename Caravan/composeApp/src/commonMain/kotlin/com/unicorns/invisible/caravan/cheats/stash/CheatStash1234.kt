package com.unicorns.invisible.caravan.cheats.stash

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.prize3_body


data object CheatStash1234 : CheatStash {
    override fun getCode() = 1234

    override fun getSum() = 123

    override fun getPrizeIndex() = 4

    override fun getBodyMessageId() = Res.string.prize3_body
}