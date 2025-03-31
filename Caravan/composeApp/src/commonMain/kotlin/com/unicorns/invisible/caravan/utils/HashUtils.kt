package com.unicorns.invisible.caravan.utils


fun getCurrentDateHashCode(): Int {
    val now = getNow().date
    return now.toString().hashCode()
}