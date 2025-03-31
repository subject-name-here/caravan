package com.unicorns.invisible.caravan.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun getNow() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())