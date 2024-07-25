package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.saveGlobal
import com.unicorns.invisible.caravan.snapshotsClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.charset.StandardCharsets


val json = Json {
    allowStructuredMapKeys = true
    ignoreUnknownKeys = true
}

fun loadLocalSave(activity: MainActivity): Save? {
    val saveFile = getSaveFile(activity)
    return try {
        saveFile.bufferedReader().use {
            val text = it.readText()
            json.decodeFromString<Save>(text)
        }
    } catch (e: Exception) {
        null
    }
}

fun deleteLocalFile(activity: MainActivity) {
    val saveFile = getSaveFile(activity)
    if (saveFile.exists()) {
        saveFile.delete()
    }
}

private fun getSaveFile(activity: MainActivity): File {
    return activity.filesDir.resolve("save")
}

fun saveOnGDAsync(activity: MainActivity): Deferred<Boolean> {
    if (snapshotsClient == null)
        return CoroutineScope(Dispatchers.Unconfined).async { false }

    val bytes = json.encodeToString(saveGlobal).toByteArray(StandardCharsets.UTF_8)
    return CoroutineScope(Dispatchers.IO).async { activity.uploadDataToDrive(bytes) }
}

fun saveOnGD(activity: MainActivity) {
    if (snapshotsClient == null)
        return

    val bytes = json.encodeToString(saveGlobal).toByteArray(StandardCharsets.UTF_8)
    CoroutineScope(Dispatchers.IO).launch { activity.uploadDataToDrive(bytes) }
}

suspend fun loadFromGD(activity: MainActivity) {
    val data = activity.fetchDataFromDrive()?.toString(StandardCharsets.UTF_8)
    if (data != null && data != "") {
        // TODO: try/catch?
        saveGlobal = json.decodeFromString<Save>(data)
    }
}