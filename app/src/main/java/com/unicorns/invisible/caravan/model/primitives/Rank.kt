package com.unicorns.invisible.caravan.model.primitives

import com.unicorns.invisible.caravan.R


enum class RankNumber(val value: Int, val nameId: Int) {
    ACE(1, R.string.ace_name),
    TWO(2, R.string.two_name),
    THREE(3, R.string.three_name),
    FOUR(4, R.string.four_name),
    FIVE(5, R.string.five_name),
    SIX(6, R.string.six_name),
    SEVEN(7, R.string.seven_name),
    EIGHT(8, R.string.eight_name),
    NINE(9, R.string.nine_name),
    TEN(10, R.string.ten_name)
}

enum class RankFace(val value: Int, val nameId: Int) {
    JACK(11, R.string.jack_name),
    QUEEN(12, R.string.queen_name),
    KING(13, R.string.king_name),
    JOKER(14, R.string.joker_name)
}