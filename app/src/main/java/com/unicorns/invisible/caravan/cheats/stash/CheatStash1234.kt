package com.unicorns.invisible.caravan.cheats.stash

import com.unicorns.invisible.caravan.R

data object CheatStash1234 : CheatStash {
    override fun getCode() = 1234

    override fun getSum() = 123

    override fun getPrizeIndex() = 4

    override fun getBodyMessageId() = R.string.prize3_body
}