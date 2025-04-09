package com.unicorns.invisible.caravan

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import com.unicorns.invisible.caravan.utils.getBackgroundColor
import com.unicorns.invisible.caravan.utils.getMusicPanelColor
import com.unicorns.invisible.caravan.utils.pauseActivitySound
import com.unicorns.invisible.caravan.utils.resumeActivitySound
import com.unicorns.invisible.caravan.utils.stopSoundEffects


var activity: MainActivity? = null

class MainActivity : SaveDataActivity() {
    override fun onPause() {
        super.onPause()
        pauseActivitySound(save.playRadioInBack)
        stopSoundEffects()
    }

    override fun onResume() {
        super.onResume()
        resumeActivitySound()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity = this

        enableEdgeToEdge()


        setContent {
            App()
        }
    }
}