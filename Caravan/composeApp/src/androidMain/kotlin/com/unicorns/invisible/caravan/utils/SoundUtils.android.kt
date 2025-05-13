package com.unicorns.invisible.caravan.utils

import android.media.MediaPlayer
import com.unicorns.invisible.caravan.activity
import com.unicorns.invisible.caravan.playingSongName
import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.soundReduced
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


private val effectPlayers = HashSet<MediaPlayer>()
private val effectPlayersLock = ReentrantLock()
actual fun stopSoundEffects() {
    effectPlayersLock.withLock {
        effectPlayers.forEach { if (it.isPlaying) it.stop() }
        effectPlayers.clear()
    }
}

@OptIn(ExperimentalResourceApi::class)
actual fun playEffectPlayerSound(soundPath: String, volumeFraction: Int) {
    val vol = saveGlobal.soundVolume / volumeFraction
    val act = activity ?: return
    val player = MediaPlayer()
    val afd = act.assets.openFd(soundPath.removePrefix("files/"))
    player.apply {
        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        setVolume(vol, vol)
        setOnCompletionListener {
            effectPlayersLock.withLock {
                effectPlayers.remove(this)
                release()
            }
        }
        setOnPreparedListener {
            effectPlayersLock.withLock {
                effectPlayers.add(this)
                start()
            }
        }
        prepare()
    }
}


private val ambientPlayers = HashSet<MediaPlayer>()
private val ambientPlayersLock = ReentrantLock()
private var wasAmbientPaused = false
actual fun stopAmbient() {
    ambientPlayersLock.withLock {
        ambientPlayers.forEach { if (it.isPlaying) it.stop() }
        ambientPlayers.clear()
    }
}
actual fun setAmbientVolume(volume: Float) {
    ambientPlayersLock.withLock {
        ambientPlayers.forEach { it.setVolume(volume, volume) }
    }
}

@OptIn(ExperimentalResourceApi::class)
actual fun startAmbient() {
    if (soundReduced) return

    val act = activity ?: return
    val vol = saveGlobal.ambientVolume / 2

    val soundPath = "files/raw/ambient${(1..8).random()}.ogg"
    val player = MediaPlayer()
    val afd = act.assets.openFd(soundPath.removePrefix("files/"))
    player.apply {
        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        setVolume(vol, vol)
        setOnCompletionListener {
            ambientPlayersLock.withLock {
                ambientPlayers.remove(this)
                release()
            }
            startAmbient()
        }
        setOnPreparedListener {
            ambientPlayersLock.withLock {
                ambientPlayers.add(this)
                if (!wasAmbientPaused) {
                    start()
                }
            }
        }
        prepare()
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
actual fun playSongFromRadio() {
    val vol = saveGlobal.radioVolume
    val act = activity ?: return
    try {
        val songName = getSongByIndex()!!
        val soundPath = songName.first
        val player = MediaPlayer()
        val afd = act.assets.openFd(soundPath.removePrefix("files/"))
        player.apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            playingSongName = songName.second
            setOnCompletionListener {
                radioPlayers.remove(this)
                release()
                nextSong()
            }
            setVolume(vol, vol)
            setOnPreparedListener {
                radioLock.withLock {
                    radioPlayers.add(this)
                    if (radioState == RadioState.PLAYING) {
                        start()
                    }
                }
            }
            prepare()
        }
    } catch (_: Exception) {
        nextSong()
    }
}

actual fun stopRadio() {
    radioLock.withLock {
        radioPlayers.forEach { if (it.isPlaying) it.stop() }
        radioPlayers.clear()
    }
}

actual fun nextSong() {
    stopRadio()

    pointer++
    if (pointer !in indices.indices) {
        pointer = 0
        indices.shuffle()
    }

    playSongFromRadio()
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


actual fun resumeRadio() {
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


actual fun pauseRadio() {
    if (radioState == RadioState.PLAYING) {
        radioLock.withLock {
            radioPlayers.forEach { it.pause() }
            radioState = RadioState.PAUSED_BY_BUTTON
        }
    }
}

actual fun setRadioVolume(volume: Float) {
    radioLock.withLock {
        radioPlayers.forEach { it.setVolume(volume, volume) }
    }
}

@OptIn(ExperimentalResourceApi::class)
actual fun playTheme(themePath: String) {
    stopRadio()
    val vol = saveGlobal.radioVolume

    val player = MediaPlayer()
    val afd = (activity ?: return).assets.openFd(themePath.removePrefix("files/"))
    player.apply {
        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        isLooping = true
        setVolume(vol, vol)
        setOnCompletionListener {
            radioLock.withLock {
                it.stop()
                radioPlayers.remove(it)
                it.release()
            }
        }
        setOnPreparedListener {
            radioLock.withLock {
                radioPlayers.add(this)
                if (radioState != RadioState.PAUSED_BY_LEAVING_ACTIVITY) {
                    start()
                }
            }
        }
        prepare()
    }
}

@OptIn(ExperimentalResourceApi::class)
actual fun playFrankPhrase(phrasePath: String) {
    val vol = saveGlobal.soundVolume / 2
    val player = MediaPlayer()
    val afd = (activity ?: return).assets.openFd(phrasePath.removePrefix("files/"))
    player.apply {
        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        setVolume(vol, vol)
        setOnCompletionListener {
            effectPlayersLock.withLock {
                effectPlayers.remove(this)
                release()
            }
        }
        setOnPreparedListener {
            effectPlayersLock.withLock {
                stopSoundEffects()
                effectPlayers.add(this)
                start()
            }
        }
        prepare()
    }
}

@OptIn(ExperimentalResourceApi::class)
actual fun playDeathPhrase(phrasePath: String) {
    val vol = saveGlobal.radioVolume
    val player = MediaPlayer()
    val soundPath = "files/death_messages/${phrasePath}"
    val afd = (activity ?: return).assets.openFd(soundPath.removePrefix("files/"))
    player.apply {
        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        setVolume(vol, vol)
        setOnCompletionListener {
            release()
        }
        setOnPreparedListener {
            start()
        }
        prepare()
    }
}
