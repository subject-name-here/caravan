package com.unicorns.invisible.caravan

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getVersion(): String {
    val act = activity ?: return ""
    return act.packageManager.getPackageInfo(act.packageName, 0).versionName ?: ""
}

actual fun openAchievements() {
    val act = activity ?: return
    act.achievementsClient?.achievementsIntent?.let {
        act.openAchievements(it)
    }
}