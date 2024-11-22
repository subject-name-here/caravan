package com.unicorns.invisible.caravan.save

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.save
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

// TODO: local save backup!!!

private fun getLocalSaveFile(activity: MainActivity): File {
    return activity.filesDir.resolve("saveMkII")
}

fun saveData(activity: MainActivity) {
    // TODO: save locally and on cloud!!!
    // saveOnGD(activity)
}

fun saveOnGDAsync(activity: MainActivity): Deferred<Boolean> {
    if (snapshotsClient == null)
        return CoroutineScope(Dispatchers.Unconfined).async { false }

    val bytes = json.encodeToString(save).toByteArray(StandardCharsets.UTF_8)
    return CoroutineScope(Dispatchers.IO).async { activity.uploadDataToDrive(bytes) }
}

// TODO: make it private
fun saveOnGD(activity: MainActivity) {
    if (snapshotsClient == null)
        return

    val bytes = json.encodeToString(save).toByteArray(StandardCharsets.UTF_8)
    CoroutineScope(Dispatchers.IO).launch { activity.uploadDataToDrive(bytes) }
}

suspend fun loadFromGD(activity: MainActivity) {
    val data = activity.fetchDataFromDrive()?.toString(StandardCharsets.UTF_8)
    if (data != null && data != "") {
        save = json.decodeFromString<Save>(data)
    }
}