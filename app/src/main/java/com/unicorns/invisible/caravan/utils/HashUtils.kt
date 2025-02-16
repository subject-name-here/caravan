package com.unicorns.invisible.caravan.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun getCurrentDateHashCode(): Int {
    return SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(Date()).hashCode()
}