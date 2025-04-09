package com.unicorns.invisible.caravan.save

import android.widget.Toast
import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.activity
import com.unicorns.invisible.caravan.isSaveLoaded
import com.unicorns.invisible.caravan.save
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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

actual fun saveData() {
    val act = activity
    if (isSaveLoaded != true || act == null) {
        return
    }

    val text = json.encodeToString(save)
    try {
        val localSave = getLocalSaveFile(act)
        localSave.bufferedWriter().use {
            it.write(text)
        }
    } catch (_: Exception) {
        MainScope().launch {
            Toast.makeText(
                act,
                "Failed to save locally :(",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val bytes = text.toByteArray(StandardCharsets.UTF_8)
    CoroutineScope(Dispatchers.IO).launch { activity?.uploadDataToDrive(bytes) }
}

fun failToLoad() {
    MainScope().launch {
        Toast.makeText(
            activity ?: return@launch,
            "Failed to load from GD :(",
            Toast.LENGTH_SHORT
        ).show()
    }
}

actual suspend fun loadGDSave(): Save? {
    return try {
        val data = activity?.fetchDataFromDrive()?.toString(StandardCharsets.UTF_8)
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

actual suspend fun loadLocalSave(): Save? {
    val act = activity
    if (act == null) return null
    val saveFile = getLocalSaveFile(act)
    return try {
        saveFile.bufferedReader().use {
            val text = it.readText()
            json.decodeFromString<Save>(text)
        }
    } catch (_: Exception) {
        failToLoad()
        null
    }
}