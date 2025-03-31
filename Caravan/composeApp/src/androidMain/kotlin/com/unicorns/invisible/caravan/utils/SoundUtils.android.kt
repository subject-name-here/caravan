package com.unicorns.invisible.caravan.utils

import android.media.MediaDataSource
import android.media.MediaPlayer
import caravan.composeapp.generated.resources.Res
import com.unicorns.invisible.caravan.playingSongName
import com.unicorns.invisible.caravan.save
import com.unicorns.invisible.caravan.soundReduced
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

class ByteMediaDataSource(val bytes: ByteArray) : MediaDataSource() {
    var isClosed = false
    override fun readAt(
        position: Long,
        buffer: ByteArray?,
        offset: Int,
        size: Int
    ): Int {
        if (position >= bytes.size || buffer == null) {
            return -1
        }

        if (position + size >= bytes.size) {
            System.arraycopy(bytes, position.toInt(), buffer, offset, bytes.size - position.toInt())
            return bytes.size - position.toInt()
        }

        System.arraycopy(bytes, position.toInt(), buffer, offset, size)
        return size
    }

    override fun getSize(): Long {
        return bytes.size.toLong()
    }

    override fun close() {
        isClosed = true
    }
}

@OptIn(ExperimentalResourceApi::class)
actual fun playEffectPlayerSound(soundPath: String, volumeFraction: Int) {
    val vol = save.soundVolume / volumeFraction
    CoroutineScope(Dispatchers.Unconfined).launch {
        val bytes = Res.readBytes(soundPath)
        val player = MediaPlayer()
        player.apply {
            setDataSource(ByteMediaDataSource(bytes))
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

    val vol = save.ambientVolume / 2

    CoroutineScope(Dispatchers.Unconfined).launch {
        val bytes = Res.readBytes("files/raw/ambient${(1..8).random()}.ogg")
        val player = MediaPlayer()
        player.apply {
            setDataSource(ByteMediaDataSource(bytes))
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
}


enum class RadioState {
    PLAYING,
    PAUSED_BY_BUTTON,
    PAUSED_BY_LEAVING_ACTIVITY,
}


private val radioPlayers = HashSet<MediaPlayer>()
private val radioLock = ReentrantLock()
private var radioState = RadioState.PLAYING
@OptIn(ExperimentalResourceApi::class)
actual fun playSongFromRadio() {
    val vol = save.radioVolume

    CoroutineScope(Dispatchers.Unconfined).launch {
        try {
            val songName = getSongByIndex()!!
            val bytes = Res.readBytes(songName.first)
            val player = MediaPlayer()
            player.apply {
                setDataSource(ByteMediaDataSource(bytes))
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
        } catch (e: Exception) {
            delay(760L)
            nextSong()
        }
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
    val vol = save.radioVolume

    CoroutineScope(Dispatchers.Unconfined).launch {
        val bytes = Res.readBytes(themePath)
        val player = MediaPlayer()
        player.apply {
            setDataSource(ByteMediaDataSource(bytes))
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
}

@OptIn(ExperimentalResourceApi::class)
actual fun playFrankPhrase(phrasePath: String) {
    val vol = save.soundVolume / 2
    CoroutineScope(Dispatchers.Unconfined).launch {
        val bytes = Res.readBytes(phrasePath)
        val player = MediaPlayer()
        player.apply {
            setDataSource(ByteMediaDataSource(bytes))
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
}

@OptIn(ExperimentalResourceApi::class)
actual fun playDeathPhrase(phrasePath: String) {
    val vol = save.radioVolume
    CoroutineScope(Dispatchers.Unconfined).launch {
        val player = MediaPlayer()
        val bytes = Res.readBytes("files/death_messages/${phrasePath}")
        player.apply {
            setDataSource(ByteMediaDataSource(bytes))
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
}
