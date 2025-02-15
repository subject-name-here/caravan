package com.unicorns.invisible.caravan.cheats.stash

import com.unicorns.invisible.caravan.R

data object CheatStashLove : CheatStash {
    override fun getCode() = 83

    override fun getSum() = 5000

    override fun getPrizeIndex() = 3

    override fun getBodyMessageId() = R.string.prize4_body
}