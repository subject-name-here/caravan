package com.unicorns.invisible.caravan.utils

import android.media.MediaPlayer
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.playingSongName
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.soundReduced
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.withLock
import java.util.concurrent.locks.ReentrantLock


private val effectPlayers = HashSet<MediaPlayer>()
private val effectPlayersLock = ReentrantLock()
fun stopSoundEffects() {
    effectPlayersLock.withLock {
        effectPlayers.forEach { if (it.isPlaying) it.stop() }
        effectPlayers.clear()
    }
}

private fun playEffectPlayerSound(activity: MainActivity, soundId: Int, volumeFraction: Int = 1) {
    val vol = save.soundVolume / volumeFraction
    MediaPlayer
        .create(activity, soundId)
        .apply {
            if (this == null) {
                return
            }
            setVolume(vol, vol)
            setOnCompletionListener {
                effectPlayersLock.withLock {
                    effectPlayers.remove(this)
                    release()
                }
            }
            effectPlayersLock.withLock {
                effectPlayers.add(this)
                start()
            }
        }
}

fun playCardFlipSound(activity: MainActivity) =
    playEffectPlayerSound(activity, getRandomCardFlipSound(), 2)

fun getRandomCardFlipSound() = listOf(
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


fun playLoseSound(activity: MainActivity) {
    playEffectPlayerSound(activity, listOf(R.raw.lose1, R.raw.lose3, R.raw.any).random(), 2)
}
fun playWinSoundAlone(activity: MainActivity) {
    playEffectPlayerSound(activity, listOf(R.raw.win1, R.raw.win2).random(), 2)
}
fun playWinSound(activity: MainActivity) {
    CoroutineScope(Dispatchers.Unconfined).launch {
        playCashSound(activity)
        delay(760L)
        playWinSoundAlone(activity)
    }
}

fun playJokerReceivedSounds(activity: MainActivity) {
    if (soundReduced) return
    playEffectPlayerSound(activity, R.raw.mus_mysteriousstranger_a_01, 2)
}
fun playJokerSounds(activity: MainActivity) {
    if (soundReduced) return
    playEffectPlayerSound(activity, R.raw.mus_mysteriousstranger_a_02, 2)
}
fun playWWSound(activity: MainActivity) {
    if (soundReduced) return
    playEffectPlayerSound(activity, R.raw.ui_wildwasteland)
}

fun playCashSound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.win_caps, 2)
fun playCloseSound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.ui_menu_cancel)
fun playClickSound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.ui_menu_ok)
fun playSelectSound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.ui_vats_move, 3)
fun playPimpBoySound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.ui_pimpboy, 3)
fun playVatsEnter(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.ui_vats_enter, 3)
fun playVatsReady(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.ui_vats_ready, 3)
fun playNoCardAlarm(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.no_cards_alarm)
fun playYesBeep(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.beep_a)
fun playNoBeep(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.beep_b)
fun playFanfares(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.fanfares)
fun playTowerCompleted(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.endgame)
fun playTowerFailed(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.death)
fun playDailyCompleted(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.ui_levelup)
fun playNukeBlownSound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.nuke_big)
fun playHeartbeatSound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.sfx_heartbeat)
fun playSlideSound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.slide)
fun playMinigunSound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.minigun_f2)
fun playNotificationSound(activity: MainActivity) = playEffectPlayerSound(activity, R.raw.notification)

private val ambientPlayers = HashSet<MediaPlayer>()
private val ambientPlayersLock = ReentrantLock()
private var wasAmbientPaused = false
fun stopAmbient() {
    ambientPlayersLock.withLock {
        ambientPlayers.forEach { if (it.isPlaying) it.stop() }
        ambientPlayers.clear()
    }
}
fun setAmbientVolume(volume: Float) {
    ambientPlayersLock.withLock {
        ambientPlayers.forEach { it.setVolume(volume, volume) }
    }
}

fun startAmbient(activity: MainActivity) {
    if (soundReduced) return

    val vol = save.ambientVolume / 2
    MediaPlayer
        .create(
            activity, listOf(
                R.raw.ambient1, R.raw.ambient2, R.raw.ambient3, R.raw.ambient4,
                R.raw.ambient5, R.raw.ambient6, R.raw.ambient7, R.raw.ambient8
            ).random()
        ).apply {
            setVolume(vol, vol)
            setOnCompletionListener {
                ambientPlayersLock.withLock {
                    ambientPlayers.remove(this)
                    release()
                }
                startAmbient(activity)
            }
            ambientPlayersLock.withLock {
                ambientPlayers.add(this)
                if (!wasAmbientPaused) {
                    start()
                }
            }
        }
}

private fun getSongsArray() = arrayOf(
    "MUS_Aint_That_A_Kick_In_the_Head.amr" to "\"Ain't That a Kick in the Head?\" - Dean Martin",
    "MUS_American_Swing.amr" to "\"American Swing\" - Gerhard Trede",
    "MUS_Big_Iron.amr" to "\"Big Iron\" - Marty Robbins",
    "MUS_Blue_Moon.amr" to "\"Blue Moon\" - Frank Sinatra",
    "MUS_Blues_For_You.amr" to "\"Blues For You\" - Gabriel Pares",
    if (save.isRadioUsesPseudonyms) {
        "MUS_Cobwebs_and_Rainbows.amr" to "\"Cobwebs and Rainbows\" - Bruce Isaac"
    } else {
        "MUS_Cobwebs_and_Rainbows.amr" to "\"Cobwebs and Rainbows\" - Joshua Sawyer/Dick Stephen Walter"
    },
    "MUS_EddyArnold_Rca_ItsASin.amr" to "\"It's a Sin\" - Eddy Arnold",
    "MUS_Goin_Under.amr" to "\"Goin' Under\" - Darrell Wayne Perry and Tommy Smith",
    "MUS_Hallo_Mister_X.amr" to "\"Hallo Mister X\" - Gerhard Trede",
    "MUS_Happy_Times.amr" to "\"Happy Times\" - Bert Weedon",
    "MUS_Heartaches_by_the_Number.amr" to "\"Heartaches by the Number\" - Guy Mitchell",
    if (save.isRadioUsesPseudonyms) {
        "MUS_HomeOnTheWastes.amr" to "\"Home on the Wastes\" - Lonesome Drifter"
    } else {
        "MUS_HomeOnTheWastes.amr" to "\"Home on the Wastes\" - Joshua Sawyer/Nathaniel Chapman"
    },
    "MUS_I_m_Movin_Out.amr" to "\"I'm Movin' Out\" - The Roues Brothers",
    "MUS_I_m_So_Blue.amr" to "\"I'm So Blue\" - Katie Thompson",
    "MUS_In_The_Shadow_Of_The_Valley.amr" to "\"In the Shadow of the Valley\" - Lost Weekend Western Swing Band",
    "MUS_Its_A_Sin_To_Tell_A_Lie.amr" to "\"It's a Sin to Tell a Lie\" - The Ink Spots",
    "MUS_Jazz_Blues_GT.amr" to "\"Jazz Blues\" - Gerhard Trede",
    "MUS_Jazz_Club_Blues_CAS.amr" to "\"Jazz Club Blues\" - Harry Bluestone",
    "MUS_Jingle_Jangle_Jingle.amr" to "\"Jingle, Jangle, Jingle\" - The Kay Kyser Orchestra",
    if (save.isRadioUsesPseudonyms) {
        "MUS_Joe_Cool_CAS.amr" to "\"Joe Cool\" - Nino Nardini"
    } else {
        "MUS_Joe_Cool_CAS.amr" to "\"Joe Cool\" - Georges Teperin"
    },
    "MUS_Johnny_Guitar.amr" to "\"Johnny Guitar\" - Peggy Lee",
    "MUS_Lazy_Day_Blues.amr" to "\"Lazy Day Blues\" - Bert Weedon",
    "MUS_Let_s_Ride_Into_The_Sunset_Together.amr" to "\"Let's Ride Into the Sunset Together\" - Lost Weekend Western Swing Band, featuring Don Burham with Patty Kistner",
    "MUS_Lone_Star.amr" to "\"Lone Star\" - Lost Weekend Western Swing Band",
    "MUS_Love_Me_As_Though_No_Tomorrow.amr" to "\"Love Me as Though There Were No Tomorrow\" - Nat King Cole",
    "MUS_Mad_About_The_Boy.amr" to "\"Mad About the Boy\" - Carmen Dragon and his Orchestra, featuring Helen Forrest",
    "MUS_Manhattan.amr" to "\"Manhattan\" - Gerhard Trede",
    if (save.isRadioUsesPseudonyms) {
        "MUS_NewVegasValley.amr" to "\"New Vegas Valley\" - Lonesome Drifter"
    } else {
        "MUS_NewVegasValley.amr" to "\"New Vegas Valley\" - Joshua Sawyer/James Melilli"
    },
    "MUS_Roundhouse_Rock.amr" to "\"Roundhouse Rock\" - Bert Weedon",
    "MUS_Sit_And_Dream.amr" to "\"Sit and Dream\" - Pete Thomas",
    "MUS_Sleepy_Town_Blues_CAS.amr" to "\"Sleepy Town Blues\" - Harry Lubin",
    "MUS_Slow_Bounce.amr" to "\"Slow Bounce\" - Gerhard Trede",
    "MUS_Slow_Sax_KOS.amr" to "\"Slow Sax\" - Christof Dejean",
    "MUS_Somethings_Gotta_Give.amr" to "\"Something's Gotta Give\" - Bing Crosby",
    "MUS_Stars_Of_The_Midnight_Range.amr" to "\"Stars of the Midnight Range\" - Johnny Bond and his Red River Valley Boys",
    "MUS_Strahlende_Trompete.amr" to "\"Strahlende Trompete\" - Gerhard Trede",
    if (save.isRadioUsesPseudonyms) {
        "MUS_StreetsOfNewReno.amr" to "\"Streets of New Reno\" - Lonesome Drifter"
    } else {
        "MUS_StreetsOfNewReno.amr" to "\"Streets of New Reno\" - Joshua Sawyer/Nathaniel Chapman"
    },
    "MUS_Von_Spanien_Nach_S_damerika.amr" to "\"Von Spanien Nach Südamerika\" - Gerhard Trede",
    "MUS_Where_Have_You_Been_All_My_Life.amr" to "\"Where Have You Been All My Life?\" - Jeff Hooper",
    "MUS_Why_Dont_You_Do_Right.amr" to "\"Why Don't You Do Right?\" - The Dave Barbour Quartet, featuring Peggy Lee",
)

var pointer = -1
val indices = getSongsArray().indices.toMutableList()
fun getSongByIndex(activity: MainActivity): Pair<String, String>? {
    val songsArray = getSongsArray()
    return if (pointer == -1) {
        if (activity.styleId == Style.SIERRA_MADRE || activity.styleId == Style.MADRE_ROJA) {
            if (save.isRadioUsesPseudonyms) {
                "begin_again.amr" to "\"Begin Again\" - Vera Keyes"
            } else {
                "begin_again.amr" to "\"Begin Again\" - Justin E. Bell, Stephanie Dowling"
            }
        } else {
            if (save.isRadioUsesPseudonyms) {
                "MUS_caravan_whiplash.amr" to "\"Caravan\" - Shaffer Conservatory Studio Band"
            } else {
                "MUS_caravan_whiplash.amr" to "\"Caravan\" - John Wasson"
            }
        }
    } else if (pointer in songsArray.indices) {
        songsArray[indices[pointer]]
    } else {
        null
    }
}


private var radioStartedFlag = false
fun startRadio(activity: MainActivity) {
    if (radioStartedFlag) {
        return
    }
    radioStartedFlag = true
    indices.shuffle()
    if (save.useCaravanIntro) {
        pointer = -1
        playSongFromRadio(activity)
    } else {
        pointer = 0
        nextSong(activity)
    }
}

enum class RadioState {
    PLAYING,
    PAUSED_BY_BUTTON,
    PAUSED_BY_LEAVING_ACTIVITY,
}

private val radioPlayers = HashSet<MediaPlayer>()
private val radioLock = ReentrantLock()
private var radioState = RadioState.PLAYING
private fun playSongFromRadio(activity: MainActivity) {
    val vol = save.radioVolume

    try {
        val songName = getSongByIndex(activity)!!
        MediaPlayer()
            .apply {
                val afd = activity.assets.openFd("radio/${songName.first}")
                playingSongName.postValue(songName.second)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                setOnCompletionListener {
                    radioPlayers.remove(this)
                    release()
                    nextSong(activity)
                }
                prepare()
                setVolume(vol, vol)
                radioLock.withLock {
                    radioPlayers.add(this)
                    if (radioState == RadioState.PLAYING) {
                        start()
                    }
                }
            }
    } catch (_: Exception) {
        CoroutineScope(Dispatchers.Unconfined).launch {
            delay(760L)
            nextSong(activity)
        }
    }
}

fun stopRadio() {
    radioLock.withLock {
        radioPlayers.forEach { if (it.isPlaying) it.stop() }
        radioPlayers.clear()
    }
}

fun nextSong(activity: MainActivity) {
    stopRadio()

    if (pointer !in getSongsArray().indices) {
        pointer = 0
        indices.shuffle()
    }

    playSongFromRadio(activity)
}

fun resumeActivitySound() {
    if (radioState == RadioState.PAUSED_BY_LEAVING_ACTIVITY) {
        radioLock.withLock {
            radioPlayers.forEach { it.start() }
            radioState = RadioState.PLAYING
        }
    }
    ambientPlayersLock.withLock {
        if (wasAmbientPaused) {
            ambientPlayers.forEach {
                it.start()
            }
            wasAmbientPaused = false
        }
    }
}
fun resumeRadio() {
    if (radioState == RadioState.PAUSED_BY_BUTTON) {
        radioLock.withLock {
            radioPlayers.forEach { it.start() }
            radioState = RadioState.PLAYING
        }
    }
}
fun pauseActivitySound(leaveRadioOn: Boolean) {
    if (radioState == RadioState.PLAYING && !leaveRadioOn) {
        radioLock.withLock {
            radioPlayers.forEach { it.pause() }
            radioState = RadioState.PAUSED_BY_LEAVING_ACTIVITY
        }
    }
    ambientPlayersLock.withLock {
        ambientPlayers.forEach {
            if (it.isPlaying) {
                wasAmbientPaused = true
                it.pause()
            }
        }
    }
}
fun pauseRadio() {
    if (radioState == RadioState.PLAYING) {
        radioLock.withLock {
            radioPlayers.forEach { it.pause() }
            radioState = RadioState.PAUSED_BY_BUTTON
        }
    }
}

fun setRadioVolume(volume: Float) {
    radioLock.withLock {
        radioPlayers.forEach { it.setVolume(volume, volume) }
    }
}

fun playTheme(activity: MainActivity, themeId: Int) {
    stopRadio()
    val vol = save.radioVolume
    MediaPlayer.create(activity, themeId)
        .apply {
            isLooping = true
            setVolume(vol, vol)
            radioLock.withLock {
                radioPlayers.add(this)
                if (radioState != RadioState.PAUSED_BY_LEAVING_ACTIVITY) {
                    start()
                }
            }
            setOnCompletionListener {
                radioLock.withLock {
                    it.stop()
                    radioPlayers.remove(it)
                    it.release()
                }
            }
        }
}
fun startLevel11Theme(activity: MainActivity) {
    if (save.isHeroic) {
        playTheme(activity, R.raw.final_heroic)
    } else {
        playTheme(activity, R.raw.frank_theme)
    }
}
fun startFinalBossTheme(activity: MainActivity) {
    if (save.isHeroic) {
        playTheme(activity, R.raw.final_heroic)
    } else {
        playTheme(activity, R.raw.final_boss)
    }
}

fun playFrankPhrase(activity: MainActivity, phraseId: Int) {
    val vol = save.soundVolume / 2
    MediaPlayer
        .create(activity, phraseId)
        .apply {
            if (this == null) {
                return
            }
            setVolume(vol, vol)
            setOnCompletionListener {
                effectPlayersLock.withLock {
                    effectPlayers.remove(this)
                    release()
                }
            }
            effectPlayersLock.withLock {
                stopSoundEffects()
                effectPlayers.add(this)
                start()
            }
        }
}