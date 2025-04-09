package com.unicorns.invisible.caravan.cheats.stash

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.prize5_body


data object CheatStashCumpleanos : CheatStash {
    override fun getCode() = 1

    override fun getSum() = 500

    override fun getPrizeIndex() = 5

    override fun getBodyMessageId() = Res.string.prize5_body
}