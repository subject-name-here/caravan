package com.unicorns.invisible.caravan.utils

import android.media.MediaPlayer
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R


fun playCardFlipSound(activity: MainActivity) {
    MediaPlayer
        .create(activity, getRandomCardFlipSound())
        .apply {
            setVolume(activity.save?.volume ?: 1f, activity.save?.volume ?: 1f)
        }
        .start()
}


fun getRandomCardFlipSound(): Int {
    return listOf(
        R.raw.fol_gmble_cardflip_01,
        R.raw.fol_gmble_cardflip_02,
        R.raw.fol_gmble_cardflip_03,
        R.raw.fol_gmble_cardflip_04,
        R.raw.fol_gmble_cardflip_05,
        R.raw.fol_gmble_cardflip_06,
        R.raw.fol_gmble_cardflip_07,
        R.raw.fol_gmble_cardflip_09,
        R.raw.fol_gmble_cardflip_10,
        R.raw.fol_gmble_cardflip_11,
    ).random()
}