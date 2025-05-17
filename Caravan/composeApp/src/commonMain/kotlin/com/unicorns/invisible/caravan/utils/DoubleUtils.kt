package com.unicorns.invisible.caravan.utils

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt


fun Double.toString(numOfDec: Int): String {
    val integerPart = this.toInt()

    if (numOfDec > 0) {
        val decimalPart = this - integerPart
        val decimalAsIntValue = (decimalPart * 10f.pow(numOfDec)).roundToInt()
        return if (decimalAsIntValue >= 10) {
            "${integerPart + 1}.${decimalAsIntValue - 10}"
        } else {
            "${integerPart}.${decimalAsIntValue}"
        }

    } else {
        return integerPart.toString()
    }
}

fun Double.isJubilee(): Boolean {
    val integerPart = this.toInt()
    val decimalPart = this - integerPart
    return abs(decimalPart) < 0.01 || abs(decimalPart - 0.5) < 0.01
}