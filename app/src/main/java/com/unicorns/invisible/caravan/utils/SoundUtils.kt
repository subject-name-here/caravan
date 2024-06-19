package com.unicorns.invisible.caravan.utils

import android.media.MediaPlayer
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style


fun playNotificationSound(activity: MainActivity, onPrepared: () -> Unit) {
    val volume = activity.save?.soundVolume ?: 1f
    effectPlayer = MediaPlayer
        .create(activity, R.raw.notification)
        .apply {
            setVolume(volume, volume)
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener {
                if (effectPlayer == this) {
                    effectPlayer = null
                }
                // release()
            }
            start()
        }
}

var effectPlayer: MediaPlayer? = null
private fun playEffectPlayerSound(activity: MainActivity, soundId: Int, volumeFraction: Int = 1) {
    val vol = (activity.save?.soundVolume ?: 1f) / volumeFraction
    effectPlayer = MediaPlayer
        .create(activity, soundId)
        .apply {
            setVolume(vol, vol)
            setOnCompletionListener {
                if (effectPlayer == this) {
                    effectPlayer = null
                }
                // release()
            }
            start()
        }
}

fun playCardFlipSound(activity: MainActivity) = playEffectPlayerSound(activity, getRandomCardFlipSound())
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

fun playLoseSound(activity: MainActivity) {
    if (effectPlayer?.isPlaying == true) {
        effectPlayer?.stop()
    }
    playEffectPlayerSound(activity, listOf(R.raw.lose1, R.raw.lose3, R.raw.any).random(), 2)
}
fun playWinSound(activity: MainActivity) {
    if (effectPlayer?.isPlaying == true) {
        effectPlayer?.stop()
    }
    playEffectPlayerSound(activity, listOf(R.raw.win1, R.raw.win2, R.raw.any).random(), 2)
}

fun playJokerReceivedSounds(activity: MainActivity) =
    playEffectPlayerSound(activity, R.raw.mus_mysteriousstranger_a_01, 2)
fun playJokerSounds(activity: MainActivity) =
    playEffectPlayerSound(activity, R.raw.mus_mysteriousstranger_a_02, 2)

fun playCloseSound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.ui_menu_cancel)
fun playClickSound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.ui_menu_ok)
fun playSelectSound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.ui_vats_move, 3)
fun playPimpBoySound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.ui_pimpboy, 3)
fun playVatsEnter(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.ui_vats_enter, 3)
fun playVatsReady(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.ui_vats_ready, 3)
fun playQuitMultiplayer(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.quit_multiplayer, 3)
fun playNoCardAlarm(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.no_cards_alarm, 3)
fun playYesBeep(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.beep_a)
fun playNoBeep(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.beep_b)
fun playFanfares(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.fanfares)

var currentPlayer: MediaPlayer? = null
fun stopMusic() {
    if (currentPlayer?.isPlaying == true) {
        currentPlayer?.stop()
        currentPlayer?.release()
    }
    currentPlayer = null
}
fun startAmbient(activity: MainActivity) {
    val vol = (activity.save?.ambientVolume ?: 1f) / 2
    currentPlayer = MediaPlayer
        .create(activity, listOf(
            R.raw.ambient1,
            R.raw.ambient2,
            R.raw.ambient3,
            R.raw.ambient4,
            R.raw.ambient5,
            R.raw.ambient6,
            R.raw.ambient7,
            R.raw.ambient8
        ).random()).apply {
            setVolume(vol, vol)
            setOnCompletionListener {
                if (currentPlayer == this) {
                    currentPlayer = null
                    // release()
                    startAmbient(activity)
                }
            }
            start()
        }
}

private val songList = ("MUS_Aint_That_A_Kick_In_the_Head.amr\n" +
        "MUS_American_Swing.amr\n" +
        "MUS_Big_Iron.amr\n" +
        "MUS_Blue_Moon.amr\n" +
        "MUS_Blues_For_You.amr\n" +
        "MUS_Cobwebs_and_Rainbows.amr\n" +
        "MUS_EddyArnold_Rca_ItsASin.amr\n" +
        "MUS_Goin_Under.amr\n" +
        "MUS_Hallo_Mister_X.amr\n" +
        "MUS_Happy_Times.amr\n" +
        "MUS_Heartaches_by_the_Number.amr\n" +
        "MUS_HomeOnTheWastes.amr\n" +
        "MUS_I_m_Movin_Out.amr\n" +
        "MUS_I_m_So_Blue.amr\n" +
        "MUS_In_The_Shadow_Of_The_Valley.amr\n" +
        "MUS_Its_A_Sin_To_Tell_A_Lie.amr\n" +
        "MUS_Jazz_Blues_GT.amr\n" +
        "MUS_Jazz_Club_Blues_CAS.amr\n" +
        "MUS_Jingle_Jangle_Jingle.amr\n" +
        "MUS_Joe_Cool_CAS.amr\n" +
        "MUS_Johnny_Guitar.amr\n" +
        "MUS_Lazy_Day_Blues.amr\n" +
        "MUS_Let_s_Ride_Into_The_Sunset_Together.amr\n" +
        "MUS_Lone_Star.amr\n" +
        "MUS_Love_Me_As_Though_No_Tomorrow.amr\n" +
        "MUS_Mad_About_The_Boy.amr\n" +
        "MUS_Manhattan.amr\n" +
        "MUS_NewVegasValley.amr\n" +
        "MUS_Roundhouse_Rock.amr\n" +
        "MUS_Sit_And_Dream.amr\n" +
        "MUS_Sleepy_Town_Blues_CAS.amr\n" +
        "MUS_Slow_Bounce.amr\n" +
        "MUS_Slow_Sax_KOS.amr\n" +
        "MUS_Somethings_Gotta_Give.amr\n" +
        "MUS_Stars_Of_The_Midnight_Range.amr\n" +
        "MUS_Strahlende_Trompete.amr\n" +
        "MUS_StreetsOfNewReno.amr\n" +
        "MUS_Von_Spanien_Nach_S_damerika.amr\n" +
        "MUS_Where_Have_You_Been_All_My_Life.amr\n" +
        "MUS_Why_Dont_You_Do_Right.amr").split("\n")

private var pointer = songList.indices.random()
private var usedIndices = mutableListOf<Int>()
private var radioStartedFlag = false
fun startRadio(activity: MainActivity) {
    if (radioStartedFlag) {
        return
    }
    radioStartedFlag = true
    if (activity.save?.useCaravanIntro != false) {
        if (activity.styleId == Style.SIERRA_MADRE || activity.styleId == Style.MADRE_ROJA) {
            playSongFromRadio(activity, "begin_again.amr")
        } else {
            playSongFromRadio(activity, "MUS_caravan_whiplash.amr")
        }
    } else {
        nextSong(activity)
    }
}

var radioPlayer: MediaPlayer? = null
var isRadioStopped = false
private fun playSongFromRadio(activity: MainActivity, songName: String) {
    val vol = activity.save?.radioVolume ?: 1f
    radioPlayer = MediaPlayer()
        .apply {
            val afd = activity.assets.openFd("radio/$songName")
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            setOnCompletionListener {
                if (radioPlayer == this) {
                    radioPlayer = null
                }
                // release()
                nextSong(activity)
            }
            prepare()
            setVolume(vol, vol)
            start()
        }
}


fun nextSong(activity: MainActivity) {
    if (radioPlayer?.isPlaying == true) {
        radioPlayer?.stop()
    }
    if (pointer !in songList.indices) {
        pointer = songList.indices.random()
    }
    playSongFromRadio(activity, songList[pointer])
    usedIndices.add(pointer)
    pointer = (songList.indices - usedIndices.toSet()).randomOrNull() ?: -1
}
fun resume() {
    isRadioStopped = false
    radioPlayer?.start()
}
fun pause() {
    isRadioStopped = true
    radioPlayer?.pause()
}
fun setRadioVolume(volume: Float) {
    radioPlayer?.setVolume(volume, volume)
}