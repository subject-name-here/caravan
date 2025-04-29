package com.unicorns.invisible.caravan.utils

import kotlin.math.pow
import kotlin.math.roundToInt


fun Double.toString(numOfDec: Int): String {
    val integerPart = this.toInt()

    if (numOfDec > 0) {
        val decimalPart = this - integerPart
        val decimalAsIntValue = (decimalPart * 10f.pow(numOfDec)).roundToInt()
        return "${integerPart}.$decimalAsIntValue"
    } else {
        return integerPart.toString()
    }
}