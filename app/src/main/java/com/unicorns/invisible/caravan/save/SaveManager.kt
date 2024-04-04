package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.MainActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


private val json = Json {
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
}

fun loadSave(activity: MainActivity): Save? {
    val saveFile = getSaveFile(activity)
    return try {
        saveFile.bufferedReader().use {
            json.decodeFromString<Save>(it.readText())
        }
    } catch (e: Exception) {
        null
    }
}

fun save(activity: MainActivity, save: Save) {
    val saveFile = getSaveFile(activity)
    val text = json.encodeToString(save)
    saveFile.bufferedWriter().use {
        it.write(text)
    }
}

private fun getSaveFile(activity: MainActivity): File {
    val directory = activity.filesDir
    return directory.resolve("save").apply { if (!exists()) createNewFile() }
}