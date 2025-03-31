package com.unicorns.invisible.caravan.utils

import kotlin.random.Random


fun weightedRandom(weights: List<Int>): Int {
    val sum = weights.sum()
    val r = Random.nextInt(sum)
    var runningSum = 0
    weights.forEachIndexed { index, w ->
        runningSum += w
        if (r < runningSum) {
            return index
        }
    }
    return weights.lastIndex
}