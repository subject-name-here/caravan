package com.unicorns.invisible.caravan.cheats

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.utils.nextSong
import com.unicorns.invisible.caravan.utils.pauseRadio
import com.unicorns.invisible.caravan.utils.playPimpBoySound
import com.unicorns.invisible.caravan.utils.resumeRadio
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data object CheatChangeTrack : Cheat {
    override fun getCode() = 1337

    override fun onEnter(activity: MainActivity, showAlertDialog: (String, String) -> Unit) {
        pauseRadio()
        playPimpBoySound(activity)
        MainScope().launch {
            delay(2000L)
            resumeRadio()
            nextSong(activity)
        }
    }
}