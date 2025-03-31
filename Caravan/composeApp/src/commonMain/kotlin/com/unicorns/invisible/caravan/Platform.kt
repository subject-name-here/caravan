package com.unicorns.invisible.caravan

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
expect fun getVersion(): String
expect fun openAchievements()