package com.unicorns.invisible.caravan.cheats.stash

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.prize2_body


data object CheatStashEventfulYear : CheatStash {
    override fun getCode() = 2024

    override fun getSum() = 2024

    override fun getPrizeIndex() = 2

    override fun getBodyMessageId() = Res.string.prize2_body
}