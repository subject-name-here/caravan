package com.unicorns.invisible.caravan.utils

import com.unicorns.invisible.caravan.Style
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.soundReduced
import com.unicorns.invisible.caravan.styleId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


expect fun stopSoundEffects()
expect fun playEffectPlayerSound(soundPath: String, volumeFraction: Int = 1)

fun playCardFlipSound() = playEffectPlayerSound(getRandomCardFlipSound(), 2)

fun getRandomCardFlipSound() = listOf(
    "files/raw/fol_gmble_cardflip_01.ogg",
    "files/raw/fol_gmble_cardflip_02.ogg",
    "files/raw/fol_gmble_cardflip_03.ogg",
    "files/raw/fol_gmble_cardflip_04.ogg",
    "files/raw/fol_gmble_cardflip_05.ogg",
    "files/raw/fol_gmble_cardflip_06.ogg",
    "files/raw/fol_gmble_cardflip_07.ogg",
    "files/raw/fol_gmble_cardflip_09.ogg",
    "files/raw/fol_gmble_cardflip_10.ogg",
    "files/raw/fol_gmble_cardflip_11.ogg",
).random()

fun playLoseSound() {
    playEffectPlayerSound(listOf("files/raw/lose1.ogg", "files/raw/lose3.ogg", "files/raw/any.ogg").random(), 2)
}
fun playWinSoundAlone() {
    playEffectPlayerSound(listOf("files/raw/win1.ogg", "files/raw/win2.ogg").random(), 2)
}
fun playWinSound() {
    CoroutineScope(Dispatchers.Unconfined).launch {
        playCashSound()
        delay(760L)
        playWinSoundAlone()
    }
}

fun playJokerReceivedSounds() {
    if (soundReduced) return
    playEffectPlayerSound("files/raw/mus_mysteriousstranger_a_01.ogg", 2)
}
fun playJokerSounds() {
    if (soundReduced) return
    playEffectPlayerSound("files/raw/mus_mysteriousstranger_a_02.ogg", 2)
}
fun playWWSound() {
    if (soundReduced) return
    playEffectPlayerSound("files/raw/ui_wildwasteland.ogg")
}

fun playCashSound() = playEffectPlayerSound("files/raw/win_caps.ogg", 2)
fun playCloseSound() = playEffectPlayerSound("files/raw/ui_menu_cancel.ogg")
fun playClickSound() = playEffectPlayerSound("files/raw/ui_menu_ok.ogg")
fun playSelectSound() = playEffectPlayerSound("files/raw/ui_vats_move.ogg", 3)
fun playPimpBoySound() = playEffectPlayerSound("files/raw/ui_pimpboy.ogg", 3)
fun playVatsEnter() = playEffectPlayerSound("files/raw/ui_vats_enter.ogg", 3)
fun playVatsReady() = playEffectPlayerSound("files/raw/ui_vats_ready.ogg", 3)
fun playNoCardAlarm() = playEffectPlayerSound("files/raw/no_cards_alarm.ogg")
fun playYesBeep() = playEffectPlayerSound("files/raw/beep_a.ogg")
fun playNoBeep() = playEffectPlayerSound("files/raw/beep_b.ogg")
fun playFanfares() = playEffectPlayerSound("files/raw/fanfares.ogg")
fun playTowerCompleted() = playEffectPlayerSound("files/raw/endgame.ogg")
fun playTowerFailed() = playEffectPlayerSound("files/raw/death.ogg")
fun playDailyCompleted() = playEffectPlayerSound("files/raw/ui_levelup.ogg")
fun playNukeBlownSound() = playEffectPlayerSound("files/raw/nuke_big.ogg")
fun playHeartbeatSound() = playEffectPlayerSound("files/raw/sfx_heartbeat.ogg")
fun playSlideSound() = playEffectPlayerSound("files/raw/slide.ogg")
fun playMinigunSound() = playEffectPlayerSound("files/raw/minigun_f2.ogg")
fun playNotificationSound() = playEffectPlayerSound("files/raw/notification.mp3")

expect fun stopAmbient()
expect fun setAmbientVolume(volume: Float)
expect fun startAmbient()

private fun getSongsArray() = arrayOf(
    "files/radio/MUS_Aint_That_A_Kick_In_the_Head.amr" to "\"Ain't That a Kick in the Head?\" - Dean Martin",
    "files/radio/MUS_American_Swing.amr" to "\"American Swing\" - Gerhard Trede",
    "files/radio/MUS_Big_Iron.amr" to "\"Big Iron\" - Marty Robbins",
    "files/radio/MUS_Blue_Moon.amr" to "\"Blue Moon\" - Frank Sinatra",
    "files/radio/MUS_Blues_For_You.amr" to "\"Blues For You\" - Gabriel Pares",
    if (save.isRadioUsesPseudonyms) {
        "files/radio/MUS_Cobwebs_and_Rainbows.amr" to "\"Cobwebs and Rainbows\" - Bruce Isaac"
    } else {
        "files/radio/MUS_Cobwebs_and_Rainbows.amr" to "\"Cobwebs and Rainbows\" - Joshua Sawyer/Dick Stephen Walter"
    },
    "files/radio/MUS_EddyArnold_Rca_ItsASin.amr" to "\"It's a Sin\" - Eddy Arnold",
    "files/radio/MUS_Goin_Under.amr" to "\"Goin' Under\" - Darrell Wayne Perry and Tommy Smith",
    "files/radio/MUS_Hallo_Mister_X.amr" to "\"Hallo Mister X\" - Gerhard Trede",
    "files/radio/MUS_Happy_Times.amr" to "\"Happy Times\" - Bert Weedon",
    "files/radio/MUS_Heartaches_by_the_Number.amr" to "\"Heartaches by the Number\" - Guy Mitchell",
    if (save.isRadioUsesPseudonyms) {
        "files/radio/MUS_HomeOnTheWastes.amr" to "\"Home on the Wastes\" - Lonesome Drifter"
    } else {
        "files/radio/MUS_HomeOnTheWastes.amr" to "\"Home on the Wastes\" - Joshua Sawyer/Nathaniel Chapman"
    },
    "files/radio/MUS_I_m_Movin_Out.amr" to "\"I'm Movin' Out\" - The Roues Brothers",
    "files/radio/MUS_I_m_So_Blue.amr" to "\"I'm So Blue\" - Katie Thompson",
    "files/radio/MUS_In_The_Shadow_Of_The_Valley.amr" to "\"In the Shadow of the Valley\" - Lost Weekend Western Swing Band",
    "files/radio/MUS_Its_A_Sin_To_Tell_A_Lie.amr" to "\"It's a Sin to Tell a Lie\" - The Ink Spots",
    "files/radio/MUS_Jazz_Blues_GT.amr" to "\"Jazz Blues\" - Gerhard Trede",
    "files/radio/MUS_Jazz_Club_Blues_CAS.amr" to "\"Jazz Club Blues\" - Harry Bluestone",
    "files/radio/MUS_Jingle_Jangle_Jingle.amr" to "\"Jingle, Jangle, Jingle\" - The Kay Kyser Orchestra",
    if (save.isRadioUsesPseudonyms) {
        "files/radio/MUS_Joe_Cool_CAS.amr" to "\"Joe Cool\" - Nino Nardini"
    } else {
        "files/radio/MUS_Joe_Cool_CAS.amr" to "\"Joe Cool\" - Georges Teperin"
    },
    "files/radio/MUS_Johnny_Guitar.amr" to "\"Johnny Guitar\" - Peggy Lee",
    "files/radio/MUS_Lazy_Day_Blues.amr" to "\"Lazy Day Blues\" - Bert Weedon",
    "files/radio/MUS_Let_s_Ride_Into_The_Sunset_Together.amr" to "\"Let's Ride Into the Sunset Together\" - Lost Weekend Western Swing Band, featuring Don Burham with Patty Kistner",
    "files/radio/MUS_Lone_Star.amr" to "\"Lone Star\" - Lost Weekend Western Swing Band",
    "files/radio/MUS_Love_Me_As_Though_No_Tomorrow.amr" to "\"Love Me as Though There Were No Tomorrow\" - Nat King Cole",
    "files/radio/MUS_Mad_About_The_Boy.amr" to "\"Mad About the Boy\" - Carmen Dragon and his Orchestra, featuring Helen Forrest",
    "files/radio/MUS_Manhattan.amr" to "\"Manhattan\" - Gerhard Trede",
    if (save.isRadioUsesPseudonyms) {
        "files/radio/MUS_NewVegasValley.amr" to "\"New Vegas Valley\" - Lonesome Drifter"
    } else {
        "files/radio/MUS_NewVegasValley.amr" to "\"New Vegas Valley\" - Joshua Sawyer/James Melilli"
    },
    "files/radio/MUS_Roundhouse_Rock.amr" to "\"Roundhouse Rock\" - Bert Weedon",
    "files/radio/MUS_Sit_And_Dream.amr" to "\"Sit and Dream\" - Pete Thomas",
    "files/radio/MUS_Sleepy_Town_Blues_CAS.amr" to "\"Sleepy Town Blues\" - Harry Lubin",
    "files/radio/MUS_Slow_Bounce.amr" to "\"Slow Bounce\" - Gerhard Trede",
    "files/radio/MUS_Slow_Sax_KOS.amr" to "\"Slow Sax\" - Christof Dejean",
    "files/radio/MUS_Somethings_Gotta_Give.amr" to "\"Something's Gotta Give\" - Bing Crosby",
    "files/radio/MUS_Stars_Of_The_Midnight_Range.amr" to "\"Stars of the Midnight Range\" - Johnny Bond and his Red River Valley Boys",
    "files/radio/MUS_Strahlende_Trompete.amr" to "\"Strahlende Trompete\" - Gerhard Trede",
    if (save.isRadioUsesPseudonyms) {
        "files/radio/MUS_StreetsOfNewReno.amr" to "\"Streets of New Reno\" - Lonesome Drifter"
    } else {
        "files/radio/MUS_StreetsOfNewReno.amr" to "\"Streets of New Reno\" - Joshua Sawyer/Nathaniel Chapman"
    },
    "files/radio/MUS_Von_Spanien_Nach_S_damerika.amr" to "\"Von Spanien Nach Südamerika\" - Gerhard Trede",
    "files/radio/MUS_Where_Have_You_Been_All_My_Life.amr" to "\"Where Have You Been All My Life?\" - Jeff Hooper",
    "files/radio/MUS_Why_Dont_You_Do_Right.amr" to "\"Why Don't You Do Right?\" - The Dave Barbour Quartet, featuring Peggy Lee",
)

var pointer = -1
val indices = getSongsArray().indices.toMutableList()
fun getSongByIndex(): Pair<String, String>? {
    val songsArray = getSongsArray()
    return if (pointer == -1) {
        if (styleId == Style.SIERRA_MADRE || styleId == Style.MADRE_ROJA) {
            if (save.isRadioUsesPseudonyms) {
                "files/radio/begin_again.amr" to "\"Begin Again\" - Vera Keyes"
            } else {
                "files/radio/begin_again.amr" to "\"Begin Again\" - Justin E. Bell, Stephanie Dowling"
            }
        } else {
            if (save.isRadioUsesPseudonyms) {
                "files/radio/MUS_caravan_whiplash.amr" to "\"Caravan\" - Shaffer Conservatory Studio Band"
            } else {
                "files/radio/MUS_caravan_whiplash.amr" to "\"Caravan\" - John Wasson"
            }
        }
    } else if (pointer in songsArray.indices) {
        songsArray[indices[pointer]]
    } else {
        null
    }
}


private var radioStartedFlag = false
fun startRadio() {
    if (radioStartedFlag) {
        return
    }
    radioStartedFlag = true
    indices.shuffle()
    if (save.useCaravanIntro) {
        pointer = -1
        playSongFromRadio()
    } else {
        pointer = 0
        nextSong()
    }
}

expect fun playSongFromRadio()
expect fun stopRadio()
expect fun nextSong()
expect fun resumeRadio()
expect fun pauseRadio()
expect fun setRadioVolume(volume: Float)
expect fun playTheme(themePath: String)

fun startLevel11Theme() {
    if (save.isHeroic) {
        playTheme("files/raw/final_heroic.ogg")
    } else {
        playTheme("files/raw/frank_theme.ogg")
    }
}
fun startFinalBossTheme() {
    if (save.isHeroic) {
        playTheme("files/raw/final_heroic.ogg")
    } else {
        playTheme("files/raw/chapter_13.ogg")
    }
}

expect fun playFrankPhrase(phrasePath: String)
expect fun playDeathPhrase(phrasePath: String)
