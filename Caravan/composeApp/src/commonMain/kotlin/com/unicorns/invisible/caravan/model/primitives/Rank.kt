package com.unicorns.invisible.caravan.model.primitives

import caravan.composeapp.generated.resources.Res
import caravan.composeapp.generated.resources.ace_name
import caravan.composeapp.generated.resources.eight_name
import caravan.composeapp.generated.resources.five_name
import caravan.composeapp.generated.resources.four_name
import caravan.composeapp.generated.resources.jack_name
import caravan.composeapp.generated.resources.joker_name
import caravan.composeapp.generated.resources.king_name
import caravan.composeapp.generated.resources.nine_name
import caravan.composeapp.generated.resources.queen_name
import caravan.composeapp.generated.resources.seven_name
import caravan.composeapp.generated.resources.six_name
import caravan.composeapp.generated.resources.ten_name
import caravan.composeapp.generated.resources.three_name
import caravan.composeapp.generated.resources.two_name
import org.jetbrains.compose.resources.StringResource


enum class RankNumber(val value: Int, val nameId: StringResource) {
    ACE(1, Res.string.ace_name),
    TWO(2, Res.string.two_name),
    THREE(3, Res.string.three_name),
    FOUR(4, Res.string.four_name),
    FIVE(5, Res.string.five_name),
    SIX(6, Res.string.six_name),
    SEVEN(7, Res.string.seven_name),
    EIGHT(8, Res.string.eight_name),
    NINE(9, Res.string.nine_name),
    TEN(10, Res.string.ten_name)
}

enum class RankFace(val value: Int, val nameId: StringResource) {
    JACK(11, Res.string.jack_name),
    QUEEN(12, Res.string.queen_name),
    KING(13, Res.string.king_name),
    JOKER(14, Res.string.joker_name)
}