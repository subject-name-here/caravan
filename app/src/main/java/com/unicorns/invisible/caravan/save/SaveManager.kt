package com.unicorns.invisible.caravan.save

import android.widget.Toast
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.isSaveLoaded
import com.unicorns.invisible.caravan.save
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.charset.StandardCharsets


val json = Json {
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
}


private fun getLocalSaveFile(activity: MainActivity): File {
    return activity.filesDir.resolve("saveMkII").apply { if (!exists()) createNewFile() }
}

fun saveData(activity: MainActivity) {
    if (isSaveLoaded.value != true) {
        return
    }

    val text = json.encodeToString(save)
    try {
        val localSave = getLocalSaveFile(activity)
        localSave.bufferedWriter().use {
            it.write(text)
        }
    } catch (_: Exception) {
        MainScope().launch {
            Toast.makeText(
                activity,
                "Failed to save locally :(",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val bytes = text.toByteArray(StandardCharsets.UTF_8)
    CoroutineScope(Dispatchers.IO).launch { activity.uploadDataToDrive(bytes) }
}


suspend fun loadGDSave(activity: MainActivity): Save? {
    fun failToLoad() {
        MainScope().launch {
            Toast.makeText(
                activity,
                "Failed to load from GD :(",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    return try {
        val data = activity.fetchDataFromDrive()?.toString(StandardCharsets.UTF_8)
        if (data != null && data != "" && data != "null") {
            json.decodeFromString<Save>(data)
        } else {
            failToLoad()
            null
        }
    } catch (_: Exception) {
        failToLoad()
        null
    }
}

fun loadLocalSave(activity: MainActivity): Save? {
    val saveFile = getLocalSaveFile(activity)
    return try {
        saveFile.bufferedReader().use {
            val text = it.readText()
            json.decodeFromString<Save>(text)
        }
    } catch (_: Exception) {
        MainScope().launch {
            Toast.makeText(
                activity,
                "Failed to load local save :(",
                Toast.LENGTH_SHORT
            ).show()
        }
        null
    }
}